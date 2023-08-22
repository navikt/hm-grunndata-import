package no.nav.hm.grunndata.importapi.transfer.product

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort.*
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface TransferStateRepository: CoroutineCrudRepository<TransferState, UUID> {
    suspend fun findBySupplierIdAndMd5(supplierId: UUID, md5: String): TransferState?
    suspend fun findByTransferStatus(transferStatus: TransferStatus,
                                     pageable: Pageable = Pageable.from(0, 1000)): Page<TransferState>

    suspend fun findBySupplierIdAndSupplierRef(supplierId: UUID, supplierRef: String,
                                               pageable: Pageable = Pageable.from(0, 100,
                                                   of(Order("created")))): Page<TransferState>
    suspend fun findOneBySupplierIdAndSupplierRefOrderByCreatedDesc(supplierId: UUID, supplierRef: String): TransferState?
}
