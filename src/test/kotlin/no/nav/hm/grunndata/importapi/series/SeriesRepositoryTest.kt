package no.nav.hm.grunndata.importapi.series

import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import no.nav.hm.grunndata.importapi.IMPORT
import no.nav.hm.grunndata.rapid.dto.SeriesStatus
import org.junit.jupiter.api.Test
import java.util.UUID

@MicronautTest
class SeriesRepositoryTest(private val seriesRepository: SeriesRepository) {

    @Test
    fun crudSeriesTest() {
        val series = Series(id = UUID.randomUUID(), supplierId = UUID.randomUUID(), name="Series X",
        status = SeriesStatus.ACTIVE, createdBy = IMPORT, updatedBy = IMPORT)
        runBlocking {
            val saved = seriesRepository.save(series)
            val found = seriesRepository.findById(saved.id)
            found.shouldNotBeNull()
            found.name shouldBe series.name
            val updated = seriesRepository.update(found.copy(name = "Series 10"))
            updated.shouldNotBeNull()
            updated.status shouldBe SeriesStatus.ACTIVE
            updated.name shouldBe "Series 10"
        }
    }
}