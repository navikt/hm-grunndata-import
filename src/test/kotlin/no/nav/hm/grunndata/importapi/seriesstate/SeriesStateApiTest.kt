package no.nav.hm.grunndata.importapi.seriesstate

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.security.TokenService
import no.nav.hm.grunndata.importapi.supplier.Supplier
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
class SeriesStateApiTest(private val client: SeriesStateAPIClient,
                         private val supplierService: SupplierService,
                         private val tokenService: TokenService) {

    private val supplierId: UUID = UUID.randomUUID()
    private var token: String
    private var supplier: Supplier

    init {
        runBlocking {
            supplierService.save(Supplier(id= supplierId, name = UUID.randomUUID().toString(),
                identifier = UUID.randomUUID().toString(), jwtid = UUID.randomUUID().toString()))
            supplier = supplierService.findById(supplierId)!!
            token = "bearer ${tokenService.token(supplier)}"
        }
    }

    @Test
    fun testCrudApi() {
        val dto = SeriesStateDTO(
            supplierId = supplierId,
            name = "Unique series Name"
        )
        val response = client.createSeries(supplierId = supplierId, dto = dto, authorization = token)
        response.status shouldBe HttpStatus.CREATED
        val created = response.body()
        created.shouldNotBeNull()

    }
}