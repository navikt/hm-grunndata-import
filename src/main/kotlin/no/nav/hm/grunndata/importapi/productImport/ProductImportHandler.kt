package no.nav.hm.grunndata.importapi.productImport

import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import java.time.LocalDateTime
import java.util.UUID
import no.nav.hm.grunndata.importapi.IMPORT
import no.nav.hm.grunndata.importapi.gdb.GdbApiClient
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.importapi.supplier.toDTO
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransfer
import no.nav.hm.grunndata.importapi.transfer.product.ProductTransferDTO
import no.nav.hm.grunndata.rapid.dto.AdminStatus
import no.nav.hm.grunndata.rapid.dto.Attributes
import no.nav.hm.grunndata.rapid.dto.CompatibleWith
import no.nav.hm.grunndata.rapid.dto.ProductRapidDTO
import no.nav.hm.grunndata.rapid.dto.ProductStatus
import no.nav.hm.grunndata.rapid.dto.TechData
import org.slf4j.LoggerFactory


@Singleton
open class ProductImportHandler(private val productImportRepository: ProductImportRepository,
                                private val supplierService: SupplierService,
                                private val gdbApiClient: GdbApiClient
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductImportHandler::class.java)
    }

    @Transactional
    open suspend fun mapSaveTransferToProductImport(transfer: ProductTransfer): ProductImport {
        val supplierId = transfer.supplierId
        val supplierRef = transfer.supplierRef
        val transferId = transfer.transferId
        val productImport = productImportRepository.findBySupplierIdAndSupplierRef(supplierId, supplierRef)?.let { inDb ->
            LOG.info("Product from supplier $supplierId and ref $supplierRef found in import database ${inDb.id}")
            val productDTO = createProductDTO(inDb.id,transfer)
            productImportRepository.update(
                inDb.copy(
                    transferId = transferId,
                    productDTO = productDTO,
                    updated = LocalDateTime.now(),
                    productStatus = productDTO.status,
                    seriesId = productDTO.seriesUUID ?: UUID.fromString(productDTO.seriesId)
                )
            )
        } ?: run {
            gdbApiClient.getProductBySupplierIdAndSupplierRef(supplierId, supplierRef)?.let { dto ->
                LOG.info("Product from supplier $supplierId and ref $supplierRef found in GDB ${dto.id} ")
                val productDTO = createProductDTO(dto.id, transfer)
                productImportRepository.save(
                    ProductImport(
                        id = dto.id,
                        transferId = transferId,
                        supplierId = supplierId,
                        supplierRef = supplierRef,
                        productDTO = productDTO,
                        productStatus = productDTO.status,
                        adminStatus = AdminStatus.PENDING,
                        seriesId = productDTO.seriesUUID ?: UUID.fromString(productDTO.seriesId)
                    )
                )
            }
        } ?: run {
            val productId = UUID.randomUUID()
            LOG.info("New product for $supplierId and ref $supplierRef with new id $productId")
            val productDTO = createProductDTO(productId, transfer)
            productImportRepository.save(
                ProductImport(
                    id = productId, transferId = transfer.transferId, supplierId = transfer.supplierId,
                    supplierRef = transfer.supplierRef,
                    productDTO = productDTO,
                    productStatus = productDTO.status, adminStatus = AdminStatus.PENDING,
                    seriesId = productDTO.seriesUUID ?: UUID.fromString(productDTO.seriesId)
                )
            )
        }
        LOG.info("mapped product ${productImport.id} and transfer id: ${productImport.transferId} " +
                "for supplierId: ${productImport.supplierId} supplierRef: ${productImport.supplierRef}")

        return productImport
    }

    private suspend fun createProductDTO(productId: UUID, transfer: ProductTransfer): ProductRapidDTO {
        return transfer.json_payload.toProductRapidDTO(productId, transfer.supplierId)
    }



    private fun ProductTransferDTO.toProductRapidDTO(productId: UUID, supplierId: UUID): ProductRapidDTO {
        val nPublished = published ?: LocalDateTime.now().minusMinutes(1)
        val nExpired = expired ?: LocalDateTime.now().plusYears(10)
        return ProductRapidDTO (
            id = productId,
            supplier = supplierService.findById(supplierId)!!.toDTO(),
            title = "", // series title will be merged later.
            articleName = articleName,
            supplierRef = supplierRef,
            isoCategory = "", // isocategory will be merged later
            attributes = Attributes (
                shortdescription = articleDescription,
                compatibleWidth = if (this.compatibleWith!=null) CompatibleWith(
                    seriesIds = compatibleWith.seriesIds) else null
            ),
            identifier = productId.toString(),
            accessory = accessory,
            sparePart = sparePart,
            seriesUUID = seriesId,
            seriesId =  seriesId.toString(),
            techData = techData.map { TechData(key = it.key, unit = it.unit, value = it.value ) },
            published = nPublished,
            expired = nExpired,
            status = mapStatus(nPublished, nExpired, this),
            createdBy = IMPORT,
            updatedBy = IMPORT,
        )
    }



    private fun mapStatus(published: LocalDateTime, expired: LocalDateTime, prodctTransferDTO: ProductTransferDTO): ProductStatus {
        return if (prodctTransferDTO.status == ProductStatus.DELETED) ProductStatus.DELETED // DELETED means it is deleted, don't care about published and expired
            else if (published.isBefore(LocalDateTime.now()) && expired.isAfter(LocalDateTime.now()))  ProductStatus.ACTIVE
            else ProductStatus.INACTIVE
    }
}
