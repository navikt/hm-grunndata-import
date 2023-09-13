package no.nav.hm.grunndata.importapi.security

import io.kotest.matchers.nulls.shouldNotBeNull
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.supplier.Supplier
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import org.junit.jupiter.api.Test
import java.util.*


@MicronautTest
class TokenAPIControllerTest(private val tokenAPIClient: TokenAPIClient,
                             private val tokenService: TokenService,
                             private val supplierService: SupplierService) {

    val supplierId: UUID = UUID.randomUUID()
    init {
        runBlocking {
            supplierService.save(
                Supplier(id= supplierId, name = UUID.randomUUID().toString(),
                identifier = UUID.randomUUID().toString(), jwtid = UUID.randomUUID().toString())
            )
        }
    }
    @Test
    fun tokenApiTest() {
        val bearerToken = "bearer ${tokenService.adminToken("tester")}"
        val supplierToken = tokenAPIClient.createSupplierToken(supplierId, bearerToken)
        val adminToken = tokenAPIClient.createAdminToken("Admin Token", bearerToken)
        supplierToken.shouldNotBeNull()
        adminToken.shouldNotBeNull()
    }
}