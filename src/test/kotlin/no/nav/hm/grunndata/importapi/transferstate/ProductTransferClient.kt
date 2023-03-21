package no.nav.hm.grunndata.importapi.transferstate

import com.fasterxml.jackson.databind.JsonNode
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import kotlinx.coroutines.flow.Flow
import java.util.*

@Client(ProductTransferController.API_V1_TRANSFERS)
interface ProductTransferClient {

    @Post(value = "/{supplierId}", processes = [MediaType.APPLICATION_JSON_STREAM])
    fun productStream(@PathVariable supplierId: UUID,
                      @Header(name="Authorization") bearerToken: String,
                      @Body json: Flow<JsonNode>): Flow<TransferResponse>

}
