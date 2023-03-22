package no.nav.hm.grunndata.importapi.productstate

import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.transferstate.TransferStateRepository
import no.nav.hm.grunndata.importapi.transferstate.TransferStatus

@Singleton
class TransferToProductState(private val productStateRepository: ProductStateRepository,
                             private val transferStateRepository: TransferStateRepository) {

    fun receivedTransfersToProductState() {
        runBlocking {
            transferStateRepository.findByTransferStatus(TransferStatus.RECEIVED, Pageable.unpaged())
        }
    }
}
