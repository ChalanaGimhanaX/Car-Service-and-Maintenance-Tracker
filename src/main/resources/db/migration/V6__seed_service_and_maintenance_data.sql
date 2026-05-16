insert into maintenance_reminders (
    vehicle_number, title, reminder_date, last_service_date, mileage_interval, status, notes, created_at, updated_at, completed_at, deleted_at
) values
    ('WP CAB-4582', 'Next oil change', '2026-06-15', '2026-05-02', 5000, 'UPCOMING', 'Call customer one week before the date.', '2026-05-03 09:00:00', '2026-05-03 09:00:00', null, null),
    ('CP KJ-9090', 'Battery check', '2026-05-18', '2026-03-10', 0, 'DUE', 'Battery warning was reported during previous visit.', '2026-05-11 10:00:00', '2026-05-11 10:00:00', null, null),
    ('NB PQ-2211', 'Tyre rotation', '2026-05-28', '2026-02-12', 8000, 'UPCOMING', 'Combine with alignment if customer agrees.', '2026-05-12 13:40:00', '2026-05-12 13:40:00', null, null);
