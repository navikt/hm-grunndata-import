package no.nav.hm.grunndata.importapi.transfer.series

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import no.nav.hm.grunndata.importapi.error.ErrorType
import no.nav.hm.grunndata.importapi.error.ImportApiError
import no.nav.hm.grunndata.importapi.iso.IsoCategoryService
import no.nav.hm.grunndata.importapi.openapi.OpenApiPageable
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.security.supplierId
import no.nav.hm.grunndata.importapi.toMD5Hex
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.importapi.transfer.series.SeriesTransferAPIController.Companion.API_V1_SERIES_TRANSFERS
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_SERIES_TRANSFERS)
@SecurityRequirement(name = "bearer-auth")
class SeriesTransferAPIController(private val seriesTransferRepository: SeriesTransferRepository,
                                  private val isoCategoryService: IsoCategoryService,
                                  private val objectMapper: ObjectMapper) {

    companion object {
        private val lOG = LoggerFactory.getLogger(SeriesTransferAPIController::class.java)
        const val API_V1_SERIES_TRANSFERS = "/api/v1/series/transfers"
    }


    @Get(value = "/{identifier}/{seriesId}")
    suspend fun getSeriesTransferBySeriesId(
        identifier: String, authentication: Authentication, seriesId: UUID,
        pageable: Pageable
    ): Page<SeriesTransferResponse> =
        seriesTransferRepository.findBySupplierIdAndSeriesId(authentication.supplierId(), seriesId, pageable).map {
            it.toResponse()
        }

    @Post(value="/{identifier}", processes = [MediaType.APPLICATION_JSON_STREAM])
    suspend fun seriesStream(
        identifier: String, authentication: Authentication,
        @Body transfers: Publisher<SeriesTransferDTO>
    ): Publisher<SeriesTransferResponse> =
        transfers.asFlow().map { transfer ->
            val md5 = objectMapper.writeValueAsString(transfer).toMD5Hex()
            val supplierId = authentication.supplierId()
            lOG.info("Got series stream from $identifier supplierId: $supplierId with seriesId: ${transfer.seriesId}")
            seriesTransferRepository.findBySupplierIdAndMd5(supplierId, md5)?.let { identical ->
                lOG.info("Series with id ${transfer.seriesId} already exists")
                identical.toResponse()
            }?: run {
                validate(transfer)
                createTransferState(transfer, supplierId, md5)
            }
        }.asPublisher()

    private suspend fun createTransferState(transfer: SeriesTransferDTO, supplierId: UUID, md5: String) =
        seriesTransferRepository.save(
            SeriesTransfer(
                supplierId = supplierId,
                seriesId = transfer.seriesId,
                json_payload = transfer,
                md5 = md5,
                transferStatus = TransferStatus.RECEIVED,
                created = LocalDateTime.now(),
                updated = LocalDateTime.now()
            )
        ).toResponse()

    private fun validate(transfer: SeriesTransferDTO) {
        if (isoCategoryService.lookUpCode(transfer.isoCategory) == null) {
            throw ImportApiError("Isocategory: ${transfer.isoCategory} does not exists", ErrorType.INVALID_VALUE)
        }
    }
}
