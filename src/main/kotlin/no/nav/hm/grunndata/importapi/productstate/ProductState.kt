package no.nav.hm.grunndata.importapi.productstate

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.time.LocalDateTime
import java.util.*

@MappedEntity("productstate_v1")
data class ProductState(
    @field:Id
    val id: UUID,
    val supplierId: UUID,
    val supplierRef: String,
    val productDTO: ProductDTO,
    val version: Long? = 0L,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)
