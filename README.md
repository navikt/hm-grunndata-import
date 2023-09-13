# hm-grunndata-import

## Development

Create a new test supplier: 
```
insert into supplier_v1(id, name, status, identifier,jwtid,updated_by,created_by) values (gen_random_uuid(), 'test supplier', 'ACTIVE', gen_random_uuid(), gen_random_uuid(), 'IMPORT', 'IMPORT');
```

A test token, can be used in localhost:
```
eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Zpbm5oamVscGVtaWRsZXIubmF2Lm5vIiwic3ViIjoiQWRtaW4gVG9rZW4iLCJpYXQiOjE2OTQ1ODk3NTEsImp0aSI6IjRiMjUzYWVkLWZhMGQtNDhiNy04NGFjLTU0MDgyMWFjNzgwYiIsInJvbGVzIjoiUk9MRV9BRE1JTiJ9.FWdilcw-hBRpv5Vnt3O7rShDoDLEf77bFARSyGxatLc
```

create a token for the supplier:
``
``