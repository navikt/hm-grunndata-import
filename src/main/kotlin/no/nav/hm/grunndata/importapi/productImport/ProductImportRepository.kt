package no.nav.hm.grunndata.importapi.productImport

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import no.nav.hm.grunndata.importapi.transfer.series.SeriesTransfer
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ProductImportRepository: CoroutineCrudRepository<ProductImport, UUID> {

    suspend fun findBySupplierIdAndSupplierRef(supplierId: UUID, supplierRef: String): ProductImport?

    suspend fun findBySupplierIdAndSeriesId(supplierId: UUID, seriesId:UUID, pageable: Pageable): Page<ProductImport>

}
