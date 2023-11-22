package no.nav.hm.grunndata.importapi.productImport

import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import java.util.*

@Singleton
class ProductImportService(private val productImportRepository: ProductImportRepository) {

    suspend fun getProductImportBySupplierIdAndSupplierRef(supplierId: UUID, supplierRef: String) =
        productImportRepository.findBySupplierIdAndSupplierRef(supplierId, supplierRef)?.toDTO()



    suspend fun getProductImportBySupplierIdAndSeriesId(supplierId: UUID, seriesId: UUID, pageable: Pageable) =
        productImportRepository.findBySupplierIdAndSeriesId(supplierId, seriesId, pageable).map { it.toDTO() }


}