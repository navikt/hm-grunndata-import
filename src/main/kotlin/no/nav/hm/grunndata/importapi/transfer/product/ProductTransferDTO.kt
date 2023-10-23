package no.nav.hm.grunndata.importapi.transfer.product


import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import no.nav.hm.grunndata.rapid.dto.MediaType
import java.time.LocalDateTime
import java.util.*


data class ProductTransferDTO (
    val title: String,
    val articleName: String,
    val shortDescription: String,
    val text: String,
    val url: String?=null,
    val manufacturer: String?=null,
    val supplierRef: String,
    val isoCategory: String,
    val accessory: Boolean = false,
    val sparePart: Boolean = false,
    val compatibleWith: CompatibleWith?=null,
    val seriesId: UUID?=null,
    val techData: List<TechData> = emptyList(),
    val media: List<MediaDTO> = emptyList(),
    val published: LocalDateTime?=null,
    val expired: LocalDateTime?=null,
    val agreements: List<ProductAgreement> = emptyList()
) {
    init {
        require(title.isNotBlank() && title.length<255) {"title is blank or title size > 255" }
        require(articleName.isNotBlank() && articleName.length<255) {"articleName is blank or articleName size > 255"}
        require(shortDescription.isNotBlank()) {"shortDescription is blank"}
        require(supplierRef.isNotBlank()) {"supplierRef is blank"}
        require(isoCategory.isNotBlank()) {"isoCategory is blank"}
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

data class ProductAgreement (
    val rank: Int,
    val postNr: Int,
    val reference: String
)



data class CompatibleWith (
    val seriesIds: Set<UUID> = emptySet(),
)