package no.nav.hm.grunndata.importapi.transfer.series

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.toMD5Hex
import no.nav.hm.grunndata.importapi.transfer.series.SeriesTransferAPIController.Companion.API_V1_SERIES_TRANSFERS
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.util.UUID

@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_SERIES_TRANSFERS)
@SecurityRequirement(name = "bearer-auth")
class SeriesTransferAPIController(private val seriesTransferRepository: SeriesTransferRepository,
                                  private val objectMapper: ObjectMapper) {


    companion object {
        const val API_V1_SERIES_TRANSFERS = "/api/v1/series/transfers"
        private val LOG = LoggerFactory.getLogger(SeriesTransferAPIController::class.java)

    }

    @Get(value="/{supplierId}/{seriesId}")
    suspend fun getTransfersBySupplierIdAndSupplierSeriesRef(supplierId: UUID, seriesId: UUID, pageable: Pageable): Page<SeriesTransferResponse> =
        seriesTransferRepository.findBySupplierIdAndSeriesId(supplierId, seriesId, pageable).map { it.toResponse() }

    @Post(value = "/{supplierId}", processes = [MediaType.APPLICATION_JSON_STREAM])
    suspend fun productStream(@PathVariable supplierId: UUID, @Body series: Publisher<SeriesTransferDTO>): Publisher<SeriesTransferResponse> =
        series.asFlow().map { s ->
            val md5 = objectMapper.writeValueAsString(s).toMD5Hex()
            LOG.info("Got product stream from $supplierId with name ${s.name}")
            seriesTransferRepository.findBySupplierIdAndMd5(supplierId, md5)?.let { identical ->
                LOG.info("Identical series ${identical.md5} with previous transfer ${identical.transferId} and name: ${s.name}")
                identical.toResponse()
            } ?: run {
                createTransferState(supplierId, s, md5)
            }
        }.asPublisher()



    private suspend fun createTransferState(supplierId: UUID,
                                            seriesTransferDTO: SeriesTransferDTO,
                                            md5: String) =
        seriesTransferRepository.save(
            SeriesTransfer(
                seriesId = seriesTransferDTO.seriesId,
                supplierId = supplierId,
                json_payload = seriesTransferDTO,
                md5 = md5
            )
        ).toResponse()

}
