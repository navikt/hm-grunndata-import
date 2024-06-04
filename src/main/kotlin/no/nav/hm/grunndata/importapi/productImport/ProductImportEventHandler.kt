package no.nav.hm.grunndata.importapi.productImport

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.rapidevent.DefaultEventHandler
import no.nav.hm.grunndata.importapi.rapidevent.EventItemService
import no.nav.hm.grunndata.importapi.rapidevent.EventItemType
import no.nav.hm.grunndata.importapi.rapidevent.EventPayload
import no.nav.hm.grunndata.importapi.rapidevent.ImportRapidPushService

@Singleton
class ProductImportEventHandler(private val eventItemService: EventItemService,
                                private val objectMapper: ObjectMapper,
                                private val importRapidPushService: ImportRapidPushService
) : DefaultEventHandler(eventItemService, objectMapper, importRapidPushService) {

    override fun getEventType(): EventItemType = EventItemType.PRODUCT

    override fun getEventPayloadClass(): Class<out EventPayload> = ProductImportDTO::class.java

}