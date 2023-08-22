package no.nav.hm.grunndata.importapi.productstate

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import no.nav.hm.grunndata.importapi.transferstate.Attributes
import no.nav.hm.grunndata.importapi.transferstate.CompatibleAttribute
import org.junit.jupiter.api.Test

@MicronautTest
class ProductAttributeTest(private val objectMapper: ObjectMapper) {

    @Test
    fun attributesTest() {
        val attributes = Attributes(
            shortdescription = "shortdescription",
            text = "text",
            manufacturer = "manufacturer",
            series = "series",
            compatible = listOf(CompatibleAttribute(hmsArtNr = "hmsArtNr")),
            url = "url"
        )
        val attributesMap: Map<String, Any> = mapOf(
            "shortdescription" to  "shortdescription",
            "text" to "text",
            "manufacturer" to "manufacturer",
            "series" to "series",
            "compatible" to listOf(CompatibleAttribute(hmsArtNr = "hmsArtNr")),
            "url" to "url"

        )
        // Testing jsons should be the same
        val attributesDes = objectMapper.readValue(objectMapper.writeValueAsString(attributesMap), Attributes::class.java)
        attributesDes.shortdescription shouldBe attributes.shortdescription
        attributesDes.text shouldBe attributes.text

    }
}
