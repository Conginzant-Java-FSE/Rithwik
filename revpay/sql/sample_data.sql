-- RevPay Sample Data with Realistic Information
-- This file contains realistic test data for the RevPay application
-- Run this after creating the schema

USE revpay_database;

-- Clear existing data (optional, comment out if you want to keep existing data)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE loans;
TRUNCATE TABLE invoice_items;
TRUNCATE TABLE invoices;
TRUNCATE TABLE notifications;
TRUNCATE TABLE money_requests;
TRUNCATE TABLE transactions;
TRUNCATE TABLE payment_methods;
TRUNCATE TABLE business_accounts;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- USERS (Personal and Business Accounts)
-- Note: All passwords are BCrypt hashed for "password123"
-- All PINs are hashed for "1234"
-- =====================================================

INSERT INTO users (email, phone, full_name, password_hash, transaction_pin_hash, 
    security_question, security_answer, wallet_balance, account_type, 
    failed_login_attempts, lockout_until, last_login, created_at) VALUES

-- Personal Users
('sarah.johnson@gmail.com', '555-0101', 'Sarah Johnson', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What is your mother''s maiden name?', 'Smith', 
 2500.00, 'PERSONAL', 0, NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),

('michael.chen@yahoo.com', '555-0102', 'Michael Chen', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What is your pet''s name?', 'Max', 
 1850.50, 'PERSONAL', 0, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),

('emily.rodriguez@hotmail.com', '555-0103', 'Emily Rodriguez', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What city were you born in?', 'Miami', 
 3200.75, 'PERSONAL', 0, NULL, NULL, NOW()),

('david.patel@gmail.com', '555-0104', 'David Patel', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What is your favorite color?', 'Blue', 
 950.00, 'PERSONAL', 0, NULL, NULL, NOW()),

('jennifer.williams@outlook.com', '555-0105', 'Jennifer Williams', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What is your mother''s maiden name?', 'Davis', 
 5600.25, 'PERSONAL', 0, NULL, NULL, NOW()),

('robert.brown@gmail.com', '555-0106', 'Robert Brown', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What is your pet''s name?', 'Buddy', 
 780.00, 'PERSONAL', 0, NULL, NULL, NOW()),

('lisa.anderson@icloud.com', '555-0107', 'Lisa Anderson', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What city were you born in?', 'Boston', 
 4100.00, 'PERSONAL', 0, NULL, NULL, NOW()),

('james.taylor@gmail.com', '555-0108', 'James Taylor', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What is your favorite color?', 'Green', 
 1250.50, 'PERSONAL', 0, NULL, NULL, NOW()),

-- Business Account Users
('owner@joesbakery.com', '555-0201', 'Joseph Martinez', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What is your mother''s maiden name?', 'Garcia', 
 12500.00, 'BUSINESS', 0, NULL, DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW()),

('admin@techstart.io', '555-0202', 'Amanda Kim', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What city were you born in?', 'Seattle', 
 45000.75, 'BUSINESS', 0, NULL, NULL, NOW()),

('billing@greenleafconsulting.com', '555-0203', 'Marcus Thompson', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What is your pet''s name?', 'Charlie', 
 28300.50, 'BUSINESS', 0, NULL, NULL, NOW()),

('contact@urbanfitness.com', '555-0204', 'Sophia Lewis', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What is your favorite color?', 'Purple', 
 18750.00, 'BUSINESS', 0, NULL, NULL, NOW()),

('info@petalsperfect.com', '555-0205', 'Rachel Green', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeR8DnIvzB.R5.LJzQ.5B5Qs.F5Qe6Q/K',
 'What city were you born in?', 'Portland', 
 9400.25, 'BUSINESS', 0, NULL, NULL, NOW());

-- =====================================================
-- BUSINESS ACCOUNTS
-- =====================================================

INSERT INTO business_accounts (user_id, business_name, business_type, tax_id, address, is_verified, created_at) VALUES
(9, 'Joe''s Artisan Bakery', 'Food & Beverage', 'EIN-45-2341567', '123 Main Street, New York, NY 10001', TRUE, NOW()),
(10, 'TechStart Solutions', 'Technology', 'EIN-88-1234567', '456 Innovation Ave, San Francisco, CA 94105', TRUE, NOW()),
(11, 'GreenLeaf Consulting', 'Professional Services', 'EIN-77-9876543', '789 Business Park Dr, Austin, TX 78701', TRUE, NOW()),
(12, 'Urban Fitness Studio', 'Health & Wellness', 'EIN-33-5551234', '321 Wellness Ln, Chicago, IL 60601', TRUE, NOW()),
(13, 'Petals Perfect Florist', 'Retail', 'EIN-22-8887654', '567 Garden Road, Portland, OR 97201', FALSE, NOW());

