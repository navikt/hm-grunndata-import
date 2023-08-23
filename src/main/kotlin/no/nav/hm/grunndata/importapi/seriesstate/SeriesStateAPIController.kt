package no.nav.hm.grunndata.importapi.seriesstate

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import java.util.*

@Controller(SeriesStateAPIController.SERIES_ENDPOINT)
class SeriesStateAPIController(private val seriesStateRepository: SeriesStateRepository) {


    companion object {
        private val LOG = LoggerFactory.getLogger(SeriesStateAPIController::class.java)
        const val SERIES_ENDPOINT = "/api/v1/series"
    }

    @Get("/{supplierId}")
    suspend fun getSeriesBySupplierId(supplierId: UUID): Flow<SeriesStateDTO> = seriesStateRepository
        .findBySupplierId(supplierId).map { it.toDTO() }

}