package no.nav.hm.grunndata.importapi.transfer.product

import com.fasterxml.jackson.databind.JsonNode
import io.micronaut.data.model.Page
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

import org.reactivestreams.Publisher
import java.util.*

@Client("\${micronaut.server.context-path}${ProductTransferAPIController.API_V1_PRODUCT_TRANSFERS}")
interface ProductTransferClient {

    @Post(value = "/{identifier}", processes = [MediaType.APPLICATION_JSON_STREAM])
    fun productStream(@PathVariable identifier: String,
                      @Header authorization: String,
                      @Body json: Publisher<JsonNode>): Publisher<ProductTransferResponse>

    @Get(value="/{identifier}/{supplierRef}")
    fun getTransfersBySupplierIdSupplierRef(@Header authorization: String, identifier: String, supplierRef: String):
            Page<ProductTransferResponse>

    @Get(value="/{identifier}/transfer/{transferId}")
    fun getTransferBySupplierIdAndTransferId(@Header authorization: String, identifier: String, transferId: UUID): ProductTransferResponse?

    @Delete("/{identifier}/{supplierRef}")
    fun deleteProduct(@Header authorization: String, identifier: String, supplierRef: String, @QueryValue delete: Boolean = false): HttpResponse<ProductTransferResponse>

}
