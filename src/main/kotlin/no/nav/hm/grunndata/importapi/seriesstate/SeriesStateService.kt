package no.nav.hm.grunndata.importapi.seriesstate

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.CacheInvalidate
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.transfer.series.SeriesTransfer
import java.util.*

@Singleton
@CacheConfig("series")
open class SeriesStateService(private val seriesStateRepository: SeriesStateRepository) {

    @Cacheable
    open fun findByIdCacheable(id: String): SeriesStateDTO? = runBlocking {
        seriesStateRepository.findById(id)?.toDTO()
    }

    suspend fun findBySupplierId(supplierId: UUID) = seriesStateRepository.findBySupplierId(supplierId).map { it.toDTO() }

    suspend fun findBySupplierIdAndName(supplierId: UUID, name: String) =
        seriesStateRepository.findBySupplierIdAndName(supplierId, name)?.toDTO()

    @CacheInvalidate(parameters = ["id"])
    open fun save(dto: SeriesStateDTO, id: String? = dto.id): SeriesStateDTO = runBlocking {
        seriesStateRepository.save(dto.toEntity()).toDTO()
    }

    @CacheInvalidate(parameters = ["id"])
    open fun update(dto: SeriesStateDTO, id: String? = dto.id): SeriesStateDTO? = runBlocking {
        seriesStateRepository.update(dto.toEntity()).toDTO()
    }

    fun mapSaveSeriesTransferToSeriesState(seriesTransfer: SeriesTransfer): SeriesState {
        TODO()
    }

}