package no.nav.hm.grunndata.importapi.productstate

import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.supplier.toDTO
import no.nav.hm.grunndata.importapi.transferstate.ProductTransferDTO
import no.nav.hm.grunndata.importapi.transferstate.TransferStateRepository
import no.nav.hm.grunndata.importapi.transferstate.TransferStatus
import no.nav.hm.grunndata.rapid.dto.*
import java.time.LocalDateTime

@Singleton
class TransferToProductState(private val productStateRepository: ProductStateRepository,
                             private val transferStateRepository: TransferStateRepository,
                             private val supplierService: SupplierService) {

    fun receivedTransfersToProductState() {
        runBlocking {
            val contents = transferStateRepository.findByTransferStatus(TransferStatus.RECEIVED).content
            contents.map {
                productStateRepository.findById(it.productId)?.let { inDb ->
                    productStateRepository.update(inDb.copy(transferId = it.transferId,
                        productDTO = it.json_payload.toProductDTO()))
                } ?: productStateRepository.save(ProductState(
                    productId = it.productId, transferId = it.transferId, supplierId = it.supplierId,
                    supplierRef = it.supplierRef, productDTO = it.json_payload.toProductDTO()
                ))
            }

        }
    }

    private suspend fun ProductTransferDTO.toProductDTO(): ProductDTO = ProductDTO(
        id = id,
        supplier = supplierService.findById(supplier)!!.toDTO(),
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
        identifier = id.toString(),
        isoCategory =isoCategory,
        accessory = accessory,
        sparePart = sparePart,
        seriesId = seriesId,
        techData = transferTechData.map { TechData(key = it.key, unit = it.unit, value = it.value ) },
        media = media.map { MediaDTO(oid = id, sourceUri = it.sourceUri, uri = it.uri, priority = it.priority,
            source = it.sourceType, type = MediaType.valueOf(it.type.name) ) },
        published =  published ?: LocalDateTime.now(),
        expired = expired ?: LocalDateTime.now().plusYears(10),
        agreementInfo = if (agreementInfo!=null) AgreementInfo(reference = agreementInfo.reference,
            rank = agreementInfo.rank, postNr = agreementInfo.postNr) else null,
        hasAgreement = (agreementInfo!=null),
        createdBy = createdBy,
        updatedBy = updatedBy,
    )
}
