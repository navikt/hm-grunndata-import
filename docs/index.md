# NAV Assistive devices API

## Introduction
Vendors can use this API to upload and publish assistive devices to 
[finnhjelpemidler.nav.no](https://finnhjelpemidler.nav.no).
Finnhjelpemidler is a public site provided by NAV, a place where you can search 
for, find information, and apply for assistive devices.

## Registration
Before you begin, you must register yourself as a vendor/supplier. Please email us
with the following information:

* Company name
* Contact email
* Contact Phone

We will send you your supplier identity including a secret key that gives you access to our API

## Authentication/Authorization
The API is not publicly open, all requests need to be authenticated using
the HTTP bearer authorization header.

Example:
```
POST https://finnhjelpemidler-api.nav.no/import/api/v1/products/transfers/{supplierId}
Accept: application/x-json-stream
Cache-Control: no-cache
Content-Type: application/x-json-stream
Authorization: Bearer <your secret key>
```
## Open API for Test and production environment
To test the integration, you can use the Open api specification, 
it is available [here in test](https://finnhjelpemidler-api.ekstern.dev.nav.no/import/swagger-ui/), and in
[prod](https://finnhjelpemidler-api.nav.no/import/swagger-ui/).

# JSON Structure

The data format is JSON, below is a diagram of the json structure:
<img src="./json-example-01.svg">
You can also download kotlin code for the DTOs
[here](https://github.com/navikt/hm-grunndata-import/blob/master/src/main/kotlin/no/nav/hm/grunndata/importapi/transfer/)

## Json properties

| Name             | Type         | Required | Norwegian translation         | Description                                                                                                                   | Example                              |
|:-----------------|:-------------|:---------|:------------------------------|:------------------------------------------------------------------------------------------------------------------------------|:-------------------------------------|
| Title            | String (255) | Yes      | Tittel                        | Title or name of the product, product variants that are connected in a series will have this as the series title              | Mini crosser X1                      |
| articleName      | String (255) | Yes      | Artikkel Navn                 | The name or title of the article                                                                                              | Mini crosser x1 4w                   |
| shortDescription | TEXT         | Yes      | Kort beskrivelse              | A short summary text                                                                                                          | A short summary text                 |
| text             | TEXT         | Yes      | Produkt beskrivelse           | A describing text, html must be welformed. We only support basic html tags                                                    | A describing text                    |
| manufacturer     | String (255) | No       | Produsent                     | The name of the company who made this product                                                                                 | Medema                               |
| supplierRef      | String (255) | Yes      | Leverandør artikkel referanse | A unique reference to identify the product                                                                                    | alphanumber eg: A4231F-132           |
 | isoCategory      | String (255) | Yes      | ISO Kategori                  | The ISO category for the product, categories can be found [here](https://finnhjelpemidler-api.nav.no/import/api/v1/categories)| 12230301                             |
| accessory        | Boolean      | Yes      | Tilbehør                      | Is this product an accessory?                                                                                                 | true                                 |
| sparePart        | Boolean      | Yes      | Reservedel                    | Is this product a spare part?                                                                                                 | false                                |
| seriesId         | UUID         | No       | Serie ID                      | A unique id for a series of products, this id linked the products into a series                                               | 603474bc-a8e8-471c-87ef-09bdc57bea59 |
| techData         | List         | No       | Tekniske data                 | A list of technical data for the product                                                                                      | see techdata table below             |
| media            | List         | No       | Media                         | A list of media files for the product                                                                                         | see media table below                |
| published        | Date         | No       | Publisert                     | The date when the product should be published                                                                                 | 2023-08-22T13:39:51.884163           |
| expired          | Date         | No       | Utløpsdato                    | The date when the product should be expired                                                                                   | 2033-08-22T13:39:51.884163           |

### Techdata

| Name  | Type         | Required | Norwegian translation | Description | Example |
|:------|:-------------|:---------|:----------------------|:------------|:--------|
| key   | String (255) | Yes      | Nøkkel                | The name of the technical data | Setebredde min |
| value | String (255) | Yes      | Verdi                 | The value of the technical data | 45 |
| unit  | String (255) | No       | Enhet                 | The unit of the technical data | cm |

Valid techdata labels with keys and units is listed [here](https://finnhjelpemidler-api.nav.no/import/api/v1/techlabels)

### Media

| Name       | Type         | Required | Norwegian translation | Description                                                       | Example                                         |
|:-----------|:-------------|:---------|:----------------------|:------------------------------------------------------------------|:------------------------------------------------|
| uri        | String (255) | Yes      | URI                   | The uri to the media file                                         | imort/12345/1223456.jpg                         |
| priority   | Integer      | Yes      | Prioritet             | The priority of the media file, 1 will always be the main picture | 1                                               |
| type       | String (255) | Yes      | Type                  | The type of the media file                                        | IMAGE, PDF, VIDEO                               |
| text       | TEXT         | Yes      | Tekst                 | A describing text for the media file                              | Main picture showing the standard configuration |
| sourceType | String (255) | Yes      | Kilde                 | The source of the media file                                      | IMPORT, EXTERNALURL                             |


More info about Media and how to upload media files below.

# REST API
This API is designed as a lightweight REST API supporting HTTP requests with JSON as payload.

### Posting using stream
Post products in stream by using Content-Type: application/x-json-stream. products are separated by a newline "\n" for
example:

```
POST https://finnhjelpemidler-api.nav.no/import/api/v1/products/transfers/{supplierId}
Accept: application/x-json-stream
Cache-Control: no-cache
Content-Type: application/x-json-stream
Authorization: Bearer <your secret key>
{
  "title": "Mini Crosser X1/X2 4W",
  "articleName" : "mini-crosser-x1-x2-4w",
  "shortDescription" : "4-hjuls scooter med manuell regulering av seteløft, ryggvinkel og seterotasjon. Leveres som standard med Ergo2 sitteenhet.",
  "text" : "Mini Crosser modell X1/ X2\n Er uten sammenligning markedets sterkeste og mest komfortable el scooter: Her får man både stor motorkraft, mulighet for ekstra stor kjørelengde og unik regulerbar fjæring pakket inn i et usedvanlig lekkert design. Nordens mest solgte scooter er spesielt konstruert for nordisk klima og geografi, hvilket betyr at den er velegnet for bruk året rundt, på dårlige veier, snøføre, og ellers hvor man ønsker ekstra stabilitet. Det er virkelig fokusert på sikkerheten, og uten at det går på kompromiss med bruksegenskaper og design. Leveres også med kabin.\n                    Hjul , fjæring og styre Mini Crosser har behagelig fjæring på alle 4 hjul, inklusive justerbare støtdempere på alle hjul. Vi har stort utvalg av ulike hjul, inklusive pigghjul. Det multijusterbare styret sikrer optimal komfort. Det er utstyrt med et kardan-ledd og kan heves, senkes og vinkles. Krever kun liten armstyrke ved kjøring. Kurv blir stående stille når man svinger. Markedets minste svingradius!\n                    Luksussete er standard. For å gi den ideelle sittestilling kan Mini Crosser Ergo-sete justeres i høyde, dybde og ryggvinkel og leveres i størrelser fra 35 til 70cm og med ulike rygghøyder. Armlenene er både høyde- og dybdejusterbare, samt oppfellbare og kan utstyres med ulike armlenspolstre. Setet er videre utstyrt med glideskinne og kan dreies 90 grader til begge sider. Det store sortimentet av seter, sete- og ryggputer og el funksjoner muliggjør nærmest enhver ønsket setetilpasning – muligheter man ellers kun finner på de mest avanserte el-rullestoler!",
  "manufacturer" : "Medema AS",
  "supplierRef" : "1500-1530",
  "isoCategory" : "12230301",
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
  "media" : [ {
    "uri" : "12345/1223456.jpg",
    "priority" : 1,
    "type" : "IMAGE",
    "sourceType": "IMPORT"
    },
    {
      "uri": "https://host.to/123.mp4",
      "priority": 2,
      "type": "VIDEO",
      "sourceType": "EXTERNALURL"
    }
  ],
  "published" : "2023-08-22T13:39:51.884163",
  "expired" : "2033-08-22T13:39:51.884163"
}
. 
.
.
```

You will continuously get receipt for each product like this:

```
HTTP/1.1 200 OK
transfer-encoding: chunked
Date: Fri, 3 Apr 2020 10:37:07 GMT
transfer-encoding: chunked
content-type: application/x-json-stream

{
  "versionId" : 1,
  "status" : "RECEIVED",
  "md5" : "3D5A0C23BC12D58D5865CF3CFC086F11",
  "items" : 1,
  "created" : "2020-04-03T12:37:07.83019",
  "updated" : "2020-04-03T12:37:07.830203"
}{
  "versionId" : 2,
  "status" : "RECEIVED",
  "md5" : "CA41CC694F62E14F72FDE43B66C9821B",
  "items" : 1,
  "created" : "2020-04-03T12:37:07.861917",
  "updated" : "2020-04-03T12:37:07.861923"
}
```

It is important to check the status for each receipt, if it is not "ERROR".
When using stream, the http status code will always return 200 OK. 

## Media upload (Image, PDF)
Media files are uploaded using multipart/form-data. 
The media files are first uploaded before they can be used in a product. 
There is support for uploading multiple files in one request. But the maximum size is 10MB for each upload.

### External media (video)
It is possible to link to external media, for example a video on youtube.
The media file will not be uploaded to NAV's servers, but will be linked to the product and displayed externally.

### Linking media files to product
When uploading media files, you will get a receipt for each file. The receipt contains a unique uri that can be used 
on the product json. 

### Upload image
```
POST https://finnhjelpemidler-api.nav.no/import/api/v1/media/transfers/files/{supplierId}/{supplierRef}
Accept: application/json
Content-Type: multipart/form-data
Authorization: Bearer <your secret key>
Files=@filename.jpg;type=image/jpeg
```
You will get a receipt like this:
```
[  
  {
    "transferId": "e5fa2fd0-1e9a-4095-9b33-35431901dc8f",
    "supplierRef": "99521146",
    "supplierId": "f639825c-2fc6-49cd-82ae-31b8ffa449a6",
    "md5": "f20912b65f5e2b4460652e53b89af091",
    "filesize": 1057306,
    "filename": "filename.jpg",
    "sourceUri": "http://localhost:8081/local/import/9c68e99a-a730-4048-ad2c-2ba8ff466b8f/6db81c58-7d3b-4c42-9c58-fd01497d75d8.jpg",
    "uri": "import/9c68e99a-a730-4048-ad2c-2ba8ff466b8f/6db81c58-7d3b-4c42-9c58-fd01497d75d8.jpg",
    "transferStatus": "DONE",
    "created": "2023-10-24T10:08:17.35855",
    "updated": "2023-10-24T10:08:17.358555"
  }
]
```
Then linking the uri to the product json within the media array:
```
  "media" : [ {
    "uri" : "import/9c68e99a-a730-4048-ad2c-2ba8ff466b8f/6db81c58-7d3b-4c42-9c58-fd01497d75d8.jpg",
    "priority" : 1,
    "type" : "IMAGE",
    "text" : "Main picture showing the standard configuration"
    "sourceType": "IMPORT"
    },
    {
      "uri": "https://youtube.com/123.mp4",
      "priority": 2,
      "type": "VIDEO",
      "text": "Video showing the product"
      "sourceType": "EXTERNALURL"
    }
  ],

```

### Media limitations
* Maximum file size is 10MB
* Only jpg, png and pdf files are supported
* You can not link more than 10 media files per product

# Series of products
A series of products is a group of products that are similar, but have different variants. 
To group the variants to a series, you have to create a series first.
Then you can upload the variants and link them to the series by using the seriesId.

## Posting a series
```
POST https://finnhjelpemidler-api.nav.no/import/api/v1/product/series/{supplierId}
Accept: application/json
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer <your secret key>
{
  "seriesId": "603474bc-a8e8-471c-87ef-09bdc57bea59",
  "title": "Mini Crosser",
  "status": "ACTIVE"
}
```

| Name    | Type         | Required | Norwegian translation | Description                                                                                                     | Example                              |
|:--------|:-------------|:---------|:----------------------|:----------------------------------------------------------------------------------------------------------------|:-------------------------------------|
| seriesId| UUID         | No       | Serie ID              | A unique id for a series of products, this id linked the products into a series                                 | 603474bc-a8e8-471c-87ef-09bdc57bea59 |
| title   | String (255) | Yes      | Serie tittel          | Title or name of the series, product variants that are connected in a series will have this as the series title | Mini crosser X1                      |
| status  | String (255) | Yes      | Status                | The status of the series, ACTIVE or INACTIVE                                                                    | ACTIVE, INACTIVE                     |

## Posting a product variant of a series
Posting a variant is exactly the same as product, and use seriesId to tell which series the variant belongs to.

```
POST https://finnhjelpemidler-api.nav.no/import/api/v1/products/transfers/{supplierId}
Accept: application/x-json-stream
Cache-Control: no-cache
Content-Type: application/x-json-stream
Authorization: Bearer <your secret key>
{
  "title": "Mini Crosser X1/X2 4W",
  "articleName" : "mini-crosser-x1-x2-4w",
  "shortDescription" : "4-hjuls scooter med manuell regulering av seteløft, ryggvinkel og seterotasjon. Leveres som standard med Ergo2 sitteenhet.",
  "text" : "Mini Crosser modell X1/ X2\n Er uten sammenligning markedets sterkeste og mest komfortable el scooter: Her får man både stor motorkraft, mulighet for ekstra stor kjørelengde og unik regulerbar fjæring pakket inn i et usedvanlig lekkert design. Nordens mest solgte scooter er spesielt konstruert for nordisk klima og geografi, hvilket betyr at den er velegnet for bruk året rundt, på dårlige veier, snøføre, og ellers hvor man ønsker ekstra stabilitet. Det er virkelig fokusert på sikkerheten, og uten at det går på kompromiss med bruksegenskaper og design. Leveres også med kabin.\n                    Hjul , fjæring og styre Mini Crosser har behagelig fjæring på alle 4 hjul, inklusive justerbare støtdempere på alle hjul. Vi har stort utvalg av ulike hjul, inklusive pigghjul. Det multijusterbare styret sikrer optimal komfort. Det er utstyrt med et kardan-ledd og kan heves, senkes og vinkles. Krever kun liten armstyrke ved kjøring. Kurv blir stående stille når man svinger. Markedets minste svingradius!\n                    Luksussete er standard. For å gi den ideelle sittestilling kan Mini Crosser Ergo-sete justeres i høyde, dybde og ryggvinkel og leveres i størrelser fra 35 til 70cm og med ulike rygghøyder. Armlenene er både høyde- og dybdejusterbare, samt oppfellbare og kan utstyres med ulike armlenspolstre. Setet er videre utstyrt med glideskinne og kan dreies 90 grader til begge sider. Det store sortimentet av seter, sete- og ryggputer og el funksjoner muliggjør nærmest enhver ønsket setetilpasning – muligheter man ellers kun finner på de mest avanserte el-rullestoler!",
  "manufacturer" : "Medema AS",
  "supplierRef" : "1500-1530",
  "isoCategory" : "12230301",
  "accessory" : false,
  "sparePart" : false,
  "seriesId": "603474bc-a8e8-471c-87ef-09bdc57bea59", <--- This is the seriesId, that was created in the previous step
  "techData" : [ {
    "key" : "Setebredde min",
    "value" : "45",
    "unit" : "cm"
  }, {
    "key" : "Kjørelengde maks",
    "value" : "45",
    "unit" : "km"
  } ],
  "media" : [ {
    "uri" : "12345/1223456.jpg",
    "priority" : 1,
    "type" : "IMAGE",
    "sourceType": "IMPORT"
    },
    {
      "uri": "https://host.to/123.mp4",
      "priority": 2,
      "type": "VIDEO",
      "sourceType": "EXTERNALURL"
    }
  ],
  "published" : "2023-08-22T13:39:51.884163",
  "expired" : "2033-08-22T13:39:51.884163"
}
. 
.
.
```

## Accessory and spare part
To upload a product that is an accessory or spare part, you must first upload the main product and connect it to a series.
Then you can upload the accessory or spare part, and link it to the main product by using the seriesId.
Set the flag accessory or sparePart to true, both parameters can be true at the same time if the product is both an 
accessory and a spare part. Use the property compatibleWith to link the accessory or spare part to the main product.

```
{
  "title" : "Lader 24 V 8 Amp, X-1400",
  "articleName" : "Lader 24 V 8 Amp, X-1400",
  "shortDescription" : "Lader for Blimo",
  "text" : "Smart vedlikeholdslader som slår seg av når batteriene er fulladet. Hvis spenningen i batteriene synker, går laderen inn og vedlikeholder automatisk vedlikeholdskostnadene. MERK! Kun beregnet på bruk med blybatterier (AGM). Å ha en ekstra lader kan være bra hvis du bruker scooteren din forskjellige steder. Kanskje det kan være praktisk å ha en på landsbygda og en i byen eller en hjemme og en på jobb, hvis du tar med deg din scooter mellom disse stedene. Når du bestiller dette produktet sammen med en promenadescooter, kommer ingen ekstra fraktkostnader til. Passer: Blimo X-1400 Blimo Kabinscooter Blimo Gatsby",
  "supplierRef" : "52970102",
  "isoCategory" : "12230301",
  "accessory" : false,
  "sparePart" : true,
  "compatibleWith": {
    "seriesIds": ["603474bc-a8e8-471c-87ef-09bdc57bea59"] <--- link it to the main product by using the seriesId
  },
  "techData" : [],
  "media" : [ {
    "uri" : "import/d22094a7-25b2-45b0-aeb8-82eec928531e/80247d3b-19ac-4ab0-909f-6ab4086fcc90.jpg",
    "priority" : 1,
    "type" : "IMAGE",
    "text": "Lader 24 V 8 Amp, X-1400"
  } ],
  "published" : "2023-08-22T13:39:51.884163",
  "expired" : "2033-08-22T13:39:51.884163"
}
```

## Getting the state of the product
All products are manually checked, if it does not follow NAVs guidelines it will be rejected. 
You can check state of the product transfer if it is approved or rejected using this:
```
GET https://finnhjelpemidler-api.nav.no/import/api/v1/products/import/{supplierId}/{supplierRef}
Accept: application/json
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer <your secret key>

```

Response:
```

```

## Deactivate product
To deactivate a product, use the product supplierRef to deactivate it.
For example:

```
DELETE https://finnhjelpemidler-api.nav.no/import/api/v1/products/transfer/{supplierId}/{supplierRef}
Accept: application/json
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer <your secret key>
```

# Definitions:

## NAV's Assistive Devices Center (Hjelpemiddelsenter)
NAV's Assistive Devices Center is a national service that provides information and guidance on assistive devices.

## Framework agreement (Rammeavtale)
Framework agreements provide an overview of the national assortment that NAV (Norwegian Labour and Welfare Administration)
has in the field of assistive devices. When applying for an assistive device from NAV's Assistive Devices Center,
you should always first consider whether one of the assistive devices found in a framework agreement can be used
to meet your needs.
More info about Rammeavtale is availble here: https://finnhjelpemidler.nav.no/rammeavtale

## HMS Article number
Products that are in a framework agreements, will get an unique HMS article number. This number will
be used in communication with NAV's Assistive Devices Center.