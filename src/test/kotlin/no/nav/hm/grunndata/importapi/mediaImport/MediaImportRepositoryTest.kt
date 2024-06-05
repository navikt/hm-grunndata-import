package no.nav.hm.grunndata.importapi.mediaImport

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import java.util.UUID
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImport
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportRepository
import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import no.nav.hm.grunndata.rapid.dto.SeriesStatus
import org.junit.jupiter.api.Test

@MicronautTest
class MediaImportRepositoryTest(private val seriesImportRepository: SeriesImportRepository,
                                private val mediaImportRepository: MediaImportRepository) {

    @Test
    fun crudMediaImport() {
        val seriesId = UUID.randomUUID()
        val seriesImport = SeriesImport(
            seriesId = seriesId,
            supplierId = UUID.randomUUID(),
            isoCategory = "12345678",
            title = "Unik series - 123",
            text = "En beskrivelse for serien",
            status = SeriesStatus.ACTIVE,
            transferId = UUID.randomUUID()
        )
        val supplierId = UUID.randomUUID()
        val mediaImport = MediaImport(
            id = UUID.randomUUID(),
            uri = "http://example123.com",
            seriesId = seriesId,
            supplierId = supplierId,
            text = "Dette er en beskrivelse",
            sourceType = MediaSourceType.IMPORT,
        )
        runBlocking {
            val series = seriesImportRepository.save(seriesImport)
            series.shouldNotBeNull()
            val saved = mediaImportRepository.save(mediaImport)
            saved.shouldNotBeNull()
            val found = mediaImportRepository.findById(saved.id)
            found.shouldNotBeNull()
            found.seriesId shouldBe seriesId
            found.supplierId shouldBe supplierId
            val updatedSeries = seriesImportRepository.findById(seriesId)
            updatedSeries.shouldNotBeNull()

        }
    }
}