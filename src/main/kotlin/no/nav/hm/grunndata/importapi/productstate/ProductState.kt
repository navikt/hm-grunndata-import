package no.nav.hm.grunndata.importapi.productstate

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import no.nav.hm.grunndata.rapid.dto.ProductDTO
import java.time.LocalDateTime
import java.util.*

@MappedEntity("productstate_v1")
data class ProductState(
    @field:Id
    val id: UUID,
    val supplierId: UUID,
    val supplierRef: String,
    @field:TypeDef(type = DataType.JSON)
    val productDTO: ProductDTO,
    val version: Long? = 0L,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)
