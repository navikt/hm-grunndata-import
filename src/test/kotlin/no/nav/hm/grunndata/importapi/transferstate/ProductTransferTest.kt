package no.nav.hm.grunndata.importapi.transferstate

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.security.TokenService
import no.nav.hm.grunndata.importapi.supplier.Supplier
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.*

@MicronautTest
class ProductTransferTest(private val client: ProductTransferClient,
                          private val supplierService: SupplierService,
                          private val tokenService: TokenService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductTransferTest::class.java)
    }

    @Test
    fun supplierTransferTest() {
        val supplierId = UUID.randomUUID()
        val supplier = Supplier(id= supplierId, name = "Medema AS", identifier = "medema_as", jwtid = UUID.randomUUID())
        val token = tokenService.token(supplier)
        runBlocking {
            supplierService.save(supplier)

        }
    }

}
