package no.nav.hm.grunndata.importapi.transfer.media

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus

@JdbcRepository(dialect = Dialect.POSTGRES)
interface MediaMetaTransferRepository: CoroutineCrudRepository<MediaMetaTransfer, UUID> {
    suspend fun findByTransferStatus(transferStatus: TransferStatus): List<MediaMetaTransfer>
    suspend fun findBySupplierIdAndSeriesId(supplierId: UUID, seriesId: UUID): List<MediaMetaTransfer>
    suspend fun findBySupplierIdAndSeriesIdAndUri(supplierId: UUID, seriesId: UUID, uri: String): MediaMetaTransfer?
    suspend fun findBySupplierIdAndMd5(supplierId: UUID?, md5: String): MediaMetaTransfer?

}