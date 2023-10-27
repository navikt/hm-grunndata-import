package no.nav.hm.grunndata.importapi.productImport

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.annotation.Version
import io.micronaut.data.model.DataType
import jakarta.persistence.Column
import no.nav.hm.grunndata.rapid.dto.*
import java.time.LocalDateTime
import java.util.*

@MappedEntity("product_import_v1")
data class ProductImport(
    @field:Id
    val id: UUID,
    val transferId: UUID,
    val supplierId: UUID,
    val supplierRef: String,
    val seriesId: UUID,
    @field:Column(name="hms_artnr")
    val hmsArtNr: String?=null,
    val productStatus: ProductStatus,
    val adminStatus: AdminStatus = AdminStatus.PENDING,
    val adminMessage: String ?= null,
    @field:TypeDef(type = DataType.JSON)
    val productDTO: ProductRapidDTO,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    @field:Version
    val version: Long?=0L
)

data class ProductImportDTO(
    val id: UUID,
    val transferId: UUID,
    val supplierId: UUID,
    val supplierRef: String,
    val seriesId: UUID,
    val hmsArtNr: String?,
    val productStatus: ProductStatus,
    val adminStatus: AdminStatus = AdminStatus.PENDING,
    val adminMessage: String ?= null,
    val productDTO: ProductRapidDTO,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val version: Long?=0L
)

data class ProductImportResponse(
    val id: UUID,
    val transferId: UUID,
    val supplierId: UUID,
    val supplierRef: String,
    val seriesId: UUID,
    val hmsArtNr: String?,
    val productStatus: ProductStatus,
    val adminStatus: AdminStatus = AdminStatus.PENDING,
    val adminMessage: String ?= null,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val version: Long?=0L
)



fun ProductImport.toDTO(): ProductImportDTO  = ProductImportDTO (
    id = id,
    transferId = transferId,
    supplierId = supplierId,
    supplierRef = supplierRef,
    seriesId = seriesId,
    hmsArtNr = hmsArtNr,
    productStatus = productStatus,
    adminStatus = adminStatus,
    adminMessage = adminMessage,
    productDTO = productDTO,
    created = created,
    updated = updated,
    version = version
)

fun ProductImport.toRapidDTO(): ProductImportRapidDTO = ProductImportRapidDTO(
    id = id,
    transferId = transferId,
    supplierId = supplierId,
    supplierRef = supplierRef,
    productDTO = productDTO,
    created = created,
    updated = updated,
    version = version!!
)

