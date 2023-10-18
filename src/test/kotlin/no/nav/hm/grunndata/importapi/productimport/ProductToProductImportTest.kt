package no.nav.hm.grunndata.importapi.productimport

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.every
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
        every { mock.getProductBySupplierIdAndSupplierRef(any(), any()) } answers {null}
        return mock
    }

    @Test
    fun testProductTransferToProductImport() {
        val supplier = Supplier(id= supplierId, name = "Medema AS", identifier = "medema_as", jwtid = UUID.randomUUID().toString())
        val seriesDTO = SeriesImportDTO(seriesId = seriesId, supplierId=supplierId, transferId = UUID.randomUUID(), expired = LocalDateTime.now(), title = "Mini Crosser")
        val product = ProductTransferDTO(isoCategory = "12230301" ,
            title = "Mini Crosser",
            hmsArtNr = "250464",
            seriesId = seriesId,
            articleName = "mini-crosser-x1-x2-4w",
            supplierRef = "mini-crosser-x1-x2-4w",
            manufacturer = "Medema AS",
            compatibleWith = null,
            shortDescription = "4-hjuls scooter med manuell regulering av seteløft, ryggvinkel og seterotasjon. Leveres som standard med Ergo2 sitteenhet.",
            text = """Mini Crosser modell X1/ X2
                    Er uten sammenligning markedets sterkeste og mest komfortable el scooter: Her får man både stor motorkraft, mulighet for ekstra stor kjørelengde og unik regulerbar fjæring pakket inn i et usedvanlig lekkert design. Nordens mest solgte scooter er spesielt konstruert for nordisk klima og geografi, hvilket betyr at den er velegnet for bruk året rundt, på dårlige veier, snøføre, og ellers hvor man ønsker ekstra stabilitet. Det er virkelig fokusert på sikkerheten, og uten at det går på kompromiss med bruksegenskaper og design. Leveres også med kabin.
                    Hjul , fjæring og styre Mini Crosser har behagelig fjæring på alle 4 hjul, inklusive justerbare støtdempere på alle hjul. Vi har stort utvalg av ulike hjul, inklusive pigghjul. Det multijusterbare styret sikrer optimal komfort. Det er utstyrt med et kardan-ledd og kan heves, senkes og vinkles. Krever kun liten armstyrke ved kjøring. Kurv blir stående stille når man svinger. Markedets minste svingradius!
                    Luksussete er standard. For å gi den ideelle sittestilling kan Mini Crosser Ergo-sete justeres i høyde, dybde og ryggvinkel og leveres i størrelser fra 35 til 70cm og med ulike rygghøyder. Armlenene er både høyde- og dybdejusterbare, samt oppfellbare og kan utstyres med ulike armlenspolstre. Setet er videre utstyrt med glideskinne og kan dreies 90 grader til begge sider. Det store sortimentet av seter, sete- og ryggputer og el funksjoner muliggjør nærmest enhver ønsket setetilpasning – muligheter man ellers kun finner på de mest avanserte el-rullestoler!"""
            ,
            techData = listOf(
                TechData("Setebredde min", "45", "cm"),
                TechData("Kjørelengde maks", "45", "km")
            ),
            media = listOf(
                MediaDTO(uri="12345/12356.png")
            ))
        val json = objectMapper.writeValueAsString(product)
        println(json)
        val transfer = ProductTransfer(supplierId=supplierId, json_payload = product, md5 = json.toMD5Hex(),
            supplierRef = product.supplierRef)

        runBlocking {
            val savedSup = supplierRepository.save(supplier)
            val savedSeries = seriesImportService.save(seriesDTO)
            savedSup.id shouldBe supplierId
            savedSeries.seriesId shouldBe seriesId
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
