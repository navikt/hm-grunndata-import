package no.nav.hm.grunndata.importapi.seriesImport

import io.micronaut.context.annotation.Factory
import io.micronaut.data.event.listeners.PostPersistEventListener
import io.micronaut.data.event.listeners.PostUpdateEventListener
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.mediaImport.MediaImport
import no.nav.hm.grunndata.importapi.mediaImport.MediaImportStatus
import no.nav.hm.grunndata.rapid.dto.MediaInfo
import org.slf4j.LoggerFactory

@Factory
class MediaImportPersistListener(private val seriesImportRepository: SeriesImportRepository) {

    companion object {
        private val LOG = LoggerFactory.getLogger(MediaImportPersistListener::class.java)
    }

    @Singleton
    fun afterProductPersist(): PostPersistEventListener<MediaImport> {
        return PostPersistEventListener { mediaImport: MediaImport ->
            runBlocking {
                LOG.debug("Media import state inserted for media: ${mediaImport.id} and series: ${mediaImport.seriesId}")
                //updateSeriesMedia(mediaImport)
            }
        }
    }

    @Singleton
    fun afterProductUpdate(): PostUpdateEventListener<MediaImport> {
        return PostUpdateEventListener { mediaImport: MediaImport ->
            runBlocking {
                LOG.debug("Media import state updated for media: ${mediaImport.id} and series: ${mediaImport.seriesId}")
                //updateSeriesMedia(mediaImport)
            }
        }
    }

    private suspend fun updateSeriesMedia(mediaImport: MediaImport) {
        seriesImportRepository.findById(mediaImport.seriesId)?.let {
            if (mediaImport.status == MediaImportStatus.ACTIVE) {
                val mediaList = it.seriesData.media + MediaInfo(
                    uri = mediaImport.uri,
                    text = mediaImport.text,
                    filename = mediaImport.filename,
                    sourceUri = mediaImport.sourceUri,
                    source = mediaImport.sourceType,
                    type = mediaImport.type,
                    priority = mediaImport.priority,
                    updated = mediaImport.updated
                )
                seriesImportRepository.update(it.copy(seriesData = it.seriesData.copy(media = mediaList)))
            } else {
                val mediaList = it.seriesData.media.filter { m -> m.uri != mediaImport.uri }.toSet()
                seriesImportRepository.update(it.copy(seriesData = it.seriesData.copy(media = mediaList)))
            }
        }
    }
}