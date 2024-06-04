package no.nav.hm.grunndata.importapi.rapidevent

interface EventHandler  {

    fun getEventType(): EventItemType

    fun sendRapidEvent(eventItem: EventItem)

    fun getEventPayloadClass(): Class<out EventPayload>

    suspend fun queueDTORapidEvent(payload: EventPayload,
                                   eventName: String,
                                   extraKeyValues: Map<String, Any> = emptyMap())

}