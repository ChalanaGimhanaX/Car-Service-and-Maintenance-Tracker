insert into service_records (
    service_code, vehicle_number, customer_name, service_type, service_date, cost, technician_name, status, notes, created_at, updated_at, deleted_at
) values
    ('SRV-1001', 'WP CAB-4582', 'Nimasha Perera', 'Full Service', '2026-05-02', 18500.00, 'Ruwan Silva', 'COMPLETED', 'Engine oil, filters, and brake inspection completed.', '2026-05-02 09:10:00', '2026-05-02 15:30:00', null),
    ('SRV-1002', 'CP KJ-9090', 'Madhavi Silva', 'AC Repair', '2026-05-10', 9200.00, 'Sajith Fernando', 'IN_PROGRESS', 'Cooling issue under inspection.', '2026-05-10 10:20:00', '2026-05-10 11:15:00', null),
    ('SRV-1003', 'NB PQ-2211', 'Kasun Rajapaksha', 'Wheel Alignment', '2026-05-14', 4500.00, 'Ruwan Silva', 'PENDING', 'Vehicle booked for afternoon slot.', '2026-05-14 08:00:00', '2026-05-14 08:00:00', null);

insert into maintenance_reminders (
    vehicle_number, title, reminder_date, last_service_date, mileage_interval, status, notes, created_at, updated_at, completed_at, deleted_at
) values
    ('WP CAB-4582', 'Next oil change', '2026-06-15', '2026-05-02', 5000, 'UPCOMING', 'Call customer one week before the date.', '2026-05-03 09:00:00', '2026-05-03 09:00:00', null, null),
    ('CP KJ-9090', 'Battery check', '2026-05-18', '2026-03-10', 0, 'DUE', 'Battery warning was reported during previous visit.', '2026-05-11 10:00:00', '2026-05-11 10:00:00', null, null),
    ('NB PQ-2211', 'Tyre rotation', '2026-05-28', '2026-02-12', 8000, 'UPCOMING', 'Combine with alignment if customer agrees.', '2026-05-12 13:40:00', '2026-05-12 13:40:00', null, null);
