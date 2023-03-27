package no.nav.hm.grunndata.importapi.transferstate

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
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
                                private val productStateRepository: ProductStateRepository,
                                private val objectMapper: ObjectMapper) {


    companion object {
        const val API_V1_TRANSFERS = "/api/v1/transfers"
        private val LOG = LoggerFactory.getLogger(ProductTransferController::class.java)

    }

    @Get(value="/{supplierId}/{supplierRef}")
    suspend fun getTransferById(supplierId: UUID, id: UUID): TransferStateDTO? =
        transferStateRepository.findById(id)?.toDTO()

    @Post(value = "/{supplierId}", processes = [MediaType.APPLICATION_JSON_STREAM])
    suspend fun productStream(@PathVariable supplierId: UUID, @Body jsonNode: Publisher<JsonNode>): Publisher<TransferStateDTO> =
        jsonNode.asFlow().map { json ->
            val content = objectMapper.writeValueAsString(json)
            val md5 = content.toMD5Hex()
            val transfer = objectMapper.treeToValue(json, ProductTransferDTO::class.java)
            LOG.info("Got product stream from $supplierId and transferId: ${transfer.transferId}")
            transferStateRepository.findBySupplierIdAndMd5(supplierId, md5)?.let { identical ->
                LOG.info("Identical product ${identical.md5} with previous transfer ${identical.transferId}")
                identical.toDTO()
            } ?: createtransferState(supplierId, transfer, md5)
        }.asPublisher()

    private suspend fun createtransferState(supplierId: UUID,
                                         transfer: ProductTransferDTO,
                                         md5: String) =
        transferStateRepository.findOneBySupplierIdAndSupplierRef(supplierId, transfer.supplierRef)?.let { state ->
            transferStateRepository.save(
                state.copy(
                    transferId = UUID.randomUUID(),
                    created = LocalDateTime.now(), md5 = md5, json_payload = transfer,
                    transferStatus = TransferStatus.RECEIVED
                )
            )
                .toDTO()
        } ?: transferStateRepository.save(
            TransferState(
                productId = UUID.randomUUID(), supplierId = supplierId,
                supplierRef = transfer.supplierRef, md5 = md5, json_payload = transfer
            )
        ).toDTO()



}
