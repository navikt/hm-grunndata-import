package no.nav.hm.grunndata.importapi.transferstate

import java.time.LocalDateTime
import java.util.*

data class TransferResponse(
    val id: UUID? = null,
    val productId: UUID? = null,
    val status: TransferStatus = TransferStatus.DONE,
    val message: String? = null,
    val supplierId: UUID,
    val supplierRef: String,
    val updated: LocalDateTime = LocalDateTime.now(),
    val md5: String,
)
