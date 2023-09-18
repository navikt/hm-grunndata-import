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
    open fun findByIdCacheable(id: String): SeriesImportDTO? = runBlocking {
        seriesImportRepository.findById(id)?.toDTO()
    }

    suspend fun findBySupplierId(supplierId: UUID) = seriesImportRepository.findBySupplierId(supplierId).map { it.toDTO() }

    suspend fun findBySupplierIdAndName(supplierId: UUID, name: String) =
        seriesImportRepository.findBySupplierIdAndName(supplierId, name)?.toDTO()

    @CacheInvalidate(parameters = ["id"])
    open fun save(dto: SeriesImportDTO, id: String? = dto.id): SeriesImportDTO = runBlocking {
        seriesImportRepository.save(dto.toEntity()).toDTO()
    }

    @CacheInvalidate(parameters = ["id"])
    open fun update(dto: SeriesImportDTO, id: String? = dto.id): SeriesImportDTO? = runBlocking {
        seriesImportRepository.update(dto.toEntity()).toDTO()
    }

}