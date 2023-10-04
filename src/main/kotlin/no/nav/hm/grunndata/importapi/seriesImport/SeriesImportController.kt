package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.seriesImport.SeriesImportController.Companion.API_V1_SERIES_IMPORT
import org.slf4j.LoggerFactory
import java.util.*

@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_SERIES_IMPORT)
@SecurityRequirement(name = "bearer-auth")
class SeriesImportController(private val seriesImportService: SeriesImportService) {
    
    companion object {
        private val LOG = LoggerFactory.getLogger(SeriesImportController::class.java)
        const val API_V1_SERIES_IMPORT = "/api/v1/series/import"
    }

    @Get("/{supplierId}/{supplierSeriesRef}")
    suspend fun getImportedSeriesBySupplierIdAndSupplierRef(supplierId: UUID, supplierSeriesRef: String) =
        seriesImportService.findBySupplierIdAndSupplierSeriesRef(supplierId, supplierSeriesRef)

    @Get("/{supplierId}")
    suspend fun GetImportedSeriesBySupplierId(supplierId: UUID) = seriesImportService.findBySupplierId(supplierId)

}