package no.nav.hm.grunndata.importapi.productImport

import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

@Singleton
@Requires(property = "schedulers.enabled", value = "true")
class TransferToProductImportScheduler(private val productTransferToProductImport: ProductTransferToProductImport) {


    companion object {
        private val LOG = LoggerFactory.getLogger(TransferToProductImportScheduler::class.java)
    }
    @Scheduled(fixedDelay = "15s")
    fun transferToProductStateTask() {
        LOG.info("Running receivedTransfers to Product Import scheduler")
        runBlocking {
            productTransferToProductImport.receivedTransfersToProductImport()
        }
    }


}
