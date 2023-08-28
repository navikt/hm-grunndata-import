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
    val isCompatibleWith: CompatibleAttribute?=null,
    val seriesId: String?=null,
    val transferTechData: List<TransferTechData> = emptyList(),
    val media: List<TransferMediaDTO> = emptyList(),
    val published: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime = published.plusYears(10),
    val agreements: List<AgreementInfo> = emptyList()
)

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

data class CompatibleAttribute(
    val id: UUID?=null,
    val seriesId: String?=null,
    val supplierRef: String?=null,
    val hmsArtNr: String?=null
)
