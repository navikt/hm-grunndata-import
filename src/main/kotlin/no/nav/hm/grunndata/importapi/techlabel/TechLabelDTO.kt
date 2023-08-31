package no.nav.hm.grunndata.importapi.techlabel

import java.time.LocalDateTime
import java.util.*

data class TechLabelDTO(
    val id: UUID = UUID.randomUUID(),
    val identifier: String,
    val label: String,
    val guide: String,
    val isocode: String,
    val type: String,
    val unit: String?,
    val createdBy: String = "HMDB",
    val updatedBy: String = "HMDB",
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)