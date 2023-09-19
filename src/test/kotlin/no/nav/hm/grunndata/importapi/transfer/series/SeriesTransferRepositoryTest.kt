package no.nav.hm.grunndata.importapi.transfer.series

import io.kotest.common.runBlocking
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
class SeriesTransferRepositoryTest(private val seriesTransferRepository: SeriesTransferRepository) {

    @Test
    fun crudTest() {
        val seriesId = UUID.randomUUID()
        val supplierId = UUID.randomUUID()
        val seriesName = "Unik series - 123"
        val transfer = SeriesTransfer(
            supplierId = supplierId,
            seriesId = seriesId,
            json_payload = SeriesTransferDTO(
                id = seriesId,
                name = seriesName
            ),
            md5 = "hexvaluemd5"
        )
        runBlocking {
            val saved = seriesTransferRepository.save(
                transfer
            )
            saved.shouldNotBeNull()
            val found = seriesTransferRepository.findBySupplierIdAndSeriesId(supplierId,seriesId)
            found.shouldNotBeNull()
            found.totalSize shouldBeGreaterThanOrEqual  1

        }
    }
}