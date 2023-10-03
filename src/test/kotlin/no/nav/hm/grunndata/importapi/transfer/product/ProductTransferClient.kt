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

    @Post(value = "/{supplierId}", processes = [MediaType.APPLICATION_JSON_STREAM])
    fun productStream(@PathVariable supplierId: UUID,
                      @Header authorization: String,
                      @Body json: Publisher<JsonNode>): Publisher<ProductTransferResponse>

    @Get(value="/{supplierId}/{supplierRef}")
    fun getTransfersBySupplierIdSupplierRef(@Header authorization: String, supplierId: UUID, supplierRef: String):
            Page<ProductTransferResponse>

    @Get(value="/{supplierId}/transferId/{transferId}")
    fun getTransferBySupplierIdAndTransferId(@Header authorization: String, supplierId: UUID, transferId: UUID): ProductTransferResponse?

    @Delete("/{supplierId}/{supplierRef}")
    fun deleteProduct(@Header authorization: String, supplierId: UUID, supplierRef: String): HttpResponse<ProductTransferResponse>

}
