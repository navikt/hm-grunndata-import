# NAV Assistive devices API

## Introduction
Vendors can use this API to upload and publish assistive devices to 
[finnhjelpemidler.nav.no](https://finnhjelpemidler.nav.no).
Finnhjelpemidler is a public site about assistive technology and provided by NAV, a place where you can search 
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
## Test environment
To test the integration, you can use https://finnhjelpemidler-api.ekstern.dev.nav.no/import.


# REST API
This API is designed as a lightweight REST API supporting HTTP requests with JSON.

## Open API
Open api specification is available [here in test](https://finnhjelpemidler-api.ekstern.dev.nav.no/import/swagger-ui/), and in
[prod](https://finnhjelpemidler-api.nav.no/import/swagger-ui/).

## Definitions:

### NAV's Assistive Devices Center (Hjelpemiddelsenter)
NAV's Assistive Devices Center is a national service that provides information and guidance on assistive devices.

### Framework agreement (Rammeavtale)
Framework agreements provide an overview of the national assortment that NAV (Norwegian Labour and Welfare Administration) 
has in the field of assistive devices. When applying for an assistive device from NAV's Assistive Devices Center, 
you should always first consider whether one of the assistive devices found in a framework agreement can be used 
to meet your needs.
More info about Rammeavtale is availble here: https://finnhjelpemidler.nav.no/rammeavtale

### HMS Article number
Products that are in a framework agreements, will get an unique HMS article number. This number will
be used in communication with NAV's Assistive Devices Center.

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
  "seriesId" : "603474bc-a8e8-471c-87ef-09bdc57bea59",
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

You will continuously get receipt for each ad like this:

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
We support following media types:
* Image (jpg, png)
* PDF (for example user manual)

### External media (video)
It is possible to link to external media, for example a video on youtube.
The media file will not be uploaded to NAV's servers, but will be linked to the product and displayed externally.   
