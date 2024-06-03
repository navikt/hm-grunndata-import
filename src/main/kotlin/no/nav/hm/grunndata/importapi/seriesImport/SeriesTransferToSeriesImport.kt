package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.ImportRapidPushService
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.importapi.transfer.series.SeriesTransfer
import no.nav.hm.grunndata.importapi.transfer.series.SeriesTransferRepository
import no.nav.hm.grunndata.rapid.dto.SeriesStatus
import no.nav.hm.grunndata.rapid.event.EventName
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import no.nav.hm.grunndata.importapi.transfer.media.MediaTransferRepository


@Singleton
open class SeriesTransferToSeriesImport(private val seriesTransferRepository: SeriesTransferRepository,
                                        private val mediaTransferRepository: MediaTransferRepository,
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
            try {
                createSeriesImport(it)
            }
            catch (e: Exception) {
                LOG.error("Error creating series import for transfer ${it.transferId}", e)
                seriesTransferRepository.update(it.copy(transferStatus = TransferStatus.ERROR, message = e.message, updated = LocalDateTime.now()))
            }
       }
    }

    @Transactional
    open suspend fun createSeriesImport(transfer: SeriesTransfer) {
        val mediaTransferList = mediaTransferRepository.findBySupplierIdAndSeriesId(transfer.supplierId, transfer.seriesId).filter { it.transferStatus == TransferStatus.DONE }
        val seriesImportDTO = seriesImportService
            .findBySupplierIdAndSeriesId(transfer.supplierId, transfer.seriesId)?.let { inDb ->
                seriesImportService.update(
                    inDb.copy(
                        transferId = transfer.transferId,
                        title = transfer.json_payload.title,
                        status = transfer.json_payload.status,
                        text = transfer.json_payload.text,
                        isoCategory = transfer.json_payload.isoCategory,
                        seriesData = SeriesDataDTO(
                            attributes = transfer.json_payload.seriesAttributes
                        ),
                        updated = LocalDateTime.now(),
                        expired = setExpiredIfNotActive(transfer.json_payload.status)
                    )
                )
            } ?: run {
            seriesImportService.save(
                SeriesImportDTO(
                    seriesId = transfer.seriesId,
                    transferId = transfer.transferId,
                    title = transfer.json_payload.title,
                    text = transfer.json_payload.text,
                    isoCategory = transfer.json_payload.isoCategory,
                    supplierId = transfer.supplierId,
                    status = transfer.json_payload.status,
                    seriesData = SeriesDataDTO(
                        attributes = transfer.json_payload.seriesAttributes
                    ),
                    expired = setExpiredIfNotActive(transfer.json_payload.status)
                )
            )
        }
        importRapidPushService.pushDTOToKafka(seriesImportDTO.toRapidDTO(), EventName.importedSeriesV1)
        seriesTransferRepository.update(
            transfer.copy(
                transferStatus = TransferStatus.DONE,
                updated = LocalDateTime.now()
            )
        )
        LOG.info(
            "Series import created for seriesId: ${seriesImportDTO.seriesId} and transfer: ${seriesImportDTO.transferId} " +
                    "with version $${seriesImportDTO.version}"
        )
    }



    private fun setExpiredIfNotActive(status: SeriesStatus): LocalDateTime =
        if (status != SeriesStatus.ACTIVE) LocalDateTime.now().minusMinutes(1)
        else LocalDateTime.now().plusYears(15)



}
