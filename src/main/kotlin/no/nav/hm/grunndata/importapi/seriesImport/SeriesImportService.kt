package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.CacheInvalidate
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import java.util.*

@Singleton
class SeriesImportService(private val seriesImportRepository: SeriesImportRepository) {

    fun findByIdCacheable(id: UUID): SeriesImportDTO? =
        runBlocking { seriesImportRepository.findById(id)?.toDTO() }

    suspend fun findBySupplierId(supplierId: UUID) = seriesImportRepository.findBySupplierId(supplierId).map { it.toDTO() }

    suspend fun findBySupplierIdAndSeriesId(supplierId: UUID, seriesId: UUID) =
        seriesImportRepository.findBySupplierIdAndSeriesId(supplierId, seriesId)?.toDTO()

    suspend fun save(dto: SeriesImportDTO, id: UUID = dto.id): SeriesImportDTO =
        seriesImportRepository.save(dto.toEntity()).toDTO()


    suspend fun update(dto: SeriesImportDTO, id: UUID = dto.id): SeriesImportDTO =
        seriesImportRepository.update(dto.toEntity()).toDTO()

    suspend fun findBySeriesId(seriesId: UUID) = seriesImportRepository.findById(seriesId)?.toDTO()

}