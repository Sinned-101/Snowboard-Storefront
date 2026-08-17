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
-- users  (1 admin, 2 experts, 6 customers)
-- Placeholder hash represents the password noted in the comment.
-- ----------------------------------------------------------------------------
INSERT INTO users (user_id, username, email, password_hash, role) VALUES
    (1, 'admin',     'admin@summitgear.test',  '$2a$10$zNtfqcd7O2IFFgEm6HJcDeXsWuFP7tSRFW5DNllgdYdfU/OZRbbNW', 'admin'),    -- pw: admin123
    (2, 'expert_kai','kai@summitgear.test',    '$2a$10$f1uvU11ARFuV/gJk9otcc.1YHhre/n/U03EurCuecNnEsRdeaSnVK', 'expert'),   -- pw: expert123
    (3, 'expert_sam','sam@summitgear.test',    '$2a$10$f1uvU11ARFuV/gJk9otcc.1YHhre/n/U03EurCuecNnEsRdeaSnVK', 'expert'),   -- pw: expert123
    (4, 'jordan',    'jordan@example.test',    '$2a$10$4MGIBnQ75dqY0txZlxatfuch8YcLL9gzJPjdIlpYI24uo8cS3a/h6', 'customer'), -- pw: pass123 | mixed orders
    (5, 'mia',       'mia@example.test',       '$2a$10$4MGIBnQ75dqY0txZlxatfuch8YcLL9gzJPjdIlpYI24uo8cS3a/h6', 'customer'), -- pw: pass123 | delivery only
    (6, 'leo',       'leo@example.test',       '$2a$10$4MGIBnQ75dqY0txZlxatfuch8YcLL9gzJPjdIlpYI24uo8cS3a/h6', 'customer'), -- pw: pass123 | delivery only
    (7, 'taylor',    'taylor@example.test',    '$2a$10$4MGIBnQ75dqY0txZlxatfuch8YcLL9gzJPjdIlpYI24uo8cS3a/h6', 'customer'), -- pw: pass123 | in-store only
    (8, 'riley',     'riley@example.test',     '$2a$10$4MGIBnQ75dqY0txZlxatfuch8YcLL9gzJPjdIlpYI24uo8cS3a/h6', 'customer'), -- pw: pass123 | delivery only
    (9, 'alex',      'alex@example.test',      '$2a$10$4MGIBnQ75dqY0txZlxatfuch8YcLL9gzJPjdIlpYI24uo8cS3a/h6', 'customer'); -- pw: pass123 | mixed orders

