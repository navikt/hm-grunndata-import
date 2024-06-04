package no.nav.hm.grunndata.importapi.mediaImport

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Version
import java.time.LocalDateTime
import java.util.UUID
import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import no.nav.hm.grunndata.rapid.dto.MediaType

@MappedEntity("media_import_v1")
data class MediaImport (
    @field:Id
    val id: UUID = UUID.randomUUID(),
    val uri: String,
    val transferId: UUID ?= null,
    val sourceUri: String = uri,
    val seriesId: UUID,
    val supplierId: UUID,
    val text: String? = null,
    val filename: String ?= null,
    val md5: String ?=null,
    val type: MediaType = MediaType.IMAGE,
    val sourceType: MediaSourceType,
    val priority: Int = 1,
    val status: MediaImportStatus = MediaImportStatus.ACTIVE,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    @field:Version
    val version: Long? = 0L
)

data class MediaImportDTO(
    val uri: String,
    val sourceUri: String?=null,
    val seriesId: UUID,
    val supplierId: UUID,
    val text: String? = null,
    val filename: String ?= null,
    val md5: String?=null,
    val sourceType: MediaSourceType,
    val priority: Int = 1,
    val status: MediaImportStatus = MediaImportStatus.ACTIVE,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val version: Long? = 0L
)


data class ExternalMediaImportDTO(
    val uri: String,
    val seriesId: UUID,
    val text: String? = null,
    val filename: String ?= null,
    val priority: Int = 1
)

fun ExternalMediaImportDTO.toMediaImport(supplierId: UUID) = MediaImport(
    uri = uri,
    seriesId = seriesId,
    supplierId = supplierId,
    text = text,
    filename = filename,
    sourceType = MediaSourceType.EXTERNALURL,
    priority = priority
)

fun MediaImport.toDTO() = MediaImportDTO(
    uri = uri,
    sourceUri = sourceUri,
    seriesId = seriesId,
    supplierId = supplierId,
    text = text,
    filename = filename,
    md5 = md5,
    sourceType = sourceType,
    priority = priority,
    status = status,
    version = version
)

enum class MediaImportStatus {
    INACTIVE, ACTIVE, DELETED
}