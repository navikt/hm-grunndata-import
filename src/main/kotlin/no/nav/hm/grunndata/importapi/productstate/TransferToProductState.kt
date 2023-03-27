package no.nav.hm.grunndata.importapi.productstate

import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.transferstate.TransferStateRepository
import no.nav.hm.grunndata.importapi.transferstate.TransferStatus
import org.slf4j.LoggerFactory

@Singleton
class TransferToProductState(private val transferStateRepository: TransferStateRepository,
                             private val productStateKafkaService: ProductStateKafkaService) {
    companion object {
        private val LOG = LoggerFactory.getLogger(TransferToProductState::class.java)
    }

    suspend fun receivedTransfersToProductState() {
        val contents = transferStateRepository.findByTransferStatus(TransferStatus.RECEIVED).content
        contents.map {
            productStateKafkaService.mapTransferToproductState(it)
        }
    }

}
