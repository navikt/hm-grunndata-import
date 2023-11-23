package no.nav.hm.grunndata.importapi.supplier

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.CacheInvalidate
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import java.util.*


@Singleton
@CacheConfig("suppliers")
open class SupplierService(private val supplierRepository: SupplierRepository) {

    @Cacheable
    open fun findById(id: UUID): Supplier? = runBlocking { supplierRepository.findById(id) }

    @CacheInvalidate(parameters = ["id"])
    open fun update(supplier: Supplier, id: UUID = supplier.id) = runBlocking { supplierRepository.update(supplier) }


    @CacheInvalidate(parameters = ["id"])
    open  fun save(supplier: Supplier, id: UUID = supplier.id) = runBlocking{ supplierRepository.save(supplier) }
    @CacheInvalidate(parameters = ["identifier"])
    open fun findByIdentifier(identifier: String) = runBlocking { supplierRepository.findByIdentifier(identifier) }
}
