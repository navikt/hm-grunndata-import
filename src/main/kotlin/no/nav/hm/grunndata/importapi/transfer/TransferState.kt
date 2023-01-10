package no.nav.hm.grunndata.importapi.transfer

import io.micronaut.data.annotation.MappedEntity
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.*

@MappedEntity("transferstate_v1")
data class TransferState(
    @field: Id
    val uuid: UUID = UUID.randomUUID(),
    val supplierId: UUID,
    val md5: String,
    val items: Int,
    val json_payload: String,
    val status: Status = Status.RECEIVED,
    val message: String? = null,
    val version: Long = 0L,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)

enum class Status {
    RECEIVED, DONE, ERROR
}


