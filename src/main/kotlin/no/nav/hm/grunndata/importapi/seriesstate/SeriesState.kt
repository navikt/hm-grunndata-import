package no.nav.hm.grunndata.importapi.seriesstate

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.time.LocalDateTime
import java.util.*

@MappedEntity("seriesstate_v1")
data class SeriesState (
    @field:Id
    val id: String,
    val supplierId: UUID,
    val name: String,
    val message: String? = null,
    val status: SerieTransferStatus,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)

enum class SerieTransferStatus {
    ACTIVE, INACTIVE
}
