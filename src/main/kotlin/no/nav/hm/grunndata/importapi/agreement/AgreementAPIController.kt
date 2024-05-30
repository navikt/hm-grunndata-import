package no.nav.hm.grunndata.importapi.agreement

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.openapi.visitor.security.SecurityRule
import io.micronaut.security.annotation.Secured
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.hm.grunndata.importapi.agreement.AgreementAPIController.Companion.API_V1_AGREEMENTS
import org.slf4j.LoggerFactory

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller(API_V1_AGREEMENTS)
@Tag(name= "Agreements")
class AgreementAPIController(private val agreementService: AgreementService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(AgreementAPIController::class.java)
        const val API_V1_AGREEMENTS = "/api/v1/agreements"
    }

    @Get("/")
    fun getAllActiveAgreements() =
        agreementService.getAllActiveAgreements().map { it.toResponse() }


    @Get("/reference/{reference}")
    fun getByReference(reference: String) = agreementService.getAgreementByReference(reference)?.toResponse()

}