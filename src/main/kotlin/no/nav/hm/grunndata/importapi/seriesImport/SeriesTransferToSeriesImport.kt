package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.ImportRapidPushService
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.importapi.transfer.series.SeriesTransferRepository
import no.nav.hm.grunndata.importapi.transfer.series.seriesImport
import no.nav.hm.grunndata.rapid.event.EventName
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Singleton
open class SeriesTransferToSeriesImport(private val seriesTransferRepository: SeriesTransferRepository,
                                   private val seriesImportService: SeriesImportService,
                                   private val importRapidPushService: ImportRapidPushService,
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(SeriesTransferToSeriesImport::class.java)
    }

    @Transactional
    open suspend fun receivedTransfersToSeriesImport() {
        val contents = seriesTransferRepository.findByTransferStatus(TransferStatus.RECEIVED).content
        LOG.info("Got ${contents.size} transfers to map to series")
        contents.forEach {
            val seriesImport = it.seriesImport()
            val seriesImportDTO = seriesImportService.findByIdCacheable(seriesImport.seriesId)?.let { inDb ->
                seriesImportService.update(
                    inDb.copy(
                        transferId = seriesImport.transferId,
                        name = seriesImport.name,
                        status = seriesImport.status
                ))
            } ?: seriesImportService.save(
                SeriesImportDTO (
                    seriesId = seriesImport.seriesId,
                    identifier = seriesImport.seriesId.toString(),
                    transferId = seriesImport.transferId,
                    name = seriesImport.name,
                    supplierId = seriesImport.supplierId)
            )
            importRapidPushService.pushDTOToKafka(seriesImportDTO.toRapidDTO(), EventName.importedSeriesV1)
            seriesTransferRepository.update(it.copy(transferStatus = TransferStatus.DONE, updated = LocalDateTime.now()))
            LOG.info("Series import created for seriesId: ${seriesImportDTO.seriesId} and transfer: ${seriesImportDTO.transferId} " +
                    "with version $${seriesImportDTO.version}")
        }
        //TODO feilhåndtering her
    }

}
