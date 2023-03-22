package no.nav.hm.grunndata.importapi.transferstate

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface TransferStateRepository: CoroutineCrudRepository<TransferState, UUID> {
    suspend fun findOneBySupplierIdAndSupplierRef(supplierId: UUID, supplierRef: String): TransferState?
}
