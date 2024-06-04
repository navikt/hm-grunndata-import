package no.nav.hm.grunndata.importapi.transfer.media

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.security.supplierId
import no.nav.hm.grunndata.importapi.transfer.media.MediaFileTransferAPIController.Companion.API_V1_MEDIA_TRANSFERS
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*
import no.nav.hm.grunndata.importapi.toMD5Hex

@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_MEDIA_TRANSFERS)
@SecurityRequirement(name = "bearer-auth")
@Tag(name = "Media File Transfers")
class MediaFileTransferAPIController(
    private val mediaUploadService: MediaUploadService,
    private val mediaFileTransferRepository: MediaFileTransferRepository,
    private val gdbApiClient: GdbApiClient
) {

    companion object {
        const val API_V1_MEDIA_TRANSFERS = "/api/v1/media/file/transfers"
        private val LOG = LoggerFactory.getLogger(MediaFileTransferAPIController::class.java)
    }


    @Get("/{identifier}/series/{seriesId}")
    suspend fun getMediaList(
        identifier: String,
        authentication: Authentication,
        seriesId: UUID
    ): HttpResponse<List<MediaFileTransferResponse>> =
        HttpResponse.ok(
            mediaFileTransferRepository.findBySupplierIdAndSeriesId(authentication.supplierId(), seriesId).let {
                mediaFileTransferRepository.findBySupplierIdAndSeriesId(authentication.supplierId(), seriesId)
                    .map { it.toResponse() }.toList()
            })


    @Post(
        value = "/{identifier}/series/{seriesId}",
        consumes = [io.micronaut.http.MediaType.MULTIPART_FORM_DATA],
        produces = [io.micronaut.http.MediaType.APPLICATION_JSON]
    )
    suspend fun uploadFiles(
        identifier: String, authentication: Authentication, seriesId: UUID,
        files: Publisher<CompletedFileUpload>
    ): HttpResponse<List<MediaFileTransferResponse>> {
        val supplierId = authentication.supplierId()
        LOG.info("Upload media files for supplier: $identifier id: $supplierId seriesId: $seriesId")
        return HttpResponse.created(files.asFlow().map {
            uploadMedia(it, seriesId, supplierId)
        }.toList()) ?: run {
            gdbApiClient.getSeriesById(seriesId)?.let { dto ->
            LOG.info("Supplier $supplierId and seriesId found in GDB ${dto.id} ")
            return HttpResponse.created(files.asFlow().map {
                uploadMedia(it, dto.id, supplierId)
        }.toList())
        } ?: run {
            LOG.info("Supplier $supplierId and seriesId not found in GDB")
            return HttpResponse.notFound()
        }
    }
}


private suspend fun uploadMedia(
    upload: CompletedFileUpload,
    seriesId: UUID,
    supplierId: UUID
): MediaFileTransferResponse {
    val transferId = UUID.randomUUID()
    LOG.info("Storing file name ${upload.name} size: ${upload.size} for transferId: $transferId with md5: ${upload.md5}")
    val mediaDTO = mediaUploadService.uploadMedia(upload, seriesId)
    LOG.info("Upload to storage got response md5: ${mediaDTO.md5}")
    val mediaFileTransfer = MediaFileTransfer(
        transferId = UUID.randomUUID(),
        supplierId = supplierId,
        seriesId = seriesId,
        filename = upload.filename,
        md5 = mediaDTO.md5,
        sourceUri = mediaDTO.sourceUri,
        uri = mediaDTO.uri,
        transferStatus = TransferStatus.RECEIVED,
        filesize = upload.size,
        objectType = mediaDTO.objectType,
        created = LocalDateTime.now(),
        updated = LocalDateTime.now()
    )
    mediaFileTransferRepository.save(mediaFileTransfer)
    return mediaFileTransfer.toResponse()
}
}


val CompletedFileUpload.extension: String
    get() = filename.substringAfterLast('.', "")

val CompletedFileUpload.md5: String get() = bytes.toMD5Hex()