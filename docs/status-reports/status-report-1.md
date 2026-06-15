# Status Report 1

**Course:** CSC 450 100 - Computer Science Capstone  
**Instructor:** James Gappy  
**Student:** Dennis Feldbruegge  
**Team Member:** Zachary Christianson  
**Submission Date:** 6/7/2026

---

## Group Project Description

For our capstone, my teammate Zachary and I want to build a web-based storefront that sells
snowboarding and mountain gear. The idea is to give customers one place where they can browse
gear, compare their options, and buy what they need without having to bounce around between
different sites. We plan to stock the usual snowboarding setup: boards, boots, bindings,
helmets, goggles, jackets, gloves, and other cold-weather gear.

The part that makes our store a little different is a built-in messaging feature that lets
shoppers ask a gear expert for advice before they commit to buying. We're planning three kinds
of users. Customers browse, buy, and can message experts. Experts answer those questions and
point people toward gear that fits. Administrators keep the catalog and the user accounts in
order. When someone logs in, they land on a dashboard that's built for their role.

## How the Project Meets the Project Requirements

- **Web application:** It runs in the browser. The front end is plain HTML, CSS, and JavaScript,
  and the server-side logic is written in Java.
- **User authentication and user management:** You have to register and log in before you can do
  anything that matters, like placing an order or messaging an expert. We track three roles
  (customer, expert, admin) and show or hide features depending on which one you are.
- **Internal messaging:** Customers and experts talk through a messaging feature that's built
  into the site, so nothing has to go through outside email.
- **Profiles and dashboards:** Every account has a profile and a dashboard. Customers see their
  orders, messages, and cart. Experts see their conversations with customers. Admins get the
  tools for managing products, categories, users, and orders.
- **Transactions between users:** Customers add gear to a cart and check out. We store each order
  with the customer, the items and quantities, the status, and the date.
- **Database with at least five tables:** We ended up with nine: `user`, `profile`, `product`,
  `category`, `cart`, `cart_items`, `order`, `order_items`, and `message`.

## Proposed Software Architecture / Solution

I'm going with a three-tier setup, mostly because it keeps the moving parts separate and easier
to work on one at a time.

1. **Presentation:** the pages people actually see and click through (home, register, login,
   product listing, product details, cart, checkout, the three dashboards, and messages),
   written in HTML, CSS, and JavaScript.
2. **Logic:** the Java code behind the scenes that handles logging in, sending users to the
   right place, processing orders, and moving messages around.
3. **Data:** a MySQL database that holds everything in the nine tables listed above.

Splitting it up this way means we can change how a page looks without touching the database, or
rework a query without breaking the front end. That should make testing a lot less painful as
the project grows.

## Project Manager and Team Responsibilities for the First Milestone

There are only two of us, so we didn't bother naming a formal project manager. We just split the
planning between us and check in with each other as we go. For the first milestone we divided
the work like this:

- **Me (Dennis):** build the MySQL database, which means all nine tables, the keys, and the
  relationships between them.
- **Zachary:** put together the page templates and the general layout for the main screens
  (home, register, login, the product pages, cart, checkout, the dashboards, and messages).

## What We Have Accomplished Since Our Last Meeting

Since we started talking about this, we've:

- agreed on the idea (a snowboarding and mountain gear store),
- settled on the tech stack (MySQL, Java, and HTML/CSS/JavaScript),
- sketched out the nine database tables and how they connect, and
- set up a shared GitHub repo to work out of.

## What Will Be Established by Our Next Meeting

By our next check-in we should have the first milestone wrapped up:

- the MySQL database created with all nine tables connected,
- a working set of page templates for the main screens, and
- agreement on naming and folder structure so the next phase (accounts and login) starts clean.

## Details of My Individual Contributions for the First Milestone

The database is my piece of the first milestone. Specifically, I'm going to:

- write out the schema for all nine tables,
- set the primary and foreign keys and wire up the relationships (orders to customers, order
  items to both orders and products, and so on), and
- create the database in MySQL and test it so it's ready for the Java side to connect to next.

## Team Developer Preferences

Zachary and I are on the same page here:

- **Database:** MySQL
- **Server-side:** Java
- **Front end:** HTML, CSS, and JavaScript
- **Staying in touch:** email and GitHub

I pushed for MySQL mainly because it's what I'm most comfortable with and it's free to run
locally while we develop.

## Tools the Group Plans to Use

- **MySQL** for the database
- **Java** for the application logic
- **HTML, CSS, and JavaScript** for the pages
- **GitHub** for version control and keeping our work in sync

## Rough Project Timeline

Here's roughly how we're pacing things out over the summer:

| Dates | Planned Work |
|-------|--------------|
| June 8 - June 21 | Create the database tables and the basic page templates. |
| June 22 - July 5 | Build the user account features: registration, login, logout, role-based access, and dashboard navigation for customers, experts, and administrators. |
| July 6 - July 19 | Build the main storefront pages: categories, product lists, product details, sample products, and admin product controls. |
| July 20 - August 2 | Build the shopping cart and order features so customers can place orders, view past orders, and see order details, and administrators can review submitted orders. |
| August 3 - August 26 | Build the messaging system between customers and experts. Test the main features, fix what's broken, add sample data, finalize everything, and record the demo video. |

## Other Relevant Details

Each milestone builds on the one before it: database first, then accounts, then the storefront,
then the cart and orders, and finally the messaging. Doing it in that order lets us get one
layer working and tested before piling the next one on top, instead of trying to wire everything
together at the very end.
