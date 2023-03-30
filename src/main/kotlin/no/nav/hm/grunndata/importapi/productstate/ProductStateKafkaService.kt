package no.nav.hm.grunndata.importapi.productstate

import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.ImportRapidPushService
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.supplier.toDTO
import no.nav.hm.grunndata.importapi.transferstate.ProductTransferDTO
import no.nav.hm.grunndata.importapi.transferstate.TransferState
import no.nav.hm.grunndata.rapid.dto.*
import org.slf4j.LoggerFactory
import java.util.UUID
import javax.transaction.Transactional

@Singleton
open class ProductStateKafkaService(private val productStateRepository: ProductStateRepository,
                               private val supplierService: SupplierService,
                               private val importRapidPushService: ImportRapidPushService) {

    val eventName = "hm-grunndata-import-productstate-transfer"

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductStateKafkaService::class.java)
    }

    @Transactional
    open suspend fun mapTransferToProductState(transfer: TransferState) {
        val productstate = productStateRepository.findById(transfer.productId)?.let { inDb ->
            productStateRepository.update(
                inDb.copy(
                    transferId = transfer.transferId,
                    productDTO = transfer.json_payload.toProductDTO(transfer.productId, transfer.supplierId)
                )
            )
        } ?: productStateRepository.save(
            ProductState(
                id = transfer.productId, transferId = transfer.transferId, supplierId = transfer.supplierId,
                supplierRef = transfer.supplierRef,
                productDTO = transfer.json_payload.toProductDTO(transfer.productId, transfer.supplierId)
            )
        )
        LOG.info("productstate ${productstate.id} and transfer id: ${productstate.transferId} push to rapid")
        importRapidPushService.pushDTOToKafka(productstate.toDTO(), eventName)
    }

    private suspend fun ProductTransferDTO.toProductDTO(productId: UUID, supplierId: UUID): ProductDTO = ProductDTO(
        id = productId,
        supplier = supplierService.findById(supplierId)!!.toDTO(),
        title = title,
        articleName = articleName,
        supplierRef = supplierRef,
        attributes = mutableMapOf<AttributeNames, Any?>().apply {
            put(AttributeNames.manufacturer, attributes.manufacturer)
            put(AttributeNames.series, attributes.series)
            put(AttributeNames.url, attributes.url)
            put(AttributeNames.text, attributes.text)
            put(AttributeNames.shortdescription, attributes.shortdescription)
            put(AttributeNames.compatible, attributes.compatible)
        }.filterValues { it !=null }.toMap() as Map<AttributeNames, Any>,
        hmsArtNr = hmsArtNr,
        identifier = productId.toString(),
        isoCategory =isoCategory,
        accessory = accessory,
        sparePart = sparePart,
        seriesId = seriesId,
        techData = transferTechData.map { TechData(key = it.key, unit = it.unit, value = it.value ) },
        media = media.map { MediaInfo( sourceUri = it.sourceUri, uri = it.uri, priority = it.priority,
            source = it.sourceType, type = MediaType.valueOf(it.type.name) ) },
        published = published,
        expired = expired,
        agreementInfo = if (agreementInfo!=null) AgreementInfo(reference = agreementInfo.reference,
            rank = agreementInfo.rank, postNr = agreementInfo.postNr) else null,
        hasAgreement = (agreementInfo!=null),
        createdBy = "IMPORT",
        updatedBy = "IMPORT",
    )
}
