package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Version
import no.nav.hm.grunndata.importapi.IMPORT
import no.nav.hm.grunndata.rapid.dto.*
import java.time.LocalDateTime
import java.util.*

@MappedEntity("series_import_v1")
data class SeriesImport (
    @field:Id
    val seriesId: UUID,
    val supplierId: UUID,
    val transferId: UUID,
    val title: String,
    val status: SeriesStatus,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val expired : LocalDateTime = LocalDateTime.now().plusYears(15),
    @field:Version
    val version: Long? = 0L
)


data class SeriesImportDTO (
    val seriesId: UUID,
    val supplierId: UUID,
    val transferId: UUID,
    val title: String,
    val status: SeriesStatus = SeriesStatus.ACTIVE,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime,
    val version: Long? = 0L
)

fun SeriesImport.toDTO(): SeriesImportDTO = SeriesImportDTO(
    seriesId = seriesId, supplierId = supplierId, title = title, status = status,
    version = version!!, created = created, updated = updated, transferId = transferId, expired = expired
)


fun SeriesImportDTO.toEntity(): SeriesImport = SeriesImport (
    seriesId = seriesId, supplierId = supplierId,  title = title, status = status,
    version = version, created = created, updated = updated, transferId = transferId, expired = expired
)

fun SeriesImportDTO.toRapidDTO(): SeriesImportRapidDTO = SeriesImportRapidDTO (
    id = seriesId,
    supplierId = supplierId,
    transferId = transferId,
    title = title,
    status = status,
    created = created,
    updated = updated,
    expired = expired,
    version = version!!
)
