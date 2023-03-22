package no.nav.hm.grunndata.importapi.transferstate

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecurityRule
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.toMD5Hex
import no.nav.hm.grunndata.importapi.transferstate.ProductTransferController.Companion.API_V1_TRANSFERS
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
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
    fun productStream(@PathVariable supplierId: UUID, @Body json: Publisher<JsonNode>): Publisher<TransferStateDTO> =
        json.asFlow().map {
            LOG.info("Got product stream from $supplierId")
            val transfer = objectMapper.treeToValue(it, ProductTransferDTO::class.java)
            val content = objectMapper.writeValueAsString(transfer)
            val md5 = content.toMD5Hex()
            transferStateRepository.findOneBySupplierIdAndSupplierRef(supplierId, transfer.supplierRef)?.let {
                    state -> transferStateRepository.save(state.copy(transferId = UUID.randomUUID(),
                        created = LocalDateTime.now(), md5 = md5, json_payload = transfer,
                transferStatus = TransferStatus.DONE))
                .toDTO()
            } ?: transferStateRepository.save(TransferState(productId = UUID.randomUUID(), supplierId = supplierId,
                supplierRef = transfer.supplierRef, md5 = md5, json_payload = transfer)).toDTO()
        }.asPublisher()

}
