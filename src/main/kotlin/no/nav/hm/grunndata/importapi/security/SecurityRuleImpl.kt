package no.nav.hm.grunndata.importapi.security

import io.micronaut.core.async.publisher.Publishers.just
import io.micronaut.http.HttpAttributes
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.AbstractSecurityRule
import io.micronaut.security.rules.SecuredAnnotationRule
import io.micronaut.security.rules.SecurityRuleResult
import io.micronaut.security.token.RolesFinder
import io.micronaut.web.router.MethodBasedRouteMatch
import io.micronaut.web.router.RouteMatch
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import no.nav.hm.grunndata.importapi.supplier.SupplierService
import no.nav.hm.grunndata.rapid.dto.SupplierStatus
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.util.*


@Singleton
class SecurityRuleImpl(rolesFinder: RolesFinder,
                       private val supplierService: SupplierService):
    AbstractSecurityRule<HttpRequest<*>>(rolesFinder) {

    private val ORDER = SecuredAnnotationRule.ORDER - 100;

    companion object {
        private val LOG = LoggerFactory.getLogger(SecurityRuleImpl::class.java)
    }

    init {
        LOG.info("Starting security rule!")
    }

    override fun getOrder(): Int = ORDER
    override fun check(request: HttpRequest<*>, authentication: Authentication?): Publisher<SecurityRuleResult> {
        val routeMatch = request.getAttribute(HttpAttributes.ROUTE_MATCH, RouteMatch::class.java).orElse(null)
        if (routeMatch!=null && routeMatch is MethodBasedRouteMatch<*, *> && authentication!=null ) {
            if (routeMatch.hasAnnotation(SecuritySupplierRule::class.java)) {
                val values = routeMatch.getValue(SecuritySupplierRule::class.java, Array<String>::class.java).get().toMutableList()
                val roles = getRoles(authentication)
                if (values.contains(Roles.ROLE_ADMIN) && roles.contains(Roles.ROLE_ADMIN)) {
                    LOG.info("Admin request allow")
                    return just(SecurityRuleResult.ALLOWED)
                }
                val identifier = routeMatch.variableValues["identifier"].toString()
                val supplierId = authentication.supplierId()
                val supplier = supplierService.findByIdentifier(identifier)
                if (supplierId != supplier?.id ) {
                    LOG.info("identifier $identifier $supplierId not allow")
                    return just(SecurityRuleResult.REJECTED)
                }
                return runBlocking {
                    if (authentication.attributes["jti"] != supplier?.jwtid || SupplierStatus.ACTIVE != supplier?.status ) {
                        LOG.warn("Rejected because jwt id does not match with claims, or supplier $supplierId is no longer active")
                        just(SecurityRuleResult.REJECTED)
                    }
                    else
                        compareRoles(values, roles)
                }

            }
        }
        return just(SecurityRuleResult.UNKNOWN)
    }
}
fun Authentication.supplierId() = UUID.fromString(attributes["supplierId"].toString())
fun Authentication.identifier() = attributes["identifier"].toString()
