package no.nav.hm.grunndata.importapi.mediaImport

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface MediaImportRepository: CoroutineCrudRepository<MediaImport, UUID> {
    suspend fun findBySupplierIdAndSeriesId(supplierId: UUID, seriesId: UUID): List<MediaImport>
    suspend fun findBySupplierIdAndId(supplierId: UUID?, id: UUID): MediaImport?
}