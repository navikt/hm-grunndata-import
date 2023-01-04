package no.nav.hm.grunndata.importapi

import io.micronaut.runtime.Micronaut

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("no.nav.hm.grunndata.importapi")
            .mainClass(Application.javaClass)
            .start()
    }
}