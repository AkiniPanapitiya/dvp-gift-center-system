-- Database Name: dvp_gift_center
-- Generated on 2025-09-24
-- This schema includes tables for both a physical POS system and an online store.

-- Drop the database if it already exists for a clean setup
DROP DATABASE IF EXISTS `dvp_gift_center`;

-- Create the new database
CREATE DATABASE `dvp_gift_center` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Switch to the newly created database
USE `dvp_gift_center`;

-- Set default settings for the session
SET TIME_ZONE = '+05:30';
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";

-- =============================================
-- Section 1: Users & Access Control
-- =============================================

-- Table for all users (customers and staff)
CREATE TABLE `USERS` (
  `user_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password_hash` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `full_name` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(20) NULL,
  `role` VARCHAR(20) NOT NULL COMMENT 'e.g., admin, cashier, customer',
  `address` TEXT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table for customer shipping/billing addresses
CREATE TABLE `CUSTOMER_ADDRESSES` (
  `address_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `customer_id` BIGINT UNSIGNED NOT NULL,
  `address_line1` VARCHAR(255) NOT NULL,
  `address_line2` VARCHAR(255) NULL,
  `city` VARCHAR(100) NOT NULL,
  `postal_code` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`address_id`),
  FOREIGN KEY (`customer_id`) REFERENCES `USERS`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table for storing cart items
CREATE TABLE `CART_ITEMS` (
  `cart_item_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `customer_id` BIGINT UNSIGNED NOT NULL,
  `product_id` BIGINT UNSIGNED NOT NULL,
  `quantity` INT UNSIGNED NOT NULL DEFAULT 1,
  `added_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cart_item_id`),
  UNIQUE KEY `unique_customer_product` (`customer_id`, `product_id`),
  FOREIGN KEY (`customer_id`) REFERENCES `USERS`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`product_id`) REFERENCES `PRODUCTS`(`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table for managing user sessions
CREATE TABLE `SESSION_TOKENS` (
  `token_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `token_hash` VARCHAR(255) NOT NULL UNIQUE,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` DATETIME NOT NULL,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`token_id`),
  FOREIGN KEY (`user_id`) REFERENCES `USERS`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table for logging all significant actions
CREATE TABLE `AUDIT_LOG` (
  `log_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NULL,
  `action_type` VARCHAR(50) NOT NULL COMMENT 'e.g., CREATE, UPDATE, DELETE',
  `table_affected` VARCHAR(50) NOT NULL,
  `record_id` VARCHAR(100) NOT NULL,
  `old_values` TEXT NULL,
  `new_values` TEXT NULL,
  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_address` VARCHAR(45) NULL,
  PRIMARY KEY (`log_id`),
  FOREIGN KEY (`user_id`) REFERENCES `USERS`(`user_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Section 2: Products & Inventory
-- =============================================

-- Table for product categories
CREATE TABLE `CATEGORIES` (
  `category_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `category_name` VARCHAR(100) NOT NULL UNIQUE,
  `description` TEXT NULL,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table for all products
CREATE TABLE `PRODUCTS` (
  `product_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `category_id` INT UNSIGNED NULL,
  `product_name` VARCHAR(150) NOT NULL,
  `product_code` VARCHAR(50) NULL UNIQUE,
  `barcode` VARCHAR(100) NULL UNIQUE,
  `description` TEXT NULL,
  `unit_price` DECIMAL(10, 2) NOT NULL,
  `cost_price` DECIMAL(10, 2) NULL,
  `image_url` VARCHAR(255) NULL,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`product_id`),
  FOREIGN KEY (`category_id`) REFERENCES `CATEGORIES`(`category_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- **NEW TABLE** to manage products available for online sales
CREATE TABLE `ONLINE_PRODUCTS` (
  `online_product_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT UNSIGNED NOT NULL UNIQUE COMMENT 'Foreign key to the main PRODUCTS table',
  `online_price` DECIMAL(10, 2) NOT NULL,
  `is_available_online` BOOLEAN NOT NULL DEFAULT TRUE,
  `online_description` TEXT NULL,
  `promotional_details` TEXT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`online_product_id`),
  FOREIGN KEY (`product_id`) REFERENCES `PRODUCTS`(`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table for tracking stock levels (1-to-1 with Products)
CREATE TABLE `INVENTORY` (
  `inventory_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT UNSIGNED NOT NULL UNIQUE,
  `current_stock` INT NOT NULL DEFAULT 0,
  `min_stock_level` INT NOT NULL DEFAULT 0,
  `max_stock_level` INT NULL,
  `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`inventory_id`),
  FOREIGN KEY (`product_id`) REFERENCES `PRODUCTS`(`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Section 3: Sales & Transactions
-- =============================================

-- Master table for all sales transactions
CREATE TABLE `TRANSACTIONS` (
  `transaction_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `customer_id` BIGINT UNSIGNED NULL,
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT 'Cashier/Staff who made the sale',
  `bill_number` VARCHAR(50) NOT NULL UNIQUE,
  `transaction_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `total_amount` DECIMAL(10, 2) NOT NULL,
  `tax_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  `discount_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  `net_amount` DECIMAL(10, 2) NOT NULL,
  `transaction_type` VARCHAR(20) NOT NULL COMMENT 'e.g., sale, return',
  `status` VARCHAR(20) NOT NULL COMMENT 'e.g., completed, pending, cancelled',
  `receipt_printed` BOOLEAN NOT NULL DEFAULT FALSE,
  `email_sent` BOOLEAN NOT NULL DEFAULT FALSE,
  `source` VARCHAR(20) NOT NULL DEFAULT 'pos_sale' COMMENT 'e.g., pos_sale, online_sale',
  PRIMARY KEY (`transaction_id`),
  FOREIGN KEY (`customer_id`) REFERENCES `USERS`(`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `USERS`(`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- **NEW TABLE** for managing online orders
CREATE TABLE `ONLINE_ORDERS` (
    `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `customer_id` BIGINT UNSIGNED NOT NULL,
    `transaction_id` BIGINT UNSIGNED NOT NULL UNIQUE COMMENT 'Links to the main TRANSACTIONS table',
    `shipping_address_id` BIGINT UNSIGNED NULL,
    `order_status` VARCHAR(50) NOT NULL COMMENT 'e.g., pending, processing, shipped, delivered, cancelled',
    `shipping_method` VARCHAR(50) NULL,
    `tracking_number` VARCHAR(100) NULL,
    `placed_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`order_id`),
    FOREIGN KEY (`customer_id`) REFERENCES `USERS`(`user_id`),
    FOREIGN KEY (`transaction_id`) REFERENCES `TRANSACTIONS`(`transaction_id`),
    FOREIGN KEY (`shipping_address_id`) REFERENCES `CUSTOMER_ADDRESSES`(`address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Line items for each transaction
CREATE TABLE `TRANSACTION_ITEMS` (
  `item_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `transaction_id` BIGINT UNSIGNED NOT NULL,
  `product_id` BIGINT UNSIGNED NOT NULL,
  `quantity` INT UNSIGNED NOT NULL,
  `unit_price` DECIMAL(10, 2) NOT NULL COMMENT 'Price at the time of sale',
  `line_total` DECIMAL(10, 2) NOT NULL,
  `discount_amount` DECIMAL(10, 2) DEFAULT 0.00,
  `tax_amount` DECIMAL(10, 2) DEFAULT 0.00,
  `return_quantity` INT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`item_id`),
  FOREIGN KEY (`transaction_id`) REFERENCES `TRANSACTIONS`(`transaction_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`product_id`) REFERENCES `PRODUCTS`(`product_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Records of payments made for transactions
CREATE TABLE `PAYMENTS` (
  `payment_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `transaction_id` BIGINT UNSIGNED NOT NULL,
  `payment_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `payment_method` VARCHAR(50) NOT NULL COMMENT 'e.g., cash, credit_card',
  `amount_paid` DECIMAL(10, 2) NOT NULL,
  `reference_number` VARCHAR(100) NULL,
  `status` VARCHAR(20) NOT NULL COMMENT 'e.g., success, failed, pending',
  PRIMARY KEY (`payment_id`),
  FOREIGN KEY (`transaction_id`) REFERENCES `TRANSACTIONS`(`transaction_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table to manage product returns
CREATE TABLE `RETURNS` (
  `return_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `original_transaction_id` BIGINT UNSIGNED NOT NULL,
  `return_transaction_id` BIGINT UNSIGNED NULL COMMENT 'A new transaction created for the exchange/refund',
  `product_id` BIGINT UNSIGNED NOT NULL,
  `return_quantity` INT UNSIGNED NOT NULL,
  `return_reason` TEXT NULL,
  `return_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `return_amount` DECIMAL(10, 2) NOT NULL,
  `status` VARCHAR(20) NOT NULL COMMENT 'e.g., refunded, exchanged, pending',
  PRIMARY KEY (`return_id`),
  FOREIGN KEY (`original_transaction_id`) REFERENCES `TRANSACTIONS`(`transaction_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (`return_transaction_id`) REFERENCES `TRANSACTIONS`(`transaction_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (`product_id`) REFERENCES `PRODUCTS`(`product_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Log of all inventory changes
CREATE TABLE `STOCK_MOVEMENTS` (
  `movement_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT UNSIGNED NOT NULL,
  `transaction_id` BIGINT UNSIGNED NULL,
  `movement_type` VARCHAR(50) NOT NULL COMMENT 'e.g., sale, return, adjustment_in, adjustment_out',
  `quantity_change` INT NOT NULL COMMENT 'Positive for stock in, negative for stock out',
  `previous_stock` INT NOT NULL,
  `new_stock` INT NOT NULL,
  `movement_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `notes` TEXT NULL,
  PRIMARY KEY (`movement_id`),
  FOREIGN KEY (`product_id`) REFERENCES `PRODUCTS`(`product_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (`transaction_id`) REFERENCES `TRANSACTIONS`(`transaction_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table to log outgoing emails
CREATE TABLE `EMAIL_LOGS` (
  `email_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `transaction_id` BIGINT UNSIGNED NULL,
  `recipient_email` VARCHAR(100) NOT NULL,
  `sent_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` VARCHAR(20) NOT NULL COMMENT 'e.g., sent, failed',
  `error_message` TEXT NULL,
  PRIMARY KEY (`email_id`),
  FOREIGN KEY (`transaction_id`) REFERENCES `TRANSACTIONS`(`transaction_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Sample Data Insertion
-- =============================================

-- Insert sample categories
INSERT INTO `CATEGORIES` (`category_name`, `description`) VALUES
('Electronics', 'Electronic gadgets and accessories'),
('Home & Garden', 'Home decoration and gardening items'),
('Fashion', 'Clothing and fashion accessories'),
('Books', 'Books and educational materials'),
('Toys & Games', 'Toys and gaming products');

-- Insert sample admin user
INSERT INTO `USERS` (`username`, `password_hash`, `email`, `full_name`, `role`, `is_active`) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye/v5xnqxQJ.OxPhWpWVHC5.9V5.c5gIe', 'admin@dvpgiftcenter.com', 'Administrator', 'admin', TRUE);

-- Insert sample products
INSERT INTO `PRODUCTS` (`category_id`, `product_name`, `product_code`, `description`, `unit_price`, `cost_price`, `image_url`, `is_active`) VALUES
(1, 'Wireless Bluetooth Headphones', 'WBH001', 'High-quality wireless headphones with noise cancellation', 89.99, 50.00, 'https://example.com/images/headphones.jpg', TRUE),
(1, 'Smartphone Stand', 'SPS001', 'Adjustable smartphone stand for desk use', 19.99, 12.00, 'https://example.com/images/phone-stand.jpg', TRUE),
(2, 'Decorative Wall Clock', 'DWC001', 'Modern decorative wall clock for living room', 45.99, 25.00, 'https://example.com/images/wall-clock.jpg', TRUE),
(3, 'Cotton T-Shirt', 'CTS001', 'Comfortable cotton t-shirt in various colors', 24.99, 15.00, 'https://example.com/images/tshirt.jpg', TRUE),
(4, 'Programming Book Set', 'PBS001', 'Complete set of programming books for beginners', 79.99, 45.00, 'https://example.com/images/book-set.jpg', TRUE);

-- Insert inventory for products
INSERT INTO `INVENTORY` (`product_id`, `current_stock`, `min_stock_level`, `max_stock_level`) VALUES
(1, 50, 10, 100),
(2, 75, 15, 150),
(3, 30, 5, 60),
(4, 100, 20, 200),
(5, 25, 5, 50);

-- Insert online products (making some products available online)
INSERT INTO `ONLINE_PRODUCTS` (`product_id`, `online_price`, `is_available_online`, `online_description`, `promotional_details`) VALUES
(1, 89.99, TRUE, 'Premium wireless headphones with superior sound quality and long battery life', 'Free shipping on orders over $50'),
(2, 19.99, TRUE, 'Perfect smartphone stand for work from home setup', 'Buy 2 get 10% off'),
(3, 45.99, TRUE, 'Stylish wall clock that complements any modern home decor', NULL),
(4, 24.99, TRUE, 'High-quality cotton t-shirt available in multiple sizes', 'Limited time offer - 20% off'),
(5, 79.99, FALSE, 'Comprehensive programming guide for software development', NULL);