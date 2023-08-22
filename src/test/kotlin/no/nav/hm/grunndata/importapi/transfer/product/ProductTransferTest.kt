package no.nav.hm.grunndata.importapi.transfer.product

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.core.async.publisher.Publishers
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow

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


    private val supplierId: UUID = UUID.randomUUID()

    @Test
    fun productTransferTest() {
        runBlocking {
           supplierService.save(Supplier(id= supplierId, name = "Medema AS",
                identifier = "medema_as", jwtid = UUID.randomUUID().toString()))
            val supplier = supplierService.findById(supplierId)!!
            val token = "bearer ${tokenService.token(supplier)}"
            val product = objectMapper.readTree(ProductTransferTest::class.java.classLoader.getResourceAsStream("json/tilbehoer.json"))
            val response = client.productStream(supplierId = supplier.id, authorization = token, json = Publishers.just(product))
            var md5: String? = null
            var productId: UUID? = null
            response.asFlow().onEach {
                LOG.info(it.md5)
                md5 = it.md5
                md5.shouldNotBeNull()
                it.transferStatus shouldBe TransferStatus.RECEIVED
            }.collect()
            val transfers = client.getTransfersBySupplierIdSupplierRef(authorization = token, supplier.id, supplierRef = "1506-1041")
            transfers.totalSize shouldBe 1

            // test identical product
            val product2 = objectMapper.readTree(ProductTransferTest::class.java.classLoader.getResourceAsStream("json/tilbehoer.json"))
            val response2 = client.productStream(supplierId = supplier.id, authorization = token, json = Publishers.just(product2))
            response2.asFlow().onEach {
                LOG.info(it.md5)
                it.md5 shouldBe md5
                it.transferStatus shouldBe TransferStatus.RECEIVED
            }.collect()

            // test "delete" product
            val delete = client.deleteProduct(authorization = token, supplierId = supplier.id, supplierRef = "1506-1041")
            delete.body().message shouldBe "deleted by supplier"

        }

    }

}
