package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.IMPORT
import no.nav.hm.grunndata.importapi.ImportRapidPushService
import no.nav.hm.grunndata.importapi.series.SeriesDTO
import no.nav.hm.grunndata.importapi.series.SeriesService
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.importapi.transfer.series.SeriesTransferRepository
import no.nav.hm.grunndata.rapid.event.EventName
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

@Singleton
open class SeriesTransferToSeriesImport(private val seriesTransferRepository: SeriesTransferRepository,
                                        private val seriesImportService: SeriesImportService,
                                        private val seriesService: SeriesService,
                                        private val importRapidPushService: ImportRapidPushService,
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(SeriesTransferToSeriesImport::class.java)
    }

    @Transactional
    open suspend fun receivedTransfersToSeriesImport() {
        val contents = seriesTransferRepository.findByTransferStatus(TransferStatus.RECEIVED).content
        LOG.info("Got ${contents.size} transfers to map to series")
        contents.forEach { transfer ->
            val seriesImportDTO = seriesImportService
                    .findBySupplierIdAndSupplierSeriesRef(transfer.supplierId, transfer.supplierSeriesRef)?.let { inDb ->
                seriesImportService.update(
                    inDb.copy(
                        transferId = transfer.transferId,
                        name = transfer.json_payload.name,
                        status = transfer.json_payload.status,
                        updated = LocalDateTime.now(),
                ))
            } ?: run {
                val seriesId = UUID.randomUUID()
                seriesImportService.save(
                    SeriesImportDTO(
                        seriesId = seriesId,
                        supplierSeriesRef = transfer.supplierSeriesRef,
                        transferId = transfer.transferId,
                        name = transfer.json_payload.name,
                        supplierId = transfer.supplierId,
                        status = transfer.json_payload.status,
                    )
                )
            }
            importRapidPushService.pushDTOToKafka(seriesImportDTO.toRapidDTO(), EventName.importedSeriesV1)
            seriesTransferRepository.update(transfer.copy(transferStatus = TransferStatus.DONE, updated = LocalDateTime.now()))
            seriesService.findById(seriesImportDTO.seriesId)?.let { inDB -> inDB.copy(
                    status = seriesImportDTO.status, expired = seriesImportDTO.expired, name = seriesImportDTO.name,  updated = LocalDateTime.now(),
                    updatedBy = IMPORT
                )
            } ?: seriesService.save(
                SeriesDTO(
                    id = seriesImportDTO.seriesId, supplierId = seriesImportDTO.supplierId, name = seriesImportDTO.name,
                    status = seriesImportDTO.status, expired = seriesImportDTO.expired, createdBy = IMPORT, updatedBy = IMPORT
                )
            )
            LOG.info("Series import created for seriesId: ${seriesImportDTO.seriesId} and transfer: ${seriesImportDTO.transferId} " +
                    "with version $${seriesImportDTO.version}")
        }
        //TODO feilhåndtering her
    }

}
