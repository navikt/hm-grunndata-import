package no.nav.hm.grunndata.importapi.productstate

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import no.nav.hm.grunndata.rapid.dto.AdminStatus
import no.nav.hm.grunndata.rapid.dto.ProductDTO
import no.nav.hm.grunndata.rapid.dto.ProductStateDTO
import java.time.LocalDateTime
import java.util.*

@MappedEntity("productstate_v1")
data class ProductState(
    @field:Id
    val id: UUID,
    val transferId: UUID,
    val supplierId: UUID,
    val supplierRef: String,
    @field:TypeDef(type = DataType.JSON)
    val productDTO: ProductDTO,
    val adminStatus: AdminStatus?=null,
    val adminMessage: String? = null,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)
fun ProductState.toDTO(): ProductStateDTO = ProductStateDTO(
    id = id,
    transferId = transferId,
    supplierId = supplierId,
    supplierRef = supplierRef,
    productDTO = productDTO,
    adminStatus = adminStatus,
    message = adminMessage,
    created = created,
    updated = updated
)
