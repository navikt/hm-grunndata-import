package no.nav.hm.grunndata.importapi.transfer.media

import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
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