-- ----------------------------------------------------------------------------
-- profile  (one per user)
-- ----------------------------------------------------------------------------
INSERT INTO profile
    (user_id, first_name, last_name, phone, address_line, city, state, postal_code, country, bio) VALUES
    (1, 'Admin',  'Admin',       '555-0100', '1 Operations Way', 'Denver',         'CO', '80202', 'USA', 'Site administrator.'),
    (2, 'Kai',   'Nakamura',    '555-0101', '22 Powder Ridge',  'Salt Lake City', 'UT', '84101', 'USA', 'Backcountry rider and gear expert. 10+ years on snow.'),
    (3, 'Sam',   'Whitfield',   '555-0102', '88 Summit Ave',    'Burlington',     'VT', '05401', 'USA', 'Park and freestyle specialist. Loves dialing in setups.'),
    (4, 'Jordan','Reyes',       '555-0110', '14 Maple St',      'Boulder',        'CO', '80301', 'USA', NULL),
    (5, 'Mia',   'Chen',        '555-0111', '7 Lakeview Dr',    'Portland',       'OR', '97201', 'USA', NULL),
    (6, 'Leo',   'Petrov',      '555-0112', '301 Birch Ln',     'Seattle',        'WA', '98101', 'USA', NULL),
    -- Taylor shops in-store only - no delivery address needed
    (7, 'Taylor','Brooks',      '555-0113', NULL,               NULL,             NULL, NULL,    'USA', NULL),
    -- Riley and Alex both have delivery orders so addresses are required
    (8, 'Riley', 'Torres',      '555-0114', '22 Pine Ave',      'Denver',         'CO', '80203', 'USA', NULL),
    (9, 'Alex',  'Durant',      '555-0115', '55 Oak St',        'Breckenridge',   'CO', '80424', 'USA', NULL);

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
    (1,  1, 'Summit All-Mountain 156',  'Versatile all-mountain board, 156cm.',          449.99, 20, '/images/summit-all-mountain-156.jpg' ),
    (2,  1, 'Powder Hound 162W',        'Wide directional powder board, 162cm.',         529.99, 12, '/images/powder-hound-162w.jpg'),
    -- Boots
    (3,  2, 'Glacier Boa Boots',        'Medium-flex boots with Boa lacing.',            259.99, 30, '/images/glacier-boa-boots.jpg'),
    (4,  2, 'Park Flex Boots',          'Soft-flex boots for freestyle riders.',         219.99, 25, '/images/park-flex-boots.jpg'),
    -- Bindings
    (5,  3, 'Ridge Lock Bindings',      'All-mountain bindings, medium flex.',           189.99, 28, '/images/ridge-lock-bindings.jpg'),
    (6,  3, 'FreeFlex Pro Bindings',    'Responsive bindings for advanced riders.',      229.99, 18, '/images/free-flex-pro-bindings.jpg'),
    -- Helmets
    (7,  4, 'Crest Snow Helmet',        'Lightweight helmet with adjustable vents.',      99.99, 40, '/images/crest-snow-helmet.jpg'),
    (8,  4, 'Guard MIPS Helmet',        'Helmet with MIPS impact protection.',           139.99, 22, '/images/guard-mips-helmet.jpg'),
    -- Goggles
    (9,  5, 'Vista Wide Goggles',       'Wide-view goggles with anti-fog lens.',          89.99, 35, '/images/vista-wide-goggles.jpg'),
    (10, 5, 'Storm Low-Light Goggles',  'Low-light lens for overcast days.',              79.99, 27, '/images/storm-low-light-goggles.jpg'),
    -- Jackets
    (11, 6, 'Alpine Shell Jacket',      'Waterproof 3-layer shell jacket.',              199.99, 24, '/images/alpine-shell-jacket.jpg'),
    (12, 6, 'Insulated Summit Jacket',  'Warm insulated jacket for cold days.',          229.99, 16, '/images/insulated-summit-jacket.jpg'),
    -- Gloves
    (13, 7, 'Frostline Gloves',         'Insulated waterproof gloves.',                   59.99, 50, '/images/frostline-gloves.jpg'),
    (14, 7, 'Summit Mittens',           'Extra-warm mittens for deep cold.',              64.99, 33, '/images/summit-mittens.jpg'),
    -- Accessories
    (15, 8, 'Edge Tuning Kit',          'All-in-one wax and edge tuning kit.',            39.99, 45, '/images/edge-tuning-kit.png'),
    (16, 8, 'Padded Board Bag',         'Padded travel bag fits boards up to 165cm.',     89.99, 19, '/images/padded-board-bag.png');

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
-- Demonstrates all channel/status combinations across 6 customers.
-- channel: 'delivery' or 'in_store'
-- In-store orders complete immediately (status='completed') with no address.
-- Delivery orders start pending and require a shipping_address.
-- shipped orders have a tracking_number and shipped_date.
-- ----------------------------------------------------------------------------
INSERT INTO orders
    (order_id, user_id, status, total_amount, order_date,
     channel, shipping_address, tracking_number, shipped_date, picked_up) VALUES

    -- Mia (5): delivery only - one order shipped
    (1,  5, 'shipped', 709.98, '2026-06-01 14:30:00',
     'delivery', '7 Lakeview Dr, Portland, OR 97201', '1Z999AA10123456784', '2026-06-03 10:00:00', NULL),

    -- Leo (6): delivery only - one order still pending
    (2,  6, 'pending',   139.99, '2026-06-14 09:15:00',
     'delivery', '301 Birch Ln, Seattle, WA 98101', NULL, NULL, NULL),

    -- Jordan (4): in-store purchase - completed immediately at point of sale
    (3,  4, 'completed',  89.99, '2026-07-02 11:00:00',
     'in_store', NULL, NULL, NULL, NULL),

    -- Jordan (4): delivery order - pending, can still be edited
    (4,  4, 'pending',   264.98, '2026-07-15 16:45:00',
     'delivery', '14 Maple St, Boulder, CO 80301', NULL, NULL, NULL),

    -- Taylor (7): in-store only, first purchase - helmet
    (5,  7, 'completed',  99.99, '2026-06-20 10:30:00',
     'in_store', NULL, NULL, NULL, NULL),

    -- Taylor (7): in-store only, second purchase - gloves
    (6,  7, 'completed',  59.99, '2026-07-08 14:00:00',
     'in_store', NULL, NULL, NULL, NULL),

    -- Riley (8): delivery only - one order shipped with tracking
    (7,  8, 'shipped',   449.99, '2026-07-05 09:00:00',
     'delivery', '22 Pine Ave, Denver, CO 80203', '1Z999AA10123456789', '2026-07-08 08:30:00', NULL),

    -- Riley (8): delivery only - second order still pending
    (8,  8, 'pending',   409.98, '2026-07-18 13:20:00',
     'delivery', '22 Pine Ave, Denver, CO 80203', NULL, NULL, NULL),

    -- Alex (9): mixed - in-store purchase completed instantly
    (9,  9, 'completed',  39.99, '2026-07-10 12:00:00',
     'in_store', NULL, NULL, NULL, NULL),

    -- Alex (9): mixed - delivery order shipped
    (10, 9, 'shipped', 229.99, '2026-07-12 10:00:00',
     'delivery', '55 Oak St, Breckenridge, CO 80424', '1Z999AA10123456790', '2026-07-14 11:00:00', NULL),

    -- Jordan (4): in-store pickup - pending, not yet collected (picked_up = 0)
    (11, 4, 'pending',   189.99, '2026-08-01 10:00:00',
     'in_store_pickup', NULL, NULL, NULL, 0),

    -- Mia (5): in-store pickup - already collected (picked_up = 1, status = completed)
    (12, 5, 'completed',  64.99, '2026-07-28 15:30:00',
     'in_store_pickup', NULL, NULL, NULL, 1);

