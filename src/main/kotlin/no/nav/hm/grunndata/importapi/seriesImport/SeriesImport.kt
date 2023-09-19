package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Version
import java.time.LocalDateTime
import java.util.*

@MappedEntity("series_import_v1")
data class SeriesImport (
    @field:Id
    val seriesId: UUID,
    val identifier: String,
    val supplierId: UUID,
    val transferId: UUID,
    val name: String,
    val message: String?=null,
    val status: SeriesStatus,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    @field:Version
    val version: Long? = 0L
)

enum class SeriesStatus {
    ACTIVE, INACTIVE, PENDING, REJECTED
}

data class SeriesImportDTO(
    val id: UUID,
    val identifier: String,
    val supplierId: UUID,
    val transferId: UUID,
    val name: String,
    val message: String?=null,
    val status: SeriesStatus = SeriesStatus.ACTIVE,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val version: Long=0L
)

fun SeriesImport.toDTO(): SeriesImportDTO = SeriesImportDTO(
    id = seriesId, identifier = identifier, supplierId = supplierId, transferId = transferId, name = name, status = status, message = message,
    version = version!!, created = created, updated = updated
)

fun SeriesImportDTO.toEntity(): SeriesImport = SeriesImport(
    seriesId = id, identifier = identifier, supplierId = supplierId, transferId = transferId, name = name, status = status, message = message,
    version = version, created = created, updated = updated
)

