package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

@Singleton
@Requires(property = "schedulers.enabled", value = "true")
class TransferToSeriesImportScheduler(private val seriesTransferToSeriesImport: SeriesTransferToSeriesImport) {


    companion object {
        private val LOG = LoggerFactory.getLogger(TransferToSeriesImportScheduler::class.java)
    }
    @Scheduled(fixedDelay = "1m")
    fun transferToProductStateTask() {
        LOG.info("Running receivedTransfers to ProductState scheduler")
        runBlocking {
            seriesTransferToSeriesImport.receivedTransfersToProductState()
        }
    }


}
