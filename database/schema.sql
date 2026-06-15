-- ============================================================================
-- Snowboard & Mountain Gear Storefront
-- MySQL schema (Milestone 1 - Dennis)
-- 10 tables: users, profile, product, category, cart, cart_items,
--            orders, order_items, conversation, message
--
-- Note: tables `users` and `orders` are named in the plural to avoid the
-- MySQL reserved words USER and ORDER, so no backtick escaping is needed.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS snowboard_storefront
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE snowboard_storefront;

-- Drop in reverse-dependency order so re-running the script is clean.
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS conversation;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS profile;
DROP TABLE IF EXISTS users;

-- ----------------------------------------------------------------------------
-- users: accounts and roles (customer, expert, admin)
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    user_id       INT UNSIGNED NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('customer', 'expert', 'admin') NOT NULL DEFAULT 'customer',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_users_username (username),
    UNIQUE KEY uq_users_email (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- profile: one-to-one extension of users (display info, contact details)
-- ----------------------------------------------------------------------------
CREATE TABLE profile (
    profile_id   INT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id      INT UNSIGNED NOT NULL,
    first_name   VARCHAR(100),
    last_name    VARCHAR(100),
    phone        VARCHAR(30),
    address_line VARCHAR(255),
    city         VARCHAR(100),
    state        VARCHAR(100),
    postal_code  VARCHAR(20),
    country      VARCHAR(100),
    bio          TEXT,
    PRIMARY KEY (profile_id),
    UNIQUE KEY uq_profile_user (user_id),
    CONSTRAINT fk_profile_user
        FOREIGN KEY (user_id) REFERENCES users (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- category: product groupings (boards, boots, bindings, etc.)
-- ----------------------------------------------------------------------------
CREATE TABLE category (
    category_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    PRIMARY KEY (category_id),
    UNIQUE KEY uq_category_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- product: items for sale, each belonging to a category
-- ----------------------------------------------------------------------------
CREATE TABLE product (
    product_id     INT UNSIGNED   NOT NULL AUTO_INCREMENT,
    category_id    INT UNSIGNED   NOT NULL,
    name           VARCHAR(200)   NOT NULL,
    description    TEXT,
    price          DECIMAL(10, 2) NOT NULL,
    stock_quantity INT UNSIGNED   NOT NULL DEFAULT 0,
    image_url      VARCHAR(500),
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id),
    KEY idx_product_category (category_id),
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES category (category_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_product_price CHECK (price >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- cart: one active cart per customer
-- ----------------------------------------------------------------------------
CREATE TABLE cart (
    cart_id    INT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id    INT UNSIGNED NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (cart_id),
    UNIQUE KEY uq_cart_user (user_id),
    CONSTRAINT fk_cart_user
        FOREIGN KEY (user_id) REFERENCES users (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- cart_items: products (and quantities) inside a cart
-- ----------------------------------------------------------------------------
CREATE TABLE cart_items (
    cart_item_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    cart_id      INT UNSIGNED NOT NULL,
    product_id   INT UNSIGNED NOT NULL,
    quantity     INT UNSIGNED NOT NULL DEFAULT 1,
    PRIMARY KEY (cart_item_id),
    UNIQUE KEY uq_cart_product (cart_id, product_id),
    KEY idx_cart_items_product (product_id),
    CONSTRAINT fk_cart_items_cart
        FOREIGN KEY (cart_id) REFERENCES cart (cart_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cart_items_product
        FOREIGN KEY (product_id) REFERENCES product (product_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_cart_items_quantity CHECK (quantity > 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- orders: a placed order belonging to a customer
-- ----------------------------------------------------------------------------
CREATE TABLE orders (
    order_id     INT UNSIGNED   NOT NULL AUTO_INCREMENT,
    user_id      INT UNSIGNED   NOT NULL,
    status       ENUM('pending', 'paid', 'shipped', 'delivered', 'cancelled')
                                NOT NULL DEFAULT 'pending',
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    order_date   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id),
    KEY idx_orders_user (user_id),
    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id) REFERENCES users (user_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- order_items: products (quantities + price snapshot) inside an order
-- ----------------------------------------------------------------------------
CREATE TABLE order_items (
    order_item_id   INT UNSIGNED   NOT NULL AUTO_INCREMENT,
    order_id        INT UNSIGNED   NOT NULL,
    product_id      INT UNSIGNED   NOT NULL,
    quantity        INT UNSIGNED   NOT NULL DEFAULT 1,
    price_at_order  DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (order_item_id),
    UNIQUE KEY uq_order_product (order_id, product_id),
    KEY idx_order_items_product (product_id),
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id) REFERENCES orders (order_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id) REFERENCES product (product_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_order_items_quantity CHECK (quantity > 0),
    CONSTRAINT chk_order_items_price CHECK (price_at_order >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- conversation: a messaging thread between a customer and an expert.
-- Messages are grouped under a conversation so each dashboard can list
-- threads instead of loose individual messages.
-- ----------------------------------------------------------------------------
CREATE TABLE conversation (
    conversation_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    customer_id     INT UNSIGNED NOT NULL,
    expert_id       INT UNSIGNED NOT NULL,
    subject         VARCHAR(200),
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (conversation_id),
    UNIQUE KEY uq_conversation_pair (customer_id, expert_id),
    KEY idx_conversation_customer (customer_id),
    KEY idx_conversation_expert (expert_id),
    CONSTRAINT fk_conversation_customer
        FOREIGN KEY (customer_id) REFERENCES users (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_conversation_expert
        FOREIGN KEY (expert_id) REFERENCES users (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_conversation_distinct CHECK (customer_id <> expert_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- message: an individual message belonging to a conversation thread.
-- sender_id records which participant wrote the message.
-- ----------------------------------------------------------------------------
CREATE TABLE message (
    message_id      INT UNSIGNED NOT NULL AUTO_INCREMENT,
    conversation_id INT UNSIGNED NOT NULL,
    sender_id       INT UNSIGNED NOT NULL,
    body            TEXT         NOT NULL,
    is_read         BOOLEAN      NOT NULL DEFAULT FALSE,
    sent_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (message_id),
    KEY idx_message_conversation (conversation_id),
    KEY idx_message_sender (sender_id),
    CONSTRAINT fk_message_conversation
        FOREIGN KEY (conversation_id) REFERENCES conversation (conversation_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_message_sender
        FOREIGN KEY (sender_id) REFERENCES users (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
