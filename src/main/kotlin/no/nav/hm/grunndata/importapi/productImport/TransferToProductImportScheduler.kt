package no.nav.hm.grunndata.importapi.productImport

import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import no.nav.hm.micronaut.leaderelection.LeaderOnly
import org.slf4j.LoggerFactory

@Singleton
@Requires(property = "schedulers.enabled", value = "true")
open class TransferToProductImportScheduler(private val productTransferToProductImport: ProductTransferToProductImport) {


    companion object {
        private val LOG = LoggerFactory.getLogger(TransferToProductImportScheduler::class.java)
    }

    @LeaderOnly
    @Scheduled(fixedDelay = "15s")
    open fun transferToProductStateTask() {
        LOG.info("Running receivedTransfers to Product Import scheduler")
        runBlocking {
            productTransferToProductImport.receivedTransfersToProductImport()
        }
    }


}
