package no.nav.hm.grunndata.importapi.transfer.series

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface SeriesTransferRepository: CrudRepository<SeriesTransfer, UUID> {

    suspend fun findBySupplierIdAndSeriesId(supplierId: UUID, seriesId: String): SeriesTransfer?

}