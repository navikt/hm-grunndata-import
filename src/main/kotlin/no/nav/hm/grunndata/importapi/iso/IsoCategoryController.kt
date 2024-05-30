package no.nav.hm.grunndata.importapi.iso

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.openapi.visitor.security.SecurityRule
import io.micronaut.security.annotation.Secured
import no.nav.hm.grunndata.rapid.dto.IsoCategoryDTO

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/v1/isocategories")
class IsoCategoryController(private val isoCategoryService: IsoCategoryService) {

    @Get("/")
    suspend fun getAllCategories(): List<IsoCategoryDTO> = isoCategoryService.retrieveAllCategories()

    @Get("/{isocode}")
    suspend fun getCategoryByIsocode(isocode: String): IsoCategoryDTO? = isoCategoryService.lookUpCode(isocode)
}