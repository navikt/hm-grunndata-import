package no.nav.hm.grunndata.importapi.productimport

import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import no.nav.hm.grunndata.importapi.productImport.ProductImport
import no.nav.hm.grunndata.importapi.productImport.ProductImportRepository
import no.nav.hm.grunndata.rapid.dto.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

@MicronautTest
class ProductImportRepositoryTest(private val productImportRepository: ProductImportRepository) {

    @Test
    fun crudProductStateTest() {
        val supplierId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val hmsArtNr = "123"
        val productDTO = ProductRapidDTO (
            id = productId,
            supplier = SupplierDTO(id= supplierId, identifier = "12345", updated = LocalDateTime.now(),
                created = LocalDateTime.now(), createdBy = "IMPORT", updatedBy = "IMPORT", info = SupplierInfo(), name = "testsupplier"),
            title = "Dette er produkt title",
            articleName = "Dette er article navn",
            attributes = Attributes(
                shortdescription = "En kort beskrivelse av produktet",
                text =  "En lang beskrivelse av produktet"
            ),
            hmsArtNr = hmsArtNr,
            identifier = "hmdb-123",
            supplierRef = "referanse-123",
            isoCategory = "12001314",
            accessory = false,
            sparePart = false,
            seriesId = productId.toString(),
            techData = listOf(TechData(key = "maksvekt", unit = "kg", value = "120")),
            media = listOf(
                MediaInfo(uri="123.mp4", text = "video av produktet", source = MediaSourceType.EXTERNALURL,
                sourceUri = "https://ekstern.url/123.mp4")
            ),
            agreementInfo = AgreementInfo(id = UUID.randomUUID(), identifier = "hmdbid-1", rank = 1, postNr = 1,
                reference = "AV-142", expired = LocalDateTime.now()), createdBy = "IMPORT", updatedBy = "IMPORT"
        )
        val state = ProductImport(id = productDTO.id, supplierId = supplierId, supplierRef = "referanse-123", hmsArtNr = hmsArtNr,
        productDTO = productDTO, transferId = UUID.randomUUID(), productStatus = productDTO.status, seriesId = UUID.fromString(productDTO.seriesId))
        runBlocking {
            val saved = productImportRepository.save(state)
            val find = productImportRepository.findBySupplierIdAndSupplierRef(supplierId, "referanse-123")
            find.shouldNotBeNull()
            find.hmsArtNr shouldBe "123"
            find.productDTO.title shouldBe "Dette er produkt title"
            val updated = productImportRepository.update(find.copy(productDTO = find.productDTO.copy(title = "ny tittel")))
            updated.shouldNotBeNull()
            updated.productDTO.title shouldBe "ny tittel"
            updated.adminStatus shouldBe AdminStatus.PENDING
        }
    }
}
