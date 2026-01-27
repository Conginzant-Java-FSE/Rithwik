-- RevPay Database Schema (Simplified)
-- Run this script to create the database and all tables

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS revpay_database;
USE revpay_database;

-- Drop existing tables in reverse order (to handle foreign key constraints)
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS invoice_items;
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS money_requests;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS payment_methods;
DROP TABLE IF EXISTS business_accounts;
DROP TABLE IF EXISTS users;

-- Users table (for both personal and business accounts)
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    transaction_pin_hash VARCHAR(255),
    security_question VARCHAR(255) NOT NULL,
    security_answer VARCHAR(255) NOT NULL,
    wallet_balance DECIMAL(15, 2) DEFAULT 0.00,
    account_type ENUM('PERSONAL', 'BUSINESS') DEFAULT 'PERSONAL',
    failed_login_attempts INT DEFAULT 0,
    lockout_until DATETIME,
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Business accounts table (extends users for business features)
CREATE TABLE IF NOT EXISTS business_accounts (
    business_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE NOT NULL,
    business_name VARCHAR(150) NOT NULL,
    business_type VARCHAR(50) NOT NULL,
    tax_id VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Payment methods table (credit/debit cards)
CREATE TABLE IF NOT EXISTS payment_methods (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    card_type ENUM('CREDIT', 'DEBIT') NOT NULL,
    card_last_four VARCHAR(4) NOT NULL,
    encrypted_card_number VARCHAR(255),
    card_holder_name VARCHAR(100) NOT NULL,
    expiry_month VARCHAR(10) NOT NULL,
    expiry_year VARCHAR(10) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    transaction_type ENUM('SEND', 'REQUEST', 'ADD_MONEY', 'WITHDRAW', 'INVOICE_PAYMENT') NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED') DEFAULT 'PENDING',
    note VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (receiver_id) REFERENCES users(user_id)
);

-- Money requests table
CREATE TABLE IF NOT EXISTS money_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    requester_id INT NOT NULL,
    target_id INT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    note VARCHAR(255),
    status ENUM('PENDING', 'ACCEPTED', 'DECLINED', 'CANCELLED') DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users(user_id),
    FOREIGN KEY (target_id) REFERENCES users(user_id)
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    message VARCHAR(500) NOT NULL,
    notification_type ENUM('TRANSACTION', 'REQUEST', 'ALERT', 'INVOICE', 'LOAN') NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Invoices table (for business accounts)
CREATE TABLE IF NOT EXISTS invoices (
    invoice_id INT PRIMARY KEY AUTO_INCREMENT,
    business_id INT NOT NULL,
    customer_id INT NOT NULL,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    total_amount DECIMAL(15, 2) NOT NULL,
    status ENUM('PENDING', 'PAID', 'OVERDUE', 'CANCELLED') DEFAULT 'PENDING',
    due_date DATE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (business_id) REFERENCES business_accounts(business_id),
    FOREIGN KEY (customer_id) REFERENCES users(user_id)
);

-- Invoice items table
CREATE TABLE IF NOT EXISTS invoice_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(15, 2) NOT NULL,
    total DECIMAL(15, 2) NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE
);

-- Loans table (for business accounts)
CREATE TABLE IF NOT EXISTS loans (
    loan_id INT PRIMARY KEY AUTO_INCREMENT,
    business_id INT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    purpose VARCHAR(255) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'ACTIVE', 'PAID_OFF') DEFAULT 'PENDING',
    interest_rate DECIMAL(5, 2) DEFAULT 0.00,
    term_months INT NOT NULL,
    monthly_payment DECIMAL(15, 2) DEFAULT 0.00,
    remaining_balance DECIMAL(15, 2) DEFAULT 0.00,
    applied_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    approved_at DATETIME,
    FOREIGN KEY (business_id) REFERENCES business_accounts(business_id)
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_transactions_sender ON transactions(sender_id);
CREATE INDEX idx_transactions_receiver ON transactions(receiver_id);
CREATE INDEX idx_transactions_date ON transactions(created_at);
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_invoices_business ON invoices(business_id);
CREATE INDEX idx_invoices_customer ON invoices(customer_id);

-- Show success message
SELECT 'RevPay database created successfully!' AS Status;
