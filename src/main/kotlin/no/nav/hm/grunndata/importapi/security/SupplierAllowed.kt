package no.nav.hm.grunndata.importapi.security

import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class SupplierAllowed (vararg val value: String)
