# Database Presentation

A slide-by-slide script for presenting the **database design** of the Snowboard & Mountain
Gear Storefront. Each numbered section below is built to become **one slide**: a title,
a few bullets for the slide body, and a **"Say this"** block of talking points to speak from.

**Course:** CSC 450 — Computer Science Capstone · **Team:** Dennis Feldbruegge, Zachary Christianson

> **Turning this into PowerPoint:** Each `##` section = one slide. The **bullets** go on the
> slide; the **"Say this"** text goes in the slide's *speaker notes*. The two ERD images
> (exported from the `.drawio` files) are the visuals for Slides 3–5.
>
> **Need more detail?** [`DATABASE.md`](DATABASE.md) ·
> [`ERD_WALKTHROUGH.md`](ERD_WALKTHROUGH.md) · [`DEPLOYMENT.md`](DEPLOYMENT.md) ·
> [README](../README.md)

---

## Slide 1 — Title

**The Database Behind the Storefront**
A MySQL design for accounts, catalog, shopping, and expert messaging.

- Snowboard & Mountain Gear Storefront
- CSC 450 Capstone — Dennis Feldbruegge & Zachary Christianson

> **Say this:** *"Our project is an online store for snowboarding and mountain gear, with a
> twist: shoppers can message a gear expert for advice before they buy. I'm going to walk you
> through the database we designed to make all of that work."*

---

## Slide 2 — Where the Database Fits

**One of three tiers**

- **Presentation** — the web pages (HTML/CSS/JavaScript)
- **Logic** — the Java that runs the app
- **Data** — our **MySQL database** ← *this presentation*

> **Say this:** *"The app is built in three layers — the pages people see, the Java logic in
> the middle, and the data layer at the bottom. The database is that foundation: it stores
> everything and enforces the rules about how the data can be used. If the database is wrong,
> nothing above it can be right — that's why we put real thought into the design."*

---

## Slide 3 — The Big Picture (Overview Diagram)

**10 tables, 4 areas** *(show the simple ERD here)*

- 🔵 **Accounts** — who you are
- 🟢 **Catalog** — what we sell
- 🟠 **Commerce** — what you buy
- 🟣 **Messaging** — how you talk to an expert

> **Say this:** *"Here's the whole database at a glance — ten tables, grouped into four areas.
> The colors aren't decoration: blue is accounts, green is the product catalog, orange is the
> shopping and ordering flow, and purple is the messaging between customers and experts. If
> you remember nothing else, remember these four groups — everything else hangs off them."*

---

## Slide 4 — Following a Purchase (Overview Diagram)

**From browsing to a placed order** *(keep the simple ERD up)*

- A customer has **one cart** → the cart holds **many items**
- Each item points to a **product**, and products sit in **categories**
- Checking out creates an **order**, which keeps its **own line items**

> **Say this:** *"Let's follow a real customer. They have one shopping cart. That cart can
> hold many items, and each item is tied to a product — and every product belongs to a
> category like boards or boots. When they check out, we create an order, and that order keeps
> its own copy of the items. I'll explain in a minute why the order gets its own copy — it's
> one of our most important design decisions."*

---

## Slide 5 — Inside the Tables (Detailed Diagram)

**The same design, with the details filled in** *(show the detailed ERD here)*

- Every table has a unique **ID** (primary key)
- **Relationships** connect them (e.g., an order belongs to a user)
- A few **rules** are built right into the database to keep data clean

> **Say this:** *"This is the same ten tables, but now you can see what's inside each one.
> Every table has a unique ID, lines show how they connect, and we've built certain rules
> directly into the database. I won't read every field — instead I want to highlight the
> handful of design choices that I think are the most interesting."*

---

## Slide 6 — Smart Choice #1: One Accounts Table for Everyone

**Customers, experts, and admins live in one `users` table**

- A single **role** field marks each person as customer, expert, or admin
- That role decides which dashboard and features they get

> **Say this:** *"We have three kinds of users — customers, experts, and admins — but instead
> of three separate tables, we use one accounts table with a 'role' label. Everyone logs in
> the same way, and the role simply decides what they can see and do. It keeps the design
> simple and means we never duplicate login logic three times."*

---

## Slide 7 — Smart Choice #2: Money Done Right

**Prices and totals are stored as exact decimals**

- We never use floating-point numbers for money
- A product's price is **copied onto the order** at checkout

> **Say this:** *"Money is one place you can't be sloppy. We store prices as exact decimal
> values so we never get those weird rounding errors. And here's the key part from earlier:
> when someone places an order, we copy the price into the order itself. So if we put a board
> on sale next week, last week's receipts still show what the customer actually paid. The
> order is a permanent record of history, not a live link to today's price."*

---

## Slide 8 — Smart Choice #3: Carts vs. Orders

**A cart is temporary; an order is permanent**

- The **cart** can change freely — add, remove, change quantities
- The **order** is locked-in history once checkout happens
- Deleting a customer with orders is **blocked**, so history is never lost

