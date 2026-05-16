# Car Service and Maintenance Tracker

This is a Spring Boot and MySQL campus project for managing users, vehicles, workshop service records, and maintenance reminders in one system.

## Modules Included

- User registration and login
- Admin user management
- Profile management
- Vehicle management
- Service management
- Maintenance reminder management
- Dashboard with summary counts
- Audit logging for important actions

## Technology Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- Thymeleaf
- MySQL 8
- Flyway

## Main Routes

### Public

- `/login`
- `/register`

### Authenticated

- `/dashboard`
- `/profile`
- `/admin/users`
- `/vehicles`
- `/services`
- `/maintenance`

## Database Tables

- `app_users`
- `audit_logs`
- `vehicles`
- `service_records`
- `maintenance_reminders`

## Demo Accounts

- Admin email: `admin@cartrack.local`
- Admin password: `Admin@1234`

- Client email: `client1@cartrack.local`
- Client password: `Client@1234`

## Local MySQL Setup

1. Install MySQL Server 8 if it is not already installed.
2. Start the MySQL service.
3. Open MySQL and run:

```sql
create database car_service_tracker;
```

4. If your MySQL username and password are not the defaults, set them in PowerShell:

```powershell
setx DB_USERNAME "root"
setx DB_PASSWORD "your_password"
```

5. If you use a custom host, port, or database name, set the full URL:

```powershell
setx DB_URL "jdbc:mysql://localhost:3306/car_service_tracker?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
```

6. Open a new terminal after using `setx`.
7. Run the project:

```powershell
mvn spring-boot:run
```

8. Open `http://localhost:8080/login`.

## Flyway Migrations

- `V1__create_schema.sql`
- `V2__seed_demo_data.sql`
- `V3__create_vehicles.sql`
- `V4__create_service_records.sql`
- `V5__create_maintenance_reminders.sql`
- `V6__seed_service_and_maintenance_data.sql`

## Project Structure

- `src/main/java/com/carrack/track/controller` for MVC controllers
- `src/main/java/com/carrack/track/service` for business logic
- `src/main/java/com/carrack/track/repository` for JPA repositories
- `src/main/java/com/carrack/track/entity` for JPA entities
- `src/main/java/com/carrack/track/dto` for form objects
- `src/main/resources/templates` for Thymeleaf pages
- `src/main/resources/db/migration` for Flyway SQL files

## Simple Viva Flow

1. Log in as admin.
2. Show the dashboard counts.
3. Open the user management page.
4. Open vehicles.
5. Open services and show a record.
6. Open maintenance and show a reminder.
7. Edit profile and save changes.
