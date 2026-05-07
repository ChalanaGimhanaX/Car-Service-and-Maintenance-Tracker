# Viva Guide

Use this as the speaking script for the project demo.

## 1. Opening Statement

This project is a Car Service and Maintenance Tracker built with Spring Boot, MySQL, Thymeleaf, and Tailwind CSS. My contribution is the user-management layer, which handles authentication, profile control, admin user management, audit logging, and access control.

## 2. What I Built

- Register a new client account
- Log in and log out securely
- Update the logged-in user's profile
- Show the dashboard summary
- Let admins search, edit, suspend, restore, and archive users
- Save every important event in the database
- Keep the other team modules as placeholders so the full repository stays complete

## 3. Why This Part Matters

Every vehicle, service, and maintenance record in the system depends on a valid user account. That is why user management is not just a side module. It is the identity layer of the whole platform.

## 4. Database Explanation

### `app_users`

This table stores all account details.

Important points:

- Passwords are stored as hashes
- Role is stored separately from status
- Soft delete is handled with `deleted_at`
- Login timestamps help prove activity

### `audit_logs`

This table stores actions such as registration, login, profile update, suspend, restore, and archive.

This is useful in viva because it proves the backend is not just saving data. It is also recording system behavior.

## 5. Demo Flow

1. Open `/login`
2. Log in using the admin account
3. Show the dashboard counters
4. Open `/admin/users`
5. Search a user
6. Edit a user
7. Suspend and restore a user
8. Open `/profile`
9. Update the profile
10. Open the placeholder pages for the other modules

## 6. What To Say About Security

- Spring Security handles authentication
- BCrypt protects passwords
- Role-based access separates admin and client behavior
- Admin-only screens are locked with `@PreAuthorize`

## 7. What To Say About Architecture

- Spring Boot provides the application structure
- JPA handles database mapping
- Flyway creates and seeds the schema
- Thymeleaf renders the UI
- Tailwind keeps the interface consistent and responsive

## 8. Expected Viva Questions

### Why did you choose MySQL?

Because the project needed a real relational backend with proper persistence, searchable records, and a structure that can scale beyond file storage.

### Why use soft delete?

Because deleted users may still be needed for audit, reporting, or future restoration.

### Why store an audit trail?

Because it proves who did what, and when. That is valuable for admin accountability.

### Why hash passwords?

To avoid storing plain text passwords in the database.

### Why separate status from role?

Because a user can be an admin or client, and still be active, suspended, pending, or deleted.
