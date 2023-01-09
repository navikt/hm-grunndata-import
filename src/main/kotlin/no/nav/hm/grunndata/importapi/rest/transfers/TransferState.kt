package no.nav.hm.grunndata.importapi.rest.transfers

import io.micronaut.data.annotation.MappedEntity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.util.*

@MappedEntity("transferstate_v1")
data class TransferState(
    @field: Id
    val uuid: UUID = UUID.randomUUID(),
    val supplierId: UUID,
    val md5: String,
    val items: Int,
    val type: PayloadType = PayloadType.PRODUCT,
    val payload: ByteArray,
    @field: Enumerated(EnumType.STRING)
    val status: Status = Status.RECEIVED,
    val message: String? = null,
    val version: Long = 0L,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransferState

        if (uuid != other.uuid) return false
        if (supplierId != other.supplierId) return false
        if (md5 != other.md5) return false
        if (items != other.items) return false
        if (type != other.type) return false
        if (status != other.status) return false
        if (message != other.message) return false
        if (created != other.created) return false
        if (updated != other.updated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + supplierId.hashCode()
        result = 31 * result + md5.hashCode()
        result = 31 * result + items
        result = 31 * result + type.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + created.hashCode()
        result = 31 * result + updated.hashCode()
        return result
    }
}

enum class Status {
    RECEIVED, DONE, ERROR
}

enum class PayloadType {
    PRODUCT, OTHER
}

