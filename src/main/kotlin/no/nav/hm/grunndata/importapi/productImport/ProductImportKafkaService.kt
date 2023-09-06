package no.nav.hm.grunndata.importapi.productImport

import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import no.nav.hm.grunndata.importapi.*
import no.nav.hm.grunndata.importapi.seriesstate.SeriesStateDTO
import no.nav.hm.grunndata.importapi.seriesstate.SeriesStateService
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.supplier.toDTO
import no.nav.hm.grunndata.importapi.transfer.product.TransferMediaType
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransferDTO
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransfer
import no.nav.hm.grunndata.rapid.dto.*
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.UUID


@Singleton
open class ProductImportKafkaService(private val productImportRepository: ProductImportRepository,
                                     private val supplierService: SupplierService,
                                     private val importRapidPushService: ImportRapidPushService,
                                     private val seriesStateService: SeriesStateService) {

    val eventName = "hm-grunndata-import-productstate-transfer"

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductImportKafkaService::class.java)
    }

    @Transactional
    open suspend fun mapSaveTransferToProductImport(transfer: ProductTransfer): ProductImport {
        val seriesId = transfer.json_payload.seriesId
        val seriesStateDTO = if (seriesId != null) seriesStateService.findByIdCacheable(seriesId) else null
        val productImport = productImportRepository.findBySupplierIdAndSupplierRef(transfer.supplierId, transfer.supplierRef)?.let { inDb ->
            productImportRepository.update(
                inDb.copy(
                    transferId = transfer.transferId,
                    productDTO = transfer.json_payload.toProductDTO(inDb.id, transfer.supplierId, seriesStateDTO),
                    updated = LocalDateTime.now()
                )
            )
        } ?: run {
            val productId = UUID.randomUUID()
            productImportRepository.save(
                ProductImport(
                    id = productId, transferId = transfer.transferId, supplierId = transfer.supplierId,
                    supplierRef = transfer.supplierRef,
                    productDTO = transfer.json_payload.toProductDTO(productId, transfer.supplierId, seriesStateDTO)
                )
            )
        }
        LOG.info("productstate ${productImport.id} and transfer id: ${productImport.transferId} " +
                "for supplierId: ${productImport.supplierId} supplierRef: ${productImport.supplierRef} push to rapid")
        importRapidPushService.pushDTOToKafka(productImport.toDTO(), eventName)
        return productImport
    }

    private suspend fun ProductTransferDTO.toProductDTO(productId: UUID, supplierId: UUID, seriesStateDTO: SeriesStateDTO?): ProductRapidDTO = ProductRapidDTO (
        id = productId,
        supplier = supplierService.findById(supplierId)!!.toDTO(),
        title = title,
        articleName = articleName,
        supplierRef = supplierRef,
        attributes = Attributes (
            series = seriesStateDTO?.name,
            shortdescription = shortDescription,
            text = text,
            compatibleWidth = if (this.compatibleWith!=null) CompatibleWith(ids = compatibleWith.ids,
                seriesIds = compatibleWith.seriesIds) else null
        ),
        hmsArtNr = hmsArtNr,
        identifier = productId.toString(),
        isoCategory =isoCategory,
        accessory = accessory,
        sparePart = sparePart,
        seriesId = seriesStateDTO?.id ?: productId.toString(), // use the productId if it's a single product
        techData = transferTechData.map { TechData(key = it.key, unit = it.unit, value = it.value ) },
        media = media.map { MediaInfo( sourceUri = it.sourceUri,
            uri = generateMediaUri(productId, it.sourceUri, it.type),
            priority = it.priority, source = it.sourceType,
            type = if (it.type == TransferMediaType.PDF) MediaType.PDF else MediaType.IMAGE) },
        published = published,
        expired = expired,
        agreements = emptyList(), // TODO,
        hasAgreement = false,
        createdBy = IMPORT,
        updatedBy = IMPORT,
    )

    private fun generateMediaUri(productId: UUID, sourceUri: String, type: TransferMediaType): String {
        val extension = when (type) {
            TransferMediaType.JPG -> "jpg"
            TransferMediaType.PNG -> "png"
            TransferMediaType.PDF -> "pdf"
            TransferMediaType.VIDEO ->  "mp4"
        }
        return "import/${productId}/${sourceUri.toMD5Hex()}.$extension"
    }

}
