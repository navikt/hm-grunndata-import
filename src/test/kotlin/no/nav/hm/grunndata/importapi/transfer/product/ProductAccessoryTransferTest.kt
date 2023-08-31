package no.nav.hm.grunndata.importapi.transfer.product

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.supplier.Supplier
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.techdata.GdbApiClient
import no.nav.hm.grunndata.importapi.techdata.TechDataLabelDTO
import no.nav.hm.grunndata.importapi.token.TokenService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
class ProductAccessoryTransferTest(private val productTransferClient: ProductTransferClient,
                                   private val supplierService: SupplierService,
                                   private val tokenService: TokenService,
                                   private val objectMapper: ObjectMapper) {


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
    fun testAccessoryTransfer() {
        runBlocking {
            val accessory =
                objectMapper.readTree(ProductTransferTest::class.java.classLoader.getResourceAsStream("json/tilbehoer.json"))
        }
    }
}