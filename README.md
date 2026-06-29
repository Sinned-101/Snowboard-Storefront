# Snowboard & Mountain Gear Storefront

A web-based storefront that sells snowboarding and mountain gear, with a built-in
messaging feature that lets shoppers ask a gear expert for advice before buying.

**Course:** CSC 450 100 - Computer Science Capstone  
**Instructor:** James Gappy  
**Team:** Dennis Feldbruegge, Zachary Christianson

## Overview

Customers can browse gear (boards, boots, bindings, helmets, goggles, jackets, gloves,
and other cold-weather gear), compare options, and check out — all in one place. A
built-in messaging feature connects shoppers with gear experts for advice.

### User Roles
- **Customer** — browse, buy, and message experts
- **Expert** — answer customer questions and recommend gear
- **Administrator** — manage the catalog and user accounts

Each role lands on a dashboard built for them.

## Architecture (Three-Tier)

1. **Presentation** — HTML, CSS, JavaScript, and Thymeleaf templates for the home page, registration, login, product pages,
   product details, cart, checkout, dashboards, and messaging
2. **Logic** — Java with Spring Boot controllers, authentication, routing, registration, logout, order processing, and messaging
3. **Data** — MySQL database with ten tables

## Tech Stack
- **Database:** MySQL
- **Server-side:** Java, Spring Boot, JDBC
- **Templates:** Thymeleaf
- **Front end:** HTML, CSS, JavaScript
- **Version control:** GitHub

## Frontend Pages

The initial frontend webpage templates have been added using HTML and CSS. These pages are currently static templates, but their sample content is aligned with the current MySQL schema and seed data.

Added pages include:

* Home page
* Product listing page
* Product details page
* Login page
* Registration page
* Customer dashboard
* Expert dashboard
* Admin dashboard
* Profile page
* Cart page
* Checkout page
* Orders page
* Messages page

The product names, categories, prices, cart items, orders, user profiles, and message examples shown on the pages are based on the sample records in `database/seed.sql`.

## Spring Boot Backend

The project now includes an initial Spring Boot Maven setup. The application can run locally through Spring Boot and uses controller routes to display pages instead of opening static HTML files directly.

Current backend features include:

* Spring Boot project structure with Maven
* Thymeleaf template support
* JDBC connection to the local MySQL database
* Registration form connected to the users and profile tables
* Login form connected to the users table
* Passwords stored using BCrypt password hashing
* Session-based login tracking
* Logout route that ends the current user session
* Error messages for failed registration and login attempts

Additional backend work will continue as dashboards, product pages, cart features, orders, and messaging are connected to the database.


## Database

The database is a MySQL schema of **10 tables** (`users`, `profile`, `category`,
`product`, `cart`, `cart_items`, `orders`, `order_items`, `conversation`, `message`)
covering accounts, the catalog, carts/orders, and customer↔expert messaging.

Full documentation lives in the [`database/`](database/) folder:

- **[Database Design](database/DATABASE.md)** — schema overview, entity-relationship
  diagram, table-by-table breakdown, design rationale, and presentation Q&A.
- **[Seed Data](database/SEED_DATA.md)** — sample/development data and test accounts.
- **[`schema.sql`](database/schema.sql)** — the full SQL DDL.
- **[`seed.sql`](database/seed.sql)** — the sample data.

## Project Models

Whole-project planning models — the actors, architecture, key flows, and domain
objects — live in the [`Project-Models/`](Project-Models/) folder. See its
[`README.md`](Project-Models/README.md) for an index. The diagrams reuse the same
color scheme as the ERD (accounts/catalog/commerce/messaging).

- **[Use Case Diagram](Project-Models/use_case_diagram.drawio)** — the three actors
  (Customer, Expert, Admin) and what each can do.
- **[Architecture Diagram](Project-Models/architecture_diagram.drawio)** — the three
  tiers (Browser → Java → MySQL) and how they connect, including Aiven hosting.
- **[Class Diagram](Project-Models/class_diagram.drawio)** — the Java domain objects in
  the logic tier and their relationships.
- **[Order Status State Diagram](Project-Models/order_status_state_diagram.drawio)** —
  the legal transitions of `orders.status` (PENDING → … → DELIVERED / CANCELLED).
- **[Sequence Diagrams](Project-Models/sequence_diagrams.md)** — step-by-step flows for
  Checkout (riskiest flow) and Login / authentication.

## Getting Started

Create and seed the database locally using MySQL:

```text
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```

Update the local database connection settings in:

```text
src/main/resources/application.properties
```

Example local settings:

```text
spring.datasource.url=jdbc:mysql://localhost:3306/snowboard_storefront
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

Run the Spring Boot application from IntelliJ using:

```text
SnowboardStorefrontApplication.java
```

Then open the application in a browser:

```text
http://localhost:8080
```

See `database/SEED_DATA.md` for details on the sample data and test accounts.

## Project Timeline

| Dates | Planned Work |
|-------|--------------|
| Jun 8 – Jun 21 | Database tables and basic page templates |
| Jun 22 – Jul 5 | User accounts: registration, login, logout, role-based access, dashboards |
| Jul 6 – Jul 19 | Storefront pages: categories, product lists, product details, admin product controls |
| Jul 20 – Aug 2 | Shopping cart and orders |
| Aug 3 – Aug 26 | Messaging system, testing, sample data, demo video |
