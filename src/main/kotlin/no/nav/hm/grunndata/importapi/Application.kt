package no.nav.hm.grunndata.importapi

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme

@OpenAPIDefinition(
    info = Info(
        title = "Accessibility Devices Vendor API",
        version = "0.1",
        description = "NAVs Api for transferring accessibility devices"
    )
)
@SecurityScheme(name = "bearer-auth", type = SecuritySchemeType.HTTP, scheme = "bearer",
    `in` = SecuritySchemeIn.HEADER, bearerFormat = "JWT")
object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("no.nav.hm.grunndata.importapi")
            .mainClass(Application.javaClass)
            .start()
    }
}

const val IMPORT = "IMPORT"