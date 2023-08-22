package no.nav.hm.grunndata.importapi.transfer.product

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.data.model.Page
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecurityRule
import no.nav.hm.grunndata.importapi.toMD5Hex
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransferAPIController.Companion.API_V1_TRANSFERS
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.UUID

@Secured(Roles.ROLE_SUPPLIER)
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
            val md5 = objectMapper.writeValueAsString(json).toMD5Hex()
            val transfer = objectMapper.treeToValue(json, ProductTransferDTO::class.java)
            LOG.info("Got product stream from $supplierId with supplierRef: ${transfer.supplierRef}")
            transferStateRepository.findBySupplierIdAndMd5(supplierId, md5)?.let { identical ->
                LOG.info("Identical product ${identical.md5} with previous transfer ${identical.transferId}")
                identical.toResponseDTO()
            } ?: createTransferState(supplierId, transfer, md5)
        }.asPublisher()

    @Delete("/{supplierId}/{supplierRef}")
    suspend fun delete(supplierId: UUID, supplierRef: String): HttpResponse<TransferStateResponseDTO>  {
        LOG.info("delete has been called for $supplierId and $supplierRef")
        return transferStateRepository.findOneBySupplierIdAndSupplierRefOrderByCreatedDesc(supplierId, supplierRef)?.let {
            val expiredPayload = it.json_payload.copy(expired = LocalDateTime.now().minusMinutes(1))
            val md5 = objectMapper.writeValueAsString(expiredPayload).toMD5Hex()
            val expiredState = it.copy(
                transferId = UUID.randomUUID(), json_payload = expiredPayload,
                message = "deleted by supplier", md5 = md5
            )
            HttpResponse.created(transferStateRepository.save(expiredState).toResponseDTO())
        } ?: HttpResponse.notFound()
    }

    private suspend fun createTransferState(supplierId: UUID,
                                            productTransfer: ProductTransferDTO,
                                            md5: String) =
        transferStateRepository.save(
            TransferState(
                supplierId = supplierId, supplierRef = productTransfer.supplierRef, md5 = md5,
                json_payload = productTransfer
            )
        ).toResponseDTO()



}
