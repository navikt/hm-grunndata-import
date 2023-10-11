package no.nav.hm.grunndata.importapi.transfer.media

import io.micronaut.data.model.Page
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.multipart.CompletedFileUpload
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import no.nav.hm.grunndata.importapi.error.ErrorType
import no.nav.hm.grunndata.importapi.error.ImportApiError
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import no.nav.hm.grunndata.importapi.productImport.ProductImport
import no.nav.hm.grunndata.importapi.productImport.ProductImportHandler
import no.nav.hm.grunndata.importapi.productImport.ProductImportRepository
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.transfer.media.MediaTransferAPIController.Companion.API_V1_MEDIA_TRANSFERS
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.rapid.dto.AdminStatus
import no.nav.hm.grunndata.rapid.dto.ProductRapidDTO
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.util.*

@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_MEDIA_TRANSFERS)
@SecurityRequirement(name = "bearer-auth")
class MediaTransferAPIController(private val mediaUploadService: MediaUploadService,
                                 private val productImportRepository: ProductImportRepository,
                                 private val mediaTransferRepository: MediaTransferRepository,
                                 private val gdbApiClient: GdbApiClient) {

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
        LOG.info("Upload media files for supplier $supplierId supplierRef: $supplierRef")
        productImportRepository.findBySupplierIdAndSupplierRef(supplierId, supplierRef)?.let { p ->
            return HttpResponse.created(files.asFlow().map {
                createMediaTransferResponse(it, p.id, supplierId, supplierRef)
            }.toList())
        } ?: run {
            gdbApiClient.getProductBySupplierIdAndSupplierRef(supplierId, supplierRef)?.let { dto ->
                LOG.info("Supplier $supplierId and ref $supplierRef found in GDB ${dto.id} ")
                return HttpResponse.created(files.asFlow().map {
                    createMediaTransferResponse(it, dto.id, supplierId, supplierRef)
                }.toList())
            } ?: run {
                val oid = UUID.randomUUID()
                LOG.info("creating new product oid $oid for media files")
                return HttpResponse.created(files.asFlow().map {
                    createMediaTransferResponse(it, oid, supplierId, supplierRef)
                }.toList())
            }
        }
    }

    private suspend fun createMediaTransferResponse(
        it: CompletedFileUpload,
        oid: UUID,
        supplierId: UUID,
        supplierRef: String
    ): MediaTransferResponse {
        val mediaDTO = mediaUploadService.uploadMedia(it, oid)
        val mediaTransfer = MediaTransfer(
            supplierId = supplierId,
            supplierRef = supplierRef,
            oid = oid,
            filename = it.filename,
            md5 = mediaDTO.md5,
            sourceUri = mediaDTO.sourceUri,
            uri = mediaDTO.uri,
            transferStatus = TransferStatus.DONE,
            fileSize = it.size
        )
        val saved = mediaTransferRepository.save(mediaTransfer)
        return saved.toTransferResponse()
    }

}




val CompletedFileUpload.extension: String
    get() = filename.substringAfterLast('.', "")

