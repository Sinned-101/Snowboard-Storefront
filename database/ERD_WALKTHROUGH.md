# ERD Walkthrough & Presentation Guide

A speaker-friendly guide to the two Entity-Relationship Diagrams for the Snowboard &
Mountain Gear Storefront. Use this to **present** the database design with confidence —
it tells you what to point at, what to say, and what questions to expect.

| Diagram file | Best for |
|--------------|----------|
| [`snowboard_storefront_erd_simple_Version2.drawio`](snowboard_storefront_erd_simple_Version2.drawio) | The **opening slide** — a clean, 10-box overview to show the "big picture" |
| [`snowboard_storefront_erd_Version2.drawio`](snowboard_storefront_erd_Version2.drawio) | The **deep-dive slide** — every table with all columns, keys, and constraints |

> **Related docs:** [`DATABASE.md`](DATABASE.md) (full written design + rationale) ·
> [`schema.sql`](schema.sql) (the actual SQL) · [`SEED_DATA.md`](SEED_DATA.md) (sample data).

---

## How to Open & Present These Diagrams

1. Go to **[app.diagrams.net](https://app.diagrams.net)** (draw.io) → **Open Existing Diagram**
   → select the `.drawio` file. No install needed.
2. To put a diagram on a slide, use **File → Export as → PNG** (or SVG) and paste the image.
3. **Presentation tip:** Open the **simple** diagram first to set the scene, then switch to
   the **detailed** one when someone asks "what's actually in each table?"

---

## Reading the Diagrams: The Notation

Both diagrams use **crow's-foot notation** for relationships:

| Symbol | Meaning | Say it as |
|--------|---------|-----------|
| `|` (single bar, **ERone**) | "exactly one" | "one" |
| crow's foot (**ERmany**) | "many" | "many" |
| `1:1` label | one-to-one | "each X has exactly one Y" |
| `1:N` label | one-to-many | "one X has many Y" |
| **Dashed** line | a second/third foreign key into the **same** table (`users`) | "another link into users" |

**Color coding** (consistent across both diagrams) groups tables by purpose:

| Color | Group | Tables |
|-------|-------|--------|
| 🔵 Blue | **Accounts** | `users`, `profile` |
| 🟢 Green | **Catalog** | `category`, `product` |
| 🟠 Orange | **Commerce** | `cart`, `cart_items`, `orders`, `order_items` |
| 🟣 Purple | **Messaging** | `conversation`, `message` |

> **Presenter line:** *"The colors aren't decoration — they map to the four real areas of
> the app: who you are, what we sell, what you buy, and how you talk to an expert."*

---

## Diagram 1 — The Overview ERD (start here)

**File:** [`snowboard_storefront_erd_simple_Version2.drawio`](snowboard_storefront_erd_simple_Version2.drawio)
**Title in file:** *"Snowboard Storefront ERD (Overview)"*

This is the 30-second elevator pitch of the database: **10 boxes, color-grouped, with
labeled relationship lines** — and deliberately **no columns**, so the audience focuses on
structure, not detail.

### What to point at, in order

1. **The four color clusters.** *"Ten tables, four areas — accounts, catalog, commerce,
   messaging."*
2. **Accounts (blue):** `users` → `profile`, labeled **1:1**. *"Every account can have one
   profile with shipping details."*
3. **Catalog (green):** `category` → `product`, labeled **1:N**. *"One category holds many
   products."*
4. **Commerce (orange):** Walk the purchase path —
   - `users` → `cart` (**1:1**): *"one active cart per user,"*
   - `cart` → `cart_items` (**1:N**) and `product` → `cart_items` (**1:N**): *"a cart holds
     many line items, each pointing to a product,"*
   - `users` → `orders` (**1:N**) → `order_items` (**1:N**): *"placing an order creates a
     permanent record with its own line items."*
5. **Messaging (purple):** `conversation` → `message` (**1:N**), with **dashed** lines from
   `users` into `conversation` (customer & expert) and `message` (sender).
   *"A conversation links a customer and an expert; each holds many messages."*
6. **The dashed lines into `users`.** Call these out explicitly — they're the one "tricky"
   part: *"`users` appears multiple times as a foreign key — a customer, an expert, and a
   message sender are all just users with different roles."*

### One-sentence summary for the slide
> *"This is the whole system at a glance: accounts and profiles, a product catalog, a
> cart-to-order purchase flow, and a customer–expert messaging thread — ten tables in four
> color-coded groups."*

---

## Diagram 2 — The Detailed ERD (the deep dive)

**File:** [`snowboard_storefront_erd_Version2.drawio`](snowboard_storefront_erd_Version2.drawio)
**Title in file:** *"Snowboard Storefront ERD"*

Same ten tables and relationships, but now **every column is shown** with its type and
constraints. Use this when the audience wants specifics. There's a **legend box** on the
diagram explaining the abbreviations.

### Column abbreviations (from the diagram legend)

| Tag | Meaning |
|-----|---------|
| **PK** | Primary Key |
| **FK** | Foreign Key |
| **U** | Unique constraint |
| **NN** | Not Null |
| **AI** | Auto Increment |

### Table-by-table — what each box shows

**🔵 `users`** — the account record for everyone.
- `PK user_id`, plus `username` (NN, U), `email` (NN, U), `password_hash` (NN),
  `role` ENUM(customer, expert, admin) (NN), `created_at` (NN).
- **Talking point:** *"One table for all three roles — the `role` column decides which
  dashboard you see."*

**🔵 `profile`** — optional personal/shipping details, one per user.
- `PK profile_id`, `FK user_id` (NN, **U** → makes it 1:1), then name, phone, full address
  fields, and `bio`.
- **Talking point:** *"Split out from `users` so the login table stays lean and a profile
  can stay optional."*

**🟢 `category`** — top-level catalog groupings.
- `PK category_id`, `name` (NN, U), `description`.
- **Talking point:** *"Normalized so a category name lives in one place."*

**🟢 `product`** — everything for sale.
- `PK product_id`, `FK category_id` (NN), `name` (NN), `description`,
  `price` DECIMAL(10,2) (NN), `stock_quantity` (NN), `image_url`, `created_at` (NN).
- **Talking point:** *"`DECIMAL` for money — never floats — so prices are exact to the cent."*

**🟠 `cart`** — a customer's working basket.
- `PK cart_id`, `FK user_id` (NN, **U** → one cart per user), `created_at`.
- **Talking point:** *"The cart lives in the database, so it survives logout and follows you
  across devices."*

**🟠 `cart_items`** — the line items inside a cart.
- `PK cart_item_id`, `FK cart_id` (NN), `FK product_id` (NN),
  `quantity` (NN) with **U(cart_id, product_id)**.
- **Talking point:** *"The unique pair means a product can't appear twice — we bump the
  quantity instead."*

**🟠 `orders`** — a finalized purchase.
- `PK order_id`, `FK user_id` (NN), `status` ENUM(pending…cancelled) (NN),
  `total_amount` DECIMAL(10,2) (NN), `order_date` (NN).
- **Talking point:** *"Separate from the cart because an order is permanent history."*

**🟠 `order_items`** — the line items inside an order.
- `PK order_item_id`, `FK order_id` (NN), `FK product_id` (NN), `quantity` (NN),
  `price_at_order` DECIMAL(10,2) (NN) with **U(order_id, product_id)**.
- **Talking point:** *"`price_at_order` snapshots the price at checkout, so changing a
  product's price later never rewrites old receipts."*

**🟣 `conversation`** — a thread linking one customer and one expert.
- `PK conversation_id`, `FK customer_id` (NN), `FK expert_id` (NN), `subject`,
  `created_at` (NN), `updated_at` (NN) with **U(customer_id, expert_id)**.
- **Talking point:** *"Two foreign keys into `users` — a customer and an expert — and a
  unique pair so there's only one thread per duo."*

**🟣 `message`** — a single message in a thread.
- `PK message_id`, `FK conversation_id` (NN), `FK sender_id` (NN), `body` (NN),
  `is_read` BOOLEAN (NN), `sent_at` (NN).
- **Talking point:** *"`sender_id` records who wrote it; `is_read` powers unread badges."*

---

## The "Why" Behind the Design (Your Best Talking Points)

These are the decisions most likely to earn marks in a capstone presentation. Pull the
matching tables up on screen as you say each one.

1. **One `users` table with a `role` column** instead of three separate tables — same login
   flow for everyone, role drives the UI.
2. **`profile` split 1:1 from `users`** — keeps the auth table small; profile stays optional.
3. **`DECIMAL` for all money** — floats can't store `19.99` exactly; `DECIMAL(10,2)` can.
4. **`price_at_order` snapshot** — historical orders stay accurate after price changes.
5. **Cart vs. order separated** — temporary/editable vs. permanent/historical lifecycles.
6. **`conversation` + `message` split** — lets dashboards show threads and sort by recency.
7. **Deliberate `ON DELETE` rules** — CASCADE where child data is meaningless without its
   parent; RESTRICT where deleting would destroy history (e.g., a user with orders).
8. **`UNIQUE` constraints enforce business rules in the DB** — no duplicate line items, one
   cart/profile per user, one conversation per customer–expert pair.
9. **ENUMs for `role` and `status`** — small fixed sets, validated at the database level.
10. **Plural `users` / `orders`** — dodges MySQL reserved words `USER`/`ORDER`.

---

## Suggested 5-Minute Presentation Flow

| Time | Slide / Action | Diagram |
|------|----------------|---------|
| 0:00 | "Here's the whole system — 10 tables, 4 areas." | **Overview** |
| 0:45 | Walk the four color groups and the purchase flow. | **Overview** |
| 2:00 | "Now let's look inside the tables." | **Detailed** |
| 2:30 | Highlight 3–4 key columns: `role`, `DECIMAL price`, `price_at_order`, unique pairs. | **Detailed** |
| 4:00 | "Here's *why* we made these choices." (use the list above) | either |
| 4:30 | Take questions (see Q&A below). | — |

---

## Anticipated Q&A (rehearse these)

- **"Why not store the cart only in the browser?"**
  A database cart survives logout and works across devices.
- **"What stops duplicate line items?"**
  The `UNIQUE (cart_id, product_id)` and `UNIQUE (order_id, product_id)` constraints — the
  app increments quantity instead of inserting a duplicate.
- **"What happens to orders if a customer is deleted?"**
  Nothing is lost — the foreign key is `RESTRICT`, so the delete is blocked while orders exist.
- **"Why duplicate the price into `order_items`?"**
  It's a point-in-time snapshot, so historical orders stay accurate after price changes.
- **"Could two people open the same thread twice?"**
  No — `UNIQUE (customer_id, expert_id)` guarantees one conversation per pair.
- **"Why does `users` connect to so many tables?"**
  Because customers, experts, and admins are all `users`; the dashed lines are just multiple
  foreign keys (customer, expert, sender) pointing back to that one table.

---

## Quick Cheat-Sheet (glance before you present)

- **10 tables, 4 color groups:** accounts (blue), catalog (green), commerce (orange),
  messaging (purple).
- **Two 1:1s:** `users–profile`, `users–cart`.
- **The money rule:** `DECIMAL`, plus `price_at_order` snapshot.
- **The duplicate-prevention rule:** unique `(cart_id, product_id)` and `(order_id, product_id)`.
- **The history rule:** orders are permanent; deleting a user with orders is blocked (RESTRICT).
- **The messaging rule:** one `conversation` per customer–expert pair; many `message`s each.
