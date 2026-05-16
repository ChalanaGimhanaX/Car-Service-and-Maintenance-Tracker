# Google Stitch UI Design Prompt

Use this prompt in Google Stitch to create the full UI design for the project.

```text
Create a complete full UI design for a web application called "Car Service and Maintenance Tracker".

Use the visual style of the OpenAI Developers Codex page as inspiration:
https://developers.openai.com/codex

Do not copy OpenAI branding, logos, exact text, icons, or assets. Only use the general design direction: clean developer-focused layout, strong typography, black-and-white theme, structured navigation, search-focused interface, clean cards, thin borders, documentation-style sections, and polished product-dashboard presentation.

Primary color: Black (#000000)
Secondary color: White (#FFFFFF)

Supporting colors:
- Dark gray: #111111
- Charcoal gray: #181818
- Border gray: #2A2A2A
- Text gray: #6B7280
- Light gray: #E5E7EB
- Page background light: #F7F7F7

Use small status colors only for badges:
- Green for active, paid, completed
- Yellow for pending, upcoming, in progress
- Red for deleted, cancelled, suspended, overdue
- Blue or gray for neutral informational states

Keep the main interface black and white. Do not use colorful gradients, decorative blobs, glassmorphism, or over-designed visual effects.

Project context:
This is a university project for a vehicle service workshop. It is built using Spring Boot, Thymeleaf, MySQL, Flyway, and basic JavaScript. The UI must look clean, practical, and realistic. It should look professional enough for a viva presentation, but not like a large enterprise SaaS product.

Application purpose:
The system manages:
1. User Management
2. Vehicle Management
3. Service Management
4. Maintenance Reminder Management
5. Billing and Invoice Management

Overall design direction:
- Developer documentation inspired admin system
- Black and white theme
- Thin borders
- Clear spacing
- Large simple page titles
- Small uppercase section labels
- Clean dashboard cards
- Compact data tables
- Practical forms
- Simple status badges
- Minimal shadows
- 6px to 8px border radius
- Easy to implement using Thymeleaf and Tailwind CSS

Do not make it look too advanced. Avoid complex animations, AI-style visuals, 3D graphics, huge marketing hero sections, colorful gradients, and unnecessary decorative illustrations.

Common layout:
- Use a top navigation bar inspired by developer documentation sites.
- App name on the left: Car Service and Maintenance Tracker.
- Main navigation links:
  - Dashboard
  - Users
  - Vehicles
  - Services
  - Maintenance
  - Invoices
  - Profile
  - Logout
- Include a prominent search area on dashboard and list pages.
- Use a clean content container with good spacing.
- On desktop, use wide tables and two-column detail layouts.
- On mobile, stack cards and forms vertically.

Navigation style:
- White background with black text for light pages, or black background with white text for dark pages.
- Active navigation item should have a black filled pill or a white outlined pill depending on the background.
- Use small line icons beside navigation labels.
- Keep navigation simple and readable.

Typography:
- Use a modern sans-serif font.
- Page title: large, bold, clear.
- Section label: small uppercase text in gray.
- Card title: medium bold.
- Body text: normal readable size.
- Table text: compact but readable.
- Avoid decorative fonts.

Button system:
- Primary button: black background, white text.
- Secondary button: white background, black text, gray border.
- Danger button: white or light red background with red text and red border.
- Disabled button: gray background and gray text.
- Buttons should have 6px to 8px radius.
- Include simple line icons in action buttons when useful.

Form system:
- Clear labels above inputs.
- Inputs with thin gray border.
- Focus state: black border or subtle outline.
- Validation errors shown under fields in red text.
- Form cards should be clean and not too large.
- Use two-column layout on desktop and one-column layout on mobile.

Table system:
- Compact admin-style tables.
- Thin row dividers.
- Light row hover state.
- Header row in light gray or black depending on theme.
- Actions placed on the right.
- Use badges for role, status, payment status, and reminder status.
- Empty state should show a simple message and an add button.

Badge system:
- ACTIVE: green
- PENDING: yellow
- SUSPENDED: red or amber
- DELETED: gray or red
- COMPLETED: green
- IN_PROGRESS: yellow
- UPCOMING: yellow
- DUE: red
- PAID: green
- CANCELLED: red
- UNPAID or PENDING PAYMENT: yellow

Screen 1: Login Page
Create a clean login screen.
Requirements:
- Black full-page background.
- White login card.
- App title: Car Service and Maintenance Tracker.
- Subtitle: Workshop management system.
- Email field.
- Password field.
- Login button.
- Register link.
- Small preview section showing example dashboard stats:
  - 124 vehicles
  - 38 service records
  - 12 reminders due
- Use minimal styling and strong contrast.

Screen 2: Register Page
Create a matching register screen.
Fields:
- Full name
- Email
- Phone
- Password
- Confirm password
- Register button
- Back to login link
Keep it simple and consistent with login.

Screen 3: Dashboard Page
Create the main dashboard.
Header:
- Small label: Workshop overview
- Title: Dashboard
- Subtitle: Live overview of users, vehicles, services, reminders, and invoices.
- Global search bar under the title.

Summary cards:
- Total users
- Active users
- Total vehicles
- Service records
- Maintenance reminders
- Invoices

Each card:
- Thin border
- Simple icon
- Large number
- Short label
- Optional small helper text

Dashboard sections:
- Recent users table
- Recent audit logs table
- Quick actions grid with cards:
  - Add user
  - Add vehicle
  - Add service record
  - Add reminder
  - Create invoice

Screen 4: User Management List
Create a user management page.
Top area:
- Title: User Management
- Add user button
- Search input
- Role filter
- Status filter

Table columns:
- Name
- Email
- Phone
- Role
- Status
- Created date
- Actions

Actions:
- View
- Edit
- Suspend
- Restore
- Delete

Screen 5: User Form and Profile
Create user add/edit form.
Fields:
- Full name
- Email
- Phone
- Role
- Status
- Password field only for create mode

Create profile/detail page:
- User information summary
- Account status badge
- Last login
- Recent activity
- Edit button

Screen 6: Vehicle Management List
Create a vehicle list page.
Top area:
- Title: Vehicle Management
- Add vehicle button
- Search input
- Status filter
- Fuel type filter

Table columns:
- Vehicle number
- Owner
- Brand
- Model
- Type
- Fuel type
- Mileage
- Status
- Actions

Actions:
- View
- Edit
- Delete/archive

Screen 7: Vehicle Form and Detail
Vehicle form fields:
- Vehicle number
- Owner dropdown
- Brand
- Model
- Type
- Fuel type
- Manufacture year
- Mileage
- Status
- Notes

Vehicle detail page:
- Vehicle number as main title
- Brand and model subtitle
- Owner information panel
- Vehicle information panel
- Service history preview table
- Maintenance reminder preview
- Edit button
- Archive/delete button

Screen 8: Service Management List
Create a service records list page.
Top area:
- Title: Service Records
- Add service record button
- Search input for service type, service center, or vehicle number
- Vehicle filter dropdown

Table columns:
- Vehicle
- Service type
- Service date
- Mileage at service
- Service center
- Cost
- Actions

Actions:
- View
- Edit
- Delete

Screen 9: Service Form and Detail
Service form fields:
- Vehicle dropdown
- Service type
- Service date
- Mileage at service
- Service center
- Cost
- Notes

Service detail page:
- Vehicle number
- Brand and model
- Service type
- Service date
- Mileage at service
- Service center
- Cost
- Notes
- Created date
- Updated date
- Edit button
- Delete button

Screen 10: Maintenance Reminder List
Create a maintenance reminder page.
Top area:
- Title: Maintenance Reminders
- Add reminder button
- Search input
- Status filter

Table columns:
- Vehicle number
- Reminder title
- Reminder date
- Last service date
- Mileage interval
- Status
- Actions

Actions:
- View
- Edit
- Mark completed
- Delete/archive

Screen 11: Maintenance Reminder Form and Detail
Reminder form fields:
- Vehicle number
- Title
- Reminder date
- Last service date
- Mileage interval
- Status
- Notes

Reminder detail page:
- Title
- Vehicle number
- Reminder date
- Last service date
- Mileage interval
- Status badge
- Notes
- Created date
- Updated date
- Completed date if available
- Edit button
- Archive/delete button

Screen 12: Billing and Invoice List
Create billing and invoice management page.
Top area:
- Title: Billing and Invoices
- Create invoice button
- Search input
- Payment status filter
- Payment method filter

Table columns:
- Invoice number
- Service record
- Vehicle
- Invoice date
- Total amount
- Payment status
- Payment method
- Actions

Actions:
- View
- Edit payment
- Print
- Cancel/delete

Screen 13: Invoice Form
Invoice form fields:
- Service record dropdown
- Invoice number
- Invoice date
- Total amount
- Payment status
- Payment method
- Notes

Keep this form clean and practical.

Screen 14: Invoice Detail and Print Preview
Create a simple invoice preview page.
Include:
- Workshop name: Car Service and Maintenance Tracker
- Invoice number
- Invoice date
- Vehicle number
- Vehicle brand/model
- Service type
- Service date
- Service center
- Mileage at service
- Total amount
- Payment status badge
- Payment method
- Notes
- Print button
- Edit button
- Back button

Design should look printable:
- White invoice panel
- Black text
- Thin border
- Clear amount section

Screen 15: Profile Page
Create profile page.
Include:
- User full name
- Email
- Phone
- Role
- Status
- Last login
- Edit profile form
- Save button

Screen 16: Error Pages
Create simple pages for:
- 403 Forbidden
- 404 Not Found
- Generic error

Each page should include:
- Large error code
- Short message
- Back to dashboard button
- Black and white style

Responsive design requirements:
- Desktop: full tables, horizontal navigation, two-column forms where useful.
- Tablet: compact tables and stacked sections.
- Mobile: navigation collapses, cards stack, forms become one column, tables scroll horizontally or become card rows.

Accessibility requirements:
- Good color contrast.
- Clear focus states.
- Labels for all form inputs.
- Buttons have readable text.
- Status should not rely only on color.

Final output expected:
Generate a complete UI design system and all main screens for the Car Service and Maintenance Tracker. The design should be detailed enough that a developer can recreate it using Thymeleaf templates and Tailwind CSS. Keep it black and white, clean, practical, developer-docs inspired, and suitable for a university project viva.
```
