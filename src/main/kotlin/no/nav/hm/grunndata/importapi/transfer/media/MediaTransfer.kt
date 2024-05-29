package no.nav.hm.grunndata.importapi.transfer.media

import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import no.nav.hm.grunndata.rapid.dto.MediaType
import java.time.LocalDateTime
import java.util.*


data class MediaTransferResponse(
    val transferId: UUID,
    val oid: UUID,
    val supplierId: UUID,
    val md5: String,
    val filesize: Long,
    val filename: String,
    val sourceUri: String,
    val objectType: ObjectType ?= ObjectType.SERIES,
    val uri: String,
    val transferStatus: TransferStatus,
    val message: String?=null,
    val created: LocalDateTime,
    val updated: LocalDateTime,
)


data class MediaInfoDTO (
    val sourceUri: String,
    val filename: String?=null,
    val uri:    String,
    val priority: Int = -1,
    val type: MediaType = MediaType.IMAGE,
    val text:   String?=null,
    val source: MediaSourceType = MediaSourceType.IMPORT,
    val updated: LocalDateTime? = LocalDateTime.now(),
) {
    override fun hashCode(): Int {
        return uri.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MediaInfoDTO) return false
        return uri == other.uri
    }
}
