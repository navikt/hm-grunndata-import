package no.nav.hm.grunndata.importapi.productimport

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldBeNull
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
import no.nav.hm.grunndata.importapi.transfer.product.*
import no.nav.hm.grunndata.rapid.dto.AdminStatus
import no.nav.hm.rapids_rivers.micronaut.RapidPushService
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

@MicronautTest
class ProductToProductImportTest(private val productTransferToProductImport: ProductTransferToProductImport,
                                 private val supplierRepository: SupplierRepository,
                                 private val productTransferRepository: ProductTransferRepository,
                                 private val productImportRepository: ProductImportRepository,
                                 private val seriesImportService: SeriesImportService,
                                 private val objectMapper: ObjectMapper
) {

    private val supplierId: UUID = UUID.randomUUID()
    private val seriesId: UUID = UUID.randomUUID()

    @MockBean
    fun rapidPushService(): RapidPushService = mockk(relaxed = true)

    @MockBean(GdbApiClient::class)
    fun gdbApiClient(): GdbApiClient {
        val mock = mockk<GdbApiClient>(relaxed = true)
        coEvery { mock.getProductBySupplierIdAndSupplierRef(any(), any()) } answers {null}
        return mock
    }

    @Test
    fun testProductTransferToProductImport() {
        val supplier = Supplier(id= supplierId, name = "Medema AS", identifier = "medema_as", jwtid = UUID.randomUUID().toString())
        val seriesDTO = SeriesImportDTO(id = seriesId,
            text = "Series text",
            isoCategory = "12230301",
            supplierId=supplierId,
            transferId = UUID.randomUUID(), expired = LocalDateTime.now(),
            title = "Mini Crosser")
        val product = ProductTransferDTO(
            seriesId = seriesId,
            articleName = "mini-crosser-x1-x2-4w",
            supplierRef = "mini-crosser-x1-x2-4w",
            compatibleWith = null,
            articleDescription = "4-hjuls scooter med manuell regulering av seteløft, ryggvinkel og seterotasjon. Leveres som standard med Ergo2 sitteenhet.",
            techData = listOf(
                TechData("Setebredde min", "45", "cm"),
                TechData("Kjørelengde maks", "45", "km")
            ))
        val json = objectMapper.writeValueAsString(product)
        println(json)
        val transfer = ProductTransfer(supplierId=supplierId, json_payload = product, md5 = json.toMD5Hex(),
            supplierRef = product.supplierRef)

        runBlocking {
            val savedSup = supplierRepository.save(supplier)
            val savedSeries = seriesImportService.save(seriesDTO)
            savedSup.id shouldBe supplierId
            savedSeries.id shouldBe seriesId
            val saved = productTransferRepository.save(transfer)
            saved.transferId.shouldNotBeNull()
            saved.transferStatus shouldBe TransferStatus.RECEIVED
            saved.transferId.shouldNotBeNull()
            productImportRepository.findBySupplierIdAndSupplierRef(saved.supplierId, saved.supplierRef).shouldBeNull()
            productTransferToProductImport.receivedTransfersToProductImport()
            val found = productImportRepository.findBySupplierIdAndSupplierRef(saved.supplierId, saved.supplierRef)
            found.shouldNotBeNull()
            found.transferId shouldBe saved.transferId
            found.id.shouldNotBeNull()
            val adminState = productImportRepository.findById(found.id)
            adminState.shouldNotBeNull()
            adminState.adminStatus shouldBe AdminStatus.PENDING
            adminState.transferId shouldBe found.transferId
        }
    }

}
