-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Sep 26, 2025 at 11:46 AM
-- Server version: 8.0.31
-- PHP Version: 8.0.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dvp_gift_center`
--

-- --------------------------------------------------------

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE IF NOT EXISTS `audit_log` (
  `log_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint UNSIGNED DEFAULT NULL,
  `action_type` varchar(50) NOT NULL COMMENT 'e.g., CREATE, UPDATE, DELETE',
  `table_affected` varchar(50) NOT NULL,
  `record_id` varchar(100) NOT NULL,
  `old_values` text,
  `new_values` text,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_address` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`log_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE IF NOT EXISTS `cart_items` (
  `cart_item_id` bigint NOT NULL AUTO_INCREMENT,
  `added_at` datetime(6) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `customer_id` bigint UNSIGNED NOT NULL,
  `product_id` bigint UNSIGNED NOT NULL,
  PRIMARY KEY (`cart_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `category_id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) NOT NULL,
  `description` text,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_name` (`category_name`),
  UNIQUE KEY `UK_41g4n0emuvcm3qyf1f6cn43c0` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `category_name`, `description`, `is_active`, `created_at`) VALUES
(4, 'Kitchen', '', 1, '2025-09-26 02:15:39'),
(5, 'Toys', '', 1, '2025-09-26 03:04:25');

-- --------------------------------------------------------

--
-- Table structure for table `customer_addresses`
--

DROP TABLE IF EXISTS `customer_addresses`;
CREATE TABLE IF NOT EXISTS `customer_addresses` (
  `address_id` bigint NOT NULL AUTO_INCREMENT,
  `address_line1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address_line2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `postal_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customer_id` bigint UNSIGNED NOT NULL,
  PRIMARY KEY (`address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `customer_addresses`
--



-- --------------------------------------------------------

--
-- Table structure for table `email_logs`
--

DROP TABLE IF EXISTS `email_logs`;
CREATE TABLE IF NOT EXISTS `email_logs` (
  `email_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `transaction_id` bigint UNSIGNED DEFAULT NULL,
  `recipient_email` varchar(100) NOT NULL,
  `sent_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL COMMENT 'e.g., sent, failed',
  `error_message` text,
  PRIMARY KEY (`email_id`),
  KEY `transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--




-- --------------------------------------------------------

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
CREATE TABLE IF NOT EXISTS `inventory` (
  `inventory_id` bigint NOT NULL AUTO_INCREMENT,
  `current_stock` int DEFAULT NULL,
  `last_updated` datetime(6) DEFAULT NULL,
  `max_stock_level` int DEFAULT NULL,
  `min_stock_level` int DEFAULT NULL,
  `product_id` bigint UNSIGNED NOT NULL,
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `UK_ce3rbi3bfstbvvyne34c1dvyv` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `inventory`
--

INSERT INTO `inventory` (`inventory_id`, `current_stock`, `last_updated`, `max_stock_level`, `min_stock_level`, `product_id`) VALUES
(6, 10, '2025-09-26 09:22:43.436819', 10, 1, 20),
(7, 16, '2025-09-26 08:34:47.208518', 25, 1, 21);

-- --------------------------------------------------------

--
-- Table structure for table `online_orders`
--

DROP TABLE IF EXISTS `online_orders`;
CREATE TABLE IF NOT EXISTS `online_orders` (
  `order_id` bigint NOT NULL AUTO_INCREMENT,
  `order_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `placed_at` datetime(6) DEFAULT NULL,
  `shipping_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tracking_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customer_id` bigint UNSIGNED NOT NULL,
  `shipping_address_id` bigint DEFAULT NULL,
  `transaction_id` bigint UNSIGNED NOT NULL,
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `UK_f2u2ew4h1c1nq0lwunqa9dmha` (`transaction_id`),
  KEY `FKfhpg336e0uy7mnr8d7l0h4roi` (`shipping_address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `online_orders`
--

INSERT INTO `online_orders` (`order_id`, `order_status`, `placed_at`, `shipping_method`, `tracking_number`, `customer_id`, `shipping_address_id`, `transaction_id`) VALUES
(5, 'PROCESSING', '2025-09-26 08:35:35.652040', NULL, NULL, 3, 5, 10),
(6, 'pending', '2025-09-26 08:57:17.160091', NULL, NULL, 3, 6, 11),
(7, 'pending', '2025-09-26 09:19:33.134431', NULL, NULL, 3, 7, 12),
(8, 'SHIPPED', '2025-09-26 09:21:43.681210', NULL, NULL, 3, 8, 13);

-- --------------------------------------------------------

--
-- Table structure for table `online_products`
--

DROP TABLE IF EXISTS `online_products`;
CREATE TABLE IF NOT EXISTS `online_products` (
  `online_product_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_available_online` bit(1) DEFAULT NULL,
  `online_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `online_price` decimal(10,2) DEFAULT NULL,
  `promotional_details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `product_id` bigint UNSIGNED NOT NULL,
  PRIMARY KEY (`online_product_id`),
  UNIQUE KEY `UK_4b5wbwjqfum10j5kfovlb8orb` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `online_products`
--

INSERT INTO `online_products` (`online_product_id`, `created_at`, `is_available_online`, `online_description`, `online_price`, `promotional_details`, `product_id`) VALUES
(4, '2025-09-26 08:34:02.005293', b'1', NULL, '1500.00', NULL, 20),
(5, '2025-09-26 08:34:47.211517', b'1', NULL, '55.00', NULL, 21);

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
CREATE TABLE IF NOT EXISTS `payments` (
  `payment_id` bigint NOT NULL AUTO_INCREMENT,
  `amount_paid` decimal(10,2) DEFAULT NULL,
  `payment_date` datetime(6) DEFAULT NULL,
  `payment_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reference_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `transaction_id` bigint UNSIGNED NOT NULL,
  PRIMARY KEY (`payment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`payment_id`, `amount_paid`, `payment_date`, `payment_method`, `reference_number`, `status`, `transaction_id`) VALUES
(5, '1500.00', '2025-09-26 08:35:35.655047', 'CREDIT_CARD', 'REF-8PGUCXJD8J9X', 'success', 10),
(6, '110.00', '2025-09-26 08:57:17.162094', 'CASH_ON_DELIVERY', 'REF-JDYWWJX3RS3K', 'success', 11),
(7, '57.75', '2025-09-26 09:19:33.137435', 'CASH_ON_DELIVERY', 'REF-UGKA0BEWBM0B', 'success', 12),
(8, '57.75', '2025-09-26 09:21:43.684231', 'CREDIT_CARD', 'REF-OBRFO8T3C5SJ', 'success', 13);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
CREATE TABLE IF NOT EXISTS `products` (
  `product_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `category_id` int UNSIGNED DEFAULT NULL,
  `product_name` varchar(150) NOT NULL,
  `product_code` varchar(50) DEFAULT NULL,
  `barcode` varchar(100) DEFAULT NULL,
  `description` text,
  `unit_price` decimal(10,2) NOT NULL,
  `cost_price` decimal(10,2) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `product_code` (`product_code`),
  UNIQUE KEY `barcode` (`barcode`),
  UNIQUE KEY `UK_qfr8vf85k3q1xinifvsl1eynf` (`barcode`),
  UNIQUE KEY `UK_922x4t23nx64422orei4meb2y` (`product_code`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`product_id`, `category_id`, `product_name`, `product_code`, `barcode`, `description`, `unit_price`, `cost_price`, `image_url`, `is_active`) VALUES
(20, 4, 'Pan', '2145', 'aa', NULL, '1500.00', '250.00', 'https://img.freepik.com/premium-photo/pan_921277-1027.jpg', 1),
(21, 5, 'remote car', '4545dfdf', '454dfdf', 'fdfd', '50.00', NULL, 'https://th.bing.com/th/id/OIP.eZzBpikk-aQYz-o7OfLeVwHaHa?o=7rm=3&rs=1&pid=ImgDetMain&o=7&rm=3', 1);

-- --------------------------------------------------------

--
-- Table structure for table `returns`
--

DROP TABLE IF EXISTS `returns`;
CREATE TABLE IF NOT EXISTS `returns` (
  `return_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `original_transaction_id` bigint UNSIGNED NOT NULL,
  `return_transaction_id` bigint UNSIGNED DEFAULT NULL,
  `product_id` bigint UNSIGNED NOT NULL,
  `return_quantity` int UNSIGNED NOT NULL,
  `return_reason` text,
  `return_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `return_amount` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL COMMENT 'e.g., refunded, exchanged, pending',
  PRIMARY KEY (`return_id`),
  KEY `original_transaction_id` (`original_transaction_id`),
  KEY `return_transaction_id` (`return_transaction_id`),
  KEY `product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `session_tokens`
--

DROP TABLE IF EXISTS `session_tokens`;
CREATE TABLE IF NOT EXISTS `session_tokens` (
  `token_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `token_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` bigint UNSIGNED NOT NULL,
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `UK_q6um3cyu2jn439mamni361y5c` (`token_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `stock_movements`
--

DROP TABLE IF EXISTS `stock_movements`;
CREATE TABLE IF NOT EXISTS `stock_movements` (
  `movement_id` bigint NOT NULL AUTO_INCREMENT,
  `movement_date` datetime(6) DEFAULT NULL,
  `movement_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `new_stock` int DEFAULT NULL,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `previous_stock` int DEFAULT NULL,
  `quantity_change` int DEFAULT NULL,
  `product_id` bigint UNSIGNED NOT NULL,
  `transaction_id` bigint UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`movement_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `stock_movements`
--

INSERT INTO `stock_movements` (`movement_id`, `movement_date`, `movement_type`, `new_stock`, `notes`, `previous_stock`, `quantity_change`, `product_id`, `transaction_id`) VALUES
(5, '2025-09-26 08:35:35.616790', 'sale', 4, 'Online order - Pan', 5, -1, 20, 10),
(6, '2025-09-26 08:57:17.058914', 'sale', 18, 'Online order - remote car', 20, -2, 21, 11),
(7, '2025-09-26 09:19:33.080933', 'sale', 17, 'Online order - remote car', 18, -1, 21, 12),
(8, '2025-09-26 09:21:43.675206', 'sale', 16, 'Online order - remote car', 17, -1, 21, 13);

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
CREATE TABLE IF NOT EXISTS `transactions` (
  `transaction_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `customer_id` bigint UNSIGNED DEFAULT NULL,
  `user_id` bigint UNSIGNED NOT NULL,
  `bill_number` varchar(50) NOT NULL,
  `transaction_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `total_amount` decimal(10,2) NOT NULL,
  `tax_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `net_amount` decimal(10,2) NOT NULL,
  `transaction_type` varchar(20) NOT NULL COMMENT 'e.g., sale, return',
  `status` varchar(20) NOT NULL COMMENT 'e.g., completed, pending, cancelled',
  `receipt_printed` tinyint(1) NOT NULL DEFAULT '0',
  `email_sent` tinyint(1) NOT NULL DEFAULT '0',
  `source` varchar(20) NOT NULL DEFAULT 'pos_sale' COMMENT 'e.g., pos_sale, online_sale',
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `bill_number` (`bill_number`),
  UNIQUE KEY `UK_chiudoeflocd9m3dlvm6ibevu` (`bill_number`),
  KEY `FKevcdt67e1ag2tsynj3yqpvpbv` (`customer_id`),
  KEY `FKqwv7rmvc8va8rep7piikrojds` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`transaction_id`, `customer_id`, `user_id`, `bill_number`, `transaction_date`, `total_amount`, `tax_amount`, `discount_amount`, `net_amount`, `transaction_type`, `status`, `receipt_printed`, `email_sent`, `source`) VALUES
(10, 3, 3, 'DVP202509260001', '2025-09-26 08:35:36', '1500.00', '0.00', '0.00', '1500.00', 'sale', 'completed', 0, 0, 'online_sale'),
(11, 3, 3, 'DVP202509260002', '2025-09-26 08:57:17', '110.00', '0.00', '0.00', '110.00', 'sale', 'completed', 0, 0, 'online_sale'),
(12, 3, 3, 'DVP202509260003', '2025-09-26 09:19:33', '55.00', '2.75', '0.00', '57.75', 'sale', 'completed', 0, 0, 'online_sale'),
(13, 3, 3, 'DVP202509260004', '2025-09-26 09:21:43', '55.00', '2.75', '0.00', '57.75', 'sale', 'completed', 0, 0, 'online_sale');

-- --------------------------------------------------------

--
-- Table structure for table `transaction_items`
--

DROP TABLE IF EXISTS `transaction_items`;
CREATE TABLE IF NOT EXISTS `transaction_items` (
  `item_id` bigint NOT NULL AUTO_INCREMENT,
  `discount_amount` decimal(10,2) DEFAULT NULL,
  `line_total` decimal(10,2) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `return_quantity` int DEFAULT NULL,
  `tax_amount` decimal(10,2) DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `product_id` bigint UNSIGNED NOT NULL,
  `transaction_id` bigint UNSIGNED NOT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `transaction_items`
--

INSERT INTO `transaction_items` (`item_id`, `discount_amount`, `line_total`, `quantity`, `return_quantity`, `tax_amount`, `unit_price`, `product_id`, `transaction_id`) VALUES
(5, '0.00', '1500.00', 1, 0, '0.00', '1500.00', 20, 10),
(6, '0.00', '110.00', 2, 0, '0.00', '55.00', 21, 11),
(7, '0.00', '55.00', 1, 0, '0.00', '55.00', 21, 12),
(8, '0.00', '55.00', 1, 0, '0.00', '55.00', 21, 13);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` varchar(20) NOT NULL COMMENT 'e.g., admin, cashier, customer',
  `address` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
--
-- Constraints for dumped tables
--

--
-- Constraints for table `audit_log`
--
ALTER TABLE `audit_log`
  ADD CONSTRAINT `audit_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `email_logs`
--
ALTER TABLE `email_logs`
  ADD CONSTRAINT `email_logs_ibfk_1` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`transaction_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `online_orders`
--
ALTER TABLE `online_orders`
  ADD CONSTRAINT `FKfhpg336e0uy7mnr8d7l0h4roi` FOREIGN KEY (`shipping_address_id`) REFERENCES `customer_addresses` (`address_id`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `returns`
--
ALTER TABLE `returns`
  ADD CONSTRAINT `returns_ibfk_1` FOREIGN KEY (`original_transaction_id`) REFERENCES `transactions` (`transaction_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `returns_ibfk_2` FOREIGN KEY (`return_transaction_id`) REFERENCES `transactions` (`transaction_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `returns_ibfk_3` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
