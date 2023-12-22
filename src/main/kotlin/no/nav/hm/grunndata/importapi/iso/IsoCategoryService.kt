package no.nav.hm.grunndata.importapi.iso

import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import no.nav.hm.grunndata.rapid.dto.IsoCategoryDTO
import org.slf4j.LoggerFactory

@Singleton
class IsoCategoryService(gdbApiClient: GdbApiClient) {

    private var isoCategories: Map<String, IsoCategoryDTO>

    companion object {
        private val LOG = LoggerFactory.getLogger(IsoCategoryService::class.java)
    }

    init {
        runBlocking {
            isoCategories =  gdbApiClient.retrieveIsoCategories().associateBy { it.isoCode }
        }
        LOG.info("Got isoCategories: ${isoCategories.size}")
    }

    fun lookUpCode(isoCode: String): IsoCategoryDTO? = isoCategories[isoCode]

    fun retrieveAllCategories(): List<IsoCategoryDTO> = isoCategories.values.toList()

}
