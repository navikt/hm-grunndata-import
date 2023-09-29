package no.nav.hm.grunndata.importapi.agreement

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import no.nav.hm.grunndata.rapid.dto.*
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

@Singleton
@CacheConfig("agreements")
open class AgreementService(private val agreementGdbApiClient: AgreementGdbApiClient) {

    companion object {
        private val LOG = LoggerFactory.getLogger(AgreementService::class.java)
    }

    @Cacheable
    open fun getAllActiveAgreements(): List<AgreementDTO> {
        LOG.info("Getting all active agreements from grunndata db")
        val params = mapOf("status" to "ACTIVE")
        return agreementGdbApiClient.findAgreements(params).content
    }

    fun getAgreementByReference(reference: String): AgreementDTO?
        = getAllActiveAgreements().find { it.reference == reference }

}

fun AgreementDTO.toResponse() = AgreementResponse(
    id = id, title = title, resume = resume, text=text, status= status,
    reference = reference, published = published, expired=expired, posts = posts.map { it.toResponse() }
)
fun AgreementPost.toResponse() = AgreementPostResponse(
    identifier = identifier, nr = nr, title = title, description = description, created = created
)

data class AgreementResponse(
    val id: UUID,
    val title: String,
    val resume: String?,
    val text: String?,
    val status: AgreementStatus = AgreementStatus.ACTIVE,
    val reference: String,
    val published: LocalDateTime = LocalDateTime.now(),
    val expired: LocalDateTime = LocalDateTime.now(),
    val posts: List<AgreementPostResponse> = emptyList(),
)

data class AgreementPostResponse (
    val identifier: String,
    val nr: Int,
    val title: String,
    val description: String,
    val created: LocalDateTime = LocalDateTime.now()
)
