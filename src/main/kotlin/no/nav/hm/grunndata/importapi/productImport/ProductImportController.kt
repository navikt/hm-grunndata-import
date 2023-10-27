package no.nav.hm.grunndata.importapi.productImport

import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.hm.grunndata.importapi.productImport.ProductImportController.Companion.API_V1_PRODUCT_IMPORT
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SecuritySupplierRule
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

    @Get("/{supplierId}/{supplierRef}")
    suspend fun getProductImportBySupplierIdAndSupplierRef(supplierId: UUID, supplierRef: String) =
        importService.getProductImportBySupplierIdAndSupplierRef(supplierId,supplierRef)?.toProductImportResponse()

    @Get("/{supplierId}/series/{seriesId}")
    suspend fun getProductImportBySupplierIdAndSeriesId(supplierId: UUID, seriesId: UUID, pageable: Pageable =
            Pageable.from(0, 100, Sort.of(Sort.Order("updated"))))
        = importService.getProductImportBySupplierIdAndSeriesId(supplierId,seriesId,pageable).map { it.toProductImportResponse() }

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
