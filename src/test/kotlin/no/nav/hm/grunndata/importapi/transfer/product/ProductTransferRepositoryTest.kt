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
        val product = ProductTransferDTO(
            articleName = "mini-crosser-x1-x2-4w",
            supplierRef = "mini-crosser-x1-x2-4w",
            seriesId = UUID.randomUUID(),
            compatibleWith = null,
            articleDescription = "4-hjuls scooter med manuell regulering av seteløft, ryggvinkel og seterotasjon. Leveres som standard med Ergo2 sitteenhet.",
            techData = listOf(
                TechData("Setebredde min", "45", "cm"),
                TechData("Kjørelengde maks", "45", "km")
            )
        )
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
