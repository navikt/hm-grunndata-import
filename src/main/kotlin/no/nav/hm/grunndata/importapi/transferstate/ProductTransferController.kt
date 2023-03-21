package no.nav.hm.grunndata.importapi.transferstate

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SupplierAllowed
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import java.util.UUID

@SupplierAllowed(value = [Roles.ROLE_SUPPLIER, Roles.ROLE_ADMIN])
@Controller("/api/v1/transfers")
@SecurityRequirement(name = "bearer-auth")
class ProductTransferController(private val supplierService: SupplierService,
                                private val objectMapper: ObjectMapper) {


    @Post(value = "/{supplierId}", processes = [MediaType.APPLICATION_JSON_STREAM])
    fun productStream(@PathVariable supplierId: UUID, @Body json: Flow<JsonNode>): Flow<TransferResponse> =
        json.map {
            val product = objectMapper.treeToValue(it, ProductTransferDTO::class.java)
            TransferResponse()
        }

}
