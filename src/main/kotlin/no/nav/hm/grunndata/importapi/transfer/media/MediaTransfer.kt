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
    val md5: String,
    val filename: String,
    val sourceUri: String,
    val uri: String,
    val transferStatus: TransferStatus = TransferStatus.RECEIVED,
    val message: String?=null,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
)
