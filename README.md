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
- **Administrator** — manage the catalog, user accounts, orders, and conversations

Each role lands on a dashboard built for them.

## Architecture (Three-Tier)

1. **Presentation** — HTML, CSS, JavaScript, and Thymeleaf templates for the home page, registration, login, product pages,
   product details, cart, checkout, dashboards, and messaging
2. **Logic** — Java with Spring Boot controllers, authentication, routing, registration, logout, dashboard navigation, product browsing, admin product/category/user management, profile editing, password changes, shopping cart management, checkout, order processing, and messaging
3. **Data** — MySQL database with ten tables

## Tech Stack
- **Database:** MySQL
- **Server-side:** Java, Spring Boot, JDBC
- **Templates:** Thymeleaf
- **Front end:** HTML, CSS, JavaScript
- **Version control:** GitHub

## Frontend Pages

The initial frontend webpage templates were created with HTML and CSS, and all main pages have now been connected to the Spring Boot backend using Thymeleaf. The application uses Spring Boot routes instead of opening static HTML files directly. The storefront also includes a site logo, homepage images, and product images displayed through Thymeleaf templates and database image paths.

Added pages include:

* Public pages: home, product listing, product details, login, and registration
* Customer pages: customer dashboard, profile, cart, checkout, order confirmation, active orders, completed orders, and order editing
* Expert pages: expert dashboard, messages, and conversation details
* Administrator pages: admin dashboard, product management, category management, user management, order management, order creation, and admin conversations
* Account pages: profile editing and password change

The product names, categories, prices, cart items, orders, user profiles, conversations, and messages shown on the pages are based on the sample records in `database/seed.sql`.

## Images and Styling

The storefront includes a custom site logo, homepage images, and product images for selected catalog items. Static images are stored in: `src/main/resources/static/images`.

Product images are displayed on product detail pages using image paths from the database, while homepage and logo images are loaded through Thymeleaf routes.

## Spring Boot Backend

The project now includes a Spring Boot Maven backend. The application runs locally through Spring Boot and uses controller routes to display pages instead of opening static HTML files directly.

Current backend features include:

* Spring Boot Maven project structure with Thymeleaf template support
* JDBC connection to MySQL for database-backed pages and features
* User registration, login, logout, BCrypt password hashing, and session-based login tracking
* Role-based dashboard navigation for customers, experts, and administrators
* Product browsing, product details, category filtering, and product image support
* Admin product, category, and user management
* Profile editing and password changes
* Shopping cart features, including add-to-cart, quantity updates, item removal, and cart total calculation
* Checkout and order placement
* Customer order features, including active orders, completed order history, and pending delivery order editing
* Order fulfillment support for delivery, in-store pickup, and walk-in in-store orders
* Admin order management, including status updates, tracking numbers, pickup collection, cancellations, reversals, and creating orders for customers
* Customer and expert messaging with conversation threads, replies, and access checks
* Admin conversation management, including viewing, replying, and reassigning conversations
* Expert assignment based on the fewest active conversation threads
* Header logo, homepage images, and product images
* Live deployment with the application and hosted database on Railway  

Additional backend work could continue with more testing, improved validation, clearer success and error messages, and additional user interface improvements.

## Database

The database is a MySQL schema of **10 tables** (`users`, `profile`, `category`,
`product`, `cart`, `cart_items`, `orders`, `order_items`, `conversation`, `message`)
covering accounts, the catalog, carts/orders, and customer↔expert messaging. The order system also includes fields for fulfillment channel, tracking information, pickup details, and order status.

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
  tiers (Browser → Java → MySQL) and how they connect, including Railway hosting.
- **[Class Diagram](Project-Models/class_diagram.drawio)** — the Java domain objects in
  the logic tier and their relationships.
- **[Order Status State Diagram](Project-Models/order_status_state_diagram.drawio)** —
  the legal transitions of `orders.status` (PENDING → … → DELIVERED / CANCELLED).
- **[Sequence Diagrams](Project-Models/sequence_diagrams.md)** — step-by-step flows for
  Checkout (riskiest flow) and Login / authentication.

## Getting Started

The application is hosted live using Railway: https://snowboard-storefront-production.up.railway.app/

Both the Spring Boot application and hosted database are deployed through Railway. Local setup is still available for development using the instructions below.

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
