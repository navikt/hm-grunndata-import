package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.CacheInvalidate
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import java.util.*

@Singleton
@CacheConfig("series")
open class SeriesImportService(private val seriesImportRepository: SeriesImportRepository) {

    @Cacheable
    open fun findByIdCacheable(id: UUID): SeriesImportDTO? =
        runBlocking { seriesImportRepository.findById(id)?.toDTO() }

    suspend fun findBySupplierId(supplierId: UUID) = seriesImportRepository.findBySupplierId(supplierId).map { it.toDTO() }

    suspend fun findBySupplierIdAndSeriesId(supplierId: UUID, seriesId: UUID) =
        seriesImportRepository.findBySupplierIdAndSeriesId(supplierId, seriesId)?.toDTO()

    @CacheInvalidate(parameters = ["id"])
    open fun save(dto: SeriesImportDTO, id: UUID = dto.seriesId): SeriesImportDTO = runBlocking {
        seriesImportRepository.save(dto.toEntity()).toDTO()
    }

    @CacheInvalidate(parameters = ["id"])
    open fun update(dto: SeriesImportDTO, id: UUID = dto.seriesId): SeriesImportDTO? = runBlocking {
        seriesImportRepository.update(dto.toEntity()).toDTO()
    }

}