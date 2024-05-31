package no.nav.hm.grunndata.importapi.transfer.series

import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import jakarta.persistence.Id
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.rapid.dto.MediaType
import no.nav.hm.grunndata.rapid.dto.SeriesStatus
import java.time.LocalDateTime
import java.util.UUID
import javax.print.attribute.standard.MediaSize
import no.nav.hm.grunndata.importapi.transfer.media.MediaDTO
import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import no.nav.hm.grunndata.rapid.dto.SeriesAttributes


@MappedEntity("series_transfer_v1")
data class SeriesTransfer(
    @field:Id
    val transferId: UUID = UUID.randomUUID(),
    val seriesId: UUID,
    val supplierId: UUID,
    @field:TypeDef(type = DataType.JSON)
    val json_payload: SeriesTransferDTO,
    val md5: String,
    val message: String? = null,
    val transferStatus: TransferStatus = TransferStatus.RECEIVED,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)

data class SeriesTransferDTO(
    val seriesId: UUID = UUID.randomUUID(),
    val title: String,
    val text: String = "",
    val isoCategory: String = "",
    val media: Set<SeriesMediaInfo> = emptySet(),
    val seriesAttributes: SeriesAttributes = SeriesAttributes(),
    val status: SeriesStatus = SeriesStatus.ACTIVE
) {
    init {
        require(title.isNotBlank() && title.length < 256) { "name is blank or name size > 255" }
        require(isoCategory.isNotBlank()) { "isoCategory is blank" }

    }
}

data class SeriesMediaInfo(
    val uri: String,
    val priority: Int = -1,
    val type: MediaType = MediaType.IMAGE,
    val source: MediaSourceType = MediaSourceType.IMPORT,
    val text: String? = null,
) {
    override fun hashCode(): Int {
        return uri.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SeriesMediaInfo) return false
        return uri == other.uri
    }
}


data class SeriesTransferResponse(
    val transferId: UUID,
    val supplierId: UUID,
    val seriesId: UUID,
    val md5: String,
    val message: String? = null,
    val transferStatus: TransferStatus,
    val created: LocalDateTime
)

fun SeriesTransfer.toResponse() = SeriesTransferResponse(
    transferId = transferId,
    seriesId = seriesId,
    supplierId = supplierId,
    md5 = md5,
    message = message,
    transferStatus = transferStatus,
    created = created
)
