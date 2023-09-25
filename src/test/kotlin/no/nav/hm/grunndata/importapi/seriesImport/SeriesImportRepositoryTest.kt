package no.nav.hm.grunndata.importapi.seriesImport

import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import no.nav.hm.grunndata.rapid.dto.SeriesStatus
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

@MicronautTest
class SeriesImportRepositoryTest(private val repository: SeriesImportRepository) {

    @Test
    fun crudTest() {
        val id = UUID.randomUUID()
        val state = SeriesImport(seriesId = id, identifier = id.toString(), supplierId = UUID.randomUUID(), transferId = UUID.randomUUID(),
            name= "Unik navn på serien +${UUID.randomUUID()}", status = SeriesStatus.ACTIVE)
        runBlocking {
            val saved = repository.save(state)
            val found = repository.findById(saved.seriesId)
            found.shouldNotBeNull()
            found.status shouldBe SeriesStatus.ACTIVE
            val updated = repository.update(found.copy(status = SeriesStatus.INACTIVE, updated = LocalDateTime.now()))
            updated.shouldNotBeNull()
            updated.status shouldBe SeriesStatus.INACTIVE
        }
    }
}