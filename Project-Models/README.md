# Project Planning & Models

Whole-project planning models for the Snowboard & Mountain Gear Storefront. Where the
[`database/`](../database/) folder models the **data tier** in depth, this folder models the
**overall system** — its actors, architecture, key flows, and domain objects.

## Diagrams in this folder

| Model | File | What it shows |
|-------|------|---------------|
| **Use Case Diagram** | [`use_case_diagram.drawio`](use_case_diagram.drawio) | The three actors (Customer, Expert, Admin) and what each can do — project scope on one page |
| **Architecture Diagram** | [`architecture_diagram.drawio`](architecture_diagram.drawio) | The three tiers (Browser → Java → MySQL) and how they connect, including Aiven hosting |
| **Class Diagram** | [`class_diagram.drawio`](class_diagram.drawio) | The Java domain objects in the logic tier and their relationships |
| **Order Status State Diagram** | [`order_status_state_diagram.drawio`](order_status_state_diagram.drawio) | The legal transitions of `orders.status` (PENDING → … → DELIVERED / CANCELLED) |
| **Sequence Diagrams** | [`sequence_diagrams.md`](sequence_diagrams.md) | Step-by-step flows for **Checkout** (riskiest flow) and **Login / authentication** |

## Why two formats?

- **draw.io (`.drawio`)** for the spatial diagrams (use case, architecture, class, state) —
  these are layout-driven and match the `.drawio` ERDs already in `database/`. Open them at
  [app.diagrams.net](https://app.diagrams.net) → *Open Existing Diagram*, and export to
  PNG/SVG for slides.
- **Mermaid in Markdown** for the sequence diagrams — these render natively on GitHub and are
  much easier to keep in sync with the code than hand-placed boxes.

## Conventions

The diagrams reuse the **color scheme from the ERD** so the whole project reads consistently:

- Accounts (blue) · Catalog (green) · Commerce (orange) · Messaging (purple)

## Related documentation

- [`../README.md`](../README.md) — project overview, architecture, timeline
- [`../database/DATABASE.md`](../database/DATABASE.md) — schema design & rationale
- [`../database/DEPLOYMENT.md`](../database/DEPLOYMENT.md) — Aiven MySQL hosting
