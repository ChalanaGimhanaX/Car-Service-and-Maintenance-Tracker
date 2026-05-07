# Car Service and Maintenance Tracker

Campus project scaffold for the user-management module backed by Spring Boot, MySQL, and Tailwind UI.

## What is implemented

- Live MySQL schema with Flyway migrations
- User registration and login
- Self-service profile view and update
- Role-based security for `ADMIN` and `CLIENT`
- Admin user search, edit, suspend, restore, and soft delete
- Login tracking and audit logs
- Seeded demo data for viva demonstration
- Placeholder pages for vehicle, service, and maintenance modules

## Demo accounts

- Admin: `admin@cartrack.local` / `Admin@1234`
- Client: `client1@cartrack.local` / `Client@1234`

## Database

Create a MySQL database named `car_service_tracker`, or let the app create it through the connection string.

Update these environment variables if needed:

- `DB_USERNAME`
- `DB_PASSWORD`

## Viva angle

The user module is the backbone of the system because every vehicle, service, and maintenance record will eventually attach to an authenticated account. The project uses a real database, audit trail, and role separation so the architecture is defensible, not decorative.
