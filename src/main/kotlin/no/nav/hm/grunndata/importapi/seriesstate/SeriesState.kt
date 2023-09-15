package no.nav.hm.grunndata.importapi.seriesstate

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import no.nav.hm.grunndata.importapi.transfer.series.SeriesTransfer
import java.time.LocalDateTime
import java.util.*

@MappedEntity("series_state_v1")
data class SeriesState (
    @field:Id
    val id: String,
    val supplierId: UUID,
    val transferId: UUID,
    val name: String,
    val message: String?=null,
    val status: SeriesStatus,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val version: Long? = 0L
)

enum class SeriesStatus {
    ACTIVE, INACTIVE, PENDING, REJECTED
}

data class SeriesStateDTO(
    val id: String = UUID.randomUUID().toString(),
    val supplierId: UUID,
    val transferId: UUID,
    val name: String,
    val message: String?=null,
    val status: SeriesStatus = SeriesStatus.ACTIVE,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val version: Long
) {
    init {
        require(name.isNotBlank()) {"name must be unique and not blank"}
    }
}


fun SeriesState.toDTO(): SeriesStateDTO = SeriesStateDTO(
    id = id, supplierId = supplierId, transferId = transferId, name = name, status = status, message = message,
    version = version!!, created = created, updated = updated
)

fun SeriesStateDTO.toEntity(): SeriesState = SeriesState(
    id = id, supplierId = supplierId, transferId = transferId, name = name, status = status, message = message,
    version = version, created = created, updated = updated
)

