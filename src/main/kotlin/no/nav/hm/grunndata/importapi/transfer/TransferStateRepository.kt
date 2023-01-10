package no.nav.hm.grunndata.importapi.transfer

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

@Repository
interface TransferStateRepository: CoroutineCrudRepository<TransferState, UUID> {

}