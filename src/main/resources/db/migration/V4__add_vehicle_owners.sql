alter table vehicles
    add column owner_id bigint null after id;

update vehicles
set owner_id = (
    select id
    from app_users
    where role = 'CLIENT'
      and status <> 'DELETED'
    order by id
    limit 1
)
where owner_id is null;

alter table vehicles
    modify owner_id bigint not null;

create index idx_vehicles_owner_id on vehicles (owner_id);

alter table vehicles
    add constraint fk_vehicles_owner
        foreign key (owner_id) references app_users (id);
