package no.nav.hm.grunndata.importapi.mediaImport

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.LocalDateTime
import java.util.UUID
import no.nav.hm.grunndata.importapi.mediaImport.MediaImportController.Companion.API_V1_MEDIA_IMPORT
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.security.supplierId
import org.slf4j.LoggerFactory

@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_MEDIA_IMPORT)
@SecurityRequirement(name = "bearer-auth")
@Tag(name = "Product Variant Transfers")
class MediaImportController(private val mediaImportRepository: MediaImportRepository) {

    companion object {
        private val LOG = LoggerFactory.getLogger(MediaImportController::class.java)
        const val API_V1_MEDIA_IMPORT = "/api/v1/media/import"
    }

    @Get("/{identifier}/series/{seriesId}")
    suspend fun getMediaImportStateForSeries(identifier: String, seriesId: UUID, authentication: Authentication): HttpResponse<List<MediaImportDTO>> {
        LOG.info("Looking up media import state for $identifier seriesId: ${seriesId}")
        return HttpResponse.ok(mediaImportRepository.findBySupplierIdAndSeriesId(authentication.supplierId(), seriesId).map { it.toDTO() })
    }

    @Get("/{identifier}/{id}")
    suspend fun getMediaImportStateById(identifier: String, id: UUID, authentication: Authentication): HttpResponse<MediaImportDTO?> {
        LOG.info("Looking up media import state for $identifier id: ${id}")
        return HttpResponse.ok(mediaImportRepository.findBySupplierIdAndId(authentication.supplierId(), id)?.toDTO())
    }

    @Post("/{identifier}/series/{seriesId}")
    suspend fun createExternalImportState(identifier: String, seriesId: UUID, authentication: Authentication,
                                          @Body media: ExternalMediaImportDTO): HttpResponse<MediaImportDTO> {
        LOG.info("Creating external media import state for $identifier seriesId: ${seriesId}")
        return HttpResponse.created(mediaImportRepository.save( media.toMediaImport(authentication.supplierId())).toDTO())
    }

    @Put("/{identifier}/{id}")
    suspend fun updateMediaImportState(identifier: String, id: UUID, authentication: Authentication,
                                       @Body media: MediaImportDTO): HttpResponse<MediaImportDTO> {
        LOG.info("Updating media import state for $identifier id: ${id}")
        mediaImportRepository.findBySupplierIdAndId(authentication.supplierId(), id)?.let {
            return HttpResponse.ok(mediaImportRepository.save(it.copy(
                text = media.text,
                priority = media.priority,
            )).toDTO())
        } ?: run {
            LOG.warn("Update not found $identifier id: ${id}")
            return HttpResponse.notFound()
        }
    }

    @Delete("/{identifier}/{id}")
    suspend fun deleteMediaImport(identifier: String, id: UUID, authentication: Authentication): HttpResponse<Unit> {
        LOG.info("Deleting media import state for $identifier id: ${id}")
        mediaImportRepository.findBySupplierIdAndId(authentication.supplierId(), id)?.let {
            mediaImportRepository.update(it.copy(status = MediaImportStatus.DELETED, updated = LocalDateTime.now()))
            return HttpResponse.ok()
        } ?: run {
            LOG.warn("Delete not found for $identifier id: ${id}")
            return HttpResponse.notFound()
        }
    }
}