package no.nav.hm.grunndata.importapi.transfer.media

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import java.time.LocalDateTime
import java.util.*
import no.nav.hm.grunndata.rapid.dto.MediaType

@MappedEntity("media_transfer_v1")
data class MediaFileTransfer (
    @field:Id
    val transferId: UUID = UUID.randomUUID(),
    val supplierId: UUID,
    val seriesId: UUID,
    val md5: String,
    val filesize: Long,
    val filename: String,
    val sourceUri: String,
    val uri: String,
    val objectType: ObjectType?= ObjectType.SERIES,
    val transferStatus: TransferStatus = TransferStatus.RECEIVED,
    val mediaType: MediaType = MediaType.IMAGE,
    val message: String?=null,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
)


data class MediaFileTransferResponse(
    val transferId: UUID,
    val seriesId: UUID,
    val supplierId: UUID,
    val md5: String,
    val filesize: Long,
    val filename: String,
    val sourceUri: String,
    val objectType: ObjectType ?= ObjectType.SERIES,
    val uri: String,
    val transferStatus: TransferStatus,
    val mediaType: MediaType = MediaType.IMAGE,
    val message: String?=null,
    val created: LocalDateTime,
    val updated: LocalDateTime,
)

fun MediaFileTransfer.toResponse() = MediaFileTransferResponse(
    transferId = transferId,
    seriesId = seriesId,
    supplierId = supplierId,
    md5 = md5,
    filesize = filesize,
    filename = filename,
    sourceUri = sourceUri,
    objectType = objectType,
    uri = uri,
    transferStatus = transferStatus,
    mediaType =  mediaType,
    message = message,
    created = created,
    updated = updated,
)