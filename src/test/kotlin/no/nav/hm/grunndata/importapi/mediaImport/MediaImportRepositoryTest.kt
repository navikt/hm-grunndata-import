package no.nav.hm.grunndata.importapi.mediaImport

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import java.util.UUID
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import org.junit.jupiter.api.Test

@MicronautTest
class MediaImportRepositoryTest(private val mediaImportRepository: MediaImportRepository) {

    @Test
    fun crudMediaImport() {
        val seriesId = UUID.randomUUID()
        val supplierId = UUID.randomUUID()
        val mediaImport = MediaImport(
            id = UUID.randomUUID(),
            uri = "http://example.com",
            seriesId = seriesId,
            supplierId = supplierId,
            text = "Dette er en beskrivelse",
            sourceType = MediaSourceType.IMPORT,
        )
        runBlocking {
            val saved = mediaImportRepository.save(mediaImport)
            saved.shouldNotBeNull()
            val found = mediaImportRepository.findById(saved.id)
            found.shouldNotBeNull()
            found.seriesId shouldBe seriesId
            found.supplierId shouldBe supplierId
        }
    }
}