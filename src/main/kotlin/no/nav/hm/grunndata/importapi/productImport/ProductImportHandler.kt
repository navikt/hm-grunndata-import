package no.nav.hm.grunndata.importapi.productImport

import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import no.nav.hm.grunndata.importapi.*
import no.nav.hm.grunndata.importapi.agreement.AgreementService
import no.nav.hm.grunndata.importapi.error.ErrorType
import no.nav.hm.grunndata.importapi.error.ImportApiError
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportService
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.supplier.toDTO
import no.nav.hm.grunndata.importapi.transfer.product.*
import no.nav.hm.grunndata.rapid.dto.*
import no.nav.hm.grunndata.rapid.dto.CompatibleWith
import no.nav.hm.grunndata.rapid.dto.TechData
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.UUID


@Singleton
open class ProductImportHandler(private val productImportRepository: ProductImportRepository,
                                private val supplierService: SupplierService,
                                private val seriesImportService: SeriesImportService,
                                private val agreementService: AgreementService,
                                private val gdbApiClient: GdbApiClient
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductImportHandler::class.java)
    }

    @Transactional
    open suspend fun mapSaveTransferToProductImport(transfer: ProductTransfer): ProductImport {
        val supplierId = transfer.supplierId
        val supplierRef = transfer.supplierRef
        val transferId = transfer.transferId
        val productImport = productImportRepository.findBySupplierIdAndSupplierRef(supplierId, supplierRef)?.let { inDb ->
            LOG.info("Product from supplier $supplierId and ref $supplierRef found in import database ${inDb.id}")
            val productDTO = createProductDTO(inDb.id,transfer)
            productImportRepository.update(
                inDb.copy(
                    transferId = transferId,
                    productDTO = productDTO,
                    updated = LocalDateTime.now(),
                    productStatus = productDTO.status,
                    seriesId = UUID.fromString(productDTO.seriesId)
                )
            )
        } ?: run {
            gdbApiClient.getProductBySupplierIdAndSupplierRef(supplierId, supplierRef)?.let { dto ->
                LOG.info("Product from supplier $supplierId and ref $supplierRef found in GDB ${dto.id} ")
                val productDTO = createProductDTO(dto.id, transfer)
                productImportRepository.save(
                    ProductImport(
                        id = dto.id,
                        transferId = transferId,
                        supplierId = supplierId,
                        supplierRef = supplierRef,
                        productDTO = productDTO,
                        productStatus = productDTO.status,
                        adminStatus = AdminStatus.PENDING,
                        seriesId = UUID.fromString(productDTO.seriesId)
                    )
                )
            }
        } ?: run {
            val productId = UUID.randomUUID()
            LOG.info("New product for $supplierId and ref $supplierRef with new id $productId")
            val productDTO = createProductDTO(productId, transfer)
            productImportRepository.save(
                ProductImport(
                    id = productId, transferId = transfer.transferId, supplierId = transfer.supplierId,
                    supplierRef = transfer.supplierRef,
                    productDTO = productDTO,
                    productStatus = productDTO.status, adminStatus = AdminStatus.PENDING, seriesId = UUID.fromString(productDTO.seriesId)
                )
            )
        }
        LOG.info("mapped product ${productImport.id} and transfer id: ${productImport.transferId} " +
                "for supplierId: ${productImport.supplierId} supplierRef: ${productImport.supplierRef}")

        return productImport
    }

    private fun createProductDTO(productId: UUID, transfer: ProductTransfer): ProductRapidDTO {
        val seriesId = transfer.json_payload.seriesId
        val series = seriesId?.let {
            seriesImportService.findByIdCacheable(seriesId)?.let {
                Series(it.seriesId, it.title, it.text)
            } ?: run {
                gdbApiClient.getSeriesById(seriesId)?.let { Series(it.id, it.title, it.text)} ?:
                throw ImportApiError("Series with id $seriesId not found", ErrorType.NOT_FOUND)
            }
        } ?: Series(productId, transfer.json_payload.title, transfer.json_payload.text) // use productId as seriesId
        return transfer.json_payload.toProductRapidDTO(productId, transfer.supplierId, series)
    }



    private fun ProductTransferDTO.toProductRapidDTO(productId: UUID, supplierId: UUID, series: Series): ProductRapidDTO {
        val nPublished = published ?: LocalDateTime.now().minusMinutes(1)
        val nExpired = expired ?: LocalDateTime.now().plusYears(10)
        return ProductRapidDTO (
            id = productId,
            supplier = supplierService.findById(supplierId)!!.toDTO(),
            title = series.title, // use series title as product title, if products are connected in a series, they have to use the same title.
            articleName = articleName,
            supplierRef = supplierRef,
            attributes = Attributes (
                shortdescription = shortDescription,
                text = series.text,
                url = url,
                compatibleWidth = if (this.compatibleWith!=null) CompatibleWith(
                    seriesIds = compatibleWith.seriesIds) else null
            ),
            identifier = productId.toString(),
            isoCategory = isoCategory,
            accessory = accessory,
            sparePart = sparePart,
            seriesId =  series.seriesId.toString(),
            techData = techData.map { TechData(key = it.key, unit = it.unit, value = it.value ) },
            media = media.map { mapMedia(it)}.toSet(),
            published = nPublished,
            expired = nExpired,
            status = mapStatus(nPublished, nExpired, this),
            agreements = agreements.map { mapProductAgreement(it) },
            hasAgreement = false,
            createdBy = IMPORT,
            updatedBy = IMPORT,
        )
    }


    private fun mapProductAgreement(agree: ProductAgreement): AgreementInfo {
        val byRef = agreementService.getAgreementByReference(agree.reference)
            ?: throw ImportApiError("Agreement with reference ${agree.reference} could not be found or is no longer active", ErrorType.NOT_FOUND)
        val post = byRef.posts.find { it.nr == agree.postNr }
            ?: throw ImportApiError("Agreement with postnr ${agree.postNr} does not exists", ErrorType.INVALID_VALUE)
        return AgreementInfo(id = byRef.id, identifier = byRef.identifier, title = byRef.title, rank = agree.rank, postNr = post.nr,
            postIdentifier = post.identifier, postTitle = post.title, reference = byRef.reference, expired = byRef.expired
        )
    }

    private fun mapStatus(published: LocalDateTime, expired: LocalDateTime, prodctTransferDTO: ProductTransferDTO): ProductStatus {
        return if (prodctTransferDTO.status == ProductStatus.DELETED) ProductStatus.DELETED // DELETED means it is deleted, don't care about published and expired
            else if (published.isBefore(LocalDateTime.now()) && expired.isAfter(LocalDateTime.now()))  ProductStatus.ACTIVE
            else ProductStatus.INACTIVE
    }


    private fun mapMedia(media: MediaDTO): MediaInfo {
        return MediaInfo(
            sourceUri = media.uri,
            uri = media.uri,
            priority = media.priority,
            source = media.sourceType,
            text = media.text,
            type = media.type,
        )
    }
    data class Series(val seriesId: UUID, val title: String, val text: String)
}
