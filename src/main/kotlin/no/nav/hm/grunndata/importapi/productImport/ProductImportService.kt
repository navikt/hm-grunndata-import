package no.nav.hm.grunndata.importapi.productImport

import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import no.nav.hm.grunndata.importapi.*
import no.nav.hm.grunndata.importapi.agreement.AgreementService
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportDTO
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportService
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.supplier.toDTO
import no.nav.hm.grunndata.importapi.transfer.product.*
import no.nav.hm.grunndata.rapid.dto.*
import no.nav.hm.grunndata.rapid.dto.CompatibleWith
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.exp


@Singleton
open class ProductImportService(private val productImportRepository: ProductImportRepository,
                                private val supplierService: SupplierService,
                                private val seriesImportService: SeriesImportService,
                                private val agreementService: AgreementService
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductImportService::class.java)
    }

    @Transactional
    open suspend fun mapSaveTransferToProductImport(transfer: ProductTransfer): ProductImport {
        val seriesId = transfer.json_payload.seriesId
        val seriesRef = transfer.json_payload.supplierSeriesRef
        val seriesStateDTO = if (seriesId != null) seriesImportService.findByIdCacheable(seriesId)
            else if ( seriesRef!=null ) seriesImportService.findBySupplierIdAndSupplierSeriesRef(transfer.supplierId, seriesRef)
            else null
        val productImport = productImportRepository.findBySupplierIdAndSupplierRef(transfer.supplierId, transfer.supplierRef)?.let { inDb ->
            val productDTO = transfer.json_payload.toProductDTO(inDb.id, transfer.supplierId, seriesStateDTO)
            productImportRepository.update(
                inDb.copy(
                    transferId = transfer.transferId,
                    productDTO = productDTO,
                    updated = LocalDateTime.now(),
                    productStatus = productDTO.status
                )
            )
        } ?: run {
            val productId = UUID.randomUUID()
            val productDTO = transfer.json_payload.toProductDTO(productId, transfer.supplierId, seriesStateDTO)
            productImportRepository.save(
                ProductImport(
                    id = productId, transferId = transfer.transferId, supplierId = transfer.supplierId,
                    supplierRef = transfer.supplierRef,
                    productDTO = productDTO,
                    productStatus = productDTO.status, adminStatus = AdminStatus.PENDING
                )
            )
        }
        LOG.info("mapped product ${productImport.id} and transfer id: ${productImport.transferId} " +
                "for supplierId: ${productImport.supplierId} supplierRef: ${productImport.supplierRef}")

        return productImport
    }

    private suspend fun ProductTransferDTO.toProductDTO(productId: UUID, supplierId: UUID, seriesImportDTO: SeriesImportDTO?): ProductRapidDTO {
        val nPublished = published ?: LocalDateTime.now().minusMinutes(1)
        val nExpired = expired ?: LocalDateTime.now().plusYears(10)
        return ProductRapidDTO (
            id = productId,
            supplier = supplierService.findById(supplierId)!!.toDTO(),
            title = title,
            articleName = articleName,
            supplierRef = supplierRef,
            attributes = Attributes (
                series = seriesImportDTO?.name,
                shortdescription = shortDescription,
                text = text,
                compatibleWidth = if (this.compatibleWith!=null) CompatibleWith(ids = compatibleWith.ids,
                    seriesIds = compatibleWith.seriesIds) else null
            ),
            hmsArtNr = hmsArtNr,
            identifier = productId.toString(),
            isoCategory = isoCategory,
            accessory = accessory,
            sparePart = sparePart,
            seriesId = seriesImportDTO?.seriesId?.toString() ?: productId.toString(), // use the productId if it's a single product
            techData = transferTechData.map { TechData(key = it.key, unit = it.unit, value = it.value ) },
            media = media.map { mapMedia(it)},
            published = nPublished,
            expired = nExpired,
            status = mapStatus(nPublished, nExpired),
            agreements = agreements.map { mapProductAgreement(it) },
            hasAgreement = false,
            createdBy = IMPORT,
            updatedBy = IMPORT,
        )
    }

    private fun mapProductAgreement(agree: ProductAgreement): AgreementInfo {
        val byRef = agreementService.getAgreementByReference(agree.reference)
            ?: throw ImportErrorException("Agreement with reference ${agree.reference} could not be found or is no longer active")
        val post = byRef.posts.find { it.nr == agree.postNr }
            ?: throw ImportErrorException("Agreement with postnr ${agree.postNr} does not exists")
        return AgreementInfo(id = byRef.id, identifier = byRef.identifier, title = byRef.title, rank = agree.rank, postNr = post.nr,
            postIdentifier = post.identifier, postTitle = post.title, reference = byRef.reference, expired = byRef.expired
        )
    }

    private fun mapStatus(published: LocalDateTime, expired: LocalDateTime): ProductStatus {
        return if (published.isBefore(LocalDateTime.now()) && expired.isAfter(LocalDateTime.now())) {
            ProductStatus.ACTIVE
        } else ProductStatus.INACTIVE
    }


    private fun mapMedia(media: TransferMediaDTO): MediaInfo {
        return MediaInfo(
            sourceUri = media.uri,
            uri = media.uri,
            priority = media.priority,
            source = media.sourceType,
            type = when (media.type) {
                TransferMediaType.PNG, TransferMediaType.JPG -> MediaType.IMAGE
                TransferMediaType.VIDEO -> MediaType.VIDEO
                TransferMediaType.PDF -> MediaType.PDF
            }
        )
    }

}
