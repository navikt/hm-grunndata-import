package no.nav.hm.grunndata.importapi.transfer.media

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.UUID
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.security.supplierId
import no.nav.hm.grunndata.importapi.toMD5Hex
import no.nav.hm.grunndata.importapi.transfer.media.MediaMetaTransferController.Companion.API_V1_MEDIA_META_TRANSFERS
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory


@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_MEDIA_META_TRANSFERS)
@SecurityRequirement(name = "bearer-auth")
@Tag(name = "Media File Transfers")
class MediaMetaTransferController(private val mediaMetaTransferRepository: MediaMetaTransferRepository,
                                  private val objectMapper: ObjectMapper) {

    companion object {
        const val API_V1_MEDIA_META_TRANSFERS = "/api/v1/media/meta/transfers"
        private val LOG =  LoggerFactory.getLogger(MediaFileTransferAPIController::class.java)
    }

    @Get("/{identifier}/series/{seriesId}")
    suspend fun getMediaMetaTransferList(identifier: String, authentication: Authentication, seriesId: UUID
    ): HttpResponse<List<MediaMetaTransferResponse>> =
        HttpResponse.ok(
            mediaMetaTransferRepository.findBySupplierIdAndSeriesId(authentication.supplierId(), seriesId).map {
                it.toResponse()
            }
        )

    @Post(
        value = "/{identifier}/series/{seriesId}",
        processes = [MediaType.APPLICATION_JSON_STREAM]
    )
    suspend fun postMediaMetaTransfer(identifier: String, authentication: Authentication, seriesId: UUID,
                                      @Body transfers: Publisher<MediaMetaTransferDTO>): Publisher<MediaMetaTransferResponse> =
        transfers.asFlow().map { transfer ->
            val supplierId = authentication.supplierId()
            LOG.info("Received transfer from $supplierId")
            val md5 = objectMapper.writeValueAsString(transfer).toMD5Hex()
            mediaMetaTransferRepository.findBySupplierIdAndMd5(supplierId, md5)?.let { identical->
                LOG.info("Transfer already exists with previous transfer ${identical.transferId}")
                identical.toResponse()
            }?: run {
                validate(transfer)
                createTransferState(transfer, md5, supplierId)
            }
        }.asPublisher()

    private suspend fun createTransferState(transfer: MediaMetaTransferDTO, md5: String, supplierId: UUID): MediaMetaTransferResponse {
        return mediaMetaTransferRepository.save(
            MediaMetaTransfer(
                uri = transfer.uri,
                supplierId = supplierId,
                seriesId = transfer.seriesId,
                text = transfer.text,
                priority = transfer.priority,
                md5 = md5
            )
        ).toResponse()
    }

    private fun validate(transfer: MediaMetaTransferDTO) {
    }

}