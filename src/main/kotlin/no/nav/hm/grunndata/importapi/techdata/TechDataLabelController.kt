package no.nav.hm.grunndata.importapi.techdata

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.openapi.visitor.security.SecurityRule
import io.micronaut.security.annotation.Secured
import io.swagger.v3.oas.annotations.tags.Tag

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/v1/techlabels")
@Tag(name= "Tech Data Labels")
class TechDataLabelController(private val techDataLabelService: TechDataLabelService) {

    @Get("/")
    suspend fun fetchTechLabels() = techDataLabelService.fetchAllTechDataLabels()

    @Get("/{isocode}")
    suspend fun fetchTechLabelsByIsocode(isocode: String) = techDataLabelService.fetchTechDataLabelsByIsoCode(isocode)

    @Get("/name/{name}")
    suspend fun fetchTechLabelsByName(name: String) = techDataLabelService.fetchTechDataLabelByKeyName(name)

}