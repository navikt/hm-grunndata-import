package no.nav.hm.grunndata.importapi.transfer.series

import com.fasterxml.jackson.databind.JsonNode
import io.micronaut.data.model.Page
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import org.reactivestreams.Publisher
import java.util.*

@Client("\${micronaut.server.context-path}${SeriesTransferAPIController.API_V1_SERIES_TRANSFERS}")
interface SeriesTransferClient {
    
    @Post(value = "/{identifier}", processes = [MediaType.APPLICATION_JSON_STREAM])
    fun seriesStream(@PathVariable identifier: String, @Header authorization: String,
                     @Body json: Publisher<JsonNode>): Publisher<SeriesTransferResponse>

    @Get(value="/{identifier}/{seriesId}")
    fun getSeriesTransferBySeriesId(@Header authorization: String, identifier: String, seriesId: UUID): Page<SeriesTransferResponse>


}