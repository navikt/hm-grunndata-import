package no.nav.hm.grunndata.importapi.transfer.series

import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import jakarta.persistence.Id
import no.nav.hm.grunndata.importapi.seriesstate.SeriesStatus
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import java.time.LocalDateTime
import java.util.*

@MappedEntity("series_transfer_v1")
data class SeriesTransfer(
    @field:Id
    val transferId: UUID = UUID.randomUUID(),
    val supplierId: UUID,
    @field:TypeDef(type = DataType.JSON)
    val json_payload: SeriesTransferDTO,
    val md5: String,
    val message: String? = null,
    val transferStatus: TransferStatus = TransferStatus.RECEIVED,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now()
)

data class SeriesTransferDTO(
    val id: String? = null,
    val name: String,
    val status: SeriesStatus = SeriesStatus.ACTIVE
)


data class SeriesTransferResponse(
    val transferId: UUID,
    val supplierId: UUID,
    val seriesId: String,
    val md5: String,
    val message: String? = null,
    val transferStatus: TransferStatus,
    val created: LocalDateTime
)