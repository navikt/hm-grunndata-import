# NAV Assistive devices API

## Introduction
Vendors can use this API to upload and publish assistive devices to 
[finnhjelpemiddel.nav.no](https://finnhjelpemiddel.nav.no).
finnhjelpemiddel is a public site provided by NAV (Norwegian Labour and Welfare Administration), 
a place where you can search for, find information, and apply for assistive devices.

### Features of the API:
* Create new, change and deactivate products, accessories and spare parts
* Connect product variants to series
* Linking accessories and spare parts to main products
* Upload media or documents (images, pdf)
* Linking external video to products (vimeo, youtube)
* Get the approval state of each product
* Get stock number (Hms number) for products in framework agreements
* Information about [framework agreements](https://finnhelpemidler-api.nav.no/import/api/v1/agreements) (rammeavtale)
* Download [Iso categories](https://finnhjelpemiddel-api.nav.no/import/api/v1/isocategories) and [techdata labels](https://finnhjelpemiddel-api.nav.no/import/api/v1/techlabels)

### Registration
Before you begin, you must register yourself as a vendor/supplier. Please email us
with the following information:

* Company name
* Contact email
* Contact Phone

We will send you your supplier identity including a secret key that gives you access to our API

### Authentication/Authorization
The API is not publicly open, all requests need to be authenticated using
the HTTP bearer authorization header.

Example:
```
POST https://finnhjelpemiddel-api.nav.no/import/api/v1/products/transfers/{identifier}
Accept: application/x-json-stream
Cache-Control: no-cache
Content-Type: application/x-json-stream
Authorization: Bearer <your secret key>
```

# Rest API

The API is designed as a lightweight REST API supporting HTTP standard requests with JSON as payload. 
Each request will return receipts in JSON format, with a http status code and a body with the result.
If ERROR was detected, the body will contain the error message.

### OpenAPI
To test the integration, you can use the OpenAPI specification,
it is available [here in test](https://finnhjelpemiddel-api.ekstern.dev.nav.no/import/swagger-ui/), and in
[prod](https://finnhjelpemiddel-api.nav.no/import/swagger-ui/).

# Json properties

The data exchange format is JSON, and the properties are described in the following tables.

## Series 

A series of products is a group of products that are similar, but have different variants.
Product variants in a series share the same title, iso category, description text and images/videos. They will be grouped together in the search result
and make it easier for the user to compare between the variants. 
It is a requirement that all variants will be grouped in series. You must first create a series before you can upload the product variants. Here is an example of a series json:

```
{
  "seriesId": "603474bc-a8e8-471c-87ef-09bdc57bea59",
  "title": "Sittemodul x:panda",
  "isoCategory": "18093901",
  "text": "Sittemodul med tilpasningsmuligheter. Hel fotplate. Finnes i fire størrelser. Sittebredde 20-46 cm. Sittedybde 22-53 cm. Passer til flere understell (se egen oversikt). Brukervekt opp til 80 kg. ",
  "keywords": ["xpanda", "R82"],
  "url": "https://www.etac.com/products/paediatrics/seating/r82-xpanda/",
  "status": "ACTIVE"
}
```

### Series properties

| Name        | Type          | Required | Norwegian translation | Description                                                                                                                      | Example                              |
|:------------|:--------------|:---------|:----------------------|:---------------------------------------------------------------------------------------------------------------------------------|:-------------------------------------|
| seriesId    | UUID          | No       | Serie ID              | A unique id for a series of products, this id linked the products into a series                                                  | 603474bc-a8e8-471c-87ef-09bdc57bea59 |
| title       | String (255)  | Yes      | Tittel                | Title or name of the series, product variants that are connected in a series will have this as the main title                    | Sittemodul x:panda                    |
| isoCategory | String (8)    | Yes      | ISO Kategori          | The ISO category for the series, categories can be found [here](https://finnhjelpemiddel-api.nav.no/import/api/v1/isocategories) | 12230301                             |
| text        | TEXT          | Yes      | Produkt beskrivelse   | A describing text, html must be welformed. We only support basic html tags                                                       | A describing text                    |
| keywords    | List          | No       | Nøkkelord             | A list of keywords that can be associated with the series, keywords can be used in search                                        | "Model 321"                          |
| url         | String (2048) | No       | URL                   | A link to the product on the vendors website                                                                                     | http://link.to/product               |
| status      | String (32)   | No       | Status                | The status of the series                                                                                                         | ACTIVE, INACTIVE, DELETED            |


## Posting a series
To create a series, you can use the following endpoint:
```  
POST https://finnhjelpemiddel-api.nav.no/import/api/v1/series/transfers/{identifier}
Accept: application/x-json-stream
Cache-Control: no-cache
Content-Type: application/x-json-stream
Authorization: Bearer <your secret key>
{
  "seriesId": "603474bc-a8e8-471c-87ef-09bdc57bea59",
  "title": "Sittemodul x:panda",
  "isoCategory": "18093901",
  "text": "Sittemodul med tilpasningsmuligheter. Hel fotplate. Finnes i fire størrelser. Sittebredde 20-46 cm. Sittedybde 22-53 cm. Passer til flere understell (se egen oversikt). Brukervekt opp til 80 kg. ",
  "keywords": ["xpanda", "R82"],
  "url": "https://www.etac.com/products/paediatrics/seating/r82-xpanda/",
  "status": "ACTIVE"
}
```

### Response
You should get a 200 OK and a receipt similar to this:
```
{
  "transferId": "9de89dfe-26d1-45be-97c5-980e4afc389b",
  "supplierId": "f639825c-2fc6-49cd-82ae-31b8ffa449a6",
  "seriesId": "603474bc-a8e8-471c-87ef-09bdc57bea59",
  "md5": "735948868931D5D1640180C0ADA52931",
  "message": null,
  "transferStatus": "RECEIVED",
  "created": "2023-10-31T09:33:22.617676738"
}
```

If the series is rejected, you will get an error message in the response telling you why.

## Updating a series
To update a series you just use the same POST endpoint as above, but with the updated data in the body, remember to use 
the same seriesId.

## Upload media to series
We support different types of media files. You can upload images in jpg and png format,
and pdf document files. The media files are uploaded using multipart/form-data. 
```
POST https://finnhjelpemiddel-api.nav.no/import/api/v1/media/file/transfers/{identifier}/{series}/{seriesId}
Accept: application/json
Content-Type: multipart/form-data
Authorization: Bearer <your secret key>

<file1>
<file2>
```
### Response
You will get a receipt for each file uploaded, and a uri that can be used later for changing metadata for the media file.
```
{
  "transferId": "9de89dfe-26d1-45be-97c5-980e4afc389b",
  "seriesId": "603474bc-a8e8-471c-87ef-09bdc57bea59",
  "supplierId": "f639825c-2fc6-49cd-82ae-31b8ffa449a6",
  "md5": "735948868931D5D1640180C0ADA52931",
  "filesize": 1057306,
  "filename": "filename.jpg",
  "souceUri": "http://uri.to/file.jpg",
  "uri": "import/9c68e99a-a730-4048-ad2c-2ba8ff466b8f/6db81c58-7d3b-4c42-9c58-fd01497d75d8.jpg",
  "transferStatus": "RECEIVED",
  "mediaType": "IMAGE",
  "message": null,
  "created": "2023-10-31T09:33:22.617676738"
  "updated": "2023-10-31T09:33:22.617676738"
    
```

### Media limitations
* Maximum file size is 10MB
* Only jpg, png and pdf files are supported
* You can not have more than 10 media files per series

### Media metadata properties
To change metadata for the media file, you can use the following endpoint and json:
```
POST https://finnhjelpemiddel-api.nav.no/import/api/v1/media/meta/transfers/{identifier}/{series}/{seriesId}
Accept: application/x-json-stream
Cache-Control: no-cache
Content-Type: application/x-json-stream
Authorization: Bearer <your secret key>
{
  "uri": "import/9c68e99a-a730-4048-ad2c-2ba8ff466b8f/6db81c58-7d3b-4c42-9c58-fd01497d75d8.jpg",
  "priority": 1,
  "seriesId": "603474bc-a8e8-471c-87ef-09bdc57bea59",
  "text": "Main picture showing the standard configuration", 
}

```
### Media metadata properties
| Name     | Type          | Required | Norwegian translation | Description       | Example                                               |
|:---------|:--------------|:---------|:----------------------|:------------------|:------------------------------------------------------|
| uri      | String (2048) | Yes      | URI                   | The uri to the media file | imort/12345/1223456.jpg or http:/youtube.com/video123 |
| priority | Integer       | Yes      | Prioritet             | The priority of the media file, 1 will always be the main picture | 1                                                     |
| text     | TEXT          | Yes      | Tekst                 | A describing text for the media file | Main picture showing the standard configuration       |
|seriesId  | UUID          | Yes      | Serie ID              | The seriesId for the series that the media file belongs to | 603474bc-a8e8-471c-87ef-09bdc57bea59                  |
| mediaType| String (32)   | No       | Mediatype             | The type of media file, IMAGE, VIDEO, PDF | IMAGE, VIDEO, PDF                                     |
| sourceType| String (32)  | No       | Kilde                 | The source of the media file, IMPORT, EXTERNALURL | IMPORT, EXTERNALURL                                   |


### Response
You will get a receipt for each change.
```
{
  "transferId": "9de89dfe-26d1-45be-97c5-980e4afc389b",
  "seriesId": "603474bc-a8e8-471c-87ef-09bdc57bea59",
  "supplierId": "f639825c-2fc6-49cd-82ae-31b8ffa449a6",
  "text": "Main picture showing the standard configuration",
  "priority": 1,
  "uri": "import/9c68e99a-a730-4048-ad2c-2ba8ff466b8f/6db81c58-7d3b-4c42-9c58-fd01497d75d8.jpg",
  "transferStatus": "RECEIVED",
  "message": null,
  "created": "2023-10-31T09:33:22.617676738"
  "updated": "2023-10-31T09:33:22.617676738"
}
```

### linking a external video (youtube, vimeo)
To link an external video to a series, you can use the same endpoint as above, but with the uri pointing to the video.
```
{
  "uri": "https://youtube.com/123.mp4",
  "priority": 2,
  "seriesId": "603474bc-a8e8-471c-87ef-09bdc57bea59",
  "text": "Video showing the product",
  "mediaType": "VIDEO",
  "sourceType": "EXTERNALURL"
}
```
## Product variant to a series
A series does not get published before it has at least one product variant connected to it.
To connect a product variant to a series, you can post the variant using the seriesId with this endpoint:

```
POST https://finnhjelpemiddel-api.nav.no/import/api/v1/products/transfers/{identifier}
Accept: application/x-json-stream
Cache-Control: no-cache
Content-Type: application/x-json-stream
Authorization: Bearer <your secret key>
{
  "articleName" : "mini-crosser-x1-x2-4w",
  "articleDescription" : "4-hjuls scooter med manuell regulering av seteløft, ryggvinkel og seterotasjon. Leveres som standard med Ergo2 sitteenhet.",
  "supplierRef" : "1500-1530",
  "seriesId": "603474bc-a8e8-471c-87ef-09bdc57bea59",
  "status": "ACTIVE",
  "accessory" : false,
  "sparePart" : false,
    "techData" : [ {
        "key" : "Setebredde min",
        "value" : "45",
        "unit" : "cm"
    }, {
        "key" : "Kjørelengde maks",
        "value" : "45",
        "unit" : "km"
    } ],
    "published" : "2023-08-22T13:39:51.884163",
    "expired" : "2033-08-22T13:39:51.884163"
}
```
### Product variant properties
| Name               | Type         | Required | Norwegian translation         | Description                        | Example            |
|:-------------------|:-------------|:---------|:------------------------------|:-----------------------------------|:-------------------|
| articleName        | String (255) | Yes      | Artikkel Navn                 | The name or title of the article   | Mini crosser x1 4w |
| articleDescription | TEXT         | Yes      | Kort beskrivelse              | A short description of the article | "Rød modell"       |
| status             | String (32)  | No       | Status                        | The status of the product           | ACTIVE, INACTIVE   |
| supplierRef        | String (255) | Yes      | Leverandør artikkel referanse | A unique reference to identify the product | alphanumber eg: A4231F-132 |
| accessory          | Boolean      | no       | Tilbehør                      | Is this product an accessory?       | true               |
| sparePart          | Boolean      | no       | Reservedel                    | Is this product a spare part?       | false              |
| seriesId           | UUID         | Yes      | Serie ID                      | A unique id for a series of products, this id linked the products into a series | 603474bc-a8e8-471c-87ef-09bdc57bea59 |
| techData           | List         | No       | Tekniske data                 | A list of technical data for the product | see techdata table below |
| published          | Date         | No       | Publisert                     | The date when the product should be published | 2023-08-22T13:39:51.884163 |
| expired            | Date         | No       | Utløpsdato                    | The date when the product expires | 2033-08-22T13:39:51.884163 |

### Techdata
Valid techdata labels with keys and units is listed [here](https://finnhjelpemiddel-api.nav.no/import/api/v1/techlabels)
Even thought it is optional to add techdata, it is recommended to add as much as possible to make it easier for the user to compare products.
Also some products in framework agreements requires techdata to be added, if data is missing or wrong the product will be rejected.

| Name  | Type         | Required | Norwegian translation | Description | Example |
|:------|:-------------|:---------|:----------------------|:------------|:--------|
| key   | String (255) | Yes      | Nøkkel                | The name of the technical data | Setebredde min |
| value | String (255) | Yes      | Verdi                 | The value of the technical data | 45 |
| unit  | String (255) | No       | Enhet                 | The unit of the technical data | cm |

### Response
You will get a receipt for each product variant posted, similar to this:
```
{
  "transferId": "9de89dfe-26d1-45be-97c5-980e4afc389b",
  "supplierId": "f639825c-2fc6-49cd-82ae-31b8ffa449a6",
  "supplierRef": "99521146",
  "seriesId": "603474bc-a8e8-471c-87ef-09bdc57bea59",
  "md5": "735948868931D5D1640180C0ADA52931",
  "transferStatus": "RECEIVED",
  "message": null,
  "created": "2023-10-31T09:33:22.617676738",
}
```


## Accessory and spare part
An accessory or sparepart is a product that is connected to a main product series, and can be used as an extra part or replacement part.
A sparepart or accessory follows the same properties as a product variant, but with the addition of the accessory and sparepart, compatibleWith property.
You must first create a series for the sparepart or accessory, upload media (images/video/pdf) and link it to the main product series 
by using the seriesId.

Set the flag accessory or sparePart to true, both parameters can be true at the same time if the product is both an 
accessory and a spare part. Use the property compatibleWith to link the accessory or spare part to the main series.
```
{
  "articleName" : "Lader 24 V 8 Amp, X-1400",
  "articleDescription" : "Lader for Blimo",
  "supplierRef" : "52970102",
  "isoCategory" : "12230301",
  "seriesId": "seriesId for the sparepart or accessory",
  "accessory" : false,
  "sparePart" : true,
  "compatibleWith": {
    "seriesIds": ["603474bc-a8e8-471c-87ef-09bdc57bea59"]
  },
  "published" : "2023-08-22T13:39:51.884163",
  "expired" : "2033-08-22T13:39:51.884163"
}
```

## Getting the state of the product
All products are manually checked, if it does not follow NAVs guidelines it will be rejected. 
You can check state of the product transfer if it is approved or rejected using this:
```
GET https://finnhjelpemiddel-api.nav.no/import/api/v1/products/import/{identifier}/{supplierRef}
Accept: application/json
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer <your secret key>

```

Response:
```
{
  "id": "9c68e99a-a730-4048-ad2c-2ba8ff466b8f",
  "transferId": "9de89dfe-26d1-45be-97c5-980e4afc389b",
  "supplierId": "f639825c-2fc6-49cd-82ae-31b8ffa449a6",
  "supplierRef": "99521146",
  "hmsArtNr": "124152",
  "seriesId": "9c68e99a-a730-4048-ad2c-2ba8ff466b8f",
  "productStatus": "ACTIVE",
  "adminStatus": "APPROVED",
  "message": null,
  "created": "2023-10-19T10:46:36.684414",
  "updated": "2023-10-19T12:21:37.457247",
  "version": 1
}
```
If the product is rejected by admin, it will have adminStatus=REJECTED and with a message telling you why it was rejected.
You can also get the Hms Number of the product if it is part of an framework agreement, by using the hmsArtNr property.

## Deactivate product
To deactivate a product, use the product supplierRef to deactivate it.
For example:

```
DELETE https://finnhjelpemiddel-api.nav.no/import/api/v1/products/transfer/{identifier}/{supplierRef}
Accept: application/json
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer <your secret key>
```
Deactivate meaning the product is expired but still searchable, if you want to remove the product completely and 
not available for search, you can add "?delete=true" as query parameter in the request.
```
DELETE https://finnhjelpemiddel-api.nav.no/import/api/v1/products/transfer/{identifier}/{supplierRef}?delete=true
```

# Definitions:

### NAV's Assistive Devices Center (Hjelpemiddelsenter)
NAV's Assistive Devices Center is a national service that provides information and guidance on assistive devices.

### Framework agreement (Rammeavtale)
Framework agreements provide an overview of the national assortment that NAV 
has in the field of assistive devices. When applying for an assistive device from NAV's Assistive Devices Center,
you should always first consider whether one of the assistive devices found in a framework agreement can be used
to meet your needs.
More info about Rammeavtale is availble [here](https://finnhjelpemiddel.nav.no/rammeavtale)

### HMS Article number
Products that are in a framework agreements and can be apply through NAV, will always get an unique HMS article number. 
HMSArtNr is a stock identification number and will be used in communication with NAV's Assistive Devices Center.

# Questions and suggestions
If you have any questions or suggestions please feel free to report it as github [issues](https://github.com/navikt/hm-grunndata-import/issues)