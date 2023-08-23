package no.nav.hm.grunndata.importapi.transfer.product

import com.fasterxml.jackson.databind.JsonNode
import io.micronaut.data.model.Page
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

import org.reactivestreams.Publisher
import java.util.*

@Client(ProductTransferAPIController.API_V1_TRANSFERS)
interface ProductTransferClient {

    @Post(value = "/{supplierId}", processes = [MediaType.APPLICATION_JSON_STREAM])
    fun productStream(@PathVariable supplierId: UUID,
                      @Header authorization: String,
                      @Body json: Publisher<JsonNode>): Publisher<TransferResponseDTO>

    @Get(value="/{supplierId}/{supplierRef}")
    fun getTransfersBySupplierIdSupplierRef(@Header authorization: String, supplierId: UUID, supplierRef: String):
            Page<TransferResponseDTO>
    @Delete("/{supplierId}/{supplierRef}")
    fun deleteProduct(@Header authorization: String, supplierId: UUID, supplierRef: String): HttpResponse<TransferResponseDTO>

}
