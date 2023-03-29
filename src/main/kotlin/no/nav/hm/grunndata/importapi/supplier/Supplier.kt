package no.nav.hm.grunndata.importapi.supplier

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import no.nav.hm.grunndata.rapid.dto.SupplierDTO
import no.nav.hm.grunndata.rapid.dto.SupplierInfo
import no.nav.hm.grunndata.rapid.dto.SupplierStatus
import java.time.LocalDateTime
import java.util.*

@MappedEntity("supplier_v1")
data class Supplier(
    @field: Id
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val status: SupplierStatus = SupplierStatus.ACTIVE,
    val jwtid: String = UUID.randomUUID().toString(),
    val identifier: String,
    val createdBy: String = "REGISTER",
    val updatedBy: String = "REGISTER",
    val updated: LocalDateTime = LocalDateTime.now(),
    val created: LocalDateTime = LocalDateTime.now()
)


data class TokenResponseDTO(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val jwtid: String = UUID.randomUUID().toString(),
    val token: String,
    val identifier: String,
    val createdBy: String = "REGISTER",
    val updatedBy: String = "REGISTER",
    val updated: LocalDateTime = LocalDateTime.now(),
    val created: LocalDateTime = LocalDateTime.now()
)

fun SupplierDTO.toEntity(): Supplier = Supplier(
    id = id, status = status, name = name, identifier = identifier, created = created, updated = updated,
    createdBy = createdBy, updatedBy=updatedBy)


fun Supplier.toDTO(): SupplierDTO = SupplierDTO (
    id = id, status = status, name = name, identifier = identifier, created = created ,updated = updated,
    createdBy = createdBy, updatedBy = updatedBy, info = SupplierInfo()
)

fun Supplier.toTokenResponseDTO(token: String): TokenResponseDTO =
    TokenResponseDTO( id = id, name = name, jwtid = jwtid, token = token, identifier = identifier,
        createdBy = createdBy, updatedBy = updatedBy, updated = updated, created = created)
