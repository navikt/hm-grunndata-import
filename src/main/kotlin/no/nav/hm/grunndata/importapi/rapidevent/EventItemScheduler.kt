package no.nav.hm.grunndata.importapi.rapidevent

import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import no.nav.hm.micronaut.leaderelection.LeaderOnly

@Singleton
@Requires(property = "schedulers.enabled", value = "true")
open class EventItemScheduler(
    private val eventItemService: EventItemService
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(EventItemScheduler::class.java)
    }

    @LeaderOnly
    @Scheduled(fixedDelay = "15m")
    open fun sendEventItemScheduler() {
        runBlocking {
            val items = eventItemService.getAllPendingStatus().sortedBy { it.updated }
            LOG.info("Running sendEventItemScheduler with ${items.size} items")
            items.forEach {
                LOG.info("sending event ${it.oid} with type ${it.type}")
                eventItemService.sendRapidEvent(it)
            }
        }
    }

    @LeaderOnly
    @Scheduled(cron = "0 0 0 * * ?")
    open fun deleteSentItemScheduler() {
        runBlocking {
            val before = LocalDateTime.now().minusDays(30)
            val deleted = eventItemService.deleteByStatusAndUpdatedBefore(EventItemStatus.SENT, before)
            LOG.info("Running deleteSentItemScheduler with $deleted items before: $before")
        }
    }
}