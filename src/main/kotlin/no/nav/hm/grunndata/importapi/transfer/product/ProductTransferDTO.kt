package no.nav.hm.grunndata.importapi.transfer.product


import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import no.nav.hm.grunndata.rapid.dto.MediaType
import no.nav.hm.grunndata.rapid.dto.ProductStatus
import java.time.LocalDateTime
import java.util.*


data class ProductTransferDTO (
    val articleName: String,
    val articleDescription: String?=null,
    val status: ProductStatus = ProductStatus.ACTIVE,
    val supplierRef: String,
    val accessory: Boolean = false,
    val sparePart: Boolean = false,
    val compatibleWith: CompatibleWith?=null,
    val seriesId: UUID,
    val techData: List<TechData> = emptyList(),
    val published: LocalDateTime?=null,
    val expired: LocalDateTime?=null
) {
    init {
        require(articleName.isNotBlank() && articleName.length<255) {"articleName is blank or articleName size > 255"}
        require(supplierRef.isNotBlank()) {"supplierRef is blank"}
    }
}

data class MediaDTO (
    val uri: String,
    val priority: Int = 1,
    val type: MediaType = MediaType.IMAGE,
    val text:   String?=null,
    val sourceType: MediaSourceType = MediaSourceType.IMPORT
)

enum class TransferProductStatus {
    ACTIVE, INACTIVE
}

data class TechData (
    val key:    String,
    val value:  String,
    val unit:   String
)

data class CompatibleWith (
    val seriesIds: Set<UUID> = emptySet(),
)