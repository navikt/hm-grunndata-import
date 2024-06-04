package no.nav.hm.grunndata.importapi.rapidevent

import java.util.UUID
import no.nav.hm.grunndata.rapid.dto.RapidDTO

interface EventPayload {
    val id: UUID
    fun toRapidDTO(): RapidDTO
}