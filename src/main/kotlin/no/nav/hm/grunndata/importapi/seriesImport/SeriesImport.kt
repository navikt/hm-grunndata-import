package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Version
import java.time.LocalDateTime
import java.util.*

@MappedEntity("series_state_v1")
data class SeriesImport (
    @field:Id
    val id: String,
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

data class SeriesStateDTO(
    val id: String = UUID.randomUUID().toString(),
    val supplierId: UUID,
    val transferId: UUID,
    val name: String,
    val message: String?=null,
    val status: SeriesStatus = SeriesStatus.ACTIVE,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val version: Long=0L
) {
    init {
        require(name.isNotBlank()) {"name must be unique and not blank"}
    }
}


fun SeriesImport.toDTO(): SeriesStateDTO = SeriesStateDTO(
    id = id, supplierId = supplierId, transferId = transferId, name = name, status = status, message = message,
    version = version!!, created = created, updated = updated
)

fun SeriesStateDTO.toEntity(): SeriesImport = SeriesImport(
    id = id, supplierId = supplierId, transferId = transferId, name = name, status = status, message = message,
    version = version, created = created, updated = updated
)

