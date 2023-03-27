package no.nav.hm.grunndata.importapi

import jakarta.inject.Singleton
import no.nav.hm.grunndata.rapid.dto.RapidDTO
import no.nav.hm.grunndata.rapid.dto.rapidDTOVersion
import no.nav.hm.rapids_rivers.micronaut.RapidPushService

@Singleton
class ImportRapidPushService(private val kafkaRapidService: RapidPushService) {

    fun pushDTOToKafka(dto: RapidDTO, eventName: String) {
        kafkaRapidService.pushToRapid(
            key = "$eventName-${dto.id}",
            eventName = eventName, payload = dto, keyValues = mapOf("createdBy" to "IMPORT",
                "dtoVersion" to rapidDTOVersion
            )
        )
    }
}