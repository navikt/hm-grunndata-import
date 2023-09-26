package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Version
import no.nav.hm.grunndata.importapi.IMPORT
import no.nav.hm.grunndata.rapid.dto.*
import java.time.LocalDateTime
import java.util.*

@MappedEntity("series_import_v1")
data class SeriesImport (
    @field:Id
    val seriesId: UUID,
    val supplierSeriesRef: String,
    val supplierId: UUID,
    val transferId: UUID,
    val name: String,
    val adminStatus: AdminStatus = AdminStatus.APPROVED,
    val adminMessage: String?=null,
    val status: SeriesStatus,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime = created.plusYears(15),
    @field:Version
    val version: Long? = 0L
)


data class SeriesImportDTO (
    val seriesId: UUID,
    val supplierSeriesRef: String,
    val supplierId: UUID,
    val transferId: UUID,
    val name: String,
    val adminStatus: AdminStatus = AdminStatus.APPROVED,
    val adminMessage: String?=null,
    val status: SeriesStatus = SeriesStatus.ACTIVE,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime = created.plusYears(15),
    val version: Long? = 0L
)

fun SeriesImport.toDTO(): SeriesImportDTO = SeriesImportDTO(
    seriesId = seriesId, supplierSeriesRef = supplierSeriesRef, supplierId = supplierId, transferId = transferId, name = name, status = status,
    adminStatus = adminStatus, adminMessage = adminMessage, version = version!!, created = created, updated = updated, expired = expired
)


fun SeriesImportDTO.toEntity(): SeriesImport = SeriesImport(
    seriesId = seriesId, supplierSeriesRef = supplierSeriesRef, supplierId = supplierId, transferId = transferId, name = name, status = status,
    adminStatus = adminStatus, adminMessage = adminMessage, version = version, created = created, updated = updated, expired = expired
)

fun SeriesImportDTO.toRapidDTO(): SeriesImportRapidDTO = SeriesImportRapidDTO (
    id = seriesId, supplierSeriesRef= supplierSeriesRef, transferId = transferId, seriesDTO =
    SeriesRapidDTO(id = seriesId, status = status, name = name, createdBy = IMPORT, supplierId = supplierId,
        identifier = seriesId.toString(), updatedBy = IMPORT, created = created, updated = updated, expired = expired), version = version!!,
    adminMessage = adminMessage, adminStatus = adminStatus
)
