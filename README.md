# hm-grunndata-import

## Development

create a new test supplier: 
```
insert into supplier_v1(id, name, status, identifier,jwtid,updated_by,created_by) values (gen_random_uuid(), 'test supplier', 'ACTIVE', gen_random_uuid(), gen_random_uuid(), 'IMPORT', 'IMPORT');

```