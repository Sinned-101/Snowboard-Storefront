-- ============================================================================
-- Snowboard & Mountain Gear Storefront
-- MySQL seed data (sample data for development and demo)
--
-- Run AFTER database/schema.sql:
--     mysql -u root -p < database/schema.sql
--     mysql -u root -p < database/seed.sql
--
-- IMPORTANT: password_hash values below are PLACEHOLDER bcrypt-style strings
-- for development only. The Java application layer is responsible for hashing
-- real passwords at registration time. Never store plaintext passwords.
--
-- Explicit primary-key ids are used so foreign keys line up deterministically,
-- which keeps the demo data repeatable.
-- ============================================================================

USE snowboard_storefront;

-- ----------------------------------------------------------------------------
-- users  (1 admin, 2 experts, 3 customers)
-- Placeholder hash represents the password noted in the comment.
-- ----------------------------------------------------------------------------
INSERT INTO users (user_id, username, email, password_hash, role) VALUES
    (1, 'admin',     'admin@summitgear.test',  '$2b$12$DEVPLACEHOLDERhashADMIN0000000000000000000000000000', 'admin'),    -- pw: admin123
    (2, 'expert_kai','kai@summitgear.test',    '$2b$12$DEVPLACEHOLDERhashEXPERT100000000000000000000000000', 'expert'),   -- pw: expert123
    (3, 'expert_sam','sam@summitgear.test',    '$2b$12$DEVPLACEHOLDERhashEXPERT200000000000000000000000000', 'expert'),   -- pw: expert123
    (4, 'jordan',    'jordan@example.test',    '$2b$12$DEVPLACEHOLDERhashCUST10000000000000000000000000000', 'customer'), -- pw: pass123
    (5, 'mia',       'mia@example.test',       '$2b$12$DEVPLACEHOLDERhashCUST20000000000000000000000000000', 'customer'), -- pw: pass123
    (6, 'leo',       'leo@example.test',       '$2b$12$DEVPLACEHOLDERhashCUST30000000000000000000000000000', 'customer'); -- pw: pass123

-- ----------------------------------------------------------------------------
-- profile  (one per user)
-- ----------------------------------------------------------------------------
INSERT INTO profile
    (user_id, first_name, last_name, phone, address_line, city, state, postal_code, country, bio) VALUES
    (1, 'Avery', 'Admin',       '555-0100', '1 Operations Way', 'Denver',      'CO', '80202', 'USA', 'Site administrator.'),
    (2, 'Kai',   'Nakamura',    '555-0101', '22 Powder Ridge',  'Salt Lake City','UT','84101', 'USA', 'Backcountry rider and gear expert. 10+ years on snow.'),
    (3, 'Sam',   'Whitfield',   '555-0102', '88 Summit Ave',    'Burlington',  'VT', '05401', 'USA', 'Park and freestyle specialist. Loves dialing in setups.'),
    (4, 'Jordan','Reyes',       '555-0110', '14 Maple St',      'Boulder',     'CO', '80301', 'USA', NULL),
    (5, 'Mia',   'Chen',        '555-0111', '7 Lakeview Dr',    'Portland',    'OR', '97201', 'USA', NULL),
    (6, 'Leo',   'Petrov',      '555-0112', '301 Birch Ln',     'Seattle',     'WA', '98101', 'USA', NULL);

-- ----------------------------------------------------------------------------
-- category  (8 groupings)
-- ----------------------------------------------------------------------------
INSERT INTO category (category_id, name, description) VALUES
    (1, 'Snowboards',  'All-mountain, freestyle, and powder boards.'),
    (2, 'Boots',       'Snowboard boots for every flex preference.'),
    (3, 'Bindings',    'Bindings to connect boots to boards.'),
    (4, 'Helmets',     'Protective headgear for the mountain.'),
    (5, 'Goggles',     'Eye protection and lenses for all conditions.'),
    (6, 'Jackets',     'Insulated and shell jackets.'),
    (7, 'Gloves',      'Gloves and mittens for cold-weather riding.'),
    (8, 'Accessories', 'Tuning tools, bags, and small gear.');

