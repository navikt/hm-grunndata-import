package no.nav.hm.grunndata.importapi.productstate

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.common.runBlocking
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.mockk
import no.nav.hm.grunndata.importapi.productImport.ProductImportRepository
import no.nav.hm.grunndata.importapi.productImport.TransferToProductImport
import no.nav.hm.grunndata.importapi.seriesstate.SeriesStateService
import no.nav.hm.grunndata.importapi.supplier.Supplier
import no.nav.hm.grunndata.importapi.supplier.SupplierRepository
import no.nav.hm.grunndata.importapi.toMD5Hex
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransfer
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransferDTO
import no.nav.hm.grunndata.importapi.transfer.product.TransferStateRepository
import no.nav.hm.rapids_rivers.micronaut.RapidPushService
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
class AccessoryToProductImportTest(private val transferToProductImport: TransferToProductImport,
                                   private val supplierRepository: SupplierRepository,
                                   private val transferStateRepository: TransferStateRepository,
                                   private val productImportRepository: ProductImportRepository,
                                   private val seriesStateService: SeriesStateService,
                                   private val objectMapper: ObjectMapper) {


    private val supplierId: UUID = UUID.randomUUID()

    @MockBean
    fun rapidPushService(): RapidPushService = mockk(relaxed = true)

    @Test
    fun testAccessoryToProductState() {
        val supplier = Supplier(id= supplierId, name = "supplier AS $supplierId", identifier = "supplier_as+$supplierId", jwtid = UUID.randomUUID().toString())
        val jsonNode = objectMapper.readTree(AccessoryToProductImportTest::class.java.classLoader.getResourceAsStream("json/tilbehoer.json"))
        val json = objectMapper.writeValueAsString(jsonNode)
        val accessory = objectMapper.treeToValue(jsonNode, ProductTransferDTO::class.java)
        val transfer = ProductTransfer(supplierId=supplierId, json_payload = accessory, md5 = json.toMD5Hex(),
            supplierRef = accessory.supplierRef)

        runBlocking {
            val savedSupplier = supplierRepository.save(supplier)
            val savedTransfer = transferStateRepository.save(transfer)
            transferToProductImport.receivedTransfersToProductState()
            productImportRepository.findBySupplierIdAndSupplierRef()

        }
    }
}