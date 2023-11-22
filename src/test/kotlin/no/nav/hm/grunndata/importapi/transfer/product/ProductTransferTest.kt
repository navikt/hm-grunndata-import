package no.nav.hm.grunndata.importapi.transfer.product

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
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import no.nav.hm.grunndata.importapi.techdata.TechDataLabelDTO
import no.nav.hm.grunndata.importapi.security.TokenService
import no.nav.hm.grunndata.rapid.dto.IsoCategoryDTO
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
    fun productTransferTest() {
        runBlocking {
            val product = objectMapper.readTree(ProductTransferTest::class.java.classLoader.getResourceAsStream("json/product.json"))
            val response = client.productStream(identifier = supplier!!.identifier, authorization = token, json = Publishers.just(product))
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
            val transfers = client.getTransfersBySupplierIdSupplierRef(authorization = token, identifier = supplier!!.identifier, supplierRef = "1500-1530")
            transfers.totalSize shouldBe 1

            val transfer = client.getTransferBySupplierIdAndTransferId(authorization = token, identifier = supplier!!.identifier, transferId = transferId!!)
            transfer?.md5 shouldBe md5

            // test identical product
            val product2 = objectMapper.readTree(ProductTransferTest::class.java.classLoader.getResourceAsStream("json/product.json"))
            val response2 = client.productStream(identifier = supplier!!.identifier, authorization = token, json = Publishers.just(product2))
            response2.asFlow().onEach {
                LOG.info(it.md5)
                it.md5 shouldBe md5
                it.transferStatus shouldBe TransferStatus.RECEIVED
            }.collect()

            // test "delete" product
            val deactivate = client.deleteProduct(authorization = token, identifier = supplier!!.identifier, supplierRef = "1500-1530")
            deactivate.body().message shouldBe "Deactivated by supplier"
            val delete = client.deleteProduct(authorization = token, identifier = supplier!!.identifier, supplierRef = "1500-1530", delete = true)
            delete.body().message shouldBe "Deleted by supplier"
        }

    }

}
