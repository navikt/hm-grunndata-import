package no.nav.hm.grunndata.importapi.supplier

import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import no.nav.hm.grunndata.rapid.dto.SupplierStatus
import org.junit.jupiter.api.Test

@MicronautTest
class SupplierServiceTest(private val supplierService: SupplierService) {

    @Test
    fun crudSupplierTest() {
        val supplier = Supplier(
            name = "Leverandør AS",
            identifier = "leverandor-as",
            createdBy = "IMPORT",
            updatedBy = "IMPORT"
        )
        runBlocking {
            val saved = supplierService.save(supplier)
            saved.shouldNotBeNull()
            saved.id shouldBe supplier.id
            val inDb = supplierService.findById(saved.id)
            inDb.shouldNotBeNull()
            inDb.name shouldBe "Leverandør AS"
            val updated = supplierService.update(inDb.copy(name = "Leverandør AS-2"))
            updated.shouldNotBeNull()
            val cached = supplierService.findById(saved.id)
            cached!!.name shouldBe "Leverandør AS-2"
            cached.createdBy shouldBe "IMPORT"
            cached.updatedBy shouldBe "IMPORT"
            val deactivated = supplierService.update(updated.copy(status = SupplierStatus.INACTIVE))
            deactivated.shouldNotBeNull()
            deactivated.status shouldBe SupplierStatus.INACTIVE

        }
    }
}