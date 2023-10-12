package no.nav.hm.grunndata.importapi.transfer.media

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface MediaTransferRepository: CoroutineCrudRepository<MediaTransfer, UUID> {

    suspend fun findBySupplierIdAndSupplierRef(supplierId: UUID, supplierRef: String,
                                               pageable: Pageable = Pageable.from(0, 100,
                                                   Sort.of(Sort.Order("created"))
                                               )): Page<MediaTransfer>
    suspend fun findBySupplierIdAndMd5(supplierId: UUID, md5: String): MediaTransfer?

    suspend fun findBySupplierIdAndFilenameAndFilesizeAndTransferStatus(supplierId: UUID, filename: String, filesize: Long, transferStatus: TransferStatus): MediaTransfer?

}