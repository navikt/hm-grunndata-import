package no.nav.hm.grunndata.importapi.productstate

import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking

@Singleton
@Requires(property = "schedulers.enabled", value = "true")
class TransferToProductStateScheduler(private val transferToProductState: TransferToProductState) {


    @Scheduled(fixedDelay = "1m")
    fun transferToProductStateTask() {
        runBlocking {
            transferToProductState.receivedTransfersToProductState()
        }
    }


}
