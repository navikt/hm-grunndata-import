package no.nav.hm.grunndata.importapi.seriesstate

import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.importapi.transfer.series.SeriesTransferRepository
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Singleton
class SeriesTransferToSeriesState(private val seriesTransferRepository: SeriesTransferRepository,
                                  private val seriesStateService: SeriesStateService
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(SeriesTransferToSeriesState::class.java)
    }

    suspend fun receivedTransfersToProductState() {
        val contents = seriesTransferRepository.findByTransferStatus(TransferStatus.RECEIVED).content
        LOG.info("Got ${contents.size} transfers to map to series")
        contents.forEach {
            val seriesState = seriesStateService.mapSaveSeriesTransferToSeriesState(it)
            val seriesStateDTO = seriesStateService.findByIdCacheable(seriesState.id)?.let { inDb ->
                seriesStateService.update(
                    inDb.copy(
                        transferId = seriesState.transferId,
                        name = seriesState.name,
                        status = seriesState.status
                ))
            } ?: seriesStateService.save(
                SeriesStateDTO (
                    id = seriesState.id,
                    transferId = seriesState.transferId,
                    name = seriesState.name,
                    supplierId = seriesState.supplierId)
            )
            LOG.info("Series state created for ${seriesStateDTO.id} and transfer: ${seriesStateDTO.transferId} " +
                    "with version $${seriesStateDTO.version}")
            seriesTransferRepository.update(it.copy(transferStatus = TransferStatus.DONE, updated = LocalDateTime.now()))
        }
        //TODO feilhåndtering her
    }

}
