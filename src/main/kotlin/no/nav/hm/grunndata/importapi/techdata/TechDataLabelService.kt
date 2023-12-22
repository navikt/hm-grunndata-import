package no.nav.hm.grunndata.importapi.techdata

import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import org.slf4j.LoggerFactory

@Singleton
class TechDataLabelService(private val gdbApiClient: GdbApiClient) {


    private var techLabelsByIso: Map<String, List<TechLabelDTO>>
    private var techLabelsByName: Map<String, List<TechLabelDTO>>



    companion object {
        private val LOG = LoggerFactory.getLogger(TechDataLabelService::class.java)
    }

    init {
        runBlocking {
            val techLabels = gdbApiClient.fetchAllTechLabels()
            techLabelsByIso = techLabels.groupBy { it.isocode }
            techLabelsByName = techLabels.groupBy { it.label }
            LOG.info("Init techlabels size ${techLabelsByIso.size}")
        }
    }

    fun fetchTechDataLabelsByIsoCode(isocode: String): List<TechLabelDTO>? = techLabelsByIso[isocode]

    fun fetchAllTechDataLabels(): Map<String, List<TechLabelDTO>> = techLabelsByIso

    fun fetchTechDataLabelByKeyName(keyName: String): List<TechLabelDTO>? = techLabelsByName[keyName.lowercase().trim()]


}