# Project Planning & Models

> This document doubles as a **presentation aide** for the project models. For each model it
> covers **what** the diagram shows, **how** to read it, the **need-to-know details** behind it,
> and **talking points / anticipated questions** you can present from directly.
>
> Where the [`database/`](../database/) folder models the **data tier** in depth (see
> [`DATABASE.md`](../database/DATABASE.md)), this folder models the **overall system** — its
> actors, architecture, key runtime flows, and domain objects.

## At a Glance

| # | Model | File | Format | What it answers |
|---|-------|------|--------|-----------------|
| 1 | **Use Case Diagram** | [`use_case_diagram.drawio`](use_case_diagram.drawio) | draw.io | *Who* uses the system and *what* can they do? |
| 2 | **Architecture Diagram** | [`architecture_diagram.drawio`](architecture_diagram.drawio) | draw.io | *How* is the system structured and deployed? |
| 3 | **Class Diagram** | [`class_diagram.drawio`](class_diagram.drawio) | draw.io | *What* are the domain objects and how do they relate? |
| 4 | **Order Status State Diagram** | [`order_status_state_diagram.drawio`](order_status_state_diagram.drawio) | draw.io | *How* does an order move through its lifecycle? |
| 5 | **Sequence Diagrams** | [`sequence_diagrams.md`](sequence_diagrams.md) | Mermaid | *Step-by-step*, how do checkout and login actually run? |

**Suggested presentation order:** Use Case → Architecture → Class → State → Sequence. This
moves from the outside in: who uses it, how it's built, what the objects are, how one object
behaves over time, and finally how the tiers collaborate at runtime.

### Conventions used across every diagram

The models reuse the **same color scheme as the ERD** so the whole project reads consistently.
Each color maps to a functional area of the schema:

| Color | Area | Tables involved |
|-------|------|-----------------|
| 🟦 Blue | **Accounts** | `users`, `profile` |
| 🟩 Green | **Catalog** | `category`, `product` |
| 🟧 Orange | **Commerce** | `cart`, `cart_items`, `orders`, `order_items` |
| 🟪 Purple | **Messaging** | `conversation`, `message` |

### Why two formats?

