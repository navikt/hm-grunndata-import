package no.nav.hm.grunndata.importapi.productstate

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ProductStateRepository: CoroutineCrudRepository<ProductState, UUID> {

    suspend fun findBySupplierIdAndSupplierRef(supplierId: UUID, supplierRef: String): ProductState?

}
