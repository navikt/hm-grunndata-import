package no.nav.hm.grunndata.importapi.transferstate

import java.time.LocalDateTime
import java.util.*

data class ProductTransferDTO (
    val title: String, // Iphone 14 Pro Max (256Gb) Blå
    val name: String, // Iphone 14 Pro Max
    val attributes: Map<String, List<String>>,
    val HMSArtNr: String?=null,
    val identifier: String?=null,
    val supplierRef: String,
    val isoCategory: String,
    val accessory: Boolean = false,
    val sparepart: Boolean = false,
    val seriesId: String?=null,
    val techData: List<TechData> = emptyList(),
    val media: List<Media> = emptyList(),
    val published: LocalDateTime?=null,
    val expired: LocalDateTime?=null,
    val agreementInfo: AgreementInfo?=null,
    val hasAgreement: Boolean = (agreementInfo!=null),
)

// TODO add more later
val attributes_key = listOf("manufacturer","description", "shortdescription", "externalurl", "compatibility")

const val IMPORT = "IMPORT"

data class AgreementInfo (
    val rank: Int?=null,
    val postNr: Int?=null,
    val reference: String?=null,
)

data class Media (
    val uuid:   UUID = UUID.randomUUID(),
    val order:  Int=1,
    val type: MediaType = MediaType.IMAGE,
    val uri:    String,
    val text:   String?=null,
    val source: MediaSourceType = MediaSourceType.EXTERNALURL
)

enum class MediaSourceType {
    EXTERNALURL
}

enum class MediaType {
    PDF,
    IMAGE,
    VIDEO,
    OTHER
}

data class TechData (
    val key:    String,
    val value:  String,
    val unit:   String?=null,
)