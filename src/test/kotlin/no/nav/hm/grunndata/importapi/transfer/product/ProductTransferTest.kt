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
import no.nav.hm.grunndata.importapi.token.TokenService
import no.nav.hm.grunndata.importapi.supplier.Supplier
import no.nav.hm.grunndata.importapi.supplier.SupplierService

import org.junit.jupiter.api.BeforeEach
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
    private var token: String = ""
    private var supplier: Supplier? = null

    @BeforeEach
    fun before() {
        runBlocking {
            supplierService.save(Supplier(id= supplierId, name = UUID.randomUUID().toString(),
                identifier = UUID.randomUUID().toString(), jwtid = UUID.randomUUID().toString()))
            supplier = supplierService.findById(supplierId)!!
            token = "bearer ${tokenService.token(supplier!!)}"
        }
    }

    @Test
    fun productTransferTest() {
        runBlocking {
            val product = objectMapper.readTree(ProductTransferTest::class.java.classLoader.getResourceAsStream("json/product.json"))
            val response = client.productStream(supplierId = supplier!!.id, authorization = token, json = Publishers.just(product))
            var md5: String? = null
            var productId: UUID? = null
            var transferId: UUID? = null
            response.asFlow().onEach {
                LOG.info(it.md5)
                md5 = it.md5
                md5.shouldNotBeNull()
                it.transferStatus shouldBe TransferStatus.RECEIVED
                transferId = it.transferId
            }.collect()
            val transfers = client.getTransfersBySupplierIdSupplierRef(authorization = token, supplier!!.id, supplierRef = "mini-crosser-x1-x2-4w")
            transfers.totalSize shouldBe 1

            val transfer = client.getTransferBySupplierIdAndTransferId(authorization = token, supplierId = supplier!!.id, transferId = transferId!!)
            transfer?.md5 shouldBe md5

            // test identical product
            val product2 = objectMapper.readTree(ProductTransferTest::class.java.classLoader.getResourceAsStream("json/product.json"))
            val response2 = client.productStream(supplierId = supplier!!.id, authorization = token, json = Publishers.just(product2))
            response2.asFlow().onEach {
                LOG.info(it.md5)
                it.md5 shouldBe md5
                it.transferStatus shouldBe TransferStatus.RECEIVED
            }.collect()

            // test "delete" product
            val delete = client.deleteProduct(authorization = token, supplierId = supplier!!.id, supplierRef = "mini-crosser-x1-x2-4w")
            delete.body().message shouldBe "deleted by supplier"
        }

    }

}
