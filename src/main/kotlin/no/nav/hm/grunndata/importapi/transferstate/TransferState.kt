package no.nav.hm.grunndata.importapi.transferstate

import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.*

@MappedEntity("transferstate_v1")
data class TransferState (
    @field:Id
    val transferId: UUID = UUID.randomUUID(),
    val productId: UUID,
    val supplierId: UUID,
    val supplierRef: String,
    val md5: String,
    @field:TypeDef(type = DataType.JSON)
    val json_payload: ProductTransferDTO,
    val message: String? = null,
    val transferStatus: TransferStatus = TransferStatus.RECEIVED,
    val created: LocalDateTime = LocalDateTime.now()
)

enum class TransferStatus {
    RECEIVED,  DONE, ERROR
}

data class TransferStateResponseDTO(
    val id: UUID,
    val productId: UUID,
    val supplierId: UUID,
    val supplierRef: String,
    val md5: String,
    val message: String? = null,
    val transferStatus: TransferStatus,
    val created: LocalDateTime)

fun TransferState.toResponseDTO(): TransferStateResponseDTO = TransferStateResponseDTO(
    id = transferId, productId = productId, supplierId = supplierId, supplierRef = supplierRef, md5 = md5,
    message = message, transferStatus = transferStatus, created = created
)
