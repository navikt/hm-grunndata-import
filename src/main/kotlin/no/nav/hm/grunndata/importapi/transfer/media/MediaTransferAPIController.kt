package no.nav.hm.grunndata.importapi.transfer.media

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import no.nav.hm.grunndata.importapi.productImport.ProductImportRepository
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.security.supplierId
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportService
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.transfer.media.MediaTransferAPIController.Companion.API_V1_MEDIA_TRANSFERS
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_MEDIA_TRANSFERS)
@SecurityRequirement(name = "bearer-auth")
class MediaTransferAPIController(private val mediaUploadService: MediaUploadService,
                                 private val productImportRepository: ProductImportRepository,
                                 private val seriesImportService: SeriesImportService,
                                 private val gdbApiClient: GdbApiClient,
                                 private val supplierService: SupplierService) {

    companion object {
        const val API_V1_MEDIA_TRANSFERS = "/api/v1/media/transfers"
        private val LOG = LoggerFactory.getLogger(MediaTransferAPIController::class.java)
    }



    @Get("/{identifier}/series/{seriesId}")
    suspend fun getMediaList(identifier: String, authentication: Authentication, seriesId: UUID): HttpResponse<List<MediaDTO>> =
        seriesImportService.findBySupplierIdAndSeriesId(authentication.supplierId(), seriesId).let {
            HttpResponse.ok(mediaUploadService.getMediaList(seriesId))
        }


    @Post(
        value = "/files/{identifier}/series/{seriesId}",
        consumes = [io.micronaut.http.MediaType.MULTIPART_FORM_DATA],
        produces = [io.micronaut.http.MediaType.APPLICATION_JSON]
    )
    suspend fun uploadFiles(identifier: String, authentication: Authentication, seriesId: UUID,
                            files: Publisher<CompletedFileUpload>): HttpResponse<List<MediaTransferResponse>>  {
        val supplierId = authentication.supplierId()
        LOG.info("Upload media files for supplier $supplierId seriesId: $seriesId")
        seriesImportService.findBySupplierIdAndSeriesId(supplierId, seriesId)?.let { s ->
            return HttpResponse.created(files.asFlow().map {
                createMediaTransferResponse(it, s.seriesId, supplierId)
            }.toList())
        } ?: run {
            gdbApiClient.getSeriesById(seriesId)?.let { dto ->
                LOG.info("Supplier $supplierId and seriesId found in GDB ${dto.id} ")
                return HttpResponse.created(files.asFlow().map {
                    createMediaTransferResponse(it, dto.id, supplierId)
                }.toList())
            } ?: run {
                LOG.info("Supplier $supplierId and seriesId not found in GDB")
                return HttpResponse.notFound()
            }
        }
    }



    private suspend fun createMediaTransferResponse(
        upload: CompletedFileUpload,
        seriesId: UUID,
        supplierId: UUID
    ): MediaTransferResponse {
        val transferId = UUID.randomUUID()
        LOG.info("Storing file ${upload.name} size: ${upload.size} for transferId: $transferId")
        val mediaDTO = mediaUploadService.uploadMedia(upload, seriesId)
            val mediaTransfer = MediaTransferResponse(
                transferId = UUID.randomUUID(),
                supplierId = supplierId,
                oid = seriesId,
                filename = upload.filename,
                md5 = mediaDTO.md5,
                sourceUri = mediaDTO.sourceUri,
                uri = mediaDTO.uri,
                transferStatus = TransferStatus.DONE,
                filesize = upload.size,
                objectType = mediaDTO.objectType,
                created = LocalDateTime.now(),
                updated = LocalDateTime.now()
            )
            return mediaTransfer
        }
    }


val CompletedFileUpload.extension: String
    get() = filename.substringAfterLast('.', "")

