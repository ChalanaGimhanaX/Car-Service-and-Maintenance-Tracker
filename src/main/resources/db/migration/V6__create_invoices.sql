create table invoices (
    id bigint not null auto_increment,
    service_record_id bigint not null,
    invoice_number varchar(40) not null,
    invoice_date date not null,
    total_amount decimal(10, 2) not null,
    payment_status varchar(20) not null,
    payment_method varchar(30),
    notes varchar(1000),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_invoices_invoice_number (invoice_number),
    key idx_invoices_service_record_id (service_record_id),
    key idx_invoices_invoice_date (invoice_date),
    key idx_invoices_payment_status (payment_status),
    constraint fk_invoices_service_record foreign key (service_record_id) references service_records (id)
) engine=InnoDB;

