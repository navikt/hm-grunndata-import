package no.nav.hm.grunndata.importapi.transfer.product


import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import java.time.LocalDateTime
import java.util.*


data class ProductTransferDTO (
    val title: String,
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
    val seriesId: String?=null,
    val transferTechData: List<TransferTechData> = emptyList(),
    val media: List<TransferMediaDTO> = emptyList(),
    val published: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime = published.plusYears(10),
    val agreements: List<AgreementInfo> = emptyList()
) {
    init {
        require(title.isNotBlank() && title.length<512) {"title is blank or title size > 512"}
        require(articleName.isNotBlank() && articleName.length<512) {"articleName is blank or articleName size > 512"}
        require(shortDescription.isNotBlank()) {"shortDescription is blank"}
        require(supplierRef.isNotBlank()) {"supplierRef is blank"}
        require(isoCategory.isNotBlank()) {"isoCategory is blank"}
        require(!accessory && !sparePart && compatibleWith ==null) {"accessory or sparePart is set, need to specify compatibleWith "}
    }
}

data class TransferMediaDTO (
    val sourceUri: String,
    val priority: Int = 1,
    val type: TransferMediaType = TransferMediaType.PNG,
    val text:   String?=null,
    val sourceType: MediaSourceType = MediaSourceType.EXTERNALURL
)

enum class TransferProductStatus {
    ACTIVE, INACTIVE
}

data class TransferTechData (
    val key:    String,
    val value:  String,
    val unit:   String
)

data class AgreementInfo (
    val rank: Int,
    val postNr: Int,
    val reference: String
)

enum class TransferMediaType {
    PDF,
    JPG,
    PNG
}

data class CompatibleWith (
    val id: List<UUID> = emptyList(),
    val seriesId: List<String> = emptyList(),
)

data class NameId(val id: String, val name: String)