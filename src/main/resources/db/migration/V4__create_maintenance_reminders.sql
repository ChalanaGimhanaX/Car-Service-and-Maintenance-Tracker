create table maintenance_reminders (
    id bigint not null auto_increment,
    title varchar(120) not null,
    vehicle_id bigint not null,
    last_service_date date,
    interval_days int,
    due_date date not null,
    status varchar(30) not null,
    priority varchar(30) not null,
    note varchar(1000),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_maintenance_reminders_vehicle foreign key (vehicle_id) references vehicles (id),
    key idx_maintenance_due_date (due_date),
    key idx_maintenance_status_due_date (status, due_date),
    key idx_maintenance_vehicle (vehicle_id)
) engine=InnoDB;
