package no.nav.hm.grunndata.importapi.gdb

import io.micronaut.data.model.Page
import io.micronaut.http.MediaType.APPLICATION_JSON
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import no.nav.hm.grunndata.importapi.techdata.TechDataLabelDTO
import no.nav.hm.grunndata.rapid.dto.IsoCategoryDTO
import no.nav.hm.grunndata.rapid.dto.ProductRapidDTO
import java.util.*

@Client("\${grunndata.db.url}")
interface GdbApiClient {

    @Get(uri="/api/v1/products", consumes = [APPLICATION_JSON])
    fun findProducts(params: Map<String, String>?=null,
                     @QueryValue("size") size: Int? = null,
                     @QueryValue("page") page: Int?=null,
                     @QueryValue("sort") sort: String? = null): Page<ProductRapidDTO>

    @Get(uri="/api/v1/isocategories", consumes = [APPLICATION_JSON])
    fun retrieveIsoCategories(): List<IsoCategoryDTO>

    @Get(uri="/api/v1/techlabels", consumes = [APPLICATION_JSON])
    fun fetchAllTechLabels(): List<TechDataLabelDTO>

    @Get(uri="/api/v1/products/{supplierId}/{supplierRef", consumes = [APPLICATION_JSON])
    fun getProductBySupplierIdAndSupplierRef(supplierId: UUID, supplierRef: String): ProductRapidDTO?

    @Get(uri="/api/v1/series/{id}", consumes = [APPLICATION_JSON])
    fun getSeriesById(id: UUID): SeriesDTO?

    @Get(uri="/api/v1/series/supplier/supplierId", consumes = [APPLICATION_JSON])
    fun getSeriesBySupplierId(supplierId: UUID): List<SeriesDTO>
}
