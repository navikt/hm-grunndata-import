package no.nav.hm.grunndata.importapi.transfer.media

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
class MediaTransferRepositoryTest(private val mediaTransferRepository: MediaTransferRepository) {

    @Test
    fun crudTest() {
        val media = MediaTransfer(
            supplierId = UUID.randomUUID(),
            seriesId = UUID.randomUUID(),
            md5 = "12345",
            filename = "12345.jpg",
            sourceUri = "http://localhost/12345.jpg",
            uri= "12345.jpg",
            filesize = 1234L
        )
        runBlocking {
            val saved = mediaTransferRepository.save(media)
            saved.shouldNotBeNull()
            val found = mediaTransferRepository.findById(saved.transferId)
            found.shouldNotBeNull()
            found.supplierId shouldBe saved.supplierId
            found.seriesId shouldBe saved.seriesId
            found.filename shouldBe "12345.jpg"
            val update = mediaTransferRepository.update(found.copy(transferStatus = TransferStatus.DONE))
            update.shouldNotBeNull()
            update.transferStatus shouldBe TransferStatus.DONE
            update.filesize shouldBe 1234L
        }
    }
}