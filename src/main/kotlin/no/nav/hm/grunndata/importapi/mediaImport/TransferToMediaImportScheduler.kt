package no.nav.hm.grunndata.importapi.mediaImport

import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import no.nav.hm.micronaut.leaderelection.LeaderOnly

@Singleton
@Requires(property = "schedulers.enabled", value = "true")
open class TransferToMediaImportScheduler(private val fileTransferToMediaImport: FileTransferToMediaImport,
                                     private val mediaMetaTransferToMediaImport: MediaMetaTransferToMediaImport) {

    companion object {
        private val LOG = org.slf4j.LoggerFactory.getLogger(TransferToMediaImportScheduler::class.java)
    }

    @LeaderOnly
    @Scheduled(fixedDelay = "15m")
    open fun transferToMediaImportTask() {
        LOG.info("Running receivedTransfers to Media Import scheduler")
        runBlocking {
            fileTransferToMediaImport.fileTransferToMediaImport()
            mediaMetaTransferToMediaImport.mediaMetaTransferToMediaImport()
        }
    }


}