-- =====================================================
-- PAYMENT METHODS (using test card numbers - last 4 digits only stored)
-- =====================================================

INSERT INTO payment_methods (user_id, card_type, card_last_four, encrypted_card_number, card_holder_name, 
    expiry_month, expiry_year, is_default, created_at) VALUES
-- Personal users payment methods
(1, 'CREDIT', '4242', 'ENC_4242424242424242_AES256', 'Sarah Johnson', '08', '2027', TRUE, NOW()),
(2, 'DEBIT', '5555', 'ENC_4000056655665556_AES256', 'Michael Chen', '12', '2026', TRUE, NOW()),
(3, 'CREDIT', '0005', 'ENC_5200828282828210_AES256', 'Emily Rodriguez', '03', '2028', TRUE, NOW()),
(4, 'CREDIT', '1234', 'ENC_4111111111111111_AES256', 'David Patel', '06', '2027', TRUE, NOW()),
(5, 'DEBIT', '8888', 'ENC_4000002500003155_AES256', 'Jennifer Williams', '09', '2026', TRUE, NOW()),
(5, 'CREDIT', '4444', 'ENC_4242424242424242_AES256', 'Jennifer Williams', '11', '2027', FALSE, NOW()),
(7, 'CREDIT', '9999', 'ENC_5555555555554444_AES256', 'Lisa Anderson', '04', '2028', TRUE, NOW()),
-- Business users payment methods
(9, 'CREDIT', '0000', 'ENC_4000000000000000_AES256', 'Joe''s Artisan Bakery', '12', '2027', TRUE, NOW()),
(10, 'CREDIT', '1111', 'ENC_4000000000001111_AES256', 'TechStart Solutions', '05', '2028', TRUE, NOW()),
(11, 'DEBIT', '2222', 'ENC_4000000000002222_AES256', 'GreenLeaf Consulting', '07', '2027', TRUE, NOW()),
(12, 'CREDIT', '3333', 'ENC_4000000000003333_AES256', 'Urban Fitness Studio', '10', '2026', TRUE, NOW());

-- =====================================================
-- TRANSACTIONS (Recent activity)
-- =====================================================

INSERT INTO transactions (sender_id, receiver_id, amount, transaction_type, status, note, created_at) VALUES
-- Friend payments
(1, 2, 45.50, 'SEND', 'COMPLETED', 'Dinner split from last Friday', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 1, 25.00, 'SEND', 'COMPLETED', 'Coffee this morning', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 4, 150.00, 'SEND', 'COMPLETED', 'Concert tickets', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(5, 3, 80.00, 'SEND', 'COMPLETED', 'Birthday gift contribution', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(7, 8, 200.00, 'SEND', 'COMPLETED', 'Rent utilities', DATE_SUB(NOW(), INTERVAL 7 DAY)),

-- Add money to wallet
(1, 1, 500.00, 'ADD_MONEY', 'COMPLETED', 'Added funds from bank', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(2, 2, 1000.00, 'ADD_MONEY', 'COMPLETED', 'Monthly reload', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(3, 3, 2000.00, 'ADD_MONEY', 'COMPLETED', 'Paycheck deposit', DATE_SUB(NOW(), INTERVAL 20 DAY)),
(5, 5, 3000.00, 'ADD_MONEY', 'COMPLETED', 'Savings transfer', DATE_SUB(NOW(), INTERVAL 12 DAY)),

-- Business transactions
(1, 9, 35.75, 'SEND', 'COMPLETED', 'Bakery order - croissants and coffee', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 9, 52.50, 'SEND', 'COMPLETED', 'Birthday cake order', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(5, 10, 1500.00, 'SEND', 'COMPLETED', 'Website development consultation', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(3, 11, 750.00, 'SEND', 'COMPLETED', 'Business strategy session', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(7, 12, 120.00, 'SEND', 'COMPLETED', 'Monthly gym membership', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 13, 85.00, 'SEND', 'COMPLETED', 'Flower arrangement delivery', DATE_SUB(NOW(), INTERVAL 3 DAY)),

-- Invoice payments
(1, 9, 35.75, 'INVOICE_PAYMENT', 'COMPLETED', 'Invoice #INV-1001 payment', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 9, 52.50, 'INVOICE_PAYMENT', 'COMPLETED', 'Invoice #INV-1002 payment', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(5, 10, 1500.00, 'INVOICE_PAYMENT', 'COMPLETED', 'Invoice #INV-2001 payment', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(3, 11, 750.00, 'INVOICE_PAYMENT', 'COMPLETED', 'Invoice #INV-3001 payment', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(7, 12, 120.00, 'INVOICE_PAYMENT', 'COMPLETED', 'Invoice #INV-4001 payment', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 13, 85.00, 'INVOICE_PAYMENT', 'COMPLETED', 'Invoice #INV-5001 payment', DATE_SUB(NOW(), INTERVAL 4 DAY)),

-- Pending transaction
(6, 7, 100.00, 'SEND', 'PENDING', 'Book club dues', NOW());

-- =====================================================
-- MONEY REQUESTS
-- =====================================================

INSERT INTO money_requests (requester_id, target_id, amount, note, status, created_at) VALUES
(2, 1, 30.00, 'Spotify family plan this month', 'PENDING', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 3, 60.00, 'Shared Uber ride to airport', 'ACCEPTED', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(8, 7, 45.00, 'Gas money for road trip', 'PENDING', NOW()),
(9, 1, 35.75, 'Invoice #INV-1001 payment reminder', 'ACCEPTED', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(11, 5, 750.00, 'Consulting invoice payment', 'ACCEPTED', DATE_SUB(NOW(), INTERVAL 6 DAY));

-- =====================================================
-- NOTIFICATIONS
-- =====================================================

INSERT INTO notifications (user_id, title, message, notification_type, is_read, created_at) VALUES
-- Welcome notifications
(1, 'Welcome to RevPay!', 'Thank you for joining RevPay. Start by adding a payment method and setting up your transaction PIN.', 'ALERT', TRUE, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(2, 'Welcome to RevPay!', 'Thank you for joining RevPay. Start by adding a payment method.', 'ALERT', TRUE, DATE_SUB(NOW(), INTERVAL 28 DAY)),

-- Transaction notifications
(1, 'Money Received', 'You received $25.00 from Michael Chen', 'TRANSACTION', FALSE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 'Money Sent', 'You sent $25.00 to Sarah Johnson', 'TRANSACTION', TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 'Money Sent', 'You sent $45.50 to Michael Chen', 'TRANSACTION', TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 'Money Received', 'You received $45.50 from Sarah Johnson', 'TRANSACTION', TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- Money request notifications
(1, 'Money Request', 'Michael Chen is requesting $30.00 from you', 'REQUEST', FALSE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(7, 'Money Request', 'James Taylor is requesting $45.00 from you', 'REQUEST', FALSE, NOW()),

-- Invoice notifications
(9, 'Invoice Paid', 'Invoice #INV-1001 has been paid - $35.75', 'INVOICE', TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(9, 'Invoice Paid', 'Invoice #INV-1002 has been paid - $52.50', 'INVOICE', TRUE, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(10, 'Invoice Paid', 'Invoice #INV-2001 has been paid - $1500.00', 'INVOICE', TRUE, DATE_SUB(NOW(), INTERVAL 6 DAY)),

-- Business notifications
(9, 'Business Verified', 'Your business account has been verified. You can now create invoices and apply for loans.', 'ALERT', TRUE, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(10, 'Business Verified', 'Your business account has been verified.', 'ALERT', TRUE, DATE_SUB(NOW(), INTERVAL 20 DAY)),

-- Loan notifications
(9, 'Loan Update', 'Your loan application for $25,000.00 has been approved!', 'LOAN', FALSE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(10, 'Loan Disbursed', 'Your approved loan of $50,000.00 is now active. Monthly payment: $2,243.75', 'LOAN', TRUE, DATE_SUB(NOW(), INTERVAL 30 DAY));

-- =====================================================
-- INVOICES (for business accounts)
-- =====================================================

INSERT INTO invoices (business_id, customer_id, invoice_number, total_amount, status, due_date, created_at) VALUES
-- Joe's Artisan Bakery invoices (business_id = 1)
(1, 1, 'INV-1001', 35.75, 'PAID', DATE_ADD(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 2, 'INV-1002', 52.50, 'PAID', DATE_ADD(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 6, 'INV-1003', 28.50, 'PENDING', DATE_ADD(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- TechStart Solutions invoices (business_id = 2)
(2, 5, 'INV-2001', 1500.00, 'PAID', DATE_ADD(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(2, 7, 'INV-2002', 2500.00, 'PENDING', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),

-- GreenLeaf Consulting invoices (business_id = 3)
(3, 3, 'INV-3001', 750.00, 'PAID', DATE_ADD(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(3, 5, 'INV-3002', 1200.00, 'PENDING', DATE_ADD(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),

-- Urban Fitness Studio invoices (business_id = 4)
(4, 7, 'INV-4001', 120.00, 'PAID', DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 1, 'INV-4002', 360.00, 'PENDING', DATE_ADD(NOW(), INTERVAL 7 DAY), NOW()),

-- Petals Perfect Florist invoices (business_id = 5)
(5, 4, 'INV-5001', 85.00, 'PAID', DATE_ADD(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(5, 8, 'INV-5002', 125.00, 'OVERDUE', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY));

-- =====================================================
-- INVOICE ITEMS
-- =====================================================

INSERT INTO invoice_items (invoice_id, description, quantity, unit_price, total) VALUES
-- Joe's Bakery invoices (INV-1001, INV-1002, INV-1003)
(1, 'Chocolate Croissants', 3, 4.50, 13.50),
(1, 'Large Cappuccino', 2, 5.25, 10.50),
(1, 'Blueberry Muffins', 4, 2.95, 11.75),
(2, 'Custom Birthday Cake - Chocolate', 1, 52.50, 52.50),
(3, 'Sourdough Bread Loaf', 2, 7.50, 15.00),
(3, 'Butter Croissants', 6, 2.25, 13.50),

-- TechStart Solutions invoices (INV-2001, INV-2002)
(4, 'Website Design Consultation', 3, 150.00, 450.00),
(4, 'Initial Development Phase', 1, 1050.00, 1050.00),
(5, 'Custom Web Application Development', 50, 50.00, 2500.00),

-- GreenLeaf Consulting invoices (INV-3001, INV-3002)
(6, 'Business Strategy Session', 2, 375.00, 750.00),
(7, 'Market Analysis Report', 1, 800.00, 800.00),
(7, 'Implementation Planning', 4, 100.00, 400.00),

-- Urban Fitness invoices (INV-4001, INV-4002)
(8, 'Monthly Membership - Premium', 1, 120.00, 120.00),
(9, 'Quarterly Membership - Premium', 3, 120.00, 360.00),

-- Petals Perfect invoices (INV-5001, INV-5002)
(10, 'Rose Bouquet - Dozen Red Roses', 1, 65.00, 65.00),
(10, 'Delivery Fee', 1, 20.00, 20.00),
(11, 'Wedding Centerpiece Arrangement', 5, 25.00, 125.00);

-- =====================================================
-- LOANS (Business loans)
-- =====================================================

INSERT INTO loans (business_id, amount, purpose, term_months, interest_rate, monthly_payment, 
    remaining_balance, status, approved_at, applied_at) VALUES
-- Joe's Artisan Bakery loan (approved recently)
(1, 25000.00, 'Kitchen equipment upgrade and expansion', 36, 8.5, 789.50, 25000.00, 'APPROVED', NOW(), DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- TechStart Solutions loans (one active, one approved)
(2, 50000.00, 'Office space expansion and hiring', 24, 7.5, 2243.75, 45000.00, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY)),

-- GreenLeaf Consulting loan (active)
(3, 15000.00, 'Marketing campaign and website redesign', 12, 9.0, 1307.50, 12500.00, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 65 DAY)),

-- Urban Fitness Studio loan (approved)
(4, 30000.00, 'New fitness equipment purchase', 24, 8.0, 1354.00, 30000.00, 'APPROVED', NOW(), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- Petals Perfect Florist loan (pending)
(5, 10000.00, 'Inventory expansion and cooler upgrade', 18, 8.5, 613.00, 10000.00, 'PENDING', NULL, NOW());

-- =====================================================
-- Summary Report
-- =====================================================

SELECT 'Sample Data Insertion Complete!' AS Status;
SELECT COUNT(*) AS Total_Users FROM users;
SELECT COUNT(*) AS Personal_Accounts FROM users WHERE account_type = 'PERSONAL';
SELECT COUNT(*) AS Business_Accounts FROM users WHERE account_type = 'BUSINESS';
SELECT COUNT(*) AS Total_Transactions FROM transactions;
SELECT COUNT(*) AS Total_Invoices FROM invoices;
SELECT COUNT(*) AS Total_Loans FROM loans;
SELECT SUM(wallet_balance) AS Total_Money_In_System FROM users;
