package no.nav.hm.grunndata.importapi.seriesstate

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.hm.grunndata.importapi.BadRequestException
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*


@Controller(SeriesStateAPIController.SERIES_ENDPOINT)
@SecuritySupplierRule(Roles.ROLE_SUPPLIER)
@SecurityRequirement(name = "bearer-auth")
class SeriesStateAPIController(private val seriesStateService: SeriesStateService) {


    companion object {
        private val LOG = LoggerFactory.getLogger(SeriesStateAPIController::class.java)
        const val SERIES_ENDPOINT = "/api/v1/series"
    }

    @Get("/{supplierId}")
    suspend fun getSeriesBySupplierId(supplierId: UUID): List<SeriesStateDTO> = seriesStateService.findBySupplierId(supplierId)


    @Post("/{supplierId}")
    suspend fun createSeries(supplierId: UUID, @Body dto: SeriesTransferDTO): HttpResponse<SeriesStateDTO> =
        seriesStateService.findBySupplierIdAndName(supplierId, dto.name)?.let {
            throw BadRequestException("series name ${dto.name} already exist with this id ${it.id}")
        } ?: HttpResponse.created(seriesStateService.save( SeriesStateDTO(
            id = dto.id ?: UUID.randomUUID().toString(), name = dto.name, supplierId = supplierId
        )))

    @Put("/{supplierId}/{id}")
    fun updateSeries(supplierId: UUID, id: String, @Body dto: SeriesTransferDTO): HttpResponse<SeriesStateDTO> =
        seriesStateService.findByIdCacheable(id)?.let { inDb ->
            HttpResponse.ok(
                seriesStateService.update(
                    inDb.copy(name = dto.name, status = dto.status, updated = LocalDateTime.now())
                )
            )
        }?: throw BadRequestException("series $id does not exist")

}