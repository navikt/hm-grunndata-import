package no.nav.hm.grunndata.importapi.productImport

import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

@Singleton
@Requires(property = "schedulers.enabled", value = "true")
class TransferToProductStateScheduler(private val transferToProductState: TransferToProductState) {


    companion object {
        private val LOG = LoggerFactory.getLogger(TransferToProductStateScheduler::class.java)
    }
    @Scheduled(fixedDelay = "1m")
    fun transferToProductStateTask() {
        LOG.info("Running receivedTransfers to ProductState scheduler")
        runBlocking {
            transferToProductState.receivedTransfersToProductState()
        }
    }


}
