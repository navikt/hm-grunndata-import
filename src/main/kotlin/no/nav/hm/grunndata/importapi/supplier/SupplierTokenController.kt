package no.nav.hm.grunndata.importapi.supplier

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.token.TokenService
import no.nav.hm.grunndata.importapi.supplier.SupplierTokenController.Companion.API_V1_TOKEN
import org.slf4j.LoggerFactory
import java.util.*


@SecuritySupplierRule(value = [Roles.ROLE_ADMIN])
@Controller(API_V1_TOKEN)
@SecurityRequirement(name = "bearer-auth")
class SupplierTokenController(private val supplierService: SupplierService,
                              private val tokenService: TokenService
) {

    companion object {
        const val API_V1_TOKEN = "/api/v1/admin/token"
        private val LOG = LoggerFactory.getLogger(SupplierTokenController::class.java)
    }

    @Post("/{supplierId}")
    suspend fun createNewTokenForSupplier(supplierId: UUID): HttpResponse<TokenResponseDTO> {
        LOG.info("admin request creating token for supplier $supplierId")
        return supplierService.findById(supplierId)?.let {
            val token = tokenService.token(it)
            HttpResponse.created(it.toTokenResponseDTO(token))
        } ?: HttpResponse.notFound()
    }

}
