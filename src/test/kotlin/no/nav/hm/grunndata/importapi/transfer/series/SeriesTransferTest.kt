package no.nav.hm.grunndata.importapi.transfer.series

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import no.nav.hm.grunndata.importapi.security.TokenService
import no.nav.hm.grunndata.importapi.supplier.Supplier
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.techdata.TechLabelDTO
import no.nav.hm.grunndata.rapid.dto.IsoCategoryDTO
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.*

@MicronautTest
class SeriesTransferTest(private val client: SeriesTransferClient, private val supplierService: SupplierService,
                         private val tokenService: TokenService, private val objectMapper: ObjectMapper) {

    companion object {
        private val LOG = LoggerFactory.getLogger(SeriesTransferTest::class.java)
    }

    private val supplierId: UUID = UUID.randomUUID()
    private var token: String = ""
    private var supplier: Supplier? = null

    @MockBean(GdbApiClient::class)
    fun gdbApiClient(): GdbApiClient {
        val mock = mockk<GdbApiClient>(relaxed = true)
        coEvery { mock.fetchAllTechLabels() } answers {
            listOf(
                TechLabelDTO(
                    identifier = "HMDB-20672",
                    label = "Setebredde min",
                    guide = "Setebredde min",
                    isocode = "30093604",
                    type = "N",
                    unit = "cm"),
                TechLabelDTO(
                    identifier = "HMDB-20673",
                    label = "Kjørelengde maks",
                    guide = "Kjørelengde maks",
                    isocode = "30093605",
                    type = "N",
                    unit = "km")
            )
        }
        coEvery { mock.retrieveIsoCategories() } answers {
            listOf(
                IsoCategoryDTO(
                isoCode = "12230301", isoTitle = "Test title", isoText = "Test text", isoLevel = 4
            )
            )
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
    fun testSeriesTransferApi() {
        runBlocking {
            
        }
    }

}