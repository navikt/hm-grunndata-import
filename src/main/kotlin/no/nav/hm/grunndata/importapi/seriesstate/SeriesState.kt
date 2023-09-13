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

data class SeriesTransferDTO(
    val id: String? = null,
    val name: String,
    val status: SeriesStatus = SeriesStatus.ACTIVE
)

data class SeriesStateDTO(
    val id: String = UUID.randomUUID().toString(),
    val supplierId: UUID,
    val name: String,
    val status: SeriesStatus = SeriesStatus.ACTIVE,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(name.isNotBlank()) {"name must be unique and not blank"}
    }
}


fun SeriesState.toDTO(): SeriesStateDTO = SeriesStateDTO(
    id = id, supplierId = supplierId, name = name, status = status, created = created, updated = updated
)

fun SeriesStateDTO.toEntity(): SeriesState = SeriesState(
    id = id ?: UUID.randomUUID().toString(), supplierId = supplierId, name = name, status = status, created = created, updated = updated
)

