package no.nav.hm.grunndata.importapi.rest.transfers

import io.micronaut.data.annotation.MappedEntity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.*

@MappedEntity
data class TransferState(
    @Id
    val uuid: UUID = UUID.randomUUID(),
    val supplierId: UUID,
    val md5: String,
    val items: Int,
    val payload: String,
    @Enumerated(EnumType.STRING)
    val status: Status = Status.RECEIVED,
    val message: String? = null,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)

enum class Status {
    RECEIVED, DONE, ERROR
}