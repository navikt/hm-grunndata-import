package no.nav.hm.grunndata.importapi.supplier

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.CacheInvalidate
import io.micronaut.cache.annotation.Cacheable
import java.util.*


@CacheConfig("suppliers")
class SupplierService(private val supplierRepository: SupplierRepository) {

    @Cacheable
    suspend fun findById(id: UUID): Supplier? = supplierRepository.findById(id)

    @CacheInvalidate(parameters = ["id"])
    suspend fun update(supplier: Supplier, id: UUID = supplier.id) = supplierRepository.update(supplier)


    @CacheInvalidate(parameters = ["id"])
    suspend fun save(supplier: Supplier, id: UUID = supplier.id) = supplierRepository.save(supplier)

}
