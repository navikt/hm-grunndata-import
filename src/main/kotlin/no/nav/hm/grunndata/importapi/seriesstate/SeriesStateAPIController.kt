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
class SeriesStateAPIController(private val seriesStateRepository: SeriesStateRepository) {


    companion object {
        private val LOG = LoggerFactory.getLogger(SeriesStateAPIController::class.java)
        const val SERIES_ENDPOINT = "/api/v1/series"
    }

    @Get("/{supplierId}")
    suspend fun getSeriesBySupplierId(supplierId: UUID): List<SeriesStateDTO> = seriesStateRepository
        .findBySupplierId(supplierId).map { it.toDTO() }

    @Post("/{supplierId}")
    suspend fun createSeries(supplierId: UUID, @Body dto: SeriesStateDTO): HttpResponse<SeriesStateDTO> =
        seriesStateRepository.findBySupplierIdAndName(supplierId, dto.name)?.let {
            throw BadRequestException("series already exist, please use this id ${it.id}")
        } ?: HttpResponse.created(seriesStateRepository.save(dto.toEntity()).toDTO())

    @Put("/{supplierId}/{id}")
    suspend fun updateSeries(supplierId: UUID, id: String, @Body dto: SeriesStateDTO): HttpResponse<SeriesStateDTO> =
        seriesStateRepository.findById(id)?.let { inDb ->
            HttpResponse.ok(
                seriesStateRepository.update(
                    inDb.copy(name = dto.name, status = dto.status, updated = LocalDateTime.now())
                ).toDTO()
            )
        }?: throw BadRequestException("series $id does not exist")


}