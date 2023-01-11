package no.nav.hm.grunndata.importapi.productstate

import no.nav.hm.grunndata.importapi.transferstate.AgreementInfo
import no.nav.hm.grunndata.importapi.transferstate.IMPORT
import java.time.LocalDateTime
import java.util.*

data class ProductDTO(
    val id: UUID = UUID.randomUUID(),
    val supplierId: UUID,
    val title: String,
    val attributes: Map<String, String> = emptyMap(),
    val status: ProductStatus = ProductStatus.INACTIVE,
    val HMSArtNr: String?=null,
    val identifier: String?=null,
    val supplierRef: String,
    val isoCategory: String,
    val accessory: Boolean = false,
    val sparepart: Boolean = false,
    val seriesId: String?=null,
    val techData: List<TechData> = emptyList(),
    val media: List<Media> = emptyList(),
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
    val published: LocalDateTime?=null,
    val expired: LocalDateTime?=null,
    val agreementInfo: AgreementInfo?,
    val hasAgreement: Boolean = (agreementInfo!=null),
    val createdBy: String = IMPORT,
    val updatedBy: String = IMPORT
)

enum class ProductStatus {
    ACTIVE, INACTIVE
}

data class Media (
    val uuid:   UUID = UUID.randomUUID(),
    val order:  Int=1,
    val type: MediaType = MediaType.IMAGE,
    val uri:    String,
    val text:   String?=null,
    val source: MediaSourceType = MediaSourceType.ONPREM
)

enum class MediaSourceType {
    // HMDB means it is stored in hjelpemiddeldatabasen
    HMDB, ONPREM, GCP, EXTERNALURL
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

