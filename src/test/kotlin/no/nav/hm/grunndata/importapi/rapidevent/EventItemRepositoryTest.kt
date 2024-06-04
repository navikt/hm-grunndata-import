package no.nav.hm.grunndata.importapi.rapidevent

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import java.util.UUID
import no.nav.hm.grunndata.importapi.seriesImport.SeriesDataDTO
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportDTO
import no.nav.hm.grunndata.rapid.dto.MediaInfo
import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import no.nav.hm.grunndata.rapid.dto.MediaType
import no.nav.hm.grunndata.rapid.dto.SeriesStatus
import org.junit.jupiter.api.Test

@MicronautTest
class EventItemRepositoryTest(private val eventItemRepository: EventItemRepository,
                              private val objectMapper: ObjectMapper) {

    @Test
    fun crudTest() {
        val seriesImportDTO = SeriesImportDTO(
            id = UUID.randomUUID(),
            supplierId = UUID.randomUUID(),
            title = "series title",
            text = "series text",
            isoCategory = "12345678",
            status = SeriesStatus.ACTIVE,
            transferId = UUID.randomUUID(),
            seriesData = SeriesDataDTO(media = setOf(
                MediaInfo(
                    uri = "http://example3.com",
                    type = MediaType.IMAGE,
                    text = "image description",
                    sourceUri = "http://example3.com",
                    source = MediaSourceType.IMPORT
                )
            ))
        )
        val extraKeyValues = mapOf("key" to "value")
        val item = EventItem(
            oid = UUID.randomUUID(),
            type = EventItemType.SERIES,
            eventName = "test",
            extraKeyValues = extraKeyValues,
            payload = objectMapper.writeValueAsString(seriesImportDTO)
        )

        runBlocking {
            val saved = eventItemRepository.save(item)
            val found = eventItemRepository.findById(saved.eventId)
            found.shouldNotBeNull()
            found.type shouldBe EventItemType.SERIES
            found.payload.shouldNotBeNull()
            found.status shouldBe EventItemStatus.PENDING
            found.extraKeyValues shouldBe extraKeyValues
            found.extraKeyValues["key"] shouldBe "value"
            val updated = eventItemRepository.update(found.copy(status = EventItemStatus.SENT))
            updated.status shouldBe EventItemStatus.SENT
            updated.payload.shouldNotBeNull()
        }
    }
}