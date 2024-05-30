package no.nav.hm.grunndata.importapi.gdb

import no.nav.hm.grunndata.rapid.dto.SeriesData
import no.nav.hm.grunndata.rapid.dto.SeriesStatus
import java.time.LocalDateTime
import java.util.*

data class SeriesDTO(
    val id: UUID = UUID.randomUUID(),
    val status: SeriesStatus = SeriesStatus.ACTIVE,
    val title: String,
    val text: String,
    val isoCategory: String,
    val seriesData: SeriesData? = null,
    val supplierId: UUID,
    val identifier: String,
    val createdBy: String,
    val updatedBy: String,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime = created.plusYears(15)
)