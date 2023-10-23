# NAV Assistive devices API

## Introduction
Vendors can use this API to upload and publish assistive devices to 
[finnhjelpemidler.nav.no](https://finnhjelpemidler.nav.no).
Finnhjelpemidler is a public site about assistive technology provided by NAV, a place where you can search 
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
POST https://finnhjelpemidler-api.nav.no/import/api/v1/transfers/{supplierId}
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