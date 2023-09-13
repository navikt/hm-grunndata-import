package no.nav.hm.grunndata.importapi.productImport

import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.productadminstate.ProductAdminState
import no.nav.hm.grunndata.importapi.productadminstate.ProductAdminStateRepository
import no.nav.hm.grunndata.importapi.transfer.product.TransferStateRepository
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import no.nav.hm.grunndata.rapid.dto.ProductStatus
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Singleton
class TransferToProductImport(private val transferStateRepository: TransferStateRepository,
                              private val productAdminStateRepository: ProductAdminStateRepository,
                              private val productImportKafkaService: ProductImportKafkaService
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(TransferToProductImport::class.java)
    }

    suspend fun receivedTransfersToProductState() {
        val contents = transferStateRepository.findByTransferStatus(TransferStatus.RECEIVED).content
        LOG.info("Got ${contents.size} transfers to map to products")
        contents.forEach {
            val productImport = productImportKafkaService.mapSaveTransferToProductImport(it)
            val productAdminState = productAdminStateRepository.findById(productImport.id)?.let { inDb ->
                productAdminStateRepository.update(inDb.copy(
                    transferId = productImport.transferId,
                    productStatus = productImport.productDTO.status,
                    updated = LocalDateTime.now(),
                    version = productImport.version
                ))
            } ?: productAdminStateRepository.save(
                ProductAdminState(
                    id = productImport.id,
                    transferId = productImport.transferId,
                    supplierId = productImport.supplierId,
                    supplierRef = productImport.supplierRef,
                    productStatus = productImport.productDTO.status,
                    version = productImport.version,
                )
            )
            LOG.info("Product admin state created for ${productAdminState.id} and transfer: ${productAdminState.transferId}")
            transferStateRepository.update(it.copy(transferStatus = TransferStatus.DONE, updated = LocalDateTime.now()))
        }
        //TODO feilhåndtering her
    }

}
