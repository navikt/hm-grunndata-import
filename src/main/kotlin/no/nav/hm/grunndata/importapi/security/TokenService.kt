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

@Singleton
class TokenService(private val secretSignatureConfiguration: SecretSignatureConfiguration) {


    fun token(supplier: Supplier): String {
        val signer = MACSigner(secretSignatureConfiguration.secret)
        val claimsSet = JWTClaimsSet.Builder()
                .subject(supplier.name)
                .jwtID(supplier.jwtid.toString())
                .issuer("https://hjelpemiddeldabasen.nav.no")
                .issueTime(Date())
                .claim("roles", Roles.ROLE_SUPPLIER)
                .claim("supplierId", supplier.id)
                .build()
        val signedJWT = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claimsSet)
        signedJWT.sign(signer)
        return signedJWT.serialize()
    }

    fun adminToken(): String {
        val signer = MACSigner(secretSignatureConfiguration.secret)
        val claimsSet = JWTClaimsSet.Builder()
                .subject("hjelpemiddeldatabasen@nav.no")
                .jwtID(UUID.randomUUID().toString())
                .issuer("https://hjelpemiddeldabasen.nav.no")
                .issueTime(Date())
                .claim("roles",Roles.ROLE_ADMIN)
                .build()
        val signedJWT = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claimsSet)
        signedJWT.sign(signer)
        return signedJWT.serialize()
    }
}
