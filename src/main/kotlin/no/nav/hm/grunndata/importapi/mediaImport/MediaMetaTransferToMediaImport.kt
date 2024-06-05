package no.nav.hm.grunndata.importapi.mediaImport

import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import java.time.LocalDateTime
import no.nav.hm.grunndata.importapi.transfer.media.MediaMetaTransfer
import no.nav.hm.grunndata.importapi.transfer.media.MediaMetaTransferRepository
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus

@Singleton
open class MediaMetaTransferToMediaImport(private val mediaMetaTransferRepository: MediaMetaTransferRepository,
                                     private val mediaImportRepository: MediaImportRepository) {

    companion object { private val LOG = org.slf4j.LoggerFactory.getLogger(MediaMetaTransferToMediaImport::class.java) }

    @Transactional
    open suspend fun mediaMetaTransferToMediaImport() {
        val transfers = mediaMetaTransferRepository.findByTransferStatus(TransferStatus.RECEIVED)
        transfers.forEach {
            try {
                createMediaImport(it)
            } catch (e: Exception) {
                LOG.error("Error creating media import for Meta transfer ${it.transferId}", e)
                mediaMetaTransferRepository.update(it.copy(transferStatus = TransferStatus.ERROR, message = e.message))
            }
        }
    }

    private suspend fun createMediaImport(metaTransfer: MediaMetaTransfer) {
        mediaImportRepository.findByUriAndSeriesId(metaTransfer.uri, metaTransfer.seriesId)?.let { mediaImport ->
            mediaImportRepository.update(
                mediaImport.copy(
                    transferId = metaTransfer.transferId,
                    text = metaTransfer.text,
                    priority = mediaImport.priority,
                    updated = LocalDateTime.now()
                )
            )
        } ?: LOG.error("Media import not found for uri ${metaTransfer.uri} and seriesId ${metaTransfer.seriesId}")

        mediaMetaTransferRepository.update(
            metaTransfer.copy(transferStatus = TransferStatus.DONE, updated = LocalDateTime.now())
        )
    }

}