package no.nav.hm.grunndata.importapi.security

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.micronaut.security.token.jwt.signature.secret.SecretSignatureConfiguration
import java.util.*
import jakarta.inject.Singleton
import no.nav.hm.grunndata.importapi.supplier.Supplier
import org.slf4j.LoggerFactory

@Singleton
class TokenService(private val secretSignatureConfiguration: SecretSignatureConfiguration) {

    companion object {
        private val LOG = LoggerFactory.getLogger(TokenService::class.java)
    }
    init {
        if (secretSignatureConfiguration.secret == "MustBeAVeryLongSecretAndUsedThisForTestImportOnly")
            LOG.warn("Using test key, can only be used in local/dev")
    }

    fun token(supplier: Supplier): String {
        val signer = MACSigner(secretSignatureConfiguration.secret)
        val claimsSet = JWTClaimsSet.Builder()
                .subject(supplier.name)
                .jwtID(supplier.jwtid)
                .issuer("https://finnhjelpemidler.nav.no")
                .issueTime(Date())
                .claim("roles", Roles.ROLE_SUPPLIER)
                .claim("supplierId", supplier.id)
                .build()
        val signedJWT = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claimsSet)
        signedJWT.sign(signer)
        return signedJWT.serialize()
    }

    fun adminToken(subject: String): String {
        val signer = MACSigner(secretSignatureConfiguration.secret)
        val claimsSet = JWTClaimsSet.Builder()
                .subject(subject)
                .jwtID(UUID.randomUUID().toString())
                .issuer("https://finnhjelpemidler.nav.no")
                .issueTime(Date())
                .claim("roles", Roles.ROLE_ADMIN)
                .build()
        val signedJWT = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claimsSet)
        signedJWT.sign(signer)
        return signedJWT.serialize()
    }
}
