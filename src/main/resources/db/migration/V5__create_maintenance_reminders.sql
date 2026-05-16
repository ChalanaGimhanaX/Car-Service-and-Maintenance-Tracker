create table maintenance_reminders (
    id bigint not null auto_increment,
    vehicle_number varchar(30) not null,
    title varchar(120) not null,
    reminder_date date not null,
    last_service_date date,
    mileage_interval int,
    status varchar(20) not null,
    notes varchar(1000),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    completed_at datetime(6),
    deleted_at datetime(6),
    primary key (id),
    key idx_maintenance_vehicle_number (vehicle_number),
    key idx_maintenance_status (status),
    key idx_maintenance_reminder_date (reminder_date)
) engine=InnoDB;
