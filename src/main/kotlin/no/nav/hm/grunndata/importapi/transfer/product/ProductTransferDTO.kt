package no.nav.hm.grunndata.importapi.transfer.product


import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import java.time.LocalDateTime
import java.util.*


data class ProductTransferDTO (
    val articleName: String,
    val shortDescription: String,
    val text: String,
    val url: String?=null,
    val manufacturer: String?=null,
    val hmsArtNr: String?=null,
    val supplierRef: String,
    val isoCategory: String,
    val accessory: Boolean = false,
    val sparePart: Boolean = false,
    val compatibleWith: CompatibleWith?=null,
    val seriesId: UUID,
    val transferTechData: List<TransferTechData> = emptyList(),
    val media: List<TransferMediaDTO> = emptyList(),
    val published: LocalDateTime?=null,
    val expired: LocalDateTime?=null,
    val agreements: List<ProductAgreement> = emptyList()
) {
    init {
        require(articleName.isNotBlank() && articleName.length<512) {"articleName is blank or articleName size > 512"}
        require(shortDescription.isNotBlank()) {"shortDescription is blank"}
        require(supplierRef.isNotBlank()) {"supplierRef is blank"}
        require(isoCategory.isNotBlank()) {"isoCategory is blank"}
    }
}

data class TransferMediaDTO (
    val uri: String,
    val priority: Int = 1,
    val type: TransferMediaType = TransferMediaType.PNG,
    val text:   String?=null,
    val sourceType: MediaSourceType = MediaSourceType.IMPORT
)

enum class TransferProductStatus {
    ACTIVE, INACTIVE
}

data class TransferTechData (
    val key:    String,
    val value:  String,
    val unit:   String
)

data class ProductAgreement (
    val rank: Int,
    val postNr: Int,
    val reference: String
)

enum class TransferMediaType {
    PDF,
    JPG,
    PNG,
    VIDEO
}

data class CompatibleWith (
    val seriesIds: Set<UUID> = emptySet(),
)