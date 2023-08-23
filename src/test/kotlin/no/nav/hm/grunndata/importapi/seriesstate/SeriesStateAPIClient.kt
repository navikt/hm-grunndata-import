package no.nav.hm.grunndata.importapi.seriesstate

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import java.util.*


@Client(SeriesStateAPIController.SERIES_ENDPOINT)
interface SeriesStateAPIClient {

    @Post("/{supplierId}", processes = [MediaType.APPLICATION_JSON])
    fun createSeries(supplierId: UUID, @Body dto: SeriesStateDTO, @Header authorization: String): HttpResponse<SeriesStateDTO>

    @Put("/{supplierId}/{id}", processes = [MediaType.APPLICATION_JSON])
    fun updateSeries(supplierId: UUID, id: String, @Body dto: SeriesStateDTO, @Header authorization: String): HttpResponse<SeriesStateDTO>

    @Get("/{supplierId}")
    fun getSeriesBySupplierId(supplierId: UUID, @Header authorization: String): List<SeriesStateDTO>
}