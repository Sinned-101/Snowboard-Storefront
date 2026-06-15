# Database Design

> This document doubles as a presentation aide. It covers **what** the schema looks like,
> **how** the tables relate, and — most importantly — **why** each design choice was made.
> The full SQL lives in [`schema.sql`](schema.sql). For seed/sample data, see
> [`SEED_DATA.md`](SEED_DATA.md).

> **🌐 Hosting:** We're using **[Aiven for MySQL](https://aiven.io/mysql)** as our online
> database host. See [`DEPLOYMENT.md`](DEPLOYMENT.md) for setup details.

## At a Glance

- **Engine:** InnoDB (every table) for foreign-key enforcement and transactions
- **Charset / collation:** `utf8mb4` / `utf8mb4_unicode_ci` (full Unicode, including emoji)
- **Database name:** `snowboard_storefront`
- **Table count:** **10**

| # | Table | Purpose |
|---|-------|---------|
| 1 | `users` | Accounts and roles (customer, expert, admin) |
| 2 | `profile` | One-to-one personal/contact details for a user |
| 3 | `category` | Product groupings (boards, boots, bindings, …) |
| 4 | `product` | Items for sale, each in a category |
| 5 | `cart` | One active cart per customer |
| 6 | `cart_items` | Products + quantities inside a cart |
| 7 | `orders` | A placed order belonging to a customer |
| 8 | `order_items` | Products + quantities + price snapshot inside an order |
| 9 | `conversation` | A messaging thread between a customer and an expert |
| 10 | `message` | An individual message inside a conversation |

## Entity-Relationship Overview

```text
users 1───1 profile
users 1───1 cart 1───* cart_items *───1 product *───1 category
users 1───* orders 1───* order_items *───1 product
users 1───* conversation *───1 users        (customer_id and expert_id)
conversation 1───* message *───1 users       (sender_id)
```

Reading the notation: `1───*` means "one to many." For example, one `cart` has many
`cart_items`, and each `cart_item` points back to exactly one `product`.

## Table-by-Table

### 1. `users`
The account record for everyone on the site.
- **PK:** `user_id`
- **Key columns:** `username` (unique), `email` (unique), `password_hash`,
  `role` ENUM(`customer`, `expert`, `admin`), `created_at`
- **Why:** A single accounts table with a `role` column keeps authentication simple —
  one place to log in — while the role drives which dashboard and features a user sees.

### 2. `profile`
Optional personal and shipping details, split out from the login record.
- **PK:** `profile_id` · **FK:** `user_id → users` (unique = one-to-one)
- **Key columns:** name, phone, full address fields, `bio`
- **Why:** Separating profile data from `users` keeps the authentication table small and
  fast, and lets a profile stay optional/incomplete without cluttering login logic.

### 3. `category`
The catalog's top-level groupings.
- **PK:** `category_id` · `name` is unique
- **Why:** Normalizing categories into their own table avoids repeating category text on
  every product and makes it easy to rename a category in one place.

### 4. `product`
Everything for sale.
- **PK:** `product_id` · **FK:** `category_id → category`
- **Key columns:** `name`, `description`, `price` DECIMAL(10,2), `stock_quantity`,
  `image_url`, `created_at`
- **Why:** `DECIMAL` (not `FLOAT`) is used for money to avoid rounding errors. A CHECK
  keeps `price >= 0`. Deleting a category is **restricted** while products reference it.

### 5. `cart`
A customer's working basket before checkout.
- **PK:** `cart_id` · **FK:** `user_id → users` (unique = one cart per user)
- **Why:** Modeling the cart in the database (rather than only in the browser session)
  means a basket survives logout and can be opened on another device.

### 6. `cart_items`
The line items within a cart.
- **PK:** `cart_item_id` · **FKs:** `cart_id → cart`, `product_id → product`
- **Unique:** `(cart_id, product_id)` — a product appears at most once per cart
- **Why:** The unique pair forces "increase the quantity" instead of creating duplicate
  rows for the same product. A CHECK keeps `quantity > 0`.

### 7. `orders`
A finalized purchase.
- **PK:** `order_id` · **FK:** `user_id → users`
- **Key columns:** `status` ENUM(`pending`, `paid`, `shipped`, `delivered`, `cancelled`),
  `total_amount`, `order_date`
- **Why:** Orders are kept separate from carts because an order is a permanent historical
  record, while a cart is temporary. Deleting a user is **restricted** if they have orders,
  so purchase history is never silently lost.

### 8. `order_items`
The line items within an order.
- **PK:** `order_item_id` · **FKs:** `order_id → orders`, `product_id → product`
- **Key column:** `price_at_order` — the unit price captured at purchase time
- **Why:** Storing `price_at_order` means past orders stay accurate even if the product's
  current price changes later. Deleting an order **cascades** to its items.

### 9. `conversation`
A messaging thread linking one customer and one expert.
- **PK:** `conversation_id` · **FKs:** `customer_id → users`, `expert_id → users`
- **Key columns:** `subject`, `created_at`, `updated_at` (auto-updates on change)
- **Unique:** `(customer_id, expert_id)` — one thread per customer/expert pair
- **Why:** Grouping messages under a conversation lets each dashboard list **threads**
  instead of loose messages. `updated_at` makes it easy to sort by most-recent activity.

### 10. `message`
A single message inside a conversation.
- **PK:** `message_id` · **FKs:** `conversation_id → conversation`, `sender_id → users`
- **Key columns:** `body`, `is_read`, `sent_at`
- **Why:** `sender_id` records who wrote each message; `is_read` powers unread badges.
  Deleting a conversation **cascades** to its messages.

## Design Choices & Rationale (Presentation Talking Points)

This is the "why" behind the schema — the part most worth presenting.

1. **Plural `users` / `orders` to dodge reserved words.**
   `USER` and `ORDER` are reserved words in MySQL. Naming the tables `users` and `orders`
   means no backtick-escaping is needed anywhere, which keeps the SQL — and the Java that
   builds queries — cleaner and less error-prone.

2. **One `users` table with a `role` column (vs. three separate tables).**
   Customers, experts, and admins share the same login flow and most of the same fields.
   A single table with an ENUM `role` avoids duplication and makes authentication uniform;
   role-specific behavior is handled in the application layer.

3. **`profile` split out one-to-one from `users`.**
   Keeps the authentication record lean and lets profile details be optional and grow
   without touching the login-critical table.

4. **`DECIMAL` for all money, never `FLOAT`/`DOUBLE`.**
   Floating-point can't represent values like 19.99 exactly, which causes cent-level
   rounding errors. `DECIMAL(10,2)` stores currency precisely.

5. **`price_at_order` snapshot on `order_items`.**
   An order is a historical record. Capturing the price at purchase time means changing a
   product's price later never rewrites what a customer was actually charged.

6. **Cart and order modeled separately.**
   A cart is temporary and editable; an order is permanent. Keeping them apart avoids
   overloading one table with two very different lifecycles.

7. **`conversation` + `message` split (instead of one flat messages table).**
   Grouping messages into threads lets dashboards show conversations, track per-thread
   subjects, and sort by recent activity. A flat list of messages would make all of that
   harder.

8. **Deliberate `ON DELETE` rules — `CASCADE` vs. `RESTRICT`.**
   - **CASCADE** where child data is meaningless without its parent: a user's `profile`,
     `cart`, `cart_items`, `conversation`, and `message`; an order's `order_items`.
   - **RESTRICT** where deletion would destroy history or integrity: you can't delete a
     `category` that still has products, a `product` still referenced by orders/carts, or a
     `users` row that still has orders.

9. **`UNIQUE` constraints to enforce business rules in the database.**
   `username`/`email` are unique; `(cart_id, product_id)` and `(order_id, product_id)` are
   unique so a product can't be duplicated within the same cart or order; one `cart` and
   one `profile` per user; one `conversation` per customer/expert pair.

10. **ENUMs for `role` and order `status`.**
    The set of valid roles and statuses is small and fixed, so an ENUM enforces valid
    values at the database level and self-documents the allowed states.

11. **`CHECK` constraints for sanity.**
    Quantities must be `> 0`, prices `>= 0`, and a conversation's customer and expert must
    differ. **Caveat:** CHECK constraints are only **enforced in MySQL 8.0.16+** — on older
    versions they are parsed but ignored, so the team should develop on 8.0.16 or newer.

12. **InnoDB + `utf8mb4` everywhere.**
    InnoDB is required for foreign keys and transactions (important at checkout).
    `utf8mb4` supports the full Unicode range, including emoji in messages and product text.

## Anticipated Q&A

- **"Why not store the cart only in the browser?"** A database cart survives logout and
  works across devices, and it lets experts/admins reason about real data.
- **"What stops duplicate line items?"** The `UNIQUE (cart_id, product_id)` and
  `UNIQUE (order_id, product_id)` constraints — the app increments quantity instead.
- **"What happens to orders if a customer is deleted?"** Nothing is lost: the FK is
  `RESTRICT`, so the delete is blocked while orders exist.
- **"Why duplicate price into `order_items`?"** It's a point-in-time snapshot so historical
  orders remain accurate after price changes.
- **"Could two people open the same thread twice?"** No — `UNIQUE (customer_id, expert_id)`
  guarantees one conversation per pair.
