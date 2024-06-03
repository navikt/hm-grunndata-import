package no.nav.hm.grunndata.importapi.mediaImport

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.util.UUID
import no.nav.hm.grunndata.rapid.dto.MediaSourceType

@MappedEntity("media_import_v1")
data class MediaImport (
    @field:Id
    val id: UUID = UUID.randomUUID(),
    val uri: String,
    val transferId: UUID ?= null,
    val sourceUri: String?=null,
    val seriesId: UUID,
    val supplierId: UUID,
    val text: String? = null,
    val filename: String ?= null,
    val md5: String ?=null,
    val sourceType: MediaSourceType,
    val priority: Int = 1
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
    val priority: Int = 1

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
    priority = priority
)

