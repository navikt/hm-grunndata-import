package no.nav.hm.grunndata.importapi.transfer.media

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import java.util.UUID
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import org.junit.jupiter.api.Test

@MicronautTest
class MediaMetaTransferRepositoryTest(private val mediaMetaTransferRepository: MediaMetaTransferRepository) {

    @Test
    fun crudTest() {
        runBlocking {
            val seriesId = UUID.randomUUID()
            val supplierId = UUID.randomUUID()
            val saved = mediaMetaTransferRepository.save(MediaMetaTransfer(
                uri = "http://example1234.com",
                supplierId = supplierId,
                seriesId = seriesId,
                text = "Dette er en beskrivelse",
                priority = 2,
            ))
            saved.shouldNotBeNull()
            saved.transferId.shouldNotBeNull()
            val found = mediaMetaTransferRepository.findById(saved.transferId)
            found.shouldNotBeNull()
            found.supplierId shouldBe supplierId
            found.seriesId shouldBe seriesId
            found.text shouldBe "Dette er en beskrivelse"
            found.status shouldBe MediaMetaTranferStatus.ACTIVE
            found.transferStatus shouldBe TransferStatus.RECEIVED
        }
    }
}