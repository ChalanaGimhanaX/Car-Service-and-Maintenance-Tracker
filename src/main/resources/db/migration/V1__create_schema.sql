create table app_users (
    id bigint not null auto_increment,
    full_name varchar(120) not null,
    email varchar(160) not null,
    phone varchar(30),
    password_hash varchar(255) not null,
    role varchar(20) not null,
    status varchar(20) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    last_login_at datetime(6),
    deleted_at datetime(6),
    primary key (id),
    unique key uk_app_users_email (email),
    key idx_app_users_role (role),
    key idx_app_users_status (status),
    key idx_app_users_created_at (created_at)
) engine=InnoDB;

create table audit_logs (
    id bigint not null auto_increment,
    actor_email varchar(160) not null,
    action varchar(40) not null,
    target_email varchar(160),
    details varchar(500) not null,
    created_at datetime(6) not null,
    primary key (id),
    key idx_audit_logs_created_at (created_at),
    key idx_audit_logs_action (action)
) engine=InnoDB;

