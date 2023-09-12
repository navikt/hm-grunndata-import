package no.nav.hm.grunndata.importapi.adminstate

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import org.slf4j.LoggerFactory
import java.util.*

@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@Controller(AdminStateController.API_V1_ADMINSTATE)
@SecurityRequirement(name = "bearer-auth")
class AdminStateController(private val productAdminStateRepository: ProductAdminStateRepository) {

    companion object {
        const val API_V1_ADMINSTATE = "/api/v1/adminstate"
        private val LOG = LoggerFactory.getLogger(AdminStateController::class.java)
    }

    @Get("/{supplierId}/{supplierRef}")
    suspend fun getBySupplierIdAndSupplierRef(supplierId: UUID, supplierRef: String): ProductAdminStateDTO?
    = productAdminStateRepository.findBySupplierIdAndSupplierRef(supplierId, supplierRef)?.toDTO()

}