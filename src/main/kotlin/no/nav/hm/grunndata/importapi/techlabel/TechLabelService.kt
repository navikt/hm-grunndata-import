package no.nav.hm.grunndata.importapi.techlabel

import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class TechLabelService(private val gdbApiClient: GdbApiClient) {

    private val techLabelsByName: Map<String, TechLabelDTO> = gdbApiClient.fetchAllTechLabels().associateBy { it.label }
    private val techLabelsByIso: Map<String, List<TechLabelDTO>> = techLabelsByName.values.groupBy { it.isocode }


    companion object {
        private val LOG = LoggerFactory.getLogger(TechLabelService::class.java)
    }

    init {
        LOG.info("Init techlabels size ${techLabelsByIso.size}")
    }

    fun fetchLabelsByIsoCode(isocode: String): List<TechLabelDTO>? = techLabelsByIso[isocode]

    fun fetchAllLabels(): Map<String, List<TechLabelDTO>> = techLabelsByIso

    fun fetchLabelByKeyName(keyName: String): TechLabelDTO? = techLabelsByName[keyName]


}