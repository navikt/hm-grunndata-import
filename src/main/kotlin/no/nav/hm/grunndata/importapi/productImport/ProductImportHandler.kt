package no.nav.hm.grunndata.importapi.productImport

import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import no.nav.hm.grunndata.importapi.*
import no.nav.hm.grunndata.importapi.agreement.AgreementService
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportDTO
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportService
import no.nav.hm.grunndata.importapi.seriesImport.toRapidDTO
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.supplier.toDTO
import no.nav.hm.grunndata.importapi.transfer.product.*
import no.nav.hm.grunndata.rapid.dto.*
import no.nav.hm.grunndata.rapid.dto.CompatibleWith
import no.nav.hm.grunndata.rapid.dto.TechData
import no.nav.hm.grunndata.rapid.event.EventName
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.UUID


@Singleton
open class ProductImportHandler(private val productImportRepository: ProductImportRepository,
                                private val supplierService: SupplierService,
                                private val seriesImportService: SeriesImportService,
                                private val agreementService: AgreementService,
                                private val gdbApiClient: GdbApiClient,
                                private val importRapidPushService: ImportRapidPushService
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
                    seriesId = productDTO.seriesUUID ?: UUID.fromString(productDTO.seriesId)
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
                        seriesId = productDTO.seriesUUID ?: UUID.fromString(productDTO.seriesId)
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
                    productStatus = productDTO.status, adminStatus = AdminStatus.PENDING,
                    seriesId = productDTO.seriesUUID ?: UUID.fromString(productDTO.seriesId)
                )
            )
        }
        LOG.info("mapped product ${productImport.id} and transfer id: ${productImport.transferId} " +
                "for supplierId: ${productImport.supplierId} supplierRef: ${productImport.supplierRef}")

        return productImport
    }

    private suspend fun createProductDTO(productId: UUID, transfer: ProductTransfer): ProductRapidDTO {
        return transfer.json_payload.toProductRapidDTO(productId, transfer.supplierId)
    }



    private fun ProductTransferDTO.toProductRapidDTO(productId: UUID, supplierId: UUID): ProductRapidDTO {
        val nPublished = published ?: LocalDateTime.now().minusMinutes(1)
        val nExpired = expired ?: LocalDateTime.now().plusYears(10)
        return ProductRapidDTO (
            id = productId,
            supplier = supplierService.findById(supplierId)!!.toDTO(),
            title = "", // series title will be merged later.
            articleName = articleName,
            supplierRef = supplierRef,
            isoCategory = "",
            attributes = Attributes (
                shortdescription = articleDescription,
                compatibleWidth = if (this.compatibleWith!=null) CompatibleWith(
                    seriesIds = compatibleWith.seriesIds) else null
            ),
            identifier = productId.toString(),
            accessory = accessory,
            sparePart = sparePart,
            seriesUUID = seriesId,
            seriesId =  seriesId.toString(),
            techData = techData.map { TechData(key = it.key, unit = it.unit, value = it.value ) },
            published = nPublished,
            expired = nExpired,
            status = mapStatus(nPublished, nExpired, this),
            createdBy = IMPORT,
            updatedBy = IMPORT,
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
}
