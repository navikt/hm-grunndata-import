package no.nav.hm.grunndata.importapi.transfer.product


import java.time.LocalDateTime
import java.util.UUID
import no.nav.hm.grunndata.rapid.dto.ProductStatus


data class ProductTransferDTO (
    val articleName: String,
    val articleDescription: String?=null,
    val supplierRef: String,
    val accessory: Boolean = false,
    val sparePart: Boolean = false,
    val status: ProductStatus = ProductStatus.ACTIVE,
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

enum class ProductTransferStatus {
    ACTIVE, INACTIVE, DELETED
}


data class TechData (
    val key:    String,
    val value:  String,
    val unit:   String
)

data class CompatibleWith (
    val seriesIds: Set<UUID> = emptySet(),
)