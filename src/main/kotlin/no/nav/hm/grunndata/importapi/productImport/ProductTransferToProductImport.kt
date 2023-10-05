package no.nav.hm.grunndata.importapi.productImport

import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.ImportRapidPushService
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransferRepository
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.rapid.event.EventName.Companion.importedProductV1
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Singleton
class ProductTransferToProductImport(private val productTransferRepository: ProductTransferRepository,
                                     private val productImportHandler: ProductImportHandler,
                                     private val importRapidPushService: ImportRapidPushService
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductTransferToProductImport::class.java)

    }

    suspend fun receivedTransfersToProductImport() {
        val contents = productTransferRepository.findByTransferStatus(TransferStatus.RECEIVED).content
        LOG.info("Got ${contents.size} transfers to map to products")
        contents.forEach {
            val productImport = productImportHandler.mapSaveTransferToProductImport(it)
            LOG.info("Product import created for ${productImport.id} and transfer: ${productImport.transferId}")
            productTransferRepository.update(it.copy(transferStatus = TransferStatus.DONE, updated = LocalDateTime.now()))
            importRapidPushService.pushDTOToKafka(productImport.toRapidDTO(), importedProductV1)
        }
        //TODO feilhåndtering her
    }

}
