package no.nav.hm.grunndata.importapi.techdata

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.openapi.visitor.security.SecurityRule
import io.micronaut.security.annotation.Secured

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/v1/techlabels")
class TechDataLabelController(private val techDataLabelService: TechDataLabelService) {

    @Get("/")
    fun fetchTechLabels() = techDataLabelService.fetchAllTechDataLabels()

    @Get("/{isocode}")
    fun fetchTechLabelsByIsocode(isocode: String) = techDataLabelService.fetchTechDataLabelsByIsoCode(isocode)
}