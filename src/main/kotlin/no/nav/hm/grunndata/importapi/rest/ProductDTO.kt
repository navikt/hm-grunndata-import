package no.nav.hm.grunndata.importapi.rest

import no.nav.hm.grunndata.importapi.rest.transfers.AgreementInfo
import no.nav.hm.grunndata.importapi.rest.transfers.Description
import no.nav.hm.grunndata.importapi.rest.transfers.Media
import no.nav.hm.grunndata.importapi.rest.transfers.MediaSourceType
import no.nav.hm.grunndata.importapi.rest.transfers.MediaType
import no.nav.hm.grunndata.importapi.rest.transfers.TechData
import java.time.LocalDateTime
import java.util.*

data class ProductDTO (
    val id: UUID = UUID.randomUUID(),
    val supplierId: UUID,
    val title: String, // Iphone 14 Pro Max (256Gb) Blå
    val name: String, // Iphone 14 Pro Max
    val manufacturer: String? = null, // Apple
    val description: String? = null,
    val shortDescription: String? = null,
    val HMSArtNr: String?=null,
    val identifier: String?=null,
    val supplierRef: String,
    val isoCategory: String,
    val accessory: Boolean = false,
    val sparepart: Boolean = false,
    val seriesId: String?=null,
    val compatibleWith: List<String> = emptyList(), // if accessory/sparepart. The names of products that this is designed for.
    val techData: List<TechData> = emptyList(),
    val media: List<Media> = emptyList(),
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val published: LocalDateTime?=null,
    val expired: LocalDateTime?=null,
    val agreementInfo: AgreementInfo?,
    val hasAgreement: Boolean = (agreementInfo!=null),
    val createdBy: String = no.nav.hm.grunndata.importapi.rest.transfers.IMPORT,
    val updatedBy: String = no.nav.hm.grunndata.importapi.rest.transfers.IMPORT
)

const val IMPORT = "IMPORT"

data class Description(val name: String?=null,
                       val shortDescription: String?=null,
                       val text: String?=null)


data class AgreementInfo (
    val id: Long,
    val identifier: String?=null,
    val rank: Int,
    val postId: Long,
    val postNr: Int,
    val postIdentifier: String?=null,
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
    val unit:   String
)