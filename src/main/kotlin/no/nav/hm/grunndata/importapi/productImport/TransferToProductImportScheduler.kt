package no.nav.hm.grunndata.importapi.productImport

import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

@Singleton
@Requires(property = "schedulers.enabled", value = "true")
class TransferToProductImportScheduler(private val transferToProductImport: TransferToProductImport) {


    companion object {
        private val LOG = LoggerFactory.getLogger(TransferToProductImportScheduler::class.java)
    }
    @Scheduled(fixedDelay = "1m")
    fun transferToProductStateTask() {
        LOG.info("Running receivedTransfers to ProductState scheduler")
        runBlocking {
            transferToProductImport.receivedTransfersToProductState()
        }
    }


}
