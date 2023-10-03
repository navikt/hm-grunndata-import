package no.nav.hm.grunndata.importapi.transfer.series

import com.fasterxml.jackson.databind.JsonNode
import io.micronaut.data.model.Page
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

import org.reactivestreams.Publisher
import java.util.*

@Client(SeriesTransferAPIController.API_V1_SERIES_TRANSFERS)
interface SeriesTransferClient {

    @Post(value = "/{supplierId}", processes = [MediaType.APPLICATION_JSON_STREAM])
    fun seriesStream(@PathVariable supplierId: UUID,
                      @Header authorization: String,
                      @Body dto: Publisher<SeriesTransferDTO>): Publisher<SeriesTransferResponse>

    @Get(value="/{supplierId}/{supplierSeriesRef}")
    fun getTransfersBySupplierIdAndSupplierSeriesRef(@Header authorization: String, supplierId: UUID, supplierSeriesRef: String):
            Page<SeriesTransferResponse>

}
