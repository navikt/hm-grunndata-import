package no.nav.hm.grunndata.importapi.transferstate

import io.micronaut.http.annotation.Controller
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.hm.grunndata.importapi.security.Roles
import no.nav.hm.grunndata.importapi.security.SupplierAllowed

@SupplierAllowed(value = [Roles.ROLE_SUPPLIER, Roles.ROLE_ADMIN])
@Controller("/api/v1/transfers")
@SecurityRequirement(name = "bearer-auth")
class ProductTransferController {
}
