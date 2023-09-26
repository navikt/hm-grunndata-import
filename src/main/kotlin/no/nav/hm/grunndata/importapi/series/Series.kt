package no.nav.hm.grunndata.importapi.series

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Version
import io.micronaut.data.runtime.criteria.update
import no.nav.hm.grunndata.rapid.dto.SeriesStatus
import java.time.LocalDateTime
import java.util.*

@MappedEntity("series_v1")
data class Series (
    @field:Id
    val id: UUID,
    val supplierId: UUID,
    val name: String,
    val status: SeriesStatus = SeriesStatus.ACTIVE,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime = created.plusYears(15),
    val createdBy: String,
    val updatedBy: String,
    @field:Version
    val version: Long? = 0L
)

data class SeriesDTO(
    val id: UUID,
    val supplierId: UUID,
    val name: String,
    val status: SeriesStatus = SeriesStatus.ACTIVE,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime = created.plusYears(15),
    val createdBy: String,
    val updatedBy: String,
    val version: Long? = 0L
)

fun Series.toDTO(): SeriesDTO = SeriesDTO(
    id = id, supplierId = supplierId, name = name, status = status, created = created, updated = updated,
    expired = expired, createdBy = createdBy, updatedBy = updatedBy, version = version
)

fun SeriesDTO.toEntity(): Series = Series(
    id = id, supplierId = supplierId, name = name, status = status, created = created, updated = updated,
    expired = expired, createdBy = createdBy, updatedBy = updatedBy, version = version
)