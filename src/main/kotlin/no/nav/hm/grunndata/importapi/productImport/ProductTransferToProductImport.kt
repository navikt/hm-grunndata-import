package no.nav.hm.grunndata.importapi.productImport

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.ImportRapidPushService
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransfer
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransferRepository
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.rapid.event.EventName.Companion.importedProductV1
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.time.LocalDateTime

@Singleton
open class ProductTransferToProductImport(private val productTransferRepository: ProductTransferRepository,
                                     private val productImportHandler: ProductImportHandler,
                                     private val productImportEventHandler: ProductImportEventHandler) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductTransferToProductImport::class.java)

    }

    suspend fun receivedTransfersToProductImport() {
        val contents = productTransferRepository.findByTransferStatus(TransferStatus.RECEIVED).content
        LOG.info("Got ${contents.size} transfers to map to products")
        contents.forEach {
            try {
                createProductImportFromTransfer(it)
            }
            catch (e: Exception) {
                LOG.error("Error creating product import for transfer ${it.transferId}", e)
                productTransferRepository.update(it.copy(transferStatus = TransferStatus.ERROR, message = e.message, updated = LocalDateTime.now()))
            }
        }
    }

    @Transactional
    open suspend fun createProductImportFromTransfer(it: ProductTransfer) {
        val productImport = productImportHandler.mapSaveTransferToProductImport(it)
        productImportEventHandler.queueDTORapidEvent(productImport.toDTO(), importedProductV1)
        productTransferRepository.update(it.copy(transferStatus = TransferStatus.DONE, updated = LocalDateTime.now()))
        LOG.info("Product import created for ${productImport.id} and transfer: ${productImport.transferId} for supplier ${it.supplierId} and supplierRef: ${it.supplierRef}")
    }

}
