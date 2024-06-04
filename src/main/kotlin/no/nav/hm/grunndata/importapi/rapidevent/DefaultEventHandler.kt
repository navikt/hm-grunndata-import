package no.nav.hm.grunndata.importapi.rapidevent

import com.fasterxml.jackson.databind.ObjectMapper

abstract class DefaultEventHandler(private val eventItemService: EventItemService,
                                   private val objectMapper: ObjectMapper,
                                   private val importRapidPushService: ImportRapidPushService): EventHandler {

    companion object {
        private val LOG = org.slf4j.LoggerFactory.getLogger(DefaultEventHandler::class.java)
    }

    override suspend fun queueDTORapidEvent(payload: EventPayload, eventName: String, extraKeyValues: Map<String, Any>) {
        LOG.info("queueDTORapidEvent for ${payload.id} with event: $eventName")
        eventItemService.createNewEventItem(
            type = getEventType(),
            oid = payload.id,
            eventName = eventName,
            payload = payload,
            extraKeyValues = extraKeyValues
        )
    }

    override fun sendRapidEvent(eventItem: EventItem) {
        val dto = objectMapper.readValue(eventItem.payload, getEventPayloadClass())
        importRapidPushService.pushToRapid(dto.toRapidDTO(), eventItem)
    }


}