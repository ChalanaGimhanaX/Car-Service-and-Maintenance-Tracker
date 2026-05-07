insert into app_users (
    full_name, email, phone, password_hash, role, status, created_at, updated_at, last_login_at, deleted_at
) values
    ('Chalana Gimhana', 'admin@cartrack.local', '0771234567', '$2b$12$8hw8He4S3ll/15NfH1DXEu0uT2FlfqzSDZA.XN97IIN37FeZB5sBe', 'ADMIN', 'ACTIVE', '2026-05-01 08:15:00', '2026-05-07 07:45:00', '2026-05-07 07:30:00', null),
    ('Nimasha Perera', 'client1@cartrack.local', '0715552101', '$2b$12$Kah06zQriWsTL2v7rre7uuGUUz/qOVkcVzGovvTJl6Umkd08D0ZKa', 'CLIENT', 'ACTIVE', '2026-05-02 09:20:00', '2026-05-07 07:10:00', '2026-05-06 18:45:00', null),
    ('Dilan Fernando', 'client2@cartrack.local', '0778889900', '$2b$12$1MpUIG4dXPrEODTN2sLuE.zjda/XOlzvDHTkiqMllQTzZ1AYkFDuW', 'CLIENT', 'SUSPENDED', '2026-05-03 10:05:00', '2026-05-07 07:05:00', '2026-05-05 12:10:00', null),
    ('Madhavi Silva', 'client3@cartrack.local', '0702233445', '$2b$12$Ft8v53qrDlFY/jCP/QNHi.FLD0oj.Q6Sayh3zlqIH7KmSU4.8xNSi', 'CLIENT', 'ACTIVE', '2026-05-04 11:30:00', '2026-05-07 06:55:00', null, null),
    ('Kasun Rajapaksha', 'client4@cartrack.local', '0763344556', '$2b$12$Kah06zQriWsTL2v7rre7uuGUUz/qOVkcVzGovvTJl6Umkd08D0ZKa', 'CLIENT', 'PENDING', '2026-05-05 14:50:00', '2026-05-07 06:40:00', null, null);

insert into audit_logs (actor_email, action, target_email, details, created_at) values
    ('admin@cartrack.local', 'USER_CREATED', 'client1@cartrack.local', 'Seeded demo account for admin panel walkthrough.', '2026-05-01 08:20:00'),
    ('admin@cartrack.local', 'LOGIN', 'admin@cartrack.local', 'Demo admin sign in captured for the viva flow.', '2026-05-07 07:30:00'),
    ('admin@cartrack.local', 'USER_SUSPENDED', 'client2@cartrack.local', 'Example status change available in the admin module.', '2026-05-07 07:35:00'),
    ('client1@cartrack.local', 'LOGIN', 'client1@cartrack.local', 'Regular client login example.', '2026-05-06 18:45:00');

