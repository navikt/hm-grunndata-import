package no.nav.hm.grunndata.importapi.transfer.media

import io.micronaut.data.annotation.DateUpdated
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.time.LocalDateTime
import java.util.UUID
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus


@MappedEntity("media_meta_transfer_v1")
data class MediaMetaTransfer(
    @field:Id
    val transferId: UUID = UUID.randomUUID(),
    val uri: String,
    val supplierId: UUID,
    val seriesId: UUID,
    val text: String? = null,
    val priority: Int = 1,
    val status: MediaMetaTranferStatus = MediaMetaTranferStatus.ACTIVE,
    val transferStatus: TransferStatus = TransferStatus.RECEIVED,
    val message: String ?= null,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
)

enum class MediaMetaTranferStatus {
    ACTIVE,
    DELETED
}
