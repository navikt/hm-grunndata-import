package no.nav.hm.grunndata.importapi.productstate

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Requires
import kotlinx.coroutines.runBlocking
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.KafkaRapid
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.River
import no.nav.hm.grunndata.rapid.dto.DraftStatus
import no.nav.hm.grunndata.rapid.dto.ProductDTO
import no.nav.hm.grunndata.rapid.dto.ProductRegistrationDTO
import no.nav.hm.grunndata.rapid.dto.rapidDTOVersion
import no.nav.hm.grunndata.rapid.event.EventName
import no.nav.hm.grunndata.rapid.event.RapidApp
import no.nav.hm.rapids_rivers.micronaut.RiverHead
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Context
@Requires(bean = KafkaRapid::class)
class AdminStatusSyncRiver(river: RiverHead,
                           private val objectMapper: ObjectMapper,
                           private val productStateRepository: ProductStateRepository): River.PacketListener {

    companion object {
        private val LOG = LoggerFactory.getLogger(AdminStatusSyncRiver::class.java)
    }

    init {
        river
            .validate { it.demandValue("createdBy", RapidApp.grunndata_register)}
            .validate { it.demandValue("eventName", EventName.registeredProductV1)}
            .validate { it.demandValue("payloadType", ProductRegistrationDTO::class.java.simpleName)}
            .validate { it.demandKey("payload")}
            .validate { it.demandKey("dtoVersion")}
            .register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val dtoVersion = packet["dtoVersion"].asLong()
        if (dtoVersion > rapidDTOVersion)
            LOG.warn("this event dto version $dtoVersion is newer than our version: $rapidDTOVersion")
        val dto = objectMapper.treeToValue(packet["payload"], ProductRegistrationDTO::class.java)
        if (DraftStatus.DONE == dto.draftStatus && "IMPORT" == dto.productDTO.createdBy) {
            runBlocking {
                productStateRepository.findById(dto.id)?.let {
                    productStateRepository.update(it.copy(adminStatus = dto.adminStatus,
                        updated = LocalDateTime.now(), adminMessage = dto.message
                    ))
                }
            }
        }
    }

}