INSERT INTO order_items (order_item_id, order_id, product_id, quantity, price_at_order) VALUES
    -- Order 1 (Mia, shipped delivery): board + boots
    (1,  1, 1,  1, 449.99),   -- Summit All-Mountain 156
    (2,  1, 3,  1, 259.99),   -- Glacier Boa Boots
    -- Order 2 (Leo, pending delivery): helmet
    (3,  2, 8,  1, 139.99),   -- Guard MIPS Helmet
    -- Order 3 (Jordan, in-store): goggles
    (4,  3, 9,  1,  89.99),   -- Vista Wide Goggles
    -- Order 4 (Jordan, pending delivery): jacket + mittens
    (5,  4, 11, 1, 199.99),   -- Alpine Shell Jacket
    (6,  4, 14, 1,  64.99),   -- Summit Mittens
    -- Order 5 (Taylor, in-store): helmet
    (7,  5, 7,  1,  99.99),   -- Crest Snow Helmet
    -- Order 6 (Taylor, in-store): gloves
    (8,  6, 13, 1,  59.99),   -- Frostline Gloves
    -- Order 7 (Riley, shipped delivery): snowboard
    (9,  7, 1,  1, 449.99),   -- Summit All-Mountain 156
    -- Order 8 (Riley, pending delivery): boots + bindings
    (10, 8, 4,  1, 219.99),   -- Park Flex Boots
    (11, 8, 5,  1, 189.99),   -- Ridge Lock Bindings
    -- Order 9 (Alex, in-store): tuning kit
    (12, 9, 15, 1,  39.99),   -- Edge Tuning Kit
    -- Order 10 (Alex, shipped delivery): insulated jacket
    (13, 10, 12, 1, 229.99),  -- Insulated Summit Jacket
    -- Order 11 (Jordan, in-store pickup pending): bindings
    (14, 11, 5,  1, 189.99),  -- Ridge Lock Bindings
    -- Order 12 (Mia, in-store pickup collected): mittens
    (15, 12, 14, 1,  64.99);  -- Summit Mittens

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
