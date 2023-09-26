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
        val  supplierSeriesRef = UUID.randomUUID().toString()
        val supplierId = UUID.randomUUID()
        val seriesName = "Unik series - 123"
        val transfer = SeriesTransfer(
            supplierId = supplierId,
            supplierSeriesRef = supplierSeriesRef,
            json_payload = SeriesTransferDTO(
                supplierSeriesRef = supplierSeriesRef,
                name = seriesName
            ),
            md5 = "hexvaluemd5"
        )
        runBlocking {
            val saved = seriesTransferRepository.save(
                transfer
            )
            saved.shouldNotBeNull()
            val found = seriesTransferRepository.findBySupplierIdAndSupplierSeriesRef(supplierId, supplierSeriesRef)
            found.shouldNotBeNull()
            found.totalSize shouldBeGreaterThanOrEqual  1

        }
    }
}