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

create a token for the supplier 0e54eade-f3ff-43dd-adee-274f48be5173:
``
curl -X 'POST' \
'http://localhost:3333/internal/token/0e54eade-f3ff-43dd-adee-274f48be5173' \
-H 'accept: application/json' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Zpbm5oamVscGVtaWRsZXIubmF2Lm5vIiwic3ViIjoiQWRtaW4gVG9rZW4iLCJpYXQiOjE2OTQ1ODk3NTEsImp0aSI6IjRiMjUzYWVkLWZhMGQtNDhiNy04NGFjLTU0MDgyMWFjNzgwYiIsInJvbGVzIjoiUk9MRV9BRE1JTiJ9.FWdilcw-hBRpv5Vnt3O7rShDoDLEf77bFARSyGxatLc'
``
{
"id": "0e54eade-f3ff-43dd-adee-274f48be5173",
"name": "test supplier",
"jwtid": "89a239b3-61fe-4f45-8601-da10de10ddda",
"token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IHN1cHBsaWVyIiwic3VwcGxpZXJJZCI6IjBlNTRlYWRlLWYzZmYtNDNkZC1hZGVlLTI3NGY0OGJlNTE3MyIsInJvbGVzIjoiUk9MRV9TVVBQTElFUiIsImlzcyI6Imh0dHBzOi8vZmlubmhqZWxwZW1pZGxlci5uYXYubm8iLCJpYXQiOjE2OTUwMjI1NzIsImp0aSI6Ijg5YTIzOWIzLTYxZmUtNGY0NS04NjAxLWRhMTBkZTEwZGRkYSJ9.6YiFYdUbJ15VQ8Y1wvBIkdFUxnyBOYPJsPNLvbgiCSs"
}
Use the supplier token from the previous call to access all api endpoints:
``
export BEARER=<token here>
``

