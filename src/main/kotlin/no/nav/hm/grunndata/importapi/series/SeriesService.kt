package no.nav.hm.grunndata.importapi.series

import jakarta.inject.Singleton
import java.util.*

@Singleton
class SeriesService(private val seriesRepository: SeriesRepository) {

    suspend fun findById(id: UUID): SeriesDTO? = seriesRepository.findById(id)?.toDTO()

    suspend fun save(dto: SeriesDTO): SeriesDTO = seriesRepository.save(dto.toEntity()).toDTO()

    suspend fun update(dto: SeriesDTO): SeriesDTO = seriesRepository.update(dto.toEntity()).toDTO()

}