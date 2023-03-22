package no.nav.hm.grunndata.importapi.transferstate

import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.*

@MappedEntity("transferstate_v1")
data class TransferState(
    @field:Id
    val transferId: UUID = UUID.randomUUID(),
    val productId: UUID,
    val supplierId: UUID,
    val supplierRef: String,
    val md5: String,
    @field:TypeDef(type = DataType.JSON)
    val json_payload: ProductTransferDTO,
    val transferStatus: TransferStatus = TransferStatus.DONE,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)

enum class TransferStatus {
    DONE, ERROR
}

data class TransferStateDTO(
    val id: UUID,
    val productId: UUID,
    val supplierId: UUID,
    val supplierRef: String,
    val md5: String,
    val json_payload: ProductTransferDTO,
    val transferStatus: TransferStatus,
    val created: LocalDateTime,
    val updated: LocalDateTime )

fun TransferState.toDTO(): TransferStateDTO = TransferStateDTO(
    id = transferId, productId = productId, supplierId = supplierId, supplierRef = supplierRef, md5 = md5,
    json_payload = json_payload, transferStatus = transferStatus, created = created, updated = updated
)
