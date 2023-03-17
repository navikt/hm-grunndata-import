package no.nav.hm.grunndata.importapi.supplier

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import no.nav.hm.grunndata.rapid.dto.SupplierDTO
import no.nav.hm.grunndata.rapid.dto.SupplierStatus
import java.awt.SystemColor.info
import java.time.LocalDateTime
import java.util.*

@MappedEntity("supplier_v1")
data class Supplier(
    @field: Id
    val id: UUID,
    val name: String,
    val status: SupplierStatus = SupplierStatus.ACTIVE,
    val jwtid: UUID = UUID.randomUUID(),
    val identifier: String,
    val createdBy: String = "REGISTER",
    val updatedBy: String = "REGISTER",
    val updated: LocalDateTime = LocalDateTime.now(),
    val created: LocalDateTime = LocalDateTime.now()
)


fun SupplierDTO.toEntity(): Supplier = Supplier(
    id = id, status = status, name = name, identifier = identifier, created = created, updated = updated,
    createdBy = createdBy, updatedBy=updatedBy)
