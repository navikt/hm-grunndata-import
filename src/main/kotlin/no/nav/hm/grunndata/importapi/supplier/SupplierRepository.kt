package no.nav.hm.grunndata.importapi.supplier

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface SupplierRepository: CoroutineCrudRepository<Supplier, UUID> {
}