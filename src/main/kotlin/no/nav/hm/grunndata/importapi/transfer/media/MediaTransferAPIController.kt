package no.nav.hm.grunndata.importapi.transfer.media

import io.micronaut.data.model.Page
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.security.annotation.Secured
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import no.nav.hm.grunndata.importapi.BadRequestException
import no.nav.hm.grunndata.importapi.error.ErrorType
import no.nav.hm.grunndata.importapi.error.ImportApiError
import no.nav.hm.grunndata.importapi.productImport.ProductImportRepository
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.transfer.media.MediaTransferAPIController.Companion.API_V1_MEDIA_TRANSFERS
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.util.*

@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_MEDIA_TRANSFERS)
@SecurityRequirement(name = "bearer-auth")
class MediaTransferAPIController(private val mediaUploadService: MediaUploadService,
                                 private val productImportRepository: ProductImportRepository,
                                 private val mediaTransferRepository: MediaTransferRepository) {

    companion object {
        const val API_V1_MEDIA_TRANSFERS = "/api/v1/media/transfers"
        private val LOG = LoggerFactory.getLogger(MediaTransferAPIController::class.java)
    }

    @Get("/{supplierId}/{supplierRef}")
    suspend fun getMediaList(supplierId: UUID, supplierRef: String): Page<MediaTransferResponse> =
        mediaTransferRepository.findBySupplierIdAndSupplierRef(supplierId, supplierRef).map {
            it.toTransferResponse()
        }



    @Post(
        value = "/files/{supplierId}/{supplierRef}",
        consumes = [io.micronaut.http.MediaType.MULTIPART_FORM_DATA],
        produces = [io.micronaut.http.MediaType.APPLICATION_JSON]
    )
    suspend fun uploadFiles(supplierId: UUID, supplierRef: String,
                            files: Publisher<CompletedFileUpload>): HttpResponse<List<MediaTransferResponse>>  {
        LOG.info("supplier $supplierId uploading files for object $supplierRef")
        productImportRepository.findBySupplierIdAndSupplierRef(supplierId, supplierRef)?.let { p ->
            return HttpResponse.created(files.asFlow().map {
                val mediaDTO = mediaUploadService.uploadMedia(it, p.id)
                val mediaTransfer = MediaTransfer(
                        supplierId = supplierId,
                        supplierRef = supplierRef,
                        oid = p.id,
                        filename = it.filename,
                        md5 = mediaDTO.md5,
                        sourceUri = mediaDTO.sourceUri,
                        uri =  mediaDTO.uri,
                        transferStatus = TransferStatus.DONE
                    )
                val saved = mediaTransferRepository.save(mediaTransfer)
                saved.toTransferResponse()
            }.toList())
        } ?: throw ImportApiError("Wrong supplierRef?", ErrorType.NOT_FOUND)
    }

}




val CompletedFileUpload.extension: String
    get() = filename.substringAfterLast('.', "")

