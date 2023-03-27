package no.nav.hm.grunndata.importapi.transferstate

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.data.model.Page
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import no.nav.hm.grunndata.importapi.productstate.ProductStateRepository
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecurityRule
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.toMD5Hex
import no.nav.hm.grunndata.importapi.transferstate.ProductTransferAPIController.Companion.API_V1_TRANSFERS
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.UUID

@SecurityRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_TRANSFERS)
@SecurityRequirement(name = "bearer-auth")
class ProductTransferAPIController(private val transferStateRepository: TransferStateRepository,
                                   private val objectMapper: ObjectMapper) {


    companion object {
        const val API_V1_TRANSFERS = "/api/v1/transfers"
        private val LOG = LoggerFactory.getLogger(ProductTransferAPIController::class.java)

    }

    @Get(value="/{supplierId}/{supplierRef}")
    suspend fun getTransfersBySupplierIdSupplierRef(supplierId: UUID, supplierRef: String): Page<TransferStateResponseDTO> =
        transferStateRepository.findBySupplierIdAndSupplierRef(supplierId, supplierRef).map {
            it.toResponseDTO()
        }

    @Post(value = "/{supplierId}", processes = [MediaType.APPLICATION_JSON_STREAM])
    suspend fun productStream(@PathVariable supplierId: UUID, @Body jsonNode: Publisher<JsonNode>): Publisher<TransferStateResponseDTO> =
        jsonNode.asFlow().map { json ->
            val content = objectMapper.writeValueAsString(json)
            val md5 = content.toMD5Hex()
            val transfer = objectMapper.treeToValue(json, ProductTransferDTO::class.java)
            LOG.info("Got product stream from $supplierId and transferId: ${transfer.transferId}")
            transferStateRepository.findBySupplierIdAndMd5(supplierId, md5)?.let { identical ->
                LOG.info("Identical product ${identical.md5} with previous transfer ${identical.transferId}")
                identical.toResponseDTO()
            } ?: createtransferState(supplierId, transfer, md5)
        }.asPublisher()

    @Delete("/{supplierId}/{supplierRef}")
    suspend fun delete(supplierId: UUID, supplierRef: String): HttpResponse<TransferStateResponseDTO>? =
        transferStateRepository.findOneBySupplierIdAndSupplierRefOrderByCreatedDesc(supplierId, supplierRef)?.let {
            val expiredPayload = it.json_payload.copy(expired = LocalDateTime.now().minusMinutes(1))
            val expiredState = it.copy(transferId = UUID.randomUUID(), json_payload = expiredPayload)
            HttpResponse.created(transferStateRepository.save(expiredState).toResponseDTO())
        } ?: HttpResponse.notFound()

    private suspend fun createtransferState(supplierId: UUID,
                                         transfer: ProductTransferDTO,
                                         md5: String) =
        transferStateRepository.findOneBySupplierIdAndSupplierRefOrderByCreatedDesc(supplierId, transfer.supplierRef)?.let { state ->
            transferStateRepository.save(
                state.copy(
                    transferId = UUID.randomUUID(),
                    created = LocalDateTime.now(), md5 = md5, json_payload = transfer,
                    transferStatus = TransferStatus.RECEIVED
                )
            )
                .toResponseDTO()
        } ?: transferStateRepository.save(
            TransferState(
                productId = UUID.randomUUID(), supplierId = supplierId,
                supplierRef = transfer.supplierRef, md5 = md5, json_payload = transfer
            )
        ).toResponseDTO()



}
