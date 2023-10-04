package no.nav.hm.grunndata.importapi.security

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.supplier.TokenResponseDTO
import no.nav.hm.grunndata.importapi.supplier.toTokenResponseDTO
import org.slf4j.LoggerFactory
import java.util.UUID

@Controller(TokenApiController.TOKEN_ENDPOINT)
@Secured(Roles.ROLE_ADMIN)
@SecurityRequirement(name = "bearer-auth")
@Hidden
class TokenApiController(private val tokenService: TokenService,
                         private val supplierService: SupplierService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(TokenApiController::class.java)
        const val TOKEN_ENDPOINT = "/internal/token"
    }

    @Post("/{supplierId}")
    suspend fun createSupplierToken(supplierId: UUID): HttpResponse<TokenResponseDTO> {
        LOG.info("Request for token for supplier $supplierId")
        return supplierService.findById(supplierId)?.let {
            HttpResponse.ok(it.toTokenResponseDTO(tokenService.token(it)))
        } ?: HttpResponse.notFound()
    }


    @Post("/admin/{subject}")
    suspend  fun createAdminToken(subject: String): HttpResponse<TokenResponseDTO> {
        LOG.info("Request for token for admin")
        return HttpResponse.ok(TokenResponseDTO(name = subject, token = tokenService.adminToken(subject)))
    }

}