package no.nav.hm.grunndata.importapi.transfer.product

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.supplier.Supplier
import no.nav.hm.grunndata.importapi.supplier.SupplierRepository
import no.nav.hm.grunndata.importapi.toMD5Hex
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
class ProductTransferRepositoryTest(private val productTransferRepository: ProductTransferRepository,
                                    private val supplierRepository: SupplierRepository,
                                    private val objectMapper: ObjectMapper) {

    private val supplierId: UUID = UUID.randomUUID()

    @Test
    fun crudRepositoryTest() {
        val supplier = Supplier(id= supplierId, name = "Medema AS", identifier = "medema_as", jwtid = UUID.randomUUID().toString())
        val product = ProductTransferDTO(isoCategory = "12230301" ,
            title = "Mini Crosser",
            articleName = "mini-crosser-x1-x2-4w",
            supplierRef = "mini-crosser-x1-x2-4w",
            seriesId = UUID.randomUUID(),
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
                MediaDTO(uri="https://medema.no/medias/2019-02/mc_x_4w_orange_10637_570x570px.jpg")
        ))
        val json = objectMapper.writeValueAsString(product)
        val transfer = ProductTransfer(supplierId=supplierId, json_payload = product, md5 = json.toMD5Hex(),
            supplierRef = product.supplierRef)

        runBlocking {
            val savedSup = supplierRepository.save(supplier)
            savedSup.id shouldBe supplierId
            val saved = productTransferRepository.save(transfer)
            saved.transferId.shouldNotBeNull()

            val db = productTransferRepository.findById(saved.transferId)
            db.shouldNotBeNull()
            db.json_payload.shouldNotBeNull()
            db.transferStatus shouldBe TransferStatus.RECEIVED

            productTransferRepository.update(db.copy(transferStatus = TransferStatus.DONE))
            val done = productTransferRepository.findById(saved.transferId)
            done.shouldNotBeNull()
            done.transferStatus shouldBe TransferStatus.DONE
        }
    }
}
