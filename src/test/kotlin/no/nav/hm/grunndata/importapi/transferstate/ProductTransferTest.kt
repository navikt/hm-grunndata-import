package no.nav.hm.grunndata.importapi.transferstate

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
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
                          private val tokenService: TokenService,
                          private val objectMapper: ObjectMapper) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductTransferTest::class.java)
    }

    @Test
    fun productTransferTest() {
        val supplierId = UUID.randomUUID()
        val supplier = Supplier(id= supplierId, name = "Medema AS", identifier = "medema_as", jwtid = UUID.randomUUID())
        val token = "bearer ${tokenService.token(supplier)}"
        val product = objectMapper.readTree(ProductTransferTest.javaClass.classLoader.getResourceAsStream("json/product.json"))
        runBlocking {
            supplierService.save(supplier)
            val response = client.productStream(supplierId = supplierId, authorization = token, json = flowOf(product))
            response.onEach {
                LOG.info("Got response md5: ${it.md5}")
            }.collect()

        }
    }

}
