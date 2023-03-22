package no.nav.hm.grunndata.importapi.transferstate

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecurityRule
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.toMD5Hex
import no.nav.hm.grunndata.importapi.transferstate.ProductTransferController.Companion.API_V1_TRANSFERS
import org.slf4j.LoggerFactory
import java.util.UUID

@SecurityRule(value = [Roles.ROLE_SUPPLIER, Roles.ROLE_ADMIN])
@Controller(API_V1_TRANSFERS)
@SecurityRequirement(name = "bearer-auth")
class ProductTransferController(private val supplierService: SupplierService,
                                private val transferStateRepository: TransferStateRepository,
                                private val objectMapper: ObjectMapper) {


    companion object {
        const val API_V1_TRANSFERS = "/api/v1/transfers"
        private val LOG = LoggerFactory.getLogger(ProductTransferController::class.java)

    }

    @Get(value="/{supplierId}/{id}")
    suspend fun getTransferById(supplierId: UUID, id: UUID): TransferStateDTO? =
        transferStateRepository.findById(id)?.toDTO()

    @Post(value = "/{supplierId}", processes = [MediaType.APPLICATION_JSON_STREAM])
    fun productStream(@PathVariable supplierId: UUID, @Body json: Flow<JsonNode>): Flow<TransferResponse> =
        json.map {
            LOG.info("Got product stream from $supplierId")
            val product = objectMapper.treeToValue(it, ProductTransferDTO::class.java)
            val content = objectMapper.writeValueAsString(product)
            val md5 = content.toMD5Hex()
            TransferResponse(id = product.id, supplierId = supplierId,
                supplierRef =  product.supplierRef, md5 = md5)
        }

}
