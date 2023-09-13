package no.nav.hm.grunndata.importapi.productadminstate

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Requires
import kotlinx.coroutines.runBlocking
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.KafkaRapid
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.River
import no.nav.hm.grunndata.importapi.IMPORT
import no.nav.hm.grunndata.importapi.productImport.ProductImportRepository
import no.nav.hm.grunndata.rapid.dto.DraftStatus
import no.nav.hm.grunndata.rapid.dto.ProductRegistrationRapidDTO
import no.nav.hm.grunndata.rapid.dto.rapidDTOVersion
import no.nav.hm.grunndata.rapid.event.EventName
import no.nav.hm.grunndata.rapid.event.RapidApp
import no.nav.hm.rapids_rivers.micronaut.RiverHead
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

@Context
@Requires(bean = KafkaRapid::class)
class AdminStatusSyncRiver(river: RiverHead,
                           private val objectMapper: ObjectMapper,
                           private val productAdminStateRepository: ProductAdminStateRepository
): River.PacketListener {

    companion object {
        private val LOG = LoggerFactory.getLogger(AdminStatusSyncRiver::class.java)
    }

    init {
        river
            .validate { it.demandValue("createdBy", RapidApp.grunndata_register)}
            .validate { it.demandValue("eventName", EventName.registeredProductV1)}
            .validate { it.demandKey("transferId")}
            .validate { it.demandKey( "version")}
            .validate { it.demandKey("payload")}
            .validate { it.demandKey("dtoVersion")}
            .register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val dtoVersion = packet["dtoVersion"].asLong()
        val version = packet["version"].asLong()
        val transferId = UUID.fromString(packet["transferId"].textValue())
        if (dtoVersion > rapidDTOVersion)
            LOG.warn("this event dto version $dtoVersion is newer than our version: $rapidDTOVersion")
        val dto = objectMapper.treeToValue(packet["payload"], ProductRegistrationRapidDTO::class.java)
        if (DraftStatus.DONE == dto.draftStatus && IMPORT == dto.productDTO.createdBy) {
            runBlocking {
                LOG.info("adminstatus sync for ${dto.id} with adminstatus ${dto.adminStatus} " +
                        "with transferId $transferId and version: $version ")
                val adminState = productAdminStateRepository.findById(dto.id)!!
                if (adminState.version == version) {
                    productAdminStateRepository.update(
                        adminState.copy(
                            productStatus = dto.productDTO.status,
                            adminStatus = dto.adminStatus,
                            adminMessage = dto.message,
                            updated = LocalDateTime.now()
                        )
                    )
                }
                else {
                    LOG.info("Registration version $version is not the same as admin state version ${adminState.version} " +
                            "will skip this")
                }
            }
        }
    }

}
