package no.nav.hm.grunndata.importapi.productImport

import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.ImportRapidPushService
import no.nav.hm.grunndata.importapi.productadminstate.ProductAdminState
import no.nav.hm.grunndata.importapi.productadminstate.ProductAdminStateRepository
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransferRepository
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.rapid.event.EventName.Companion.importedProductV1
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Singleton
class ProductTransferToProductImport(private val productTransferRepository: ProductTransferRepository,
                                     private val productAdminStateRepository: ProductAdminStateRepository,
                                     private val productImportService: ProductImportService,
                                     private val importRapidPushService: ImportRapidPushService
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductTransferToProductImport::class.java)

    }

    suspend fun receivedTransfersToProductState() {
        val contents = productTransferRepository.findByTransferStatus(TransferStatus.RECEIVED).content
        LOG.info("Got ${contents.size} transfers to map to products")
        contents.forEach {
            val productImport = productImportService.mapSaveTransferToProductImport(it)
            val productAdminState = productAdminStateRepository.findById(productImport.id)?.let { inDb ->
                productAdminStateRepository.update(inDb.copy(
                    transferId = productImport.transferId,
                    productStatus = productImport.productDTO.status,
                    updated = LocalDateTime.now(),
                    version = productImport.version
                ))
            } ?: productAdminStateRepository.save(
                ProductAdminState(
                    id = productImport.id,
                    transferId = productImport.transferId,
                    supplierId = productImport.supplierId,
                    supplierRef = productImport.supplierRef,
                    productStatus = productImport.productDTO.status,
                    version = productImport.version,
                )
            )
            LOG.info("Product admin state created for ${productAdminState.id} and transfer: ${productAdminState.transferId}")
            productTransferRepository.update(it.copy(transferStatus = TransferStatus.DONE, updated = LocalDateTime.now()))
            importRapidPushService.pushDTOToKafka(productImport.toDTO(), importedProductV1)
        }
        //TODO feilhåndtering her
    }

}
