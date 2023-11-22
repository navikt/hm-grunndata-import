package no.nav.hm.grunndata.importapi.productImport

import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.hm.grunndata.importapi.productImport.ProductImportController.Companion.API_V1_PRODUCT_IMPORT
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
import no.nav.hm.grunndata.importapi.security.supplierId
import org.slf4j.LoggerFactory
import java.util.*

@SecuritySupplierRule(value = [Roles.ROLE_SUPPLIER])
@Controller(API_V1_PRODUCT_IMPORT)
@SecurityRequirement(name = "bearer-auth")
class ProductImportController(private val importService: ProductImportService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductImportController::class.java)
        const val API_V1_PRODUCT_IMPORT = "/api/v1/product/import"
    }

    @Get("/{identifier}/{supplierRef}")
    suspend fun getProductImportBySupplierIdAndSupplierRef(identifier: String, supplierRef: String, authentication: Authentication) =
        importService.getProductImportBySupplierIdAndSupplierRef(authentication.supplierId(),supplierRef)?.toProductImportResponse()

    @Get("/{identifier}/series/{seriesId}")
    suspend fun getProductImportBySupplierIdAndSeriesId(identifier: String, seriesId: UUID, authentication: Authentication, pageable: Pageable =
            Pageable.from(0, 100, Sort.of(Sort.Order("updated"))))
        = importService.getProductImportBySupplierIdAndSeriesId(authentication.supplierId(),seriesId,pageable).map { it.toProductImportResponse() }

}


private fun ProductImportDTO.toProductImportResponse() = ProductImportResponse (
    id = id,
    transferId = transferId,
    supplierId = supplierId,
    supplierRef = supplierRef,
    seriesId = seriesId,
    hmsArtNr = hmsArtNr,
    productStatus = productStatus,
    adminStatus = adminStatus,
    adminMessage = adminMessage,
    created = created,
    updated = updated,
    version = version
)

