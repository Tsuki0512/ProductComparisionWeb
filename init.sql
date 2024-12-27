-- 确保使用正确的数据库
CREATE DATABASE IF NOT EXISTS product_comparison;
USE product_comparison;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `uid` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `email` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `jd_cookie` varchar(4000) DEFAULT NULL,
  `tb_cookie` varchar(4000) DEFAULT NULL,
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
  `pid` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `productname` varchar(255) NOT NULL,
  `platform` varchar(100) NOT NULL,
  `current_price` decimal(10,2) NOT NULL,
  `category` varchar(255) DEFAULT NULL,
  `specification` text,
  `barcode` varchar(255) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `historical_prices` varchar(2000) DEFAULT NULL,
  UNIQUE KEY `platform_barcode_unique` (`platform`, `barcode`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 价格追踪表
CREATE TABLE IF NOT EXISTS `price_tracking` (
  `uid` int NOT NULL,
  `pid` int NOT NULL,
  KEY `price_tracking_ibfk_1` (`uid`),
  KEY `price_tracking_product_fk` (`pid`),
  CONSTRAINT `price_tracking_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `price_tracking_product_fk` FOREIGN KEY (`pid`) REFERENCES `product` (`pid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;