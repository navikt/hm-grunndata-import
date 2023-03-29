package no.nav.hm.grunndata.importapi.supplier

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecurityRule
import no.nav.hm.grunndata.importapi.security.TokenService
import no.nav.hm.grunndata.importapi.supplier.SupplierTokenController.Companion.API_V1_TOKEN
import org.slf4j.LoggerFactory
import java.util.*


@SecurityRule(value = [Roles.ROLE_ADMIN])
@Controller(API_V1_TOKEN)
class SupplierTokenController(private val supplierService: SupplierService,
                              private val tokenService: TokenService) {

    companion object {
        const val API_V1_TOKEN = "/api/v1/admin/token"
        private val LOG = LoggerFactory.getLogger(SupplierTokenController::class.java)
    }

    @Post("/{supplierId}")
    suspend fun createNewTokenForSupplier(supplierId: UUID): HttpResponse<TokenResponseDTO> {
        return supplierService.findById(supplierId)?.let {
            val token = tokenService.token(it)
            HttpResponse.created(it.toTokenResponseDTO(token))
        } ?: HttpResponse.notFound()
    }

}
