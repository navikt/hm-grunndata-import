package no.nav.hm.grunndata.importapi.supplier

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.time.LocalDateTime
import java.util.*

@MappedEntity("supplier_v1")
data class Supplier(
    @field: Id
    val id: UUID,
    val jwtid: UUID,
    val identifier: String,
    val createdBy: String = "REGISTER",
    val updatedBy: String = "REGISTER",
    val updated: LocalDateTime = LocalDateTime.now(),
    val created: LocalDateTime = LocalDateTime.now()
)