> **Say this:** *"We deliberately kept carts and orders as separate things. A cart is a
> work-in-progress — you change it constantly. An order is a finished, permanent record. We
> even prevent deleting a customer who has past orders, so our sales history can never be
> silently erased. Keeping these two lifecycles apart keeps the data honest."*

---

## Slide 9 — Smart Choice #4: Rules Built Into the Database

**The database enforces good behavior automatically**

- No duplicate items in the same cart or order (quantity goes up instead)
- One cart and one profile per person
- Only one conversation thread per customer–expert pair

> **Say this:** *"Rather than trusting the app to always behave, we built guardrails into the
> database itself. You can't accidentally add the same product twice to a cart — it just
> bumps the quantity. Each person has exactly one cart and one profile. And a customer and an
> expert can only have one conversation thread between them. These rules protect the data no
> matter what the rest of the app does."*

---

## Slide 10 — Smart Choice #5: Messaging as Threads

**Conversations group messages, like an inbox**

- A **conversation** links one customer and one expert
- Each conversation holds **many messages**
- We can sort by most recent activity and show unread badges

> **Say this:** *"For the expert-advice feature, we didn't just dump messages in one big pile.
> We group them into conversations — think of it like an email thread between a customer and
> an expert. That lets each dashboard show clean threads, sort them by what's most recent, and
> mark messages as read or unread. It's a small structural choice that makes the whole feature
> feel polished."*

---

## Slide 11 — Smart Choice #6: Built on Solid Foundations (Normalization & Speed)

**Organized to 3NF, and indexed to stay fast**

- **Normalized** so every fact lives in exactly one place — no duplicated data to drift
- One intentional exception: we *snapshot* the price onto orders (history, by design)
- **Indexed** the columns we search and join on most, so it stays fast as data grows

> **Say this:** *"Two foundational choices tie everything together. First, the design is
> normalized — to Third Normal Form, if you want the textbook term — which just means every
> piece of information is stored in exactly one place. A product's category name lives only in
> the category table, so it can never get out of sync. The one exception is that price snapshot
> on orders, and that's on purpose, for history. Second, we added indexes on the columns we
> search and join on most often — like looking up a customer's orders or loading a
> conversation's messages — so the app stays fast even as the data grows. Together, these keep
> the data both correct and quick."*

---

## Slide 12 — Sample Data, Ready to Demo

**The database comes pre-loaded with realistic test data**

- Example products, categories, carts, orders, and conversations
- **Test accounts** for each role (customer, expert, admin)

> **Say this:** *"To make demos and testing easy, the database ships with realistic sample
> data already loaded — products, orders, conversations, and a ready-made test account for
> each role. So anyone on the team can spin it up and immediately see the app working with
> real-looking data instead of an empty screen."*

---

## Slide 13 — It's Live in the Cloud

**Hosted on Aiven's free managed MySQL**

- Accessible online, not just on one laptop
- Runs standard MySQL, so every rule we designed works in the cloud too
- Free for a student project, and credentials are kept secure

> **Say this:** *"This isn't just a design on paper or a database on one person's laptop —
> it's hosted online with Aiven's free managed MySQL. That means the whole team connects to
> the same live database, all of our built-in rules work exactly the same in the cloud, and we
> keep our passwords out of the code for security. It's free, which is perfect for a capstone."*

---

## Slide 14 — Recap

**Why this design is solid**

- 🔵🟢🟠🟣 Ten tables, four clear areas
- 💵 Money stored exactly, with prices snapshotted on orders
- 🛒 Carts and orders kept separate — history is protected
- 🛡️ Rules built into the database to prevent bad data
- 🧱 Normalized to 3NF and indexed for speed
- ☁️ Live in the cloud on Aiven MySQL

> **Say this:** *"To sum up: we organized everything into four clear areas, we handle money
> carefully, we protect order history, we built guardrails directly into the database, we
> normalized the design and indexed it for speed, and we deployed it live in the cloud. Every
> one of these was a deliberate choice to make the store reliable and trustworthy. Happy to
> take any questions."*

---

## Slide 15 — Q&A (Backup Slide)

Keep these answers in your back pocket:

- **Why not keep the cart only in the browser?**
  A database cart survives logout and follows you across devices.
- **What stops duplicate items in a cart?**
  The database blocks it — it bumps the quantity instead.
- **What if a customer is deleted — do we lose their orders?**
  No. We block deleting a customer who has order history.
- **Why copy the price onto the order?**
  So old receipts stay accurate even after prices change.
- **Why does everything connect back to the users table?**
  Because customers, experts, and admins are all just users with different roles.
- **Is the database normalized?**
  Yes — to Third Normal Form. The one duplicated value (price on an order) is a deliberate
  historical snapshot, not a normalization slip.
- **Where is it hosted?**
  Aiven's free managed MySQL — the same standard MySQL, running in the cloud.

---

### Presenter Tips

- **Time:** ~6 minutes for Slides 1–14, leaving room for Q&A.
- **Lead with the picture:** put the Overview diagram up early (Slide 3) and don't rush it.
- **Don't read fields:** on the detailed diagram, talk about *choices*, not every column.
- **Repeat the four colors** — it's the mental model the audience will remember.
