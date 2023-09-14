package no.nav.hm.grunndata.importapi.seriesstate

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
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
                         private val tokenService: TokenService,
                         private val objectMapper: ObjectMapper
) {

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
        val dto = SeriesTransferDTO (
            name = "Unique series name"
        )
        val response = client.createSeries(supplierId = supplierId, dto = dto, authorization = token)
        response.status shouldBe HttpStatus.CREATED
        val created = response.body()
        created.shouldNotBeNull()
        created.name shouldBe "Unique series name"
        val dto2 = SeriesTransferDTO(name = "Unique series name 2")
        val created2 = client.createSeries(supplierId, dto2, token)
        created2.shouldNotBeNull()
        val changed = dto.copy(name = "Unique series name 3")
        val updated = client.updateSeries(supplierId, created2.body().id!!, changed, token)
        updated.shouldNotBeNull()
        updated.body().name shouldBe "Unique series name 3"
        client.getSeriesBySupplierId(supplierId = supplierId, authorization = token).size shouldBeGreaterThanOrEqual  2
        println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto))
    }
}