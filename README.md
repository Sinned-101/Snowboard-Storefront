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

1. **Presentation** — HTML, CSS, and JavaScript (home, register, login, product listing,
   product details, cart, checkout, three dashboards, messages)
2. **Logic** — Java (authentication, routing, order processing, messaging)
3. **Data** — MySQL database with ten tables

## Tech Stack
- **Database:** MySQL
- **Server-side:** Java
- **Front end:** HTML, CSS, JavaScript
- **Version control:** GitHub

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
_Setup instructions to be added as the project develops._

Create and seed the database locally (MySQL 8.0.16+ recommended):

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```

See [`database/SEED_DATA.md`](database/SEED_DATA.md) for details on the sample data and
test accounts.

## Project Timeline

| Dates | Planned Work |
|-------|--------------|
| Jun 8 – Jun 21 | Database tables and basic page templates |
| Jun 22 – Jul 5 | User accounts: registration, login, logout, role-based access, dashboards |
| Jul 6 – Jul 19 | Storefront pages: categories, product lists, product details, admin product controls |
| Jul 20 – Aug 2 | Shopping cart and orders |
| Aug 3 – Aug 26 | Messaging system, testing, sample data, demo video |
