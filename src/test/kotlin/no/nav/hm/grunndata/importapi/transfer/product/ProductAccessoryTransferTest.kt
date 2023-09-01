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
import no.nav.hm.grunndata.importapi.techdata.GdbApiClient
import no.nav.hm.grunndata.importapi.techdata.TechDataLabelDTO
import no.nav.hm.grunndata.importapi.token.TokenService
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
class ProductAccessoryTransferTest(private val client: ProductTransferClient,
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
                    identifier = "HMDB-20674",
                    label = "Farge",
                    guide = "Farge",
                    isocode = "24091802",
                    type = "C",
                    unit = ""),
                TechDataLabelDTO(
                    identifier = "HMDB-20675",
                    label = "Materiale",
                    guide = "Materiale",
                    isocode = "04481502",
                    type = "C",
                    unit = ""),
                TechDataLabelDTO(
                    identifier = "HMDB-20675",
                    label = "Belastning maks",
                    guide = "Belastning maks",
                    isocode = "18301805",
                    type = "N",
                    unit = "kg")

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
            val accessory = objectMapper.readTree(ProductAccessoryTransferTest::class.java.classLoader.getResourceAsStream("json/tilbehoer.json"))
            val transferDTO = objectMapper.treeToValue(accessory, ProductTransferDTO::class.java)
            val response = client.productStream(supplierId = supplier!!.id, authorization = token, json = Publishers.just(accessory))
            var md5: String? = null
            var productId: UUID? = null
            var transferId: UUID? = null
            response.asFlow().onEach {
                md5 = it.md5
                md5.shouldNotBeNull()
                it.transferStatus shouldBe TransferStatus.RECEIVED
                transferId = it.transferId
            }.collect()
            val transfers = client.getTransfersBySupplierIdSupplierRef(authorization = token, supplier!!.id, supplierRef = "625067")
            transfers.totalSize shouldBe 1

            val transfer = client.getTransferBySupplierIdAndTransferId(authorization = token, supplierId = supplier!!.id, transferId = transferId!!)
            transfer?.md5 shouldBe md5
        }
    }
}