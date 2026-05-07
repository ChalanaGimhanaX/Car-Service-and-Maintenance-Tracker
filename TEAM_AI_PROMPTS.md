# AI Prompts For Other Team Members

Use these prompts separately when each team member wants to generate their own module with AI. Do not paste this file into the README.

## Prompt 1: Vehicle Management

```text
You are building the Vehicle Management module for a Spring Boot, MySQL, and Thymeleaf campus project called Car Service and Maintenance Tracker.

Create only the vehicle module. Do not modify the user management module or the shared layout except where a link or placeholder is needed.

Requirements:
- Add a Vehicle entity, repository, service, and controller.
- Support create, read, update, and delete for vehicles.
- Store data in MySQL with Flyway migrations.
- Use Spring Boot, Spring Data JPA, Thymeleaf, Tailwind CSS, and Java 17.
- Include fields such as vehicle number, model, type, brand, fuel type, year, mileage, status, and notes.
- Add a searchable vehicle list page.
- Add a vehicle create/edit form.
- Add a vehicle detail page.
- Use soft delete if possible.
- Add validation, clean error handling, and audit logging if the pattern already exists in the project.
- Keep the UI consistent with the existing dark Tailwind dashboard style.
- Leave the other modules untouched.

Write code directly into the existing project structure and keep the code production-like, not toy code.
```

## Prompt 2: Service Management

```text
You are building the Service Management module for a Spring Boot, MySQL, and Thymeleaf campus project called Car Service and Maintenance Tracker.

Create only the service-record module. Do not change the user module or vehicle module logic except for read-only integration points.

Requirements:
- Add a ServiceRecord entity, repository, service, and controller.
- Support create, read, update, and delete for service records.
- Store data in MySQL using Flyway migrations.
- Include fields such as service date, service type, description, cost, next due date, mechanic name, and linked vehicle reference.
- Add a service entry form.
- Add a service history table.
- Add filtering by vehicle and date if practical.
- Keep the same dark, professional Tailwind styling used in the existing project.
- Add validation and meaningful error messages.
- Use the existing application patterns and naming style.
- Leave all other modules untouched.

Write code directly into the project and keep it aligned with the current architecture.
```

## Prompt 3: Maintenance Reminder

```text
You are building the Maintenance Reminder module for a Spring Boot, MySQL, and Thymeleaf campus project called Car Service and Maintenance Tracker.

Create only the maintenance module. Do not modify the user module, vehicle module, or service module except where an integration read is needed.

Requirements:
- Add a MaintenanceReminder entity, repository, service, and controller.
- Track upcoming maintenance dates based on service history.
- Store reminder data in MySQL with Flyway migrations.
- Include fields such as reminder title, linked vehicle, due date, reminder status, priority, and note.
- Show an upcoming reminders list and a simple details page.
- Mark overdue reminders clearly in the UI.
- Keep the Tailwind design consistent with the rest of the project.
- Add validation and clean error handling.
- Preserve the existing structure and naming style.
- Leave other modules untouched.

Write code directly into the project and keep it realistic enough for viva demonstration.
```

## Prompt 4: Dashboard Enhancement

```text
You are building the Dashboard enhancement for a Spring Boot, MySQL, and Thymeleaf campus project called Car Service and Maintenance Tracker.

Create only dashboard improvements. Do not rewrite the user module or other team modules.

Requirements:
- Improve the dashboard with real counters and summary cards.
- Add charts or visual summaries only if they fit the existing application style.
- Pull counts from the database using service layer methods.
- Show recent users, recent service records, recent vehicle additions, and maintenance due items if data is available.
- Keep the layout clean, dark, and professional.
- Use Tailwind CSS and the existing Thymeleaf layout system.
- Keep the code compatible with the current architecture.
- Leave the other modules untouched.

Write code directly into the project and keep the final result polished enough for a viva demo.
```
