package no.nav.hm.grunndata.importapi.transfer.media

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import java.time.LocalDateTime
import java.util.*

@MappedEntity("media_transfer_v1")
data class MediaTransfer (
    @field:Id
    val transferId: UUID = UUID.randomUUID(),
    val supplierRef: String,
    val supplierId: UUID,
    val oid: UUID,
    val md5: String,
    val filesize: Long,
    val filename: String,
    val sourceUri: String,
    val uri: String,
    val transferStatus: TransferStatus = TransferStatus.RECEIVED,
    val message: String?=null,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
)

data class MediaTransferResponse(
    val transferId: UUID,
    val supplierRef: String,
    val supplierId: UUID,
    val md5: String,
    val filesize: Long,
    val filename: String,
    val sourceUri: String,
    val uri: String,
    val transferStatus: TransferStatus,
    val message: String?=null,
    val created: LocalDateTime,
    val updated: LocalDateTime,
)

fun MediaTransfer.toTransferResponse() = MediaTransferResponse(
    transferId, supplierRef, supplierId, md5, filesize, filename, sourceUri, uri, transferStatus, message, created, updated
)