package no.nav.hm.grunndata.importapi.adminstate

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ProductAdminStateRepository: CoroutineCrudRepository<ProductAdminState, UUID> {


}