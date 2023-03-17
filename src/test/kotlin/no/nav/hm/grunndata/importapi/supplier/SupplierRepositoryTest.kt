package no.nav.hm.grunndata.importapi.supplier

import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import no.nav.hm.grunndata.rapid.dto.SupplierStatus
import org.junit.jupiter.api.Test


@MicronautTest
class SupplierRepositoryTest(private val supplierRepository: SupplierRepository) {

    @Test
    fun crudSupplierTest() {
        val supplier = Supplier(
            name = "Leverandør AS",
            identifier = "leverandor-as",
            createdBy = "IMPORT",
            updatedBy = "IMPORT"
        )
        runBlocking {
            val saved = supplierRepository.save(supplier)
            saved.shouldNotBeNull()
            saved.id shouldBe supplier.id
            val inDb = supplierRepository.findById(saved.id)
            inDb.shouldNotBeNull()
            inDb.name shouldBe "Leverandør AS"
            val updated = supplierRepository.update(inDb.copy(name = "Leverandør AS-2"))
            updated.shouldNotBeNull()
            updated.name shouldBe "Leverandør AS-2"
            updated.createdBy shouldBe "IMPORT"
            updated.updatedBy shouldBe "IMPORT"
            val deactivated = supplierRepository.update(updated.copy(status = SupplierStatus.INACTIVE))
            deactivated.shouldNotBeNull()
            deactivated.status shouldBe SupplierStatus.INACTIVE

        }
    }
}