-- ----------------------------------------------------------------------------
-- product  (~2 per category)
-- ----------------------------------------------------------------------------
INSERT INTO product (product_id, category_id, name, description, price, stock_quantity, image_url) VALUES
    -- Snowboards
    (1,  1, 'Summit All-Mountain 156',  'Versatile all-mountain board, 156cm.',          449.99, 20, NULL),
    (2,  1, 'Powder Hound 162W',        'Wide directional powder board, 162cm.',         529.99, 12, NULL),
    -- Boots
    (3,  2, 'Glacier Boa Boots',        'Medium-flex boots with Boa lacing.',            259.99, 30, NULL),
    (4,  2, 'Park Flex Boots',          'Soft-flex boots for freestyle riders.',         219.99, 25, NULL),
    -- Bindings
    (5,  3, 'Ridge Lock Bindings',      'All-mountain bindings, medium flex.',           189.99, 28, NULL),
    (6,  3, 'FreeFlex Pro Bindings',    'Responsive bindings for advanced riders.',      229.99, 18, NULL),
    -- Helmets
    (7,  4, 'Crest Snow Helmet',        'Lightweight helmet with adjustable vents.',      99.99, 40, NULL),
    (8,  4, 'Guard MIPS Helmet',        'Helmet with MIPS impact protection.',           139.99, 22, NULL),
    -- Goggles
    (9,  5, 'Vista Wide Goggles',       'Wide-view goggles with anti-fog lens.',          89.99, 35, NULL),
    (10, 5, 'Storm Low-Light Goggles',  'Low-light lens for overcast days.',              79.99, 27, NULL),
    -- Jackets
    (11, 6, 'Alpine Shell Jacket',      'Waterproof 3-layer shell jacket.',              199.99, 24, NULL),
    (12, 6, 'Insulated Summit Jacket',  'Warm insulated jacket for cold days.',          229.99, 16, NULL),
    -- Gloves
    (13, 7, 'Frostline Gloves',         'Insulated waterproof gloves.',                   59.99, 50, NULL),
    (14, 7, 'Summit Mittens',           'Extra-warm mittens for deep cold.',              64.99, 33, NULL),
    -- Accessories
    (15, 8, 'Edge Tuning Kit',          'All-in-one wax and edge tuning kit.',            39.99, 45, NULL),
    (16, 8, 'Padded Board Bag',         'Padded travel bag fits boards up to 165cm.',     89.99, 19, NULL);

-- ----------------------------------------------------------------------------
-- cart + cart_items
-- Jordan (user 4) has an active cart with two items.
-- ----------------------------------------------------------------------------
INSERT INTO cart (cart_id, user_id) VALUES
    (1, 4);

INSERT INTO cart_items (cart_item_id, cart_id, product_id, quantity) VALUES
    (1, 1, 9,  1),   -- Vista Wide Goggles
    (2, 1, 13, 2);   -- Frostline Gloves x2

-- ----------------------------------------------------------------------------
-- orders + order_items
-- Mia (user 5): a delivered order. Leo (user 6): a pending order.
-- price_at_order captures the unit price at purchase time.
-- total_amount matches the sum of (quantity * price_at_order).
-- ----------------------------------------------------------------------------
INSERT INTO orders (order_id, user_id, status, total_amount, order_date) VALUES
    (1, 5, 'delivered', 709.98, '2026-06-01 14:30:00'),   -- 449.99 (board) + 259.99 (boots) = 709.98
    (2, 6, 'pending',   139.99, '2026-06-14 09:15:00');

INSERT INTO order_items (order_item_id, order_id, product_id, quantity, price_at_order) VALUES
    -- Order 1 (Mia): one board + one pair of boots
    (1, 1, 1, 1, 449.99),   -- Summit All-Mountain 156
    (2, 1, 3, 1, 259.99),   -- Glacier Boa Boots
    -- Order 2 (Leo): one helmet
    (3, 2, 8, 1, 139.99);   -- Guard MIPS Helmet

-- ----------------------------------------------------------------------------
-- conversation + message
-- Jordan (customer 4) and Kai (expert 2) discuss board sizing.
-- A mix of read/unread messages demonstrates unread badges.
-- ----------------------------------------------------------------------------
INSERT INTO conversation (conversation_id, customer_id, expert_id, subject, created_at, updated_at) VALUES
    (1, 4, 2, 'Which board size for all-mountain?', '2026-06-10 17:00:00', '2026-06-10 17:12:00');

INSERT INTO message (message_id, conversation_id, sender_id, body, is_read, sent_at) VALUES
    (1, 1, 4, 'Hi! I''m 5''10" and ride mostly all-mountain. Is the Summit 156 a good fit?', TRUE,  '2026-06-10 17:00:00'),
    (2, 1, 2, 'Great question! The 156 works well for your height. If you like faster riding, the 162W gives more stability.', TRUE,  '2026-06-10 17:06:00'),
    (3, 1, 4, 'Got it. I mostly cruise groomers and dip into powder sometimes.', TRUE,  '2026-06-10 17:10:00'),
    (4, 1, 2, 'Then the 156 all-mountain is a solid pick. Pair it with the Ridge Lock bindings and you''re set.', FALSE, '2026-06-10 17:12:00');
