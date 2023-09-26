package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface SeriesImportRepository: CoroutineCrudRepository<SeriesImport, UUID> {

    suspend fun findBySupplierIdAndSupplierSeriesRef(supplierId: UUID, supplierSeriesRef: String): SeriesImport?

    suspend fun findBySupplierId(supplierId: UUID): List<SeriesImport>

}
