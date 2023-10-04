package no.nav.hm.grunndata.importapi.productImport

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.annotation.Version
import io.micronaut.data.model.DataType
import no.nav.hm.grunndata.rapid.dto.*
import no.nav.hm.grunndata.rapid.dto.ProductStateDTO
import java.time.LocalDateTime
import java.util.*

@MappedEntity("product_import_v1")
data class ProductImport(
    @field:Id
    val id: UUID,
    val transferId: UUID,
    val supplierId: UUID,
    val supplierRef: String,
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

fun ProductImport.toDTO(): ProductImportRapidDTO = ProductImportRapidDTO(
    id = id,
    transferId = transferId,
    supplierId = supplierId,
    supplierRef = supplierRef,
    productDTO = productDTO,
    created = created,
    updated = updated,
    version = version!!
)

