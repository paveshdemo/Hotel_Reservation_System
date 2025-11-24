CREATE DATABASE  IF NOT EXISTS `hotel_reservation_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `hotel_reservation_db`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: hotel_reservation_db
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Temporary view structure for view `available_rooms_view`
--

DROP TABLE IF EXISTS `available_rooms_view`;
/*!50001 DROP VIEW IF EXISTS `available_rooms_view`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `available_rooms_view` AS SELECT 
 1 AS `room_id`,
 1 AS `room_number`,
 1 AS `floor_number`,
 1 AS `price_per_night`,
 1 AS `room_description`,
 1 AS `image_url`,
 1 AS `type_name`,
 1 AS `type_description`,
 1 AS `max_occupancy`,
 1 AS `amenities`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `booking`
--

DROP TABLE IF EXISTS `booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `check_in_date` date NOT NULL,
  `check_out_date` date NOT NULL,
  `status` enum('APPROVED','CANCELLED','PENDING') NOT NULL,
  `room_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq83pan5xy2a6rn0qsl9bckqai` (`room_id`),
  CONSTRAINT `FKq83pan5xy2a6rn0qsl9bckqai` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking`
--

LOCK TABLES `booking` WRITE;
/*!40000 ALTER TABLE `booking` DISABLE KEYS */;
/*!40000 ALTER TABLE `booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `booking_details_view`
--

