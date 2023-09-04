package no.nav.hm.grunndata.importapi.productimport

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.mockk
import no.nav.hm.grunndata.importapi.productImport.ProductImportRepository
import no.nav.hm.grunndata.importapi.productImport.TransferToProductImport
import no.nav.hm.grunndata.importapi.seriesstate.SeriesStateDTO
import no.nav.hm.grunndata.importapi.seriesstate.SeriesStateService
import no.nav.hm.grunndata.importapi.supplier.Supplier
import no.nav.hm.grunndata.importapi.supplier.SupplierRepository
import no.nav.hm.grunndata.importapi.toMD5Hex
import no.nav.hm.grunndata.importapi.transfer.product.*
import no.nav.hm.rapids_rivers.micronaut.RapidPushService
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
class ProductToProductImportTest(private val transferToProductImport: TransferToProductImport,
                                 private val supplierRepository: SupplierRepository,
                                 private val transferStateRepository: TransferStateRepository,
                                 private val productImportRepository: ProductImportRepository,
                                 private val seriesStateService: SeriesStateService,
                                 private val objectMapper: ObjectMapper) {

    private val supplierId: UUID = UUID.randomUUID()
    private val seriesId: UUID = UUID.randomUUID()

    @MockBean
    fun rapidPushService(): RapidPushService = mockk(relaxed = true)

    @Test
    fun testProductTransferToProductState() {
        val supplier = Supplier(id= supplierId, name = "Medema AS", identifier = "medema_as", jwtid = UUID.randomUUID().toString())
        val seriesDTO = SeriesStateDTO(id = seriesId.toString(), supplierId=supplierId, name = "Mini Crosser")
        val product = ProductTransferDTO(title = "Mini Crosser X1 4W",  isoCategory = "12230301" ,
            hmsArtNr = "250464",
            seriesId = seriesId.toString(),
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
            transferTechData = listOf(
                TransferTechData("Setebredde min", "45", "cm"),
                TransferTechData("Kjørelengde maks", "45", "km")
            ),
            media = listOf(
                TransferMediaDTO(sourceUri="https://medema.no/medias/2019-02/mc_x_4w_orange_10637_570x570px.jpg")
            ))
        val json = objectMapper.writeValueAsString(product)
        println(json)
        val transfer = ProductTransfer(supplierId=supplierId, json_payload = product, md5 = json.toMD5Hex(),
            supplierRef = product.supplierRef)

        runBlocking {
            val savedSup = supplierRepository.save(supplier)
            val savedSeries = seriesStateService.save(seriesDTO)
            savedSup.id shouldBe supplierId
            savedSeries.id shouldBe seriesId.toString()
            val saved = transferStateRepository.save(transfer)
            saved.transferId.shouldNotBeNull()
            saved.transferStatus shouldBe TransferStatus.RECEIVED
            saved.transferId.shouldNotBeNull()
            productImportRepository.findBySupplierIdAndSupplierRef(saved.supplierId, saved.supplierRef).shouldBeNull()
            transferToProductImport.receivedTransfersToProductState()
            val found = productImportRepository.findBySupplierIdAndSupplierRef(saved.supplierId, saved.supplierRef)
            found.shouldNotBeNull()
            found.transferId shouldBe saved.transferId
            found.id.shouldNotBeNull()
        }
    }


}