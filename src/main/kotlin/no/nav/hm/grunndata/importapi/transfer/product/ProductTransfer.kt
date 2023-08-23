package no.nav.hm.grunndata.importapi.transfer.product

import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.*

@MappedEntity("product_transfer_v1")
data class ProductTransfer (
    @field:Id
    val transferId: UUID = UUID.randomUUID(),
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

data class TransferResponseDTO(
    val transferId: UUID,
    val supplierId: UUID,
    val supplierRef: String,
    val md5: String,
    val message: String? = null,
    val transferStatus: TransferStatus,
    val created: LocalDateTime)

fun ProductTransfer.toResponseDTO(): TransferResponseDTO = TransferResponseDTO(
    transferId = transferId, supplierId = supplierId, supplierRef = supplierRef, md5 = md5,
    message = message, transferStatus = transferStatus, created = created
)