DROP TABLE IF EXISTS `booking_details_view`;
/*!50001 DROP VIEW IF EXISTS `booking_details_view`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `booking_details_view` AS SELECT 
 1 AS `booking_id`,
 1 AS `booking_reference`,
 1 AS `check_in_date`,
 1 AS `check_out_date`,
 1 AS `number_of_guests`,
 1 AS `total_amount`,
 1 AS `booking_status`,
 1 AS `payment_status`,
 1 AS `customer_name`,
 1 AS `customer_email`,
 1 AS `phone_number`,
 1 AS `room_number`,
 1 AS `room_type`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `booking_id` bigint NOT NULL AUTO_INCREMENT,
  `booking_reference` varchar(20) NOT NULL,
  `customer_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  `check_in_date` date NOT NULL,
  `check_out_date` date NOT NULL,
  `number_of_guests` int NOT NULL,
  `number_of_nights` int NOT NULL,
  `room_price_per_night` decimal(10,2) NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `booking_status` enum('PENDING','PENDING_PAYMENT','CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED','COMPLETED','NO_SHOW','APPROVED') DEFAULT 'PENDING',
  `payment_status` enum('PENDING','COMPLETED','FAILED','REFUNDED','PARTIAL_REFUND') DEFAULT 'PENDING',
  `special_requests` varchar(500) DEFAULT NULL,
  `customer_notes` varchar(1000) DEFAULT NULL,
  `admin_notes` varchar(1000) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cancelled_at` timestamp NULL DEFAULT NULL,
  `cancellation_reason` varchar(500) DEFAULT NULL,
  `qr_code_path` varchar(500) DEFAULT NULL,
  `checked_in_at` datetime(6) DEFAULT NULL,
  `checked_out_at` datetime(6) DEFAULT NULL,
  `discount_amount` decimal(10,2) DEFAULT NULL,
  `original_amount` decimal(10,2) DEFAULT NULL,
  `promo_code` varchar(20) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`booking_id`),
  UNIQUE KEY `booking_reference` (`booking_reference`),
  KEY `idx_booking_dates` (`check_in_date`,`check_out_date`),
  KEY `idx_booking_status` (`booking_status`),
  KEY `idx_customer_bookings` (`customer_id`),
  KEY `idx_room_bookings` (`room_id`),
  KEY `idx_bookings_status_date` (`booking_status`,`created_at`),
  KEY `fk_bookings_user` (`user_id`),
  CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE RESTRICT,
  CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_bookings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `bookings_chk_1` CHECK (((`number_of_guests` >= 1) and (`number_of_guests` <= 10)))
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (1,'BK3476912776',3,11,'2025-08-21','2025-08-23',2,2,18000.00,40320.00,'PENDING_PAYMENT','PENDING','','',NULL,'2025-08-18 16:45:48','2025-08-18 16:45:48',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'BK5089797461',3,2,'2025-08-21','2025-08-22',2,1,12000.00,13440.00,'PENDING_PAYMENT','PENDING','','',NULL,'2025-08-18 16:48:29','2025-08-18 16:48:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'BK9200319284',3,1,'2025-08-21','2025-08-23',1,2,8500.00,19040.00,'CANCELLED','PENDING','','',NULL,'2025-08-19 14:02:00','2025-10-25 19:00:54','2025-10-25 13:30:55',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'BK9321488937',3,1,'2025-08-21','2025-08-23',1,2,8500.00,19040.00,'PENDING_PAYMENT','PENDING','','',NULL,'2025-08-19 14:02:12','2025-08-19 14:02:12',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,'BK5544328696',3,1,'2025-08-21','2025-08-22',1,1,8500.00,9520.00,'PENDING_PAYMENT','PENDING','','',NULL,'2025-08-19 14:29:14','2025-08-19 14:29:14',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,'BK0849928999',3,1,'2025-08-22','2025-08-23',1,1,8500.00,9520.00,'PENDING_PAYMENT','PENDING','','',NULL,'2025-08-20 13:41:25','2025-08-20 13:41:25',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(7,'BK2241726943',3,2,'2025-08-22','2025-08-23',1,1,12000.00,13440.00,'PENDING_PAYMENT','PENDING','','',NULL,'2025-08-20 14:00:24','2025-08-20 14:00:24',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(8,'BK9878633477',3,2,'2025-08-22','2025-08-23',1,1,12000.00,13440.00,'PENDING_PAYMENT','PENDING','','',NULL,'2025-08-20 14:13:08','2025-08-20 14:13:08',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(9,'BK8092243099',3,2,'2025-08-23','2025-08-24',1,1,12000.00,13440.00,'PENDING_PAYMENT','PENDING','','',NULL,'2025-08-20 14:26:49','2025-08-20 14:26:49',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(10,'BK5202573311',3,2,'2025-08-22','2025-08-23',1,1,12000.00,13440.00,'CONFIRMED','COMPLETED','','',NULL,'2025-08-21 10:38:40','2025-08-21 10:39:56',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(13,'BK2127030154',3,2,'2025-08-23','2025-08-24',2,1,12000.00,13440.00,'CANCELLED','REFUNDED','aaasdss','fflf',NULL,'2025-08-21 15:00:13','2025-08-22 00:24:43','2025-08-22 00:24:43','Cancelled by customer',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(14,'BK5222605763',3,2,'2025-09-02','2025-09-03',1,1,12000.00,13440.00,'CONFIRMED','COMPLETED','fff','wdwdd',NULL,'2025-08-31 11:45:22','2025-08-31 11:46:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(15,'BK9230178646',3,1,'2025-09-03','2025-09-04',1,1,8500.00,9520.00,'PENDING_PAYMENT','PENDING','','',NULL,'2025-08-31 22:58:43','2025-08-31 22:58:43',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(16,'BK9230172405',3,1,'2025-09-03','2025-09-04',1,1,8500.00,9520.00,'PENDING_PAYMENT','PENDING','','',NULL,'2025-08-31 22:58:43','2025-08-31 22:58:43',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(17,'BK4229809699',3,2,'2025-09-04','2025-09-05',1,1,12000.00,13440.00,'CANCELLED','COMPLETED','','',NULL,'2025-09-01 11:53:43','2025-10-25 19:02:37','2025-10-25 13:32:37',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(18,'BK1523616489',2,1,'2025-10-03','2025-10-04',1,1,8500.00,9520.00,'CONFIRMED','COMPLETED','ddaass','dddaa',NULL,'2025-09-30 14:25:52','2025-09-30 14:26:54',NULL,NULL,NULL,NULL,NULL,0.00,NULL,NULL,NULL),(19,'BK0013485046',7,3,'2025-10-06','2025-10-08',2,2,12000.00,26880.00,'CANCELLED','PENDING','vswhjwk','mnndkj',NULL,'2025-10-05 08:00:01','2025-10-26 04:10:09','2025-10-25 22:40:09',NULL,NULL,NULL,NULL,0.00,NULL,NULL,NULL),(20,'BK3148775947',7,1,'2025-10-07','2025-10-09',1,2,8500.00,19040.00,'CANCELLED','PENDING','','',NULL,'2025-10-05 08:55:15','2025-10-25 19:02:16','2025-10-25 13:32:16',NULL,NULL,NULL,NULL,0.00,NULL,NULL,NULL),(21,'BK6748157716',7,7,'2025-10-24','2025-10-27',2,3,25000.00,84000.00,'CANCELLED','PENDING','ghg','jkhgfdfg',NULL,'2025-10-21 10:51:15','2025-10-25 19:01:02','2025-10-25 13:31:02',NULL,NULL,NULL,NULL,0.00,NULL,NULL,NULL),(22,'BK1299307378',7,3,'2025-11-22','2025-11-25',2,3,12000.00,40320.00,'PENDING_PAYMENT','PENDING','abcdefgh','abcdefgh',NULL,'2025-11-21 07:15:30','2025-11-21 07:15:30',NULL,NULL,NULL,NULL,NULL,0.00,NULL,NULL,NULL);
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customer_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `address` text,
  `city` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `postal_code` varchar(20) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `id_number` varchar(50) DEFAULT NULL,
  `emergency_contact_name` varchar(100) DEFAULT NULL,
  `emergency_contact_phone` varchar(20) DEFAULT NULL,
  `preferences` json DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`customer_id`),
  KEY `idx_customers_user` (`user_id`),
  CONSTRAINT `customers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,2,'+94771234567','123 Main Street, Colombo','Colombo','Sri Lanka','00100',NULL,NULL,NULL,NULL,NULL,'2025-08-17 19:23:41','2025-08-17 19:23:41'),(2,3,'+94771234567','123 Main Street, Colombo','Colombo','Sri Lanka','10100',NULL,NULL,NULL,NULL,NULL,'2025-08-18 02:35:26','2025-08-18 02:35:26'),(3,4,'+94779393662','No 24/2, Monnankulama, Galgamuwa',NULL,'Sri Lankan',NULL,'2002-02-24','200205500754',NULL,NULL,NULL,'2025-08-18 04:01:15','2025-08-18 04:01:15'),(6,8,'+94779393662','no,24/2, monnankulama,galgamuwa',NULL,'Sri Lankan',NULL,'2007-09-15','200205500754',NULL,NULL,NULL,'2025-09-22 06:48:11','2025-09-22 06:48:11'),(7,10,'+94757809030','D. S Office Front Road Akkaraipattu\r\nampara',NULL,'Sri Lankan',NULL,'2003-08-02','IT24100022',NULL,NULL,NULL,'2025-10-02 23:35:56','2025-10-02 23:35:56');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_read` bit(1) NOT NULL,
  `message` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_read` bit(1) DEFAULT NULL,
  `message` text NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` bigint NOT NULL AUTO_INCREMENT,
  `booking_id` bigint NOT NULL,
  `payment_method` enum('PAYHERE','CREDIT_CARD','DEBIT_CARD','CASH','BANK_TRANSFER') NOT NULL,
  `payment_provider` varchar(50) DEFAULT NULL,
  `transaction_id` varchar(100) DEFAULT NULL,
  `payhere_payment_id` varchar(100) DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL,
  `currency` varchar(3) DEFAULT 'LKR',
  `payment_status` enum('PENDING','PROCESSING','COMPLETED','FAILED','CANCELLED','REFUNDED') DEFAULT 'PENDING',
  `payment_date` timestamp NULL DEFAULT NULL,
  `gateway_response` text,
  `failure_reason` text,
  `refund_amount` decimal(10,2) DEFAULT '0.00',
  `refund_date` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `completed_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `transaction_id` (`transaction_id`),
  KEY `booking_id` (`booking_id`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_transaction_id` (`transaction_id`),
  KEY `idx_payment_date` (`payment_date`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,10,'CREDIT_CARD',NULL,'58feba97-77d9-4ff3-9968-6311b2ac4ce7',NULL,13440.00,'LKR','COMPLETED','2025-08-21 10:39:50',NULL,NULL,0.00,NULL,'2025-08-21 10:39:50','2025-08-21 10:39:50','2025-08-21 16:09:50.032512'),(4,13,'CREDIT_CARD',NULL,'1310ada6-7a27-4ab8-8ee2-b424ea804d66',NULL,13440.00,'LKR','COMPLETED','2025-08-21 15:00:48',NULL,NULL,0.00,NULL,'2025-08-21 15:00:48','2025-08-21 15:00:48','2025-08-21 20:30:47.983349'),(5,14,'CREDIT_CARD',NULL,'f9082d16-aeab-4343-b57f-83cbb9226ddd',NULL,13440.00,'LKR','COMPLETED','2025-08-31 11:46:28',NULL,NULL,0.00,NULL,'2025-08-31 11:46:28','2025-08-31 11:46:28','2025-08-31 17:16:27.912156'),(6,17,'CREDIT_CARD',NULL,'bcaac504-7485-4ee3-a659-46eb06c3db9a',NULL,13440.00,'LKR','COMPLETED','2025-09-01 11:54:05',NULL,NULL,0.00,NULL,'2025-09-01 11:54:05','2025-09-01 11:54:05','2025-09-01 17:24:04.740966'),(7,18,'CREDIT_CARD',NULL,'93deff2d-d97e-421a-9edf-7dfc6215fed7',NULL,9520.00,'LKR','COMPLETED','2025-09-30 14:26:17',NULL,NULL,0.00,NULL,'2025-09-30 14:26:17','2025-09-30 14:26:17','2025-09-30 19:56:16.805593');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotion_usage`
--

DROP TABLE IF EXISTS `promotion_usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promotion_usage` (
  `usage_id` bigint NOT NULL AUTO_INCREMENT,
  `discount_applied` decimal(10,2) NOT NULL,
  `used_at` datetime(6) NOT NULL,
  `booking_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `promotion_id` bigint NOT NULL,
  PRIMARY KEY (`usage_id`),
  KEY `FK9xl425xpylqw9ayx67j0d6bjc` (`booking_id`),
  KEY `FK4x8sa31599c6wj1kqqclu2fnu` (`customer_id`),
  KEY `FKejsynwyybbw6r6eo7w0p6sfa5` (`promotion_id`),
  CONSTRAINT `FK4x8sa31599c6wj1kqqclu2fnu` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `FK9xl425xpylqw9ayx67j0d6bjc` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
  CONSTRAINT `FKejsynwyybbw6r6eo7w0p6sfa5` FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`promotion_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotion_usage`
--

LOCK TABLES `promotion_usage` WRITE;
/*!40000 ALTER TABLE `promotion_usage` DISABLE KEYS */;
/*!40000 ALTER TABLE `promotion_usage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotions`
--

DROP TABLE IF EXISTS `promotions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promotions` (
  `promotion_id` bigint NOT NULL AUTO_INCREMENT,
  `applicable_room_types` json DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `discount_type` enum('FIXED_AMOUNT','PERCENTAGE') NOT NULL,
  `discount_value` decimal(10,2) NOT NULL,
  `end_date` datetime(6) NOT NULL,
  `image_path` varchar(500) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `maximum_discount` decimal(10,2) DEFAULT NULL,
  `minimum_booking_amount` decimal(10,2) DEFAULT NULL,
  `promo_code` varchar(20) NOT NULL,
  `start_date` datetime(6) NOT NULL,
  `terms_conditions` text,
  `title` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `usage_count` int DEFAULT NULL,
  `usage_limit` int DEFAULT NULL,
  `created_by` bigint NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`promotion_id`),
  UNIQUE KEY `UKlb6axxr9obo82kwyavxvj917y` (`promo_code`),
  KEY `FKdmyppdycrsqwl5mikrw105clk` (`created_by`),
  CONSTRAINT `FKdmyppdycrsqwl5mikrw105clk` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `promotions_chk_1` CHECK ((`usage_limit` >= 1))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotions`
--

LOCK TABLES `promotions` WRITE;
/*!40000 ALTER TABLE `promotions` DISABLE KEYS */;
INSERT INTO `promotions` VALUES (2,NULL,'2025-09-30 14:48:41.349232','daaas','PERCENTAGE',20.00,'2025-10-08 14:47:00.000000','/images/promotions/21465a5a-eb7a-4535-a38f-ea8e58811143.jpeg',_binary '',10000.00,10.00,'SUMMER21','2025-10-01 14:47:00.000000','test','Summer special ','2025-09-30 14:48:41.349232',0,20,1,NULL);
/*!40000 ALTER TABLE `promotions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `review_id` bigint NOT NULL AUTO_INCREMENT,
  `admin_response` text,
  `comment` text,
  `created_at` datetime(6) DEFAULT NULL,
  `is_approved` bit(1) DEFAULT NULL,
  `is_verified_stay` bit(1) DEFAULT NULL,
  `rating` int NOT NULL,
  `responded_at` datetime(6) DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `booking_id` bigint DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  PRIMARY KEY (`review_id`),
  KEY `FK28an517hrxtt2bsg93uefugrm` (`booking_id`),
  KEY `FK4sm0k8kw740iyuex3vwwv1etu` (`customer_id`),
  KEY `FKoppowk1pob9qiujo31erx63x1` (`room_id`),
  CONSTRAINT `FK28an517hrxtt2bsg93uefugrm` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
  CONSTRAINT `FK4sm0k8kw740iyuex3vwwv1etu` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `FKoppowk1pob9qiujo31erx63x1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,NULL,'dfdfdf','2025-09-30 22:38:30.743989',_binary '',_binary '',5,NULL,'tttt','2025-09-30 22:48:16.525475',18,2,1);
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `available` bit(1) NOT NULL,
  `price_per_night` decimal(38,2) NOT NULL,
  `room_number` varchar(255) NOT NULL,
  `type` enum('DELUXE','DOUBLE','SINGLE','SUITE') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKfvetq5dj3wcvmdf19bbof0os6` (`room_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_types`
--

DROP TABLE IF EXISTS `room_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_types` (
  `type_id` bigint NOT NULL AUTO_INCREMENT,
  `type_name` varchar(50) NOT NULL,
  `description` text,
  `base_price` decimal(10,2) NOT NULL,
  `max_occupancy` int NOT NULL,
  `amenities` json DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`type_id`),
  UNIQUE KEY `type_name` (`type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_types`
--

LOCK TABLES `room_types` WRITE;
/*!40000 ALTER TABLE `room_types` DISABLE KEYS */;
INSERT INTO `room_types` VALUES (1,'Standard Single','Comfortable single room with essential amenities',8500.00,1,'[\"WiFi\", \"AC\", \"TV\", \"Private Bathroom\"]','2025-08-17 19:23:41','2025-08-17 19:23:41'),(2,'Standard Double','Spacious double room perfect for couples',12000.00,2,'[\"WiFi\", \"AC\", \"TV\", \"Private Bathroom\", \"Mini Fridge\"]','2025-08-17 19:23:41','2025-08-17 19:23:41'),(3,'Deluxe Room','Luxurious room with premium amenities',18000.00,3,'[\"WiFi\", \"AC\", \"TV\", \"Private Bathroom\", \"Mini Fridge\", \"Balcony\", \"Room Service\"]','2025-08-17 19:23:41','2025-08-17 19:23:41'),(4,'Family Suite','Large suite perfect for families',25000.00,4,'[\"WiFi\", \"AC\", \"TV\", \"Private Bathroom\", \"Mini Fridge\", \"Balcony\", \"Room Service\", \"Sofa Bed\"]','2025-08-17 19:23:41','2025-08-17 19:23:41'),(5,'Presidential Suite','Ultimate luxury accommodation',45000.00,6,'[\"WiFi\", \"AC\", \"TV\", \"Private Bathroom\", \"Mini Fridge\", \"Balcony\", \"Room Service\", \"Jacuzzi\", \"Butler Service\"]','2025-08-17 19:23:41','2025-08-17 19:23:41');
/*!40000 ALTER TABLE `room_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `room_id` bigint NOT NULL AUTO_INCREMENT,
  `room_number` varchar(10) NOT NULL,
  `type_id` bigint NOT NULL,
  `floor_number` int NOT NULL,
  `status` enum('AVAILABLE','OCCUPIED','MAINTENANCE','OUT_OF_ORDER') DEFAULT 'AVAILABLE',
  `price_per_night` decimal(10,2) NOT NULL,
  `description` text,
  `amenities` json DEFAULT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`),
  UNIQUE KEY `room_number` (`room_number`),
  KEY `idx_rooms_status` (`status`),
  KEY `idx_rooms_type` (`type_id`),
  KEY `idx_rooms_status_type` (`status`,`type_id`),
  CONSTRAINT `rooms_ibfk_1` FOREIGN KEY (`type_id`) REFERENCES `room_types` (`type_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES (1,'101',1,1,'AVAILABLE',8500.00,'Cozy single room on the ground floor',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(2,'102',2,1,'AVAILABLE',12000.00,'Comfortable double room with garden view',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(3,'103',2,1,'AVAILABLE',12000.00,'Double room near reception',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(4,'104',1,1,'OCCUPIED',8500.00,'Single room with courtyard views',NULL,NULL,'2025-08-17 19:23:41','2025-10-25 13:31:54'),(5,'201',3,2,'AVAILABLE',18000.00,'Deluxe room with city view',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(6,'202',3,2,'OCCUPIED',18000.00,'Deluxe room with balcony',NULL,NULL,'2025-08-17 19:23:41','2025-10-25 12:23:17'),(7,'203',4,2,'AVAILABLE',25000.00,'Family suite with separate living area',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(8,'204',3,2,'MAINTENANCE',18000.00,'Deluxe room under maintenance',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(9,'301',4,3,'AVAILABLE',25000.00,'Spacious family suite with ocean view',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(10,'302',5,3,'AVAILABLE',45000.00,'Presidential suite with panoramic views',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(11,'303',3,3,'AVAILABLE',18000.00,'Deluxe room on top floor',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(12,'304',4,3,'AVAILABLE',25000.00,'Family suite with mountain view',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(13,'401',2,4,'AVAILABLE',12000.00,'Double room with excellent view',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(14,'402',2,4,'AVAILABLE',12000.00,'Corner double room',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(15,'403',1,4,'AVAILABLE',8500.00,'Single room on top floor',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38'),(16,'404',3,4,'AVAILABLE',18000.00,'Deluxe room with premium location',NULL,'/images/room-default.jpg','2025-08-17 19:23:41','2025-09-30 18:55:38');
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `role` varchar(255) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `email_verified` tinyint(1) DEFAULT '0',
  `verification_token` varchar(255) DEFAULT NULL,
  `reset_password_token` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_users_email` (`email`),
  KEY `idx_users_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin@hotel.com','$2a$10$5nfm2mElVp.cVGixqoVdiuJXXzhcP6b1Eplp4IOKnYpLPkLL0dU7C','Hotel','Administrator','ADMIN',1,1,NULL,NULL,'2025-08-17 19:23:41','2025-09-30 01:12:12'),(2,'Batman','bruce@gmail.com','$2a$10$QYx1PsQe5aDAUC9WcS6D4.wDmDPw/VOcJ/vGV0OUbj6XZSUGn81b6','bruce','wane','RECEPTIONIST',1,0,NULL,NULL,'2025-08-17 19:23:41','2025-10-26 00:21:56'),(3,'customer1','customer@hotel.com','$2a$10$a.lyutfEg55Ls5HrHYcQduOXVTLtC6dklXXpdbfQZRf/3BVlLucRS','John','Doe','CUSTOMER',1,1,NULL,NULL,'2025-08-18 02:35:26','2025-08-18 02:35:26'),(4,'hasindu_theekshana','hasindutwm@gmail.com','$2a$10$.Xi92CyOawlRZKlRUbY80.sN5Ujut2wMVPmO8WtWxgyrE8oYTgxla','Hasindu ','Wanninayake','HOUSEKEEPING',1,0,NULL,NULL,'2025-08-18 04:01:15','2025-10-26 05:06:18'),(8,'admin1','admin1@gmail.com','$2a$10$e1wc5u30mFEQeABOM.Ne7u6I8U1VFdasfqtqYsGgiNVLIvHelpPLi','admin','super','ADMIN',1,1,NULL,NULL,'2025-09-22 06:48:11','2025-09-22 12:18:23'),(10,'pavesh','pavesh2003@gmail.com','$2a$10$Zq0KrhYST/0VBePfrw8fCOg1qd1eiunhX3ZsaYI//GNLzxQWPvcGa','Pavesh','Thayalan','CUSTOMER',1,1,NULL,NULL,'2025-10-02 23:35:56','2025-10-02 23:35:56'),(13,'kinukshan','kinu2003@gmail.com','$2a$10$q3/Ogu.9fDdKX8Jkq2w7YOG/6HQcVk6k7eGuR5edp/nOtC2gq/m.i','kinu','kinukshan','MARKETING',1,0,NULL,NULL,'2025-10-03 15:21:58','2025-10-03 15:21:58'),(16,'Stark','tony2025@gmail.com','$2a$10$mntpFdQCIFclrvuI9krvUOKvmRmvg1nuo0LCVXMz61i05IJCdnp2.','Tony','Stark','ACCOUNTANT',1,0,NULL,NULL,'2025-10-04 18:51:02','2025-10-04 18:53:54'),(19,'Razni','razni@gmail.com','$2a$10$VebwTeWXJjAo0wQu5OQyMeOb9PexDCI1G9.y.zkGSKaYp8vnZzc2.','Razni','Ahamed','ACCOUNTANT',1,0,NULL,NULL,'2025-10-06 05:43:26','2025-10-25 12:49:52'),(24,'jeevith','jeevith@gmail.com','$2a$10$gqMBRZzX4GLb29Mdp.x6ZevM9izbbDMj4Rd0FY.r2ySTzxmKZX61e','jeevith','jee','ADMIN',1,0,NULL,NULL,'2025-10-25 13:09:01','2025-10-25 13:09:01'),(25,'receptionist','receptionist@hotel.com','$2a$10$2palXUb.in30/0JmKBs0BeOUNwDWbuBLrPy6OfjBY9IjilC8XWQrG','Sarah','Johnson','RECEPTIONIST',1,1,NULL,NULL,'2025-10-25 13:14:14','2025-10-25 13:14:14'),(27,'demo','demo@gmail.com','$2a$10$XmgBR7rxP7tHqGZj/X59y.tSPJPtgoT9J46phq1zvjgabzAq1.Q6W','demo','user','HOUSEKEEPING',1,0,NULL,NULL,'2025-10-26 00:05:11','2025-10-26 00:05:11');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `available_rooms_view`
--

/*!50001 DROP VIEW IF EXISTS `available_rooms_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `available_rooms_view` AS select `r`.`room_id` AS `room_id`,`r`.`room_number` AS `room_number`,`r`.`floor_number` AS `floor_number`,`r`.`price_per_night` AS `price_per_night`,`r`.`description` AS `room_description`,`r`.`image_url` AS `image_url`,`rt`.`type_name` AS `type_name`,`rt`.`description` AS `type_description`,`rt`.`max_occupancy` AS `max_occupancy`,`rt`.`amenities` AS `amenities` from (`rooms` `r` join `room_types` `rt` on((`r`.`type_id` = `rt`.`type_id`))) where (`r`.`status` = 'AVAILABLE') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `booking_details_view`
--

/*!50001 DROP VIEW IF EXISTS `booking_details_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `booking_details_view` AS select `b`.`booking_id` AS `booking_id`,`b`.`booking_reference` AS `booking_reference`,`b`.`check_in_date` AS `check_in_date`,`b`.`check_out_date` AS `check_out_date`,`b`.`number_of_guests` AS `number_of_guests`,`b`.`total_amount` AS `total_amount`,`b`.`booking_status` AS `booking_status`,`b`.`payment_status` AS `payment_status`,concat(`u`.`first_name`,' ',`u`.`last_name`) AS `customer_name`,`u`.`email` AS `customer_email`,`c`.`phone_number` AS `phone_number`,`r`.`room_number` AS `room_number`,`rt`.`type_name` AS `room_type`,`b`.`created_at` AS `created_at` from ((((`bookings` `b` join `customers` `c` on((`b`.`customer_id` = `c`.`customer_id`))) join `users` `u` on((`c`.`user_id` = `u`.`user_id`))) join `rooms` `r` on((`b`.`room_id` = `r`.`room_id`))) join `room_types` `rt` on((`r`.`type_id` = `rt`.`type_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-21 19:21:46
