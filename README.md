# hm-grunndata-import

## Development

Create a new test supplier: 
```
insert into supplier_v1(id, name, status, identifier,jwtid,updated_by,created_by) values ('0e54eade-f3ff-43dd-adee-274f48be5173', 'test supplier', 'ACTIVE', 'test-supplier-1', '89a239b3-61fe-4f45-8601-da10de10ddda', 'IMPORT', 'IMPORT');
```

A test token, can be used in localhost:
```
eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Zpbm5oamVscGVtaWRsZXIubmF2Lm5vIiwic3ViIjoiQWRtaW4gVG9rZW4iLCJpYXQiOjE2OTQ1ODk3NTEsImp0aSI6IjRiMjUzYWVkLWZhMGQtNDhiNy04NGFjLTU0MDgyMWFjNzgwYiIsInJvbGVzIjoiUk9MRV9BRE1JTiJ9.FWdilcw-hBRpv5Vnt3O7rShDoDLEf77bFARSyGxatLc
```

create a token for the supplier f639825c-2fc6-49cd-82ae-31b8ffa449a6:
``
curl -X 'POST' \
'http://localhost:3333/import/internal/token/f639825c-2fc6-49cd-82ae-31b8ffa449a6' \
-H 'accept: application/json' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Zpbm5oamVscGVtaWRsZXIubmF2Lm5vIiwic3ViIjoiQWRtaW4gVG9rZW4iLCJpYXQiOjE2OTQ1ODk3NTEsImp0aSI6IjRiMjUzYWVkLWZhMGQtNDhiNy04NGFjLTU0MDgyMWFjNzgwYiIsInJvbGVzIjoiUk9MRV9BRE1JTiJ9.FWdilcw-hBRpv5Vnt3O7rShDoDLEf77bFARSyGxatLc'
``
{
"id" : "f639825c-2fc6-49cd-82ae-31b8ffa449a6",
"name" : "Finn Hans Hjelpmedler Test",
"jwtid" : "049ef21a-685d-4bfc-9559-88b99f2b057f",
"token" : "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGaW5uIEhhbnMgSGplbHBtZWRsZXIgVGVzdCIsInN1cHBsaWVySWQiOiJmNjM5ODI1Yy0yZmM2LTQ5Y2QtODJhZS0zMWI4ZmZhNDQ5YTYiLCJyb2xlcyI6IlJPTEVfU1VQUExJRVIiLCJpc3MiOiJodHRwczovL2Zpbm5oamVscGVtaWRsZXIubmF2Lm5vIiwiaWF0IjoxNjk3MDI2MjA2LCJqdGkiOiIwNDllZjIxYS02ODVkLTRiZmMtOTU1OS04OGI5OWYyYjA1N2YifQ.stcUQU0i-GorPBYfSLL_Oqw7ROFDuH0xDL5j0I8aaKE"
}
Use the supplier token from the previous call to access all api endpoints:
``
export BEARER=<token here>
``

