package no.nav.hm.grunndata.importapi.productImport

import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.transfer.product.TransferStateRepository
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Singleton
class TransferToProductImport(private val transferStateRepository: TransferStateRepository,
                              private val productImportKafkaService: ProductImportKafkaService
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(TransferToProductImport::class.java)
    }

    suspend fun receivedTransfersToProductState() {
        val contents = transferStateRepository.findByTransferStatus(TransferStatus.RECEIVED).content
        LOG.info("Got ${contents.size} transfers to map to products")
        contents.map {
            productImportKafkaService.mapTransferToProductState(it)
            transferStateRepository.update(it.copy(transferStatus = TransferStatus.DONE, updated = LocalDateTime.now()))
        }
        //TODO feilhåndtering her
    }

}
