package no.nav.hm.grunndata.importapi.transfer.series

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface SeriesTransferRepository: CoroutineCrudRepository<SeriesTransfer, UUID> {

    suspend fun findBySupplierIdAndSeriesId(supplierId: UUID, seriesId: String, pageable: Pageable = Pageable.from(0, 100,
                                                                                      Sort.of(Sort.Order("created"))
                                                                                  )): Page<SeriesTransfer>
    suspend fun findBySupplierIdAndTransferId(supplierId: UUID, transferId: UUID): SeriesTransfer?
    suspend fun findBySupplierIdAndMd5(supplierId: UUID, md5: String): SeriesTransfer?
    suspend fun findByTransferStatus(transferStatus: TransferStatus,pageable: Pageable = Pageable.from(0, 1000)): Page<SeriesTransfer>

}