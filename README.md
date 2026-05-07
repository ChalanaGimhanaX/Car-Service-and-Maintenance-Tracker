# Car Service and Maintenance Tracker

This repository is a Spring Boot, MySQL, and Tailwind CSS campus project focused on the **user management** module, with clean placeholders for the remaining team-owned modules.

## Project Goal

The system is designed to manage people and later connect them to vehicles, services, and maintenance records. My part is the identity and access layer, which is the core of the whole system because every other module depends on a real logged-in user.

## What This Project Contains

### Implemented module

- User registration
- Login and logout
- Self-profile view and update
- Admin user list
- Admin search by name, email, or phone
- Admin edit user details
- Admin suspend user
- Admin restore user
- Admin soft delete user
- Password hashing with BCrypt
- Role-based access control
- Login timestamp tracking
- Admin audit logging
- Seeded demo data for viva presentation

### Reserved module placeholders

- Vehicle Management
- Service Management
- Maintenance Reminder
- Dashboard shell for the full system

These placeholders are present in the source tree so the full project structure looks like a team-built product.

## Technology Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- Thymeleaf
- MySQL 8
- Flyway for database migration
- Tailwind CSS via CDN
- Lucide icons via CDN

## How the Application Works

### Authentication flow

1. A user opens `/login`.
2. Spring Security checks the email and password.
3. If the account is valid, the app redirects to `/dashboard`.
4. The login handler updates `last_login_at`.
5. An audit log entry is saved for the login.

### Registration flow

1. A visitor opens `/register`.
2. The form validates full name, email, phone, password, and confirmation.
3. The password is hashed before it is stored.
4. The new user is saved with role `CLIENT`.
5. An audit log entry is created for the registration.

### Admin management flow

1. Admin opens `/admin/users`.
2. The app loads users from MySQL with pagination.
3. Admin can filter by keyword and status.
4. Admin can edit a user, suspend a user, restore a user, or archive a user with soft delete.
5. Every action is written to `audit_logs`.

### Profile flow

1. A logged-in user opens `/profile`.
2. The current account details are loaded from the session principal.
3. The user can update full name and phone number.
4. The update is saved to MySQL and logged in the audit trail.

## Routes

### Public routes

- `/login`
- `/register`
- `/error`
- `/placeholder/**`

### Authenticated routes

- `/dashboard`
- `/profile`
- `/admin/users`
- `/admin/users/{id}/edit`
- `/admin/users/{id}/suspend`
- `/admin/users/{id}/restore`
- `/admin/users/{id}/delete`
- `/vehicles`
- `/services`
- `/maintenance`

## Roles

### ADMIN

- Can access the admin console
- Can view and manage all users
- Can change user status
- Can archive users

### CLIENT

- Can log in
- Can view and update their own profile
- Cannot access admin-only actions

## Database Design

### `app_users`

Stores every account in the system.

Fields:

- `id`
- `full_name`
- `email`
- `phone`
- `password_hash`
- `role`
- `status`
- `created_at`
- `updated_at`
- `last_login_at`
- `deleted_at`

### `audit_logs`

Stores a history of important system actions.

Fields:

- `id`
- `actor_email`
- `action`
- `target_email`
- `details`
- `created_at`

## Seeded Demo Accounts

- Admin: `admin@cartrack.local`
- Password: `Admin@1234`

- Client 1: `client1@cartrack.local`
- Password: `Client@1234`

- Client 2: `client2@cartrack.local`
- Password: `Client@3456`

These accounts exist so the viva can be demonstrated immediately after startup.

## Important Pages

- Login page
- Registration page
- Dashboard page
- User management table
- Edit user page
- Profile page
- Placeholder module pages
- Error pages for 403, 404, and generic errors

## Project Structure

- `src/main/java/com/carrack/track/config`  
  Security setup, login success handler, and global exception handling.

- `src/main/java/com/carrack/track/controller`  
  Web controllers for auth, dashboard, profile, admin, and placeholders.

- `src/main/java/com/carrack/track/dto`  
  Form objects and dashboard snapshot data.

- `src/main/java/com/carrack/track/entity`  
  JPA entities for users and audit logs.

- `src/main/java/com/carrack/track/enums`  
  Role, account status, and audit action enums.

- `src/main/java/com/carrack/track/repository`  
  JPA repositories for database access.

- `src/main/java/com/carrack/track/service`  
  Interfaces and security principal classes.

- `src/main/java/com/carrack/track/service/impl`  
  Service implementations for user handling, dashboard data, and audit logging.

- `src/main/resources/db/migration`  
  Flyway SQL scripts for schema creation and demo seeding.

- `src/main/resources/templates`  
  Thymeleaf views for the UI.

- `src/main/resources/static`  
  Custom CSS and JavaScript.

## Configuration

### Application file

`src/main/resources/application.yml` contains the MySQL connection settings and JPA/Flyway configuration.

Default database name:

- `car_service_tracker`

Environment variables you can override:

- `DB_USERNAME`
- `DB_PASSWORD`

### Database behavior

- Hibernate runs in `validate` mode
- Flyway creates and seeds the schema
- The app expects the tables to exist exactly as defined in migration files

## Files That Matter Most

- `pom.xml`  
  Declares Spring Boot, security, JPA, validation, Flyway, MySQL, and Thymeleaf dependencies.

- `V1__create_schema.sql`  
  Builds the user and audit tables.

- `V2__seed_demo_data.sql`  
  Inserts demo accounts and audit history.

- `SecurityConfig.java`  
  Controls login, logout, password encoding, and access rules.

- `UserServiceImpl.java`  
  Holds the actual register/update/search/suspend/restore/delete logic.

- `AdminUserController.java`  
  Drives the admin table and edit actions.

- `AuthController.java`  
  Drives register and login screens.

- `DashboardController.java`  
  Loads the dashboard counters and recent records.

- `ProfileController.java`  
  Lets a user edit their own profile.

## Viva Talking Points

- The project uses a real database instead of file storage.
- Passwords are stored as hashes, not plain text.
- Admin actions are tracked in an audit table.
- Soft delete is used instead of physical delete.
- Role-based access keeps admin functions separate from client functions.
- The placeholder modules keep the repository complete as a team project.
- The dashboard proves the backend is live because it reads counters and recent records from MySQL.

## Local Run Notes

1. Start MySQL.
2. Create or allow the app to create the `car_service_tracker` database.
3. Set `DB_USERNAME` and `DB_PASSWORD` if your local MySQL credentials differ.
4. Run the Spring Boot application.
5. Open `/login` and sign in with the admin demo account.

## Demo Sequence

1. Log in as admin.
2. Show the dashboard counts and audit trail.
3. Open the user list.
4. Search a user.
5. Edit a user.
6. Suspend and restore a user.
7. Open the profile page and update your own details.
8. Show the placeholder module pages for the other team components.

