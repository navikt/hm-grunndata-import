package no.nav.hm.grunndata.importapi.seriesstate

import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

@MicronautTest
class SeriesStateRepositoryTest(private val repository: SeriesStateRepository) {

    @Test
    fun crudTest() {
        val id = "seriesid +${UUID.randomUUID()}"
        val state = SeriesState(id = id, supplierId = UUID.randomUUID(),
            name= "Unik navn på serien +${UUID.randomUUID()}", status = SerieTransferStatus.ACTIVE)
        runBlocking {
            val saved = repository.save(state)
            val found = repository.findById(saved.id)
            found.shouldNotBeNull()
            found.status shouldBe SerieTransferStatus.ACTIVE
            val updated = repository.update(found.copy(status = SerieTransferStatus.INACTIVE, updated = LocalDateTime.now()))
            updated.shouldNotBeNull()
            updated.status shouldBe SerieTransferStatus.INACTIVE
        }
    }
}