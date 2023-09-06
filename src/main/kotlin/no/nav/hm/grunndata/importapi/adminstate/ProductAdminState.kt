package no.nav.hm.grunndata.importapi.adminstate

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import no.nav.hm.grunndata.rapid.dto.AdminStatus
import no.nav.hm.grunndata.rapid.dto.ProductStatus
import java.time.LocalDateTime
import java.util.UUID

@MappedEntity("product_admin_state_v1")
data class ProductAdminState(
    @field:Id
    val id: UUID,
    val transferId: UUID,
    val supplierId: UUID,
    val supplierRef: String,
    val productStatus: ProductStatus = ProductStatus.INACTIVE,
    val adminStatus: AdminStatus = AdminStatus.PENDING,
    val adminMessage: String?,
    val version: Long,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)

