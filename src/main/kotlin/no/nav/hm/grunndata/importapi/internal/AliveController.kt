package no.nav.hm.grunndata.importapi.internal

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.openapi.visitor.security.SecurityRule
import io.micronaut.security.annotation.Secured
import io.swagger.v3.oas.annotations.Hidden

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/internal")
@Hidden
class AliveController {

    @Get("/isAlive")
    fun alive() = "ALIVE"

    @Get("/isReady")
    fun ready() = "OK"

}