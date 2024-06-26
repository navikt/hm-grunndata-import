package no.nav.hm.grunndata.importapi.transfer.series

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.common.runBlocking
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
class SeriesTransferRepositoryTest(
    private val seriesTransferRepository: SeriesTransferRepository,
    private val objectMapper: ObjectMapper
) {

    @Test
    fun crudTest() {
        val supplierId = UUID.randomUUID()
        val seriesName = "Unik series - 123"
        val seriesId = UUID.randomUUID()
        val transfer = SeriesTransfer(
            supplierId = supplierId,
            seriesId = seriesId,
            json_payload = SeriesTransferDTO(
                seriesId = seriesId,
                title = seriesName,
                text = "En beskrivelse for serien",
                isoCategory = "12345678"
            ),
            md5 = "hexvaluemd5"
        )
        runBlocking {
            val saved = seriesTransferRepository.save(
                transfer
            )
            saved.shouldNotBeNull()
            val found = seriesTransferRepository.findBySupplierIdAndSeriesId(supplierId, seriesId, Pageable.unpaged())
            found.shouldNotBeNull()
            found.totalSize shouldBeGreaterThanOrEqual 1
            println(objectMapper.writeValueAsString(transfer))

        }
    }
}