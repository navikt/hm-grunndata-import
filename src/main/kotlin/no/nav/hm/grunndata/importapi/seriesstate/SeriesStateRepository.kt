package no.nav.hm.grunndata.importapi.seriesstate

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface SeriesStateRepository: CoroutineCrudRepository<SeriesState, String> {

    suspend fun findBySupplierIdAndName(supplierId: UUID, name: String): SeriesState?

    suspend fun findBySupplierId(supplierId: UUID): Flow<SeriesState>

}
