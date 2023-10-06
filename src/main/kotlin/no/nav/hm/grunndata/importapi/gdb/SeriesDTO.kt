package no.nav.hm.grunndata.importapi.gdb

import no.nav.hm.grunndata.rapid.dto.SeriesStatus
import java.time.LocalDateTime
import java.util.*

data class SeriesDTO(
    val id: UUID = UUID.randomUUID(),
    val supplierId: UUID,
    val status: SeriesStatus = SeriesStatus.ACTIVE,
    val name: String,
    val identifier: String,
    val createdBy: String,
    val updatedBy: String,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime = LocalDateTime.now()
)