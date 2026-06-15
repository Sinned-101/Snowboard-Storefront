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
3. **Data** — MySQL database with nine tables

## Tech Stack
- **Database:** MySQL
- **Server-side:** Java
- **Front end:** HTML, CSS, JavaScript
- **Version control:** GitHub

## Database Schema (9 tables)
`user`, `profile`, `product`, `category`, `cart`, `cart_items`, `order`, `order_items`, `message`

## Getting Started
_Setup instructions to be added as the project develops._

## Project Timeline

| Dates | Planned Work |
|-------|--------------|
| Jun 8 – Jun 21 | Database tables and basic page templates |
| Jun 22 – Jul 5 | User accounts: registration, login, logout, role-based access, dashboards |
| Jul 6 – Jul 19 | Storefront pages: categories, product lists, product details, admin product controls |
| Jul 20 – Aug 2 | Shopping cart and orders |
| Aug 3 – Aug 26 | Messaging system, testing, sample data, demo video |
