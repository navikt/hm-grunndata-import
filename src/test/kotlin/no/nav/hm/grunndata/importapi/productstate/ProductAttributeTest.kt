package no.nav.hm.grunndata.importapi.productstate

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import no.nav.hm.grunndata.importapi.transferstate.Attributes
import no.nav.hm.grunndata.importapi.transferstate.CompatibleAttribute
import no.nav.hm.grunndata.rapid.dto.AttributeNames
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
        val attributesMap: Map<AttributeNames, Any> = mapOf(
            AttributeNames.shortdescription to  "shortdescription",
            AttributeNames.text to "text",
            AttributeNames.manufacturer to "manufacturer",
            AttributeNames.series to "series",
            AttributeNames.compatible to listOf(CompatibleAttribute(hmsArtNr = "hmsArtNr")),
            AttributeNames.url to "url"

        )
        // Testing jsons should be the same
        val attributesDes = objectMapper.readValue(objectMapper.writeValueAsString(attributesMap), Attributes::class.java)
        attributesDes.shortdescription shouldBe attributes.shortdescription
        attributesDes.text shouldBe attributes.text

    }
}
