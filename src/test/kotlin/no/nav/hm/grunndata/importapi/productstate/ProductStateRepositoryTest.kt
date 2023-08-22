package no.nav.hm.grunndata.importapi.productstate

import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import no.nav.hm.grunndata.rapid.dto.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

@MicronautTest
class ProductStateRepositoryTest(private val productStateRepository: ProductStateRepository) {

    @Test
    fun crudProductStateTest() {
        val supplierId = UUID.randomUUID()
        val productDTO = ProductRapidDTO (
            id = UUID.randomUUID(),
            supplier = SupplierDTO(id= supplierId, identifier = "12345", updated = LocalDateTime.now(),
                created = LocalDateTime.now(), createdBy = "IMPORT", updatedBy = "IMPORT", info = SupplierInfo(), name = "testsupplier"),
            title = "Dette er produkt title",
            articleName = "Dette er article navn",
            attributes = Attributes(
                shortdescription = "En kort beskrivelse av produktet",
                text =  "En lang beskrivelse av produktet"
            ),
            hmsArtNr = "123",
            identifier = "hmdb-123",
            supplierRef = "referanse-123",
            isoCategory = "12001314",
            accessory = false,
            sparePart = false,
            seriesId = "series-123",
            techData = listOf(TechData(key = "maksvekt", unit = "kg", value = "120")),
            media = listOf(
                MediaInfo(uri="123.jpg", text = "bilde av produktet", source = MediaSourceType.EXTERNALURL,
                sourceUri = "https://ekstern.url/123.jpg")
            ),
            agreementInfo = AgreementInfo(id = UUID.randomUUID(), identifier = "hmdbid-1", rank = 1, postNr = 1,
                reference = "AV-142", expired = LocalDateTime.now()), createdBy = "IMPORT", updatedBy = "IMPORT"
        )
        val state = ProductState(id = productDTO.id, supplierId = supplierId, supplierRef = "referanse-123",
        productDTO = productDTO, transferId = UUID.randomUUID())
        runBlocking {
            val saved = productStateRepository.save(state)
            val find = productStateRepository.findBySupplierIdAndSupplierRef(supplierId, "referanse-123")
            find.shouldNotBeNull()
            find.productDTO.title shouldBe "Dette er produkt title"
            val updated = productStateRepository.update(find.copy(productDTO = find.productDTO.copy(title = "ny tittel")))
            updated.shouldNotBeNull()
            updated.productDTO.title shouldBe "ny tittel"
        }
    }
}