- **draw.io (`.drawio`)** for the spatial diagrams (use case, architecture, class, state) —
  these are layout-driven and match the `.drawio` ERDs already in `database/`. Open them at
  [app.diagrams.net](https://app.diagrams.net) → *Open Existing Diagram*, then **export to
  PNG/SVG** (*File → Export as*) to drop straight into slides.
- **Mermaid in Markdown** for the sequence diagrams — these render natively on GitHub and are
  far easier to keep in sync with the code than hand-placed boxes. For slides, screenshot the
  rendered diagram from the GitHub page.

---

## 1. Use Case Diagram

**File:** [`use_case_diagram.drawio`](use_case_diagram.drawio)

### What it shows
The complete **scope of the project on one page**: the three actors and every action each one
can perform inside the system boundary. It's the answer to "what are we actually building?"

### How to read it
- **Stick figures** on the outside are **actors** (roles). The large box is the **system
  boundary** — everything inside is functionality we build.
- **Ovals** are **use cases** (things an actor can do).
- A **plain line** from an actor to a use case means "this actor participates in this use
  case." There are no arrowheads — association lines in a use case diagram are undirected.

### Actors and their use cases

| Actor | Use cases |
|-------|-----------|
| **Customer** 🟦 | Register · Log In / Log Out · Manage Profile · Browse / Search Products · View Product Details · Manage Cart · Check Out / Place Order · View Order History · Message an Expert |
| **Expert** 🟦 | Log In / Log Out · Manage Profile · View Customer Questions · Answer / Recommend Gear |
| **Administrator** 🟦 | Log In / Log Out · Manage Catalog (Products / Categories) · Manage User Accounts · Manage / Update Orders |

### Need-to-know details
- **`Log In / Log Out` and `Manage Profile` are shared** by all three roles. This is a direct
  visual consequence of the database decision to use **one `users` table with a `role` column**
  rather than three separate account tables — one login flow serves everyone.
- Use-case ovals are **colored by functional area** (accounts/catalog/commerce/messaging), so
  the diagram visually ties back to both the ERD and the architecture diagram.
- The diagram captures **roles, not individual people** — one person could be both a customer
  and an admin; the role is what determines available use cases.

### Talking points / anticipated questions
- **"Why does the Customer have the most use cases?"** The customer is the primary revenue
  actor — browsing, buying, and messaging are the core product. Experts and admins exist to
  support that experience.
- **"Where's the boundary between Expert and Admin?"** Experts work with **customers and
  content** (answering questions, recommending gear). Admins work with the **system** (catalog,
  accounts, orders). Keeping them as distinct actors documents that separation of duties.
- **"Does an actor map to exactly one database role?"** Yes — the three actors line up 1:1 with
  the `users.role` ENUM (`customer`, `expert`, `admin`).

---

## 2. Architecture Diagram

**File:** [`architecture_diagram.drawio`](architecture_diagram.drawio)

### What it shows
The system's **three-tier architecture** and how the tiers connect — from the user's browser,
through the Java application server, down to the MySQL database hosted on Aiven.

### How to read it
Three stacked bands, top to bottom, each talking **only to its immediate neighbor**:

| Tier | Technology | Responsibilities |
|------|-----------|------------------|
| **Presentation** 🟦 | HTML / CSS / JavaScript (browser) | The pages the user sees: Home, Register, Login, Product List, Product Details, Cart, Checkout, the three Dashboards, and Messages |
| **Logic** 🟩 | Java (application server) | Authentication & Sessions · Routing & Role-Based Access · Order Processing (cart → order) · Messaging |
| **Data** 🟧 | MySQL / InnoDB (on ☁ Aiven) | The `snowboard_storefront` schema of 10 tables |

### The connections (and why they matter)
- **Presentation ↔ Logic** travels over **HTTPS** (requests/responses).
- **Logic ↔ Data** travels over **JDBC secured with SSL/TLS** (SQL queries).
- The **arrows only connect adjacent tiers**. The presentation tier **never touches the
  database directly** — every data operation goes through the Java logic tier. This is the
  single most important rule of the diagram.

### Need-to-know details
- **The data tier is hosted on [Aiven for MySQL](https://aiven.io/mysql)** (managed cloud),
  which is why it's drawn inside a cloud boundary. Connection/setup specifics live in
  [`../database/DEPLOYMENT.md`](../database/DEPLOYMENT.md).
- **InnoDB is called out deliberately** — it's required for the foreign keys and transactions
  the logic tier relies on, most critically the all-or-nothing checkout (see the sequence
  diagram).
- The four boxes inside the logic tier map directly to the **grouped use cases** in the use
  case diagram (auth, routing/roles, orders, messaging), so the two diagrams reinforce each
  other.

### Talking points / anticipated questions
- **"Why three tiers instead of two?"** Separating presentation from logic from data means each
  layer can change independently — you can restyle the UI without touching business rules, or
  swap database hosting without rewriting pages.
- **"Why can't the browser query the database directly?"** Security and integrity. Routing every
  request through the Java tier means authentication, role checks, and validation can't be
  bypassed, and the database credentials never leave the server.
- **"Why is the connection to the database encrypted?"** Because the database lives in the cloud
  (Aiven), traffic crosses the public internet, so JDBC runs over SSL/TLS to protect data in
  transit.

---

## 3. Class Diagram

**File:** [`class_diagram.drawio`](class_diagram.drawio)

### What it shows
The **domain model of the logic tier** — the Java objects the application works with at runtime
and the relationships between them. It's the object-oriented mirror of the database schema.

### How to read it
- Each box is a **class**: name on top, **fields** in the middle, **methods** at the bottom.
- **Filled diamond ◆ (composition)** = "is made of / owns." The part can't meaningfully exist
  without the whole (e.g., a `CartItem` only exists as part of a `Cart`).
- **Open arrow → (association)** = a looser reference between independent objects (e.g., a
  `CartItem` references a `Product`, but the product exists on its own).
- **Dashed arrows** are references back to `User` for specific roles (a conversation's
  customer/expert, a message's sender).
- **`<<enumeration>>`** boxes (`Role`, `OrderStatus`) are fixed sets of allowed values.

### The classes

| Class | Area | Key fields | Key methods |
|-------|------|-----------|-------------|
| `User` 🟦 | Accounts | `userId`, `username`, `email`, `passwordHash`, `role`, `createdAt` | `register()`, `authenticate()` |
| `Role` 🟦 *(enum)* | Accounts | `CUSTOMER`, `EXPERT`, `ADMIN` | — |
| `Profile` 🟦 | Accounts | `fullName`, `phone`, `address`, `bio` | — |
| `Category` 🟩 | Catalog | `categoryId`, `name`, `description` | — |
| `Product` 🟩 | Catalog | `name`, `price`, `stockQuantity`, `imageUrl` | `isInStock()` |
| `Cart` 🟧 | Commerce | `cartId`, `items: List<CartItem>` | `addItem()`, `removeItem()`, `getTotal()`, `checkout()` |
| `CartItem` 🟧 | Commerce | `product`, `quantity` | — |
| `Order` 🟧 | Commerce | `status: OrderStatus`, `totalAmount`, `orderDate`, `items` | `cancel()`, `updateStatus()` |
| `OrderItem` 🟧 | Commerce | `product`, `quantity`, `priceAtOrder` | — |
| `OrderStatus` 🟧 *(enum)* | Commerce | `PENDING`, `PAID`, `SHIPPED`, `DELIVERED`, `CANCELLED` | — |
| `Conversation` 🟪 | Messaging | `customer`, `expert`, `subject`, `messages` | — |
| `Message` 🟪 | Messaging | `sender`, `body`, `isRead`, `sentAt` | — |

### Key relationships
- `User` **owns** one `Profile` and one `Cart` (composition); has **many** `Order`s.
- `Category` has **many** `Product`s.
- `Cart` is **composed of** many `CartItem`s; each `CartItem` **references** one `Product`.
- `Order` is **composed of** many `OrderItem`s; each `OrderItem` **references** one `Product`.
- `Conversation` is **composed of** many `Message`s; both reference `User` (customer/expert and
  sender).

### Need-to-know details
- **`OrderItem.priceAtOrder` vs. `Product.price`** — the most important field on the diagram.
  `OrderItem` stores its **own** `priceAtOrder` (a snapshot taken at purchase time) rather than
  reading the live `Product.price`, so historical orders stay accurate even after a price
  change. This is the object-oriented expression of the same decision documented in the schema.
- **`CartItem` has no price field, but `OrderItem` does.** A cart is live (it always reflects the
  current product price), whereas an order is a permanent record (it freezes the price). The
  presence/absence of that one field captures the cart-vs-order lifecycle difference.
- **The two enums (`Role`, `OrderStatus`) match the database ENUMs exactly**, keeping the Java
  model and the schema in lockstep. `OrderStatus` is the subject of the next diagram.
- **Composition vs. association is intentional**: deleting a `Cart`/`Order` should take its items
  with it (composition → `ON DELETE CASCADE` in the schema), but must **not** delete the
  referenced `Product` (association → `ON DELETE RESTRICT`).

### Talking points / anticipated questions
- **"Why does `OrderItem` copy the price but `CartItem` doesn't?"** A cart should always show
  today's price; an order must remember what the customer actually paid. Snapshotting on the
  order — not the cart — is what makes that work.
- **"How does the class diagram relate to the database?"** Nearly 1:1 — each class corresponds to
  a table, composition lines correspond to cascading foreign keys, and associations correspond to
  restricted foreign keys. The class diagram is the runtime view; the ERD is the storage view.
- **"Why are `Role` and `OrderStatus` enums instead of plain strings?"** The set of valid values
  is small and fixed, so an enum enforces correctness in code and self-documents the options —
  mirroring the database ENUMs.

---

## 4. Order Status State Diagram

**File:** [`order_status_state_diagram.drawio`](order_status_state_diagram.drawio)

### What it shows
The **lifecycle of a single order** — every legal value of `orders.status` and the only
transitions allowed between them. It zooms in on one object (`Order`) and shows how it behaves
over time.

### How to read it
- The **filled black dot** is the **start** (an order is created).
- **Rounded boxes** are **states** (the order's current status).
- **Arrows** are **transitions**, labeled with the event that causes them.
- The **ringed dots** are **terminal states** — once reached, the order doesn't change again.

### The states and transitions
- **Happy path:** `PENDING` → `PAID` → `SHIPPED` → `DELIVERED`
  - *order placed* → **PENDING**
  - *payment confirmed* → **PAID**
  - *shipped* → **SHIPPED**
  - *delivered* → **DELIVERED** *(terminal)*
- **Cancellation off-ramp:** `PENDING` → `CANCELLED`, or `PAID` → `CANCELLED` *(with refund)*
  - **CANCELLED** is **terminal**.

| From | Event | To |
|------|-------|----|
| *(start)* | order placed | **PENDING** |
| PENDING | payment confirmed | **PAID** |
| PAID | shipped | **SHIPPED** |
| SHIPPED | delivered | **DELIVERED** (terminal) |
| PENDING | cancel | **CANCELLED** (terminal) |
| PAID | cancel / refund | **CANCELLED** (terminal) |

### Need-to-know details
- **These five states are exactly the `orders.status` ENUM** in the schema. The diagram is the
  visual rulebook for what that column is allowed to do.
- **Cancellation is only allowed early** — from `PENDING` or `PAID`. Once an order is `SHIPPED`,
  it can no longer be cancelled (it can only move forward to `DELIVERED`). This is a deliberate
  business rule, not an oversight.
- **`DELIVERED` and `CANCELLED` are dead ends.** There are no transitions out of them, which is
  what makes an order a permanent historical record once it's done.
- The diagram backs the `Order.updateStatus()` / `Order.cancel()` methods from the class
  diagram: those methods should **reject any transition not drawn here**.

### Talking points / anticipated questions
- **"Why can't a SHIPPED order be cancelled?"** Once goods are in transit, cancellation no longer
  makes sense — the appropriate path is delivery (and a separate return process, if added later).
- **"What enforces these rules?"** Two layers: the database ENUM restricts the column to these
  five values, and the application logic (`updateStatus`) restricts the *order* in which they can
  change.
- **"Why distinguish PENDING from PAID?"** It separates "order created but not yet paid" from
  "payment confirmed." Cancelling from `PAID` additionally implies a refund, which the label
  makes explicit.

---

## 5. Sequence Diagrams

**File:** [`sequence_diagrams.md`](sequence_diagrams.md)

### What they show
**Step-by-step runtime interactions** between the three tiers for the two most important flows.
Where the architecture diagram shows the tiers *statically*, these show them *collaborating over
time* to complete a real task.

### How to read them
- **Vertical lines** are participants (the actor's browser, the Java logic tier, the MySQL
  database). Time flows **downward**.
- **Arrows** are messages/calls between participants; dashed arrows are responses.
- **`alt` / `else` / `loop`** blocks show conditional branches and repetition.

### Flow A — Checkout / Place Order *(the riskiest flow)*
Turns a temporary cart into a permanent order. Its defining characteristic: **all database steps
run inside one transaction** — either everything succeeds, or everything rolls back.

**The sequence, in brief:**
1. Customer confirms checkout; the logic tier verifies they're logged in.
2. `BEGIN TRANSACTION`, then load the cart and its items.
3. If the cart is empty → error and `ROLLBACK`.
4. For each item, **check stock**; if any item is short → `ROLLBACK` with an out-of-stock error.
5. `INSERT` the order (`status = PENDING`), then for each item `INSERT` an `order_item`
   (capturing `price_at_order`) **and** decrement the product's `stock_quantity`.
6. Clear the cart, then `COMMIT`.
7. Return the order confirmation.

**Need-to-know details:**
- **`price_at_order` is captured here** — the moment that snapshot decision (from the schema and
  class diagram) actually happens.
- **Stock is checked *and* decremented inside the transaction**, preventing two shoppers from
  both buying the last item.
- **The cart is cleared only after a successful commit** — any failure rolls back and leaves the
  cart intact so the customer can retry.
- The new order starts in **`PENDING`**, handing off to the state diagram for what comes next.

### Flow B — Login / Authentication
How a returning user signs in and is routed to the right dashboard.

**The sequence, in brief:**
1. User submits username + password.
2. Logic tier looks up the user by username.
3. No match → generic "login failed."
4. Match → verify the submitted password against the stored `password_hash`.
5. Wrong password → generic "login failed."
6. Correct → create a session (storing user id + role), then redirect based on **role**
   (Customer / Expert / Admin dashboard).

**Need-to-know details:**
- **Passwords are never compared in plain text** — the app checks against the stored
  `password_hash`.
- **Failure messages are intentionally generic** so they don't reveal whether the username or the
  password was the wrong part (an account-enumeration safeguard).
- **One login flow serves all three roles** — the `role` on the `users` row decides the landing
  page. This is the runtime payoff of the single-`users`-table design seen in the use case and
  class diagrams.

### Talking points / anticipated questions
- **"Why wrap checkout in a transaction?"** So stock counts and orders can never drift out of
  sync. A partial checkout (order created but stock not decremented, or vice versa) would corrupt
  inventory — the transaction makes it all-or-nothing.
- **"What stops overselling the last item?"** The stock check and decrement happen together inside
  the transaction, so a second concurrent checkout sees the updated stock.
- **"Why are login errors vague?"** A specific message ("no such user" vs. "wrong password") would
  let an attacker discover which usernames exist. One generic message avoids that.
- **"Why are sequence diagrams text (Mermaid) instead of draw.io?"** They render natively on
  GitHub and are much easier to keep accurate as the code evolves than manually positioned boxes.

---

## How the models fit together

The five models tell one connected story — each answers a different question about the **same**
system, and design decisions echo across all of them:

- The **single `users` table with a role** appears as shared use cases (#1), a role-based routing
  box (#2), the `User`/`Role` classes (#3), and the role-based redirect at login (#5).
- The **price snapshot** appears as `OrderItem.priceAtOrder` (#3) and is *captured* during the
  checkout transaction (#5).
- The **order lifecycle** is named in the `Order` class and `OrderStatus` enum (#3), drawn in full
  in the state diagram (#4), and *initiated* (`PENDING`) at the end of checkout (#5).
- The **color-coded functional areas** (accounts/catalog/commerce/messaging) are consistent across
  every diagram and the ERD, so the audience can trace one concept through all of them.

## Related documentation

- [`../README.md`](../README.md) — project overview, architecture summary, timeline
- [`../database/DATABASE.md`](../database/DATABASE.md) — schema design, ERD, and full rationale
- [`../database/DEPLOYMENT.md`](../database/DEPLOYMENT.md) — Aiven MySQL hosting and connection setup
- [`../database/SEED_DATA.md`](../database/SEED_DATA.md) — sample data and test accounts
