package no.nav.hm.grunndata.importapi.transfer.series

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.core.async.publisher.Publishers
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.supplier.Supplier
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.techdata.GdbApiClient
import no.nav.hm.grunndata.importapi.techdata.TechDataLabelDTO
import no.nav.hm.grunndata.importapi.security.TokenService
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.rapid.dto.IsoCategoryDTO
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.*

@MicronautTest
class SeriesTransferTest(private val client: SeriesTransferClient,
                         private val supplierService: SupplierService,
                         private val tokenService: TokenService,
                         private val objectMapper: ObjectMapper) {

    companion object {
        private val LOG = LoggerFactory.getLogger(SeriesTransferTest::class.java)
    }


    private val supplierId: UUID = UUID.randomUUID()
    private var token: String = ""
    private var supplier: Supplier? = null

    @MockBean(GdbApiClient::class)
    fun gdbApiClient(): GdbApiClient {
        val mock = mockk<GdbApiClient>(relaxed = true)
        every { mock.fetchAllTechLabels() } answers {
            listOf(
                TechDataLabelDTO(
                    identifier = "HMDB-20672",
                    label = "Setebredde min",
                    guide = "Setebredde min",
                    isocode = "30093604",
                    type = "N",
                    unit = "cm"),
                TechDataLabelDTO(
                    identifier = "HMDB-20673",
                    label = "Kjørelengde maks",
                    guide = "Kjørelengde maks",
                    isocode = "30093605",
                    type = "N",
                    unit = "km")
            )
        }
        every { mock.retrieveIsoCategories() } answers {
            listOf(IsoCategoryDTO(
                isoCode = "12230301", isoTitle = "Test title", isoText = "Test text", isoLevel = 4
            ))
        }
        return mock
    }

    init {
        runBlocking {
            supplierService.save(Supplier(id= supplierId, name = UUID.randomUUID().toString(),
                identifier = UUID.randomUUID().toString(), jwtid = UUID.randomUUID().toString()))
            supplier = supplierService.findById(supplierId)!!
            token = "bearer ${tokenService.token(supplier!!)}"
        }
    }

    @Test
    fun seriesTransferTest() {
        runBlocking {
            val series = objectMapper.readValue(SeriesTransferTest::class.java.classLoader.getResourceAsStream("json/series.json"),SeriesTransferDTO::class.java)
            val response = client.seriesStream(supplierId = supplier!!.id, authorization = token, dto = Publishers.just(series))
            var md5: String? = null
            var seriesId: UUID? = null
            var transferId: UUID? = null
            response.asFlow().onEach {
                LOG.info(it.md5)
                md5 = it.md5
                md5.shouldNotBeNull()
                it.transferStatus shouldBe TransferStatus.RECEIVED
                transferId = it.transferId
                seriesId = it.seriesId
            }.collect()
            val transfers = client.getTransfersBySupplierIdAndSupplierSeriesId(authorization = token, supplier!!.id, seriesId!!)
            transfers.totalSize shouldBe 1

        }

    }

}
