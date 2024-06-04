package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.annotation.Version
import io.micronaut.data.model.DataType
import no.nav.hm.grunndata.rapid.dto.*
import java.time.LocalDateTime
import java.util.*
import no.nav.hm.grunndata.importapi.rapidevent.EventPayload

@MappedEntity("series_import_v1")
data class SeriesImport (
    @field:Id
    val seriesId: UUID,
    val supplierId: UUID,
    val transferId: UUID,
    val isoCategory: String,
    val title: String,
    val text: String,
    val status: SeriesStatus,
    @field:TypeDef(type = DataType.JSON)
    val seriesData: SeriesDataDTO = SeriesDataDTO(),
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val expired : LocalDateTime = LocalDateTime.now().plusYears(15),
    @field:Version
    val version: Long? = 0L
)


data class SeriesImportDTO (
    override val id: UUID,
    val supplierId: UUID,
    val transferId: UUID,
    val isoCategory: String,
    val title: String,
    val text: String,
    val status: SeriesStatus = SeriesStatus.ACTIVE,
    val seriesData: SeriesDataDTO = SeriesDataDTO(),
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime = LocalDateTime.now().plusYears(15),
    val version: Long? = 0L
): EventPayload {
    override fun toRapidDTO(): RapidDTO = SeriesImportRapidDTO(
        id = id,
        supplierId = supplierId,
        transferId = transferId,
        title = title,
        text = text,
        isoCategory = isoCategory,
        status = status,
        seriesData = SeriesData(media = seriesData.media, attributes = seriesData.attributes),
        created = created,
        updated = updated,
        expired = expired,
        version = version!!
    )
}

data class SeriesDataDTO(
    val media: Set<MediaInfo> = emptySet(),
    val attributes: SeriesAttributes = SeriesAttributes(),
)

fun SeriesImport.toDTO(): SeriesImportDTO = SeriesImportDTO(
    id = seriesId, supplierId = supplierId, title = title, text = text, isoCategory = isoCategory, status = status,
    version = version!!, created = created, updated = updated, transferId = transferId, expired = expired
)


fun SeriesImportDTO.toEntity(): SeriesImport = SeriesImport (
    seriesId = id, supplierId = supplierId,  title = title, status = status, text = text, isoCategory = isoCategory,
    version = version, created = created, updated = updated, transferId = transferId, expired = expired
)
