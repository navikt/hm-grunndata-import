package no.nav.hm.grunndata.importapi.productstate

import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort.*
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
/*        runBlocking {
            val contents = transferStateRepository.findByTransferStatus(TransferStatus.RECEIVED, Pageable.from(0, 1000,
                of(Order.asc("updated")))).content
            contents.map {
                productStateRepository.findById(it.productId)?.let { inDb ->
                    productStateRepository.update(inDb.copy(transferId = it.transferId,
                        productDTO = it.json_payload.toProductDTO()))
                }
            }

        }*/
    }

/*    private suspend fun ProductTransferDTO.toProductDTO(): ProductDTO = ProductDTO(
        id = id,
        supplier = supplierService.findById(supplier)!!.toDTO(),
        title = title,
        supplierRef = supplierRef,
        attributes = attributes.mapKeys { AttributeNames.valueOf(it.key.name) },
        hmsArtNr = hmsArtNr,
        identifier = id.toString(),
        isoCategory =isoCategory,
        accessory = accessory,
        sparePart = sparePart,
        seriesId = seriesId,
        techData = transferTechData.map { TechData() },
        media = media,
        published = published,
        expired = expired,
        agreementInfo = agreementInfo,
        hasAgreement = (agreementInfo!=null),
        createdBy = createdBy,
        updatedBy = updatedBy,
    )*/
}
