package no.nav.hm.grunndata.importapi.techdata

import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import org.slf4j.LoggerFactory

@Singleton
class TechDataLabelService(private val gdbApiClient: GdbApiClient) {

    private val techLabelsByName: Map<String, TechDataLabelDTO> = gdbApiClient.fetchAllTechLabels().associateBy { it.label.lowercase().trim() }
    private val techLabelsByIso: Map<String, List<TechDataLabelDTO>> = techLabelsByName.values.groupBy { it.isocode }


    companion object {
        private val LOG = LoggerFactory.getLogger(TechDataLabelService::class.java)
    }

    init {
        LOG.info("Init techlabels size ${techLabelsByIso.size}")
    }

    fun fetchTechDataLabelsByIsoCode(isocode: String): List<TechDataLabelDTO>? = techLabelsByIso[isocode]

    fun fetchAllTechDataLabels(): Map<String, List<TechDataLabelDTO>> = techLabelsByIso

    fun fetchTechDataLabelByKeyName(keyName: String): TechDataLabelDTO? = techLabelsByName[keyName.lowercase().trim()]


}