package no.nav.hm.grunndata.importapi.seriesImport

import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import no.nav.hm.grunndata.rapid.dto.MediaInfo
import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import no.nav.hm.grunndata.rapid.dto.SeriesAttributes
import no.nav.hm.grunndata.rapid.dto.SeriesStatus
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

@MicronautTest
class SeriesImportRepositoryTest(private val repository: SeriesImportRepository) {

    @Test
    fun crudTest() {
        val id = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val import1 = SeriesImport(seriesId = id, text= "series text", isoCategory = "12324567", supplierId = UUID.randomUUID(), transferId = UUID.randomUUID(),
            title= "Unik navn på serien +${UUID.randomUUID()}", status = SeriesStatus.ACTIVE)
        val import2 = SeriesImport(seriesId = id2, text = "series text 2", isoCategory = "12324567", supplierId = UUID.randomUUID(), transferId = UUID.randomUUID(),
            title = "Unik navn på serien +${UUID.randomUUID()}", status = SeriesStatus.ACTIVE, seriesData = SeriesDataDTO(
                media = setOf(
                    MediaInfo(uri = "123.jog", text = "bilde 1", source = MediaSourceType.IMPORT, sourceUri = "https://localhost/123.jpg", updated = LocalDateTime.now()),
                    MediaInfo(uri = "124.jog", text = "bilde 2", source = MediaSourceType.IMPORT, sourceUri = "https://localhost/124.jpg", updated = LocalDateTime.now())
                ),
                attributes = SeriesAttributes(keywords = setOf("keyword1", "keyword2"))
            )
        )
        runBlocking {
            val saved = repository.save(import1)
            val saved2 = repository.save(import2)
            val found = repository.findById(saved.seriesId)
            val found2 = repository.findById(saved2.seriesId)
            found.shouldNotBeNull()
            found2.shouldNotBeNull()
            found.status shouldBe SeriesStatus.ACTIVE
            val updated = repository.update(found.copy(status = SeriesStatus.INACTIVE, updated = LocalDateTime.now()))
            updated.shouldNotBeNull()
            updated.status shouldBe SeriesStatus.INACTIVE
            updated.text shouldBe "series text"
            updated.isoCategory shouldBe "12324567"
            found2.seriesData.media.size shouldBe 2
            found2.seriesData.attributes.keywords!!.size shouldBe 2
        }
    }
}