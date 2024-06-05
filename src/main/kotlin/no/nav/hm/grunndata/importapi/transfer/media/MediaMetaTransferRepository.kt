package no.nav.hm.grunndata.importapi.transfer.media

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus

@JdbcRepository(dialect = Dialect.POSTGRES)
interface MediaMetaTransferRepository: CoroutineCrudRepository<MediaMetaTransfer, UUID> {
    suspend fun findByTransferStatus(transferStatus: TransferStatus): List<MediaMetaTransfer>

}