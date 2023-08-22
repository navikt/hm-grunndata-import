package no.nav.hm.grunndata.importapi.productstate

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.hm.grunndata.importapi.productstate.ProductStateAPIController.Companion.API_V1_PRODUCTSTATE
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecurityRule
import org.slf4j.LoggerFactory
import java.util.*

@SecurityRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_PRODUCTSTATE)
@SecurityRequirement(name = "bearer-auth")
class ProductStateAPIController(private val productStateRepository: ProductStateRepository) {

    companion object {
        const val API_V1_PRODUCTSTATE = "/api/v1/products"
        private val LOG = LoggerFactory.getLogger(ProductStateAPIController::class.java)
    }

    @Get("/{supplierId}/{supplierRef}")
    suspend fun getProductStateBySupplierRef(supplierId: UUID, supplierRef: String): HttpResponse<ProductStateResponseDTO> =
        productStateRepository.findBySupplierIdAndSupplierRef(supplierId, supplierRef)?.let {
            HttpResponse.ok(it.toResponseDTO())
        } ?: HttpResponse.notFound()


}
