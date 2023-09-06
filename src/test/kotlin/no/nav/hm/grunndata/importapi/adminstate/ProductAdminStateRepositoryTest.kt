package no.nav.hm.grunndata.importapi.adminstate

import io.kotest.common.runBlocking
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import no.nav.hm.grunndata.rapid.dto.AdminStatus
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
class ProductAdminStateRepositoryTest(private val productAdminStateRepository: ProductAdminStateRepository) {

    @Test
    fun crudProductAdminState() {
        val supplierRef = UUID.randomUUID().toString()
        val supplierId = UUID.randomUUID()
        val state = ProductAdminState(
            id = UUID.randomUUID(),
            transferId = UUID.randomUUID(),
            supplierId = supplierId,
            supplierRef = supplierRef,
            adminStatus = AdminStatus.REJECTED,
            adminMessage = "Feil title",
            version = 1L
        )

        runBlocking {
            val saved = productAdminStateRepository.save(state)
            saved.shouldNotBeNull()
            val find = productAdminStateRepository.findById(saved.id)
            find.shouldNotBeNull()
            find.adminStatus shouldBe AdminStatus.REJECTED
            val updated = productAdminStateRepository.update(find.copy(adminStatus = AdminStatus.APPROVED, adminMessage = null))
            updated.adminStatus shouldBe AdminStatus.APPROVED
            updated.adminMessage shouldBe null
        }

    }
}