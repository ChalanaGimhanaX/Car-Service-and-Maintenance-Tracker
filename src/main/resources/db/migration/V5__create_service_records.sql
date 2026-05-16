create table service_records (
                                 id bigint not null auto_increment,
                                 vehicle_id bigint not null,
                                 service_type varchar(100) not null,
                                 service_date date not null,
                                 mileage_at_service int not null,
                                 service_center varchar(150),
                                 cost decimal(10, 2) not null,
                                 notes varchar(1000),
                                 created_at datetime(6) not null,
                                 updated_at datetime(6) not null,
                                 primary key (id),
                                 key idx_service_records_vehicle_id (vehicle_id),
                                 key idx_service_records_service_date (service_date),
                                 constraint fk_service_records_vehicle foreign key (vehicle_id) references vehicles (id)
) engine=InnoDB;

