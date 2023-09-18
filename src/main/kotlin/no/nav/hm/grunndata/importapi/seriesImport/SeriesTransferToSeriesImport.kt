package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.ImportRapidPushService
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.importapi.transfer.series.SeriesTransferRepository
import no.nav.hm.grunndata.importapi.transfer.series.toSeriesState
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
            val seriesState = it.toSeriesState()
            val seriesImportDTO = seriesImportService.findByIdCacheable(seriesState.id)?.let { inDb ->
                seriesImportService.update(
                    inDb.copy(
                        transferId = seriesState.transferId,
                        name = seriesState.name,
                        status = seriesState.status
                ))
            } ?: seriesImportService.save(
                SeriesImportDTO (
                    id = seriesState.id,
                    transferId = seriesState.transferId,
                    name = seriesState.name,
                    supplierId = seriesState.supplierId)
            )
            LOG.info("Series state created for ${seriesImportDTO.id} and transfer: ${seriesImportDTO.transferId} " +
                    "with version $${seriesImportDTO.version}")
            seriesTransferRepository.update(it.copy(transferStatus = TransferStatus.DONE, updated = LocalDateTime.now()))
        }
        //TODO feilhåndtering her
    }

}
