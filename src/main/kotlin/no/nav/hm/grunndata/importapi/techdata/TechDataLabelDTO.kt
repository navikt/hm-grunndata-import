package no.nav.hm.grunndata.importapi.techdata

import java.time.LocalDateTime
import java.util.*

data class TechLabelDTO(
    val identifier: String,
    val label: String,
    val guide: String,
    val isocode: String,
    val type: String,
    val unit: String?
)