# Seed Data

Sample/development data for the Snowboard & Mountain Gear Storefront, loaded by
[`seed.sql`](seed.sql). This data exists so the team — and anyone presenting or demoing — can
spin up a database that immediately looks real: populated catalog, an active cart, placed
orders, and a customer↔expert conversation, plus a ready-made **test account for every role**.

> Load it **after** the schema:
>
> ```bash
> mysql -u root -p < database/schema.sql
> mysql -u root -p < database/seed.sql
> ```
>
> For cloud (Aiven) loading, see [`DEPLOYMENT.md`](DEPLOYMENT.md). For the schema itself, see
> [`DATABASE.md`](DATABASE.md).

---

## ⚠️ About the passwords

The `password_hash` values in [`seed.sql`](seed.sql) are **placeholder development strings**,
**not** real bcrypt hashes. They exist only so the rows are well-formed. **You cannot actually
log in with these until the Java application layer implements real password hashing** at
registration/login time.

- **Never store plaintext passwords.** Real hashing happens in the logic tier.
- The "passwords" listed below are the *intended* dev passwords noted in `seed.sql` comments —
  they're for documentation and future testing once auth is wired up.
- These are throwaway test accounts on test-only email domains (`*.test`). Don't reuse these
  credentials anywhere real.

---

## Test Accounts

Six users are seeded: **1 admin, 2 experts, 3 customers**. (`user_id` is shown because the rest
of the seed data references these ids.)

| `user_id` | Role | Username | Email | Dev password |
|-----------|------|----------|-------|--------------|
| 1 | `admin` | `admin` | `admin@summitgear.test` | `admin123` |
| 2 | `expert` | `expert_kai` | `kai@summitgear.test` | `expert123` |
| 3 | `expert` | `expert_sam` | `sam@summitgear.test` | `expert123` |
| 4 | `customer` | `jordan` | `jordan@example.test` | `pass123` |
| 5 | `customer` | `mia` | `mia@example.test` | `pass123` |
| 6 | `customer` | `leo` | `leo@example.test` | `pass123` |

Each user has a matching row in `profile` (one-to-one). Admin and the two experts have a `bio`;
the three customers have `bio = NULL` to show that profiles can be partially filled in.

---

## What Else Gets Loaded

### 🟢 Catalog — 8 categories, 16 products

Eight categories, each with two products (16 total). Prices are realistic and stock is
non-zero so checkout flows work out of the box.

| `category_id` | Category | Products (`product_id`) |
|---------------|----------|--------------------------|
| 1 | Snowboards | Summit All-Mountain 156 (1), Powder Hound 162W (2) |
| 2 | Boots | Glacier Boa Boots (3), Park Flex Boots (4) |
| 3 | Bindings | Ridge Lock Bindings (5), FreeFlex Pro Bindings (6) |
| 4 | Helmets | Crest Snow Helmet (7), Guard MIPS Helmet (8) |
| 5 | Goggles | Vista Wide Goggles (9), Storm Low-Light Goggles (10) |
| 6 | Jackets | Alpine Shell Jacket (11), Insulated Summit Jacket (12) |
| 7 | Gloves | Frostline Gloves (13), Summit Mittens (14) |
| 8 | Accessories | Edge Tuning Kit (15), Padded Board Bag (16) |

### 🟠 Commerce — an active cart and two orders

**Cart** (`cart_id = 1`) belongs to **Jordan** (`user_id = 4`) and holds two line items:

| Product | Qty |
|---------|-----|
| Vista Wide Goggles (9) | 1 |
| Frostline Gloves (13) | 2 |

**Orders** demonstrate two different statuses from the order lifecycle:

| `order_id` | Customer | Status | `total_amount` | Line items (`price_at_order`) |
|------------|----------|--------|----------------|-------------------------------|
| 1 | Mia (5) | `delivered` | 709.98 | Summit All-Mountain 156 ×1 @ 449.99 · Glacier Boa Boots ×1 @ 259.99 |
| 2 | Leo (6) | `pending` | 139.99 | Guard MIPS Helmet ×1 @ 139.99 |

> **Note:** each order's `total_amount` equals the sum of `quantity × price_at_order` across its
> items (449.99 + 259.99 = 709.98; 139.99 = 139.99). `price_at_order` is the **snapshot** price
> captured at checkout — see design rationale in [`DATABASE.md`](DATABASE.md).

### 🟣 Messaging — one conversation, four messages

A single conversation (`conversation_id = 1`) between **Jordan** (customer 4) and **Kai**
(expert 2), subject *"Which board size for all-mountain?"*. It contains four messages that
alternate between the two participants. The final message is left **unread** (`is_read = FALSE`)
to demonstrate unread badges in the dashboards.

---

## Why the Seed Data Is Built This Way

- **Explicit primary-key ids.** Every `INSERT` sets its `*_id` explicitly so foreign keys line
  up deterministically and the data is repeatable across reloads.
- **Covers every table.** All 10 tables get at least one row, so the demo exercises the full
  schema — accounts, profiles, catalog, cart, orders, and messaging.
- **Designed to show off constraints.** Quantities are `> 0`, the conversation's customer and
  expert differ (`customer_id <> expert_id`), and no product is duplicated within a cart or
  order — every row respects the schema's `CHECK` and `UNIQUE` rules.
- **Two order statuses** (`delivered` and `pending`) so the order-status lifecycle has visible,
  real examples to point at during a demo.

---

## Related Documentation

- [`schema.sql`](schema.sql) — the table definitions this data loads into
- [`seed.sql`](seed.sql) — the actual `INSERT` statements
- [`DATABASE.md`](DATABASE.md) — full schema design and rationale
- [`DEPLOYMENT.md`](DEPLOYMENT.md) — loading the data on Aiven (cloud MySQL)
- [`ERD_WALKTHROUGH.md`](ERD_WALKTHROUGH.md) — presentation guide for the ERDs
- [`../README.md`](../README.md) — project overview
