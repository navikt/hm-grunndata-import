package no.nav.hm.grunndata.importapi.transfer.media

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.transfer.product.TransferStatus
import org.junit.jupiter.api.Test
import java.util.*
import no.nav.hm.grunndata.rapid.dto.MediaType

@MicronautTest
class MediaFileTransferRepositoryTest(private val mediaFileTransferRepository: MediaFileTransferRepository) {

    @Test
    fun crudTest() {
        val media = MediaFileTransfer(
            supplierId = UUID.randomUUID(),
            seriesId = UUID.randomUUID(),
            md5 = "12345",
            filename = "12345.jpg",
            objectType = ObjectType.SERIES,
            sourceUri = "http://localhost/12345.jpg",
            uri= "12345.jpg",
            filesize = 1234L
        )
        runBlocking {
            val saved = mediaFileTransferRepository.save(media)
            saved.shouldNotBeNull()
            val found = mediaFileTransferRepository.findById(saved.transferId)
            found.shouldNotBeNull()
            found.supplierId shouldBe saved.supplierId
            found.seriesId shouldBe saved.seriesId
            found.filename shouldBe "12345.jpg"
            val update = mediaFileTransferRepository.update(found.copy(transferStatus = TransferStatus.DONE))
            update.shouldNotBeNull()
            update.transferStatus shouldBe TransferStatus.DONE
            update.filesize shouldBe 1234L
            update.objectType shouldBe ObjectType.SERIES
            update.mediaType shouldBe MediaType.IMAGE
        }
    }
}