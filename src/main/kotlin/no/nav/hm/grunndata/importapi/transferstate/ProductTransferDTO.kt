package no.nav.hm.grunndata.importapi.transferstate


import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import java.time.LocalDateTime
import java.util.*

data class ProductTransferDTO (
    val supplier: UUID,
    val title: String,
    val attributes: Map<AttributeNames, Any>,
    val status: TransferProductStatus = TransferProductStatus.INACTIVE,
    val hmsArtNr: String?=null,
    val supplierRef: String,
    val isoCategory: String,
    val accessory: Boolean = false,
    val sparePart: Boolean = false,
    val seriesId: String?,
    val transferTechData: List<TransferTechData> = emptyList(),
    val media: List<TransferMediaDTO> = emptyList(),
    val published: LocalDateTime?=null,
    val expired: LocalDateTime?=null,
    val agreementInfo: AgreementInfo?=null
)

data class TransferMediaDTO (
    val sourceUri: String,
    val uri: String = sourceUri,
    val priority: Int = 1,
    val type: MediaType = MediaType.IMAGE,
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
    val reference: String?=null
)

enum class MediaType {
    PDF,
    IMAGE,
    VIDEO
}

enum class AttributeNames(private val type: AttributeType) {

    manufacturer(AttributeType.STRING),
    articlename(AttributeType.STRING),
    compatible(AttributeType.LIST),
    series(AttributeType.STRING),
    shortdescription(AttributeType.HTML),
    text(AttributeType.HTML),
    url(AttributeType.URL),

}

enum class AttributeType {
    STRING, HTML, URL, LIST, JSON, BOOLEAN
}

data class CompatibleAttribute(val id: UUID?=null,
                               val reference: String?=null,
                               val hmsArtNr: String?)
