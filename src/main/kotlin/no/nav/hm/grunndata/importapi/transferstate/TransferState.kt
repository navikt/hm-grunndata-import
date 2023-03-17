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
    val id: UUID = UUID.randomUUID(),
    val supplierId: UUID,
    val reference: String,
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


