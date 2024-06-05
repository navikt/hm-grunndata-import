package no.nav.hm.grunndata.importapi.mediaImport

import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import java.time.LocalDateTime
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.seriesImport.SeriesDataDTO
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportEventHandler
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportService
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.rapid.dto.MediaInfo
import no.nav.hm.grunndata.rapid.event.EventName
import no.nav.hm.micronaut.leaderelection.LeaderOnly

@Singleton
@Requires(property = "schedulers.enabled", value = "true")
open class TransferToMediaImportScheduler(private val fileTransferToMediaImport: FileTransferToMediaImport,
                                          private val mediaMetaTransferToMediaImport: MediaMetaTransferToMediaImport,
                                          private val seriesImportEventHandler: SeriesImportEventHandler,
                                          private val seriesImportService: SeriesImportService,
                                          private val  mediaImportRepository: MediaImportRepository) {

    companion object {
        private val LOG = org.slf4j.LoggerFactory.getLogger(TransferToMediaImportScheduler::class.java)
    }

    @LeaderOnly
    @Scheduled(fixedDelay = "15m")
    open fun transferToMediaImportTask() {
        LOG.info("Running receivedTransfers to Media Import scheduler")
        runBlocking {
            val doneFileTransfers = fileTransferToMediaImport.fileTransferToMediaImport().filter { it.transferStatus == TransferStatus.DONE }
            val doneMetaTransfers = mediaMetaTransferToMediaImport.mediaMetaTransferToMediaImport().filter { it.transferStatus == TransferStatus.DONE }
            val seriesToUpdate = doneFileTransfers.map { it.seriesId } + doneMetaTransfers.map { it.seriesId }.distinct()
            LOG.info("Got ${seriesToUpdate.size} series to update")
            seriesToUpdate.forEach { seriesId -> LOG.info("updating series: $seriesId")
                val mediaList = mediaImportRepository.findBySeriesIdAndStatus(seriesId, MediaImportStatus.ACTIVE).sortedBy { it.priority }
                seriesImportService.findBySeriesId(seriesId)?.let { inDb ->
                    val seriesImportDTO = seriesImportService.update(
                        inDb.copy(
                            seriesData = SeriesDataDTO(
                                attributes = inDb.seriesData.attributes,
                                media = mediaList.map { media ->
                                    MediaInfo(
                                        uri = media.uri,
                                        type = media.type,
                                        text = media.text,
                                        sourceUri = media.sourceUri,
                                        source = media.sourceType,
                                        priority = media.priority,
                                        filename = media.filename,
                                        updated = media.updated
                                    )
                                }.toSet()
                            ),
                            updated = LocalDateTime.now()
                        )
                    )
                    seriesImportEventHandler.queueDTORapidEvent(seriesImportDTO, EventName.importedSeriesV1)
                } ?: LOG.error("Series not found for id $seriesId")
            }
        }
    }

}