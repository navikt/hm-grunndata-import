package no.nav.hm.grunndata.importapi.productimport

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.common.runBlocking
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import no.nav.hm.grunndata.importapi.productImport.ProductImportRepository
import no.nav.hm.grunndata.importapi.productImport.ProductTransferToProductImport
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportDTO
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportService
import no.nav.hm.grunndata.importapi.supplier.Supplier
import no.nav.hm.grunndata.importapi.supplier.SupplierRepository
import no.nav.hm.grunndata.importapi.toMD5Hex
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransfer
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransferDTO
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransferRepository
import no.nav.hm.rapids_rivers.micronaut.RapidPushService
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

@MicronautTest
class AccessoryToProductImportTest(private val productTransferToProductImport: ProductTransferToProductImport,
                                   private val supplierRepository: SupplierRepository,
                                   private val seriesImportService: SeriesImportService,
                                   private val productTransferRepository: ProductTransferRepository,
                                   private val productImportRepository: ProductImportRepository,
                                   private val objectMapper: ObjectMapper) {


    private val supplierId: UUID = UUID.randomUUID()

    @MockBean
    fun rapidPushService(): RapidPushService = mockk(relaxed = true)

    @MockBean(GdbApiClient::class)
    fun gdbApiClient(): GdbApiClient {
        val mock = mockk<GdbApiClient>(relaxed = true)
        coEvery { mock.getProductBySupplierIdAndSupplierRef(any(), any()) } answers {null}
        return mock
    }

    @Test
    fun testAccessoryToProductState() {
        val seriesId = UUID.randomUUID()
        val supplier = Supplier(id= supplierId, name = "supplier AS $supplierId", identifier = "supplier_as+$supplierId", jwtid = UUID.randomUUID().toString())

        val jsonNode = objectMapper.readTree(AccessoryToProductImportTest::class.java.classLoader.getResourceAsStream("json/tilbehoer.json"))
        val json = objectMapper.writeValueAsString(jsonNode)
        val accessory = objectMapper.treeToValue(jsonNode, ProductTransferDTO::class.java)
        val transfer = ProductTransfer(supplierId=supplierId, json_payload = accessory, md5 = json.toMD5Hex(),
            supplierRef = accessory.supplierRef, seriesId = seriesId)

        runBlocking {
            val series = seriesImportService.save(SeriesImportDTO(
                id = UUID.fromString("603474bc-a8e8-471c-87ef-09bdc57bea59"),
                supplierId=supplierId, transferId = UUID.randomUUID(),
                expired = LocalDateTime.now().plusYears(15),
                title = "Mini Crosser", text = "Mini Crosser", isoCategory = "12324567"))
            val savedSupplier = supplierRepository.save(supplier)
            val savedTransfer = productTransferRepository.save(transfer)
            productTransferToProductImport.receivedTransfersToProductImport()
            val productImport = productImportRepository.findBySupplierIdAndSupplierRef(supplierId, accessory.supplierRef)
            productImport.shouldNotBeNull()
            productImport.id.shouldNotBeNull()
            productImport.productDTO.id shouldBe productImport.id
            productImport.productDTO.accessory shouldBe true
            productImport.productDTO.attributes.compatibleWidth.shouldNotBeNull()
            productImport.productDTO.attributes.compatibleWidth!!.seriesIds.shouldNotBeEmpty()
        }
    }
}