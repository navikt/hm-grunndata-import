package no.nav.hm.grunndata.importapi.transfer.media

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface MediaFileTransferRepository: CoroutineCrudRepository<MediaFileTransfer, UUID> {


    suspend fun findBySupplierIdAndMd5(supplierId: UUID, md5: String): MediaFileTransfer?

    suspend fun findBySupplierIdAndFilenameAndFilesizeAndTransferStatus(supplierId: UUID, filename: String, filesize: Long, transferStatus: TransferStatus): MediaFileTransfer?

    suspend fun findBySupplierIdAndSeriesId(supplierId: UUID, seriesId: UUID): List<MediaFileTransfer>

    suspend fun findByTransferStatus(transferStatus: TransferStatus): List<MediaFileTransfer>


}