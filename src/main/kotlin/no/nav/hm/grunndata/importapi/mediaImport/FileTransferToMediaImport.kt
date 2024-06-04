package no.nav.hm.grunndata.importapi.mediaImport

import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.transfer.media.MediaFileTransfer
import no.nav.hm.grunndata.importapi.transfer.media.MediaFileTransferRepository
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.rapid.dto.MediaSourceType

@Singleton
class FileTransferToMediaImport(
    private val mediaFileTransferRepository: MediaFileTransferRepository,
    private val mediaImportRepository: MediaImportRepository
) {

    companion object {
        private val LOG = org.slf4j.LoggerFactory.getLogger(FileTransferToMediaImport::class.java)
    }

    suspend fun fileTransferToMediaImport() {
        val transfers = mediaFileTransferRepository.findByTransferStatus(TransferStatus.RECEIVED)
        transfers.forEach {
            try {
                createMediaImport(it)
            } catch (e: Exception) {
                LOG.error("Error creating media import for transfer ${it.transferId}", e)
                mediaFileTransferRepository.update(it.copy(transferStatus = TransferStatus.ERROR, message = e.message))
            }
        }
    }


    private suspend fun createMediaImport(transfer: MediaFileTransfer) {
        val mediaList = mediaImportRepository.findBySupplierIdAndSeriesId(transfer.supplierId, transfer.seriesId)
        val mediaImport = MediaImport(
            uri = transfer.uri,
            transferId = transfer.transferId,
            supplierId = transfer.supplierId,
            type = transfer.mediaType,
            seriesId = transfer.seriesId,
            md5 = transfer.md5,
            sourceType = MediaSourceType.IMPORT,
            sourceUri = transfer.sourceUri,
            text = transfer.filename,
            filename = transfer.filename,
            priority = mediaList.size + 1
        )
        LOG.info("Created new media import ${transfer.uri} from transfer ${transfer.transferId}")
        mediaImportRepository.save(mediaImport)
    }

}