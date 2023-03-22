package no.nav.hm.grunndata.importapi.security

import io.micronaut.core.async.publisher.Publishers.just
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.AbstractSecurityRule
import io.micronaut.security.rules.SecuredAnnotationRule
import io.micronaut.security.rules.SecurityRuleResult
import io.micronaut.security.token.RolesFinder
import io.micronaut.web.router.MethodBasedRouteMatch
import io.micronaut.web.router.RouteMatch
import org.slf4j.LoggerFactory
import java.util.*
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import org.reactivestreams.Publisher


@Singleton
class SecurityRuleImpl(rolesFinder: RolesFinder,
                       private val supplierService: SupplierService): AbstractSecurityRule(rolesFinder) {

    private val ORDER = SecuredAnnotationRule.ORDER - 1

    companion object {
        private val LOG = LoggerFactory.getLogger(SecurityRuleImpl::class.java)
    }

    override fun check(request: HttpRequest<*>, routeMatch: RouteMatch<*>?, authentication: Authentication?): Publisher<SecurityRuleResult> {
        if (routeMatch is MethodBasedRouteMatch<*, *> && authentication!=null ) {
            if (routeMatch.hasAnnotation(SecurityRule::class.java)) {
                val values = routeMatch.getValue(SecurityRule::class.java, Array<String>::class.java).get().toMutableList()
                val roles = getRoles(authentication)
                if (values.contains(Roles.ROLE_ADMIN) && roles.contains(Roles.ROLE_ADMIN)) {
                    LOG.debug("Admin request allow")
                    return just(SecurityRuleResult.ALLOWED)
                }
                val supplierId = routeMatch.variableValues["supplierId"].toString()
                if (supplierId != authentication.attributes["supplierId"]) {
                    LOG.debug("Rejected because provider id does not match with claims")
                    return just(SecurityRuleResult.REJECTED)
                }
                return runBlocking {
                    val supplier = supplierService.findById(UUID.fromString(supplierId))
                    if (authentication.attributes["jti"] != supplier?.jwtid ) {
                        LOG.warn("Rejected because jwt id does not match with claims")
                        just(SecurityRuleResult.REJECTED)
                    }
                    else
                        compareRoles(values, roles)
                }

            }
        }
        return just(SecurityRuleResult.UNKNOWN)
    }

    override fun getOrder(): Int = ORDER

}
