package no.nav.hm.grunndata.importapi.transferstate


import no.nav.hm.grunndata.rapid.dto.MediaSourceType
import java.time.LocalDateTime
import java.util.*


data class ProductTransferDTO (
    val supplier: UUID,
    val title: String,
    val articleName: String,
    val attributes: Attributes = Attributes(),
    val hmsArtNr: String?=null,
    val supplierRef: String,
    val isoCategory: String,
    val accessory: Boolean = false,
    val sparePart: Boolean = false,
    val seriesId: String?,
    val transferTechData: List<TransferTechData> = emptyList(),
    val media: List<TransferMediaDTO> = emptyList(),
    val published: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime = published.plusYears(10),
    val agreementInfo: AgreementInfo?=null,
    val createdBy: String = "IMPORT",
    val updatedBy: String = "IMPORT"
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
    val reference: String
)

enum class MediaType {
    PDF,
    IMAGE,
    VIDEO
}

data class Attributes(val manufacturer: String? = null,
                      val compatible: List<CompatibleAttribute>? = null,
                      val series: String? = null,
                      val shortdescription: String? = null,
                      val text: String? = null,
                      val url: String? = null)
data class CompatibleAttribute(val id: UUID?=null,
                               val reference: String?=null,
                               val hmsArtNr: String?)

