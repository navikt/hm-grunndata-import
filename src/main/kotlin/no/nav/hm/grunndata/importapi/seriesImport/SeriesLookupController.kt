package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.Cacheable
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import no.nav.hm.grunndata.importapi.gdb.SeriesDTO
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.security.supplierId
import no.nav.hm.grunndata.importapi.seriesImport.SeriesLookupController.Companion.API_V1_SERIES_LOOKUP
import org.checkerframework.checker.signature.qual.Identifier
import org.slf4j.LoggerFactory
import java.util.UUID

@Controller(API_V1_SERIES_LOOKUP)
@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@SecurityRequirement(name = "bearer-auth")
@CacheConfig("series")
open class SeriesLookupController(private val gdbApiClient: GdbApiClient) {

    companion object {
        private val LOG = LoggerFactory.getLogger(SeriesLookupController::class.java)
        const val API_V1_SERIES_LOOKUP = "/api/v1/series/lookup"
    }

    @Get("/{identifier}")
    @Cacheable
    open fun getSeriesBySupplierId(identifier: String, authentication: Authentication): List<SeriesDTO> {
        val supplierId = authentication.supplierId()
        LOG.info("Looking up series for supplierId: ${supplierId}")
        return runBlocking { gdbApiClient.getSeriesBySupplierId(supplierId) }
    }

}