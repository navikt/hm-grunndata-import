package no.nav.hm.grunndata.importapi.transferstate

import com.fasterxml.jackson.databind.JsonNode
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
                      @Body json: Publisher<JsonNode>): Publisher<TransferStateResponseDTO>

    @Get(value="/{supplierId}/{id}")
    fun getTransferById(supplierId: UUID, id: UUID): TransferStateResponseDTO?

}
