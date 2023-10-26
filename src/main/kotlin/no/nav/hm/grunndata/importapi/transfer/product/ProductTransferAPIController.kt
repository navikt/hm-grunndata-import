package no.nav.hm.grunndata.importapi.transfer.product

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import no.nav.hm.grunndata.importapi.error.ErrorType
import no.nav.hm.grunndata.importapi.error.ImportApiError
import no.nav.hm.grunndata.importapi.iso.IsoCategoryService
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.techdata.TechDataLabelService
import no.nav.hm.grunndata.importapi.toMD5Hex
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransferAPIController.Companion.API_V1_PRODUCT_TRANSFERS
import no.nav.hm.grunndata.rapid.dto.ProductStatus
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.UUID

@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_PRODUCT_TRANSFERS)
@SecurityRequirement(name = "bearer-auth")
class ProductTransferAPIController(private val productTransferRepository: ProductTransferRepository,
                                   private val techDataLabelService: TechDataLabelService,
                                   private val objectMapper: ObjectMapper,
                                   private val isoCategoryService: IsoCategoryService) {


    companion object {
        const val API_V1_PRODUCT_TRANSFERS = "/api/v1/products/transfers"
        private val LOG = LoggerFactory.getLogger(ProductTransferAPIController::class.java)

    }

    @Get(value="/{supplierId}/{supplierRef}")
    suspend fun getTransfersBySupplierIdSupplierRef(supplierId: UUID, supplierRef: String, pageable: Pageable): Page<ProductTransferResponse> =
        productTransferRepository.findBySupplierIdAndSupplierRef(supplierId, supplierRef,pageable).map {
            it.toResponseDTO()
        }

    @Get(value="/{supplierId}/transfer/{transferId}")
    suspend fun getTransfersBySupplierIdAndTransferId(supplierId: UUID, transferId: UUID): ProductTransferResponse? =
        productTransferRepository.findBySupplierIdAndTransferId(supplierId, transferId)?.toResponseDTO()

    @Post(value = "/{supplierId}", processes = [MediaType.APPLICATION_JSON_STREAM])
    suspend fun productStream(@PathVariable supplierId: UUID, @Body transfers: Publisher<ProductTransferDTO>): Publisher<ProductTransferResponse> =
        transfers.asFlow().map { transfer ->
            val md5 = objectMapper.writeValueAsString(transfer).toMD5Hex()
            LOG.info("Got product stream from $supplierId with supplierRef: ${transfer.supplierRef}")
            productTransferRepository.findBySupplierIdAndMd5(supplierId, md5)?.let { identical ->
                LOG.info("Identical product ${identical.md5} with previous transfer ${identical.transferId}")
                identical.toResponseDTO()
            } ?: run {
                validate(transfer)
                createTransferState(supplierId, transfer, md5)
            }
        }.asPublisher()

    private fun validate(transfer: ProductTransferDTO) {
        if (transfer.techData.isNotEmpty()) {
            transfer.techData.forEach {
                val label = techDataLabelService.fetchTechDataLabelByKeyName(it.key)
                if (label == null ||  label.unit != it.unit)
                    throw ImportApiError("Wrong techlabel key ${it.key} and unit: ${it.unit}", ErrorType.INVALID_VALUE)
            }
        }
        if ((transfer.accessory || transfer.sparePart) && transfer.compatibleWith == null) {
            throw ImportApiError("It is accessory or sparePart, must set compatibleWidth", ErrorType.MISSING_PARAMETER)
        }

        if (isoCategoryService.lookUpCode(transfer.isoCategory) == null) {
            throw ImportApiError("Isocategory ${transfer.isoCategory} does not exist", ErrorType.INVALID_VALUE)
        }
    }

    @Delete("/{supplierId}/{supplierRef}")
    suspend fun delete(supplierId: UUID, supplierRef: String, @QueryValue(defaultValue = "false") delete: Boolean): HttpResponse<ProductTransferResponse>  {
        LOG.info("delete has been called for $supplierId and $supplierRef")
        return productTransferRepository.findOneBySupplierIdAndSupplierRefOrderByCreatedDesc(supplierId, supplierRef)?.let {
            val expiredPayload = it.json_payload.copy(expired = LocalDateTime.now().minusMinutes(1), status = ProductStatus.DELETED)
            val md5 = objectMapper.writeValueAsString(expiredPayload).toMD5Hex()
            val action = if (delete) "Deleted" else "Deactivated"
            val expiredState = it.copy(
                transferId = UUID.randomUUID(), json_payload = expiredPayload,
                message = "${action} by supplier", md5 = md5
            )
            HttpResponse.created(productTransferRepository.save(expiredState).toResponseDTO())
        } ?: HttpResponse.notFound()
    }

    private suspend fun createTransferState(supplierId: UUID,
                                            productTransfer: ProductTransferDTO,
                                            md5: String) =
        productTransferRepository.save(
            ProductTransfer(
                supplierId = supplierId, supplierRef = productTransfer.supplierRef, md5 = md5,
                json_payload = productTransfer
            )
        ).toResponseDTO()



}
