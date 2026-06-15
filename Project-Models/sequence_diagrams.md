# Sequence Diagrams

Step-by-step interaction diagrams for the project's two most important flows. These are
written in **Mermaid** so they render automatically on GitHub and are easy to keep in sync
with the code as it evolves.

> The visual, spatial diagrams (use case, architecture, class, state) live as `.drawio`
> files in this same folder. Sequence diagrams are kept as text because they're far easier
> to maintain that way and GitHub renders them natively.

---

## 1. Checkout / Place Order (the riskiest flow)

This is the most complex interaction in the system: it must turn a temporary cart into a
permanent order **atomically**. Either every step below succeeds, or none of them do —
otherwise stock and orders could drift out of sync. That's why the database steps run inside
a single **transaction**.

```mermaid
sequenceDiagram
    autonumber
    actor C as Customer (Browser)
    participant L as Java Logic Tier
    participant DB as MySQL (Aiven)

    C->>L: POST /checkout (confirm order)
    L->>L: Verify user is logged in (session)

    Note over L,DB: All DB steps run in ONE transaction
    L->>DB: BEGIN TRANSACTION
    L->>DB: SELECT cart + cart_items for user
    DB-->>L: Cart contents

    alt Cart is empty
        L-->>C: Error: cart is empty
        L->>DB: ROLLBACK
    else Cart has items
        loop For each cart item
            L->>DB: Check product stock_quantity
            alt Not enough stock
                L->>DB: ROLLBACK
                L-->>C: Error: "<product>" out of stock
            end
        end

        L->>DB: INSERT INTO orders (user, status=PENDING, total)
        DB-->>L: new order_id

        loop For each cart item
            L->>DB: INSERT INTO order_items (order_id, product, qty, price_at_order)
            L->>DB: UPDATE product SET stock_quantity = stock_quantity - qty
        end

        L->>DB: DELETE cart_items for this cart
        L->>DB: COMMIT
        DB-->>L: OK
        L-->>C: Order confirmation (order #, summary)
    end
```

**Key points to call out:**
- **`price_at_order` is captured here** — the order stores the price at purchase time, so it
  stays accurate even if the product's price changes later.
- **Stock is checked *and* decremented inside the transaction** — preventing two shoppers from
  both buying the last item.
- **The cart is cleared only after a successful commit** — if anything fails, the rollback
  leaves the cart untouched so the customer can try again.
- The new order starts in **`PENDING`** (see the Order Status state diagram for what's next).

---

## 2. Login / Authentication

How a returning user signs in and is routed to the dashboard for their role.

```mermaid
sequenceDiagram
    autonumber
    actor U as User (Browser)
    participant L as Java Logic Tier
    participant DB as MySQL (Aiven)

    U->>L: POST /login (username, password)
    L->>DB: SELECT user WHERE username = ?
    DB-->>L: user row (or none)

    alt No matching user
        L-->>U: Login failed (generic message)
    else User found
        L->>L: Verify password against stored password_hash
        alt Password incorrect
            L-->>U: Login failed (generic message)
        else Password correct
            L->>L: Create session, store user id + role
            L->>L: Determine landing page from role
            alt role = CUSTOMER
                L-->>U: Redirect to Customer dashboard
            else role = EXPERT
                L-->>U: Redirect to Expert dashboard
            else role = ADMIN
                L-->>U: Redirect to Admin dashboard
            end
        end
    end
```

**Key points to call out:**
- **Passwords are never compared in plain text** — the app verifies the submitted password
  against the stored `password_hash`.
- **Failure messages are intentionally generic** ("login failed") so they don't reveal
  whether the username or the password was the wrong part.
- **One login flow serves all three roles** — the `role` on the `users` row decides which
  dashboard the user lands on, which is exactly why we used a single accounts table.
