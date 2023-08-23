package no.nav.hm.grunndata.importapi.seriesstate

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.time.LocalDateTime
import java.util.*

@MappedEntity("series_state_v1")
data class SeriesState (
    @field:Id
    val id: String,
    val supplierId: UUID,
    val name: String,
    val status: SeriesStatus,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)

enum class SeriesStatus {
    ACTIVE, INACTIVE
}

data class SeriesStateDTO(
    val id: String,
    val supplierId: UUID,
    val name: String,
    val status: SeriesStatus,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)

fun SeriesState.toDTO(): SeriesStateDTO = SeriesStateDTO(
    id = id, supplierId = supplierId, name = name, status = status, created = created, updated = updated
)


