package no.nav.hm.grunndata.importapi.security

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import org.slf4j.LoggerFactory
import java.util.UUID

@Controller(TokenApiController.TOKEN_ENDPOINT)
@Secured(Roles.ROLE_ADMIN)
class TokenApiController(private val tokenService: TokenService,
                         private val supplierService: SupplierService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(TokenApiController::class.java)
        const val TOKEN_ENDPOINT = "/internal/token"
    }

    @Post("/{supplierId}")
    suspend fun createSupplierToken(supplierId: UUID): String {
        LOG.info("Request for token for supplier $supplierId")
        val supplier = supplierService.findById(supplierId)
        return tokenService.token(supplier!!)
    }


    @Post("/admin/{subject}")
    suspend  fun createAdminToken(subject: String): String {
        LOG.info("Request for token for admin")
        return tokenService.adminToken(subject)
    }

}