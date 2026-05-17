alter table service_records
    add column service_status varchar(30) not null default 'PENDING';

update service_records
set service_status = 'COMPLETED'
where service_status is null or service_status = '';
