package no.nav.hm.grunndata.importapi.transfer.series

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import no.nav.hm.grunndata.importapi.BadRequestException
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.seriesstate.SeriesStateDTO
import no.nav.hm.grunndata.importapi.seriesstate.SeriesStateService
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*


@Controller(SeriesStateAPIController.SERIES_ENDPOINT)
@SecuritySupplierRule(Roles.ROLE_SUPPLIER)
@SecurityRequirement(name = "bearer-auth")
class SeriesStateAPIController(private val seriesStateService: SeriesStateService,
                               private val objectMapper: ObjectMapper) {


    companion object {
        private val LOG = LoggerFactory.getLogger(SeriesStateAPIController::class.java)
        const val SERIES_ENDPOINT = "/api/v1/series/transfers"
    }

    @Get("/{supplierId}")
    suspend fun getSeriesBySupplierId(supplierId: UUID): List<SeriesStateDTO> = seriesStateService.findBySupplierId(supplierId)


    @Post(value = "/{supplierId}", processes = [MediaType.APPLICATION_JSON_STREAM])
    suspend fun createSeries(supplierId: UUID, @Body jsonNode: JsonNode): Publisher<SeriesTransferResponse> =
        jsonNode.asFlow().map { json ->
            val md5 =
        seriesStateService.findBySupplierIdAndName(supplierId, dto.name)?.let {
            throw BadRequestException("series name ${dto.name} already exist with this id ${it.id}")
        } ?: HttpResponse.created(seriesStateService.save(
            SeriesStateDTO(
            id = dto.id ?: UUID.randomUUID().toString(), name = dto.name, supplierId = supplierId
        )
        ))

    @Put("/{supplierId}/{id}")
    fun updateSeries(supplierId: UUID, id: String, @Body dto: JsonNode): HttpResponse<SeriesTransferResponse> =
        seriesStateService.findByIdCacheable(id)?.let { inDb ->
            HttpResponse.ok(
                seriesStateService.update(
                    inDb.copy(name = dto.name, status = dto.status, updated = LocalDateTime.now())
                )
            )
        }?: throw BadRequestException("series $id does not exist")

}