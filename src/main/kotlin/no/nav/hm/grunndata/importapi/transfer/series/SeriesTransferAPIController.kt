package no.nav.hm.grunndata.importapi.transfer.series

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
import no.nav.hm.grunndata.importapi.ImportErrorException
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.toMD5Hex
import no.nav.hm.grunndata.importapi.transfer.series.SeriesTransferAPIController.Companion.API_V1_SERIES_TRANSFERS
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.UUID

@Secured(Roles.ROLE_SUPPLIER)
@Controller(API_V1_SERIES_TRANSFERS)
@SecurityRequirement(name = "bearer-auth")
class SeriesTransferAPIController(private val seriesTransferRepository: SeriesTransferRepository,
                                  private val objectMapper: ObjectMapper) {


    companion object {
        const val API_V1_SERIES_TRANSFERS = "/api/v1/series/transfers"
        private val LOG = LoggerFactory.getLogger(SeriesTransferAPIController::class.java)

    }

    @Get(value="/{supplierId}/{seriesId}")
    suspend fun getTransfersBySupplierIdSeriesID(supplierId: UUID, seriesId: String): Page<SeriesTransferResponse> =
        seriesTransferRepository.findBySupplierIdAndSeriesId(supplierId, seriesId).map {
            it.toResponse()
        }

    @Get(value="/{supplierId}/transferId/{transferId}")
    suspend fun getTransfersBySupplierIdAndTransferId(supplierId: UUID, transferId: UUID): SeriesTransferResponse? =
        seriesTransferRepository.findBySupplierIdAndTransferId(supplierId, transferId)?.toResponse()

    @Post(value = "/{supplierId}", processes = [MediaType.APPLICATION_JSON_STREAM])
    suspend fun productStream(@PathVariable supplierId: UUID, @Body jsonNode: Publisher<JsonNode>): Publisher<SeriesTransferResponse> =
        jsonNode.asFlow().map { json ->
            val md5 = objectMapper.writeValueAsString(json).toMD5Hex()
            val transfer = objectMapper.treeToValue(json, SeriesTransferDTO::class.java)
            LOG.info("Got product stream from $supplierId with name ${transfer.name}")
            seriesTransferRepository.findBySupplierIdAndMd5(supplierId, md5)?.let { identical ->
                LOG.info("Identical series ${identical.md5} with previous transfer ${identical.transferId} and name: ${transfer.name}")
                identical.toResponse()
            } ?: run {
                createTransferState(supplierId, transfer, md5)
            }
        }.asPublisher()



    private suspend fun createTransferState(supplierId: UUID,
                                            seriesTransferDTO: SeriesTransferDTO,
                                            md5: String) =
        seriesTransferRepository.save(
            SeriesTransfer(
                seriesId = seriesTransferDTO.id ?: UUID.randomUUID().toString(), supplierId = supplierId,
                json_payload = seriesTransferDTO, md5 = md5
            )
        ).toResponse()



}
