package no.nav.hm.grunndata.importapi.transferstate


import java.time.LocalDateTime
import java.util.*

data class ProductTransferDTO (
    val id: UUID = UUID.randomUUID(),
    val supplier: UUID,
    val title: String,
    val attributes: Map<AttributeNames, Any>,
    val status: TransferProductStatus = TransferProductStatus.INACTIVE,
    val hmsArtNr: String?=null,
    val identifier: String,
    val supplierRef: String,
    val isoCategory: String,
    val accessory: Boolean = false,
    val sparePart: Boolean = false,
    val seriesId: String? = id.toString(),
    val transferTechData: List<TransferTechData> = emptyList(),
    val media: List<TransferMediaDTO> = emptyList(),
    val published: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime = published.plusYears(20),
    val agreementInfo: AgreementInfo?=null
)

data class TransferMediaDTO (
    val sourceUri: String,
    val priority: Int = 1,
    val type: MediaType = MediaType.IMAGE,
    val text:   String?=null,
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
