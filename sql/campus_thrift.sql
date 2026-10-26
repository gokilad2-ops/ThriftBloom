-- =====================================================================
-- CAMPUS THRIFT (thrift) — Database Initialization Script
-- For MySQL 8.0+
-- Database: campus_thrift
-- =====================================================================

CREATE DATABASE IF NOT EXISTS campus_thrift
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE campus_thrift;

-- ---------------------------------------------------------------------
-- Table: users
-- Stores student accounts with secure PBKDF2-HMAC-SHA256 password hashes
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    registration_number VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    department VARCHAR(100) DEFAULT 'General',
    year_of_study VARCHAR(50) DEFAULT '1st Year',
    phone VARCHAR(20) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_regno (registration_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Table: items
-- Stores academic materials, books, kits, and thrift items
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    item_condition VARCHAR(50) NOT NULL,
    listing_type VARCHAR(20) NOT NULL DEFAULT 'SELL',
    price DECIMAL(10,2) DEFAULT 0.00,
    image_path VARCHAR(255) DEFAULT NULL,
    exchange_details TEXT DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_items_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_items_owner (owner_id),
    INDEX idx_items_status (status),
    INDEX idx_items_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Table: item_requests
-- Stores student item requests (SELL/DONATE/EXCHANGE inquiries)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS item_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    requester_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes TEXT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_requests_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT fk_requests_requester FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_req_item (item_id),
    INDEX idx_req_requester (requester_id),
    INDEX idx_req_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- Sample Seed Data
-- Demo Password for all sample users: student123
-- =====================================================================

INSERT IGNORE INTO users (id, name, registration_number, email, password_hash, department, year_of_study, phone) VALUES
(1, 'Aadhya Raman', '2025501042', 'aadhya.raman@campus.edu', '8a2e1f4095bb12a67e914d7a0b3c6e9d:138a20967a5be9c235b2e564db7391ab10531a7c8135be9d0263f253a6db9041', 'ECE', '2nd Year', '+91 98765 43210'),
(2, 'Ishaan Kumar', '2025502186', 'ishaan.k@campus.edu', '8a2e1f4095bb12a67e914d7a0b3c6e9d:138a20967a5be9c235b2e564db7391ab10531a7c8135be9d0263f253a6db9041', 'CSE', '3rd Year', '+91 91234 56780'),
(3, 'Meera S.', '2025503077', 'meera.s@campus.edu', '8a2e1f4095bb12a67e914d7a0b3c6e9d:138a20967a5be9c235b2e564db7391ab10531a7c8135be9d0263f253a6db9041', 'Mathematics', 'All Years', '+91 90000 12345'),
(4, 'Kavin Raj', '2025500916', 'kavin.r@campus.edu', '8a2e1f4095bb12a67e914d7a0b3c6e9d:138a20967a5be9c235b2e564db7391ab10531a7c8135be9d0263f253a6db9041', 'Mechanical', '2nd Year', '+91 94444 33221');

INSERT IGNORE INTO items (id, owner_id, name, description, category, item_condition, listing_type, price, exchange_details, status) VALUES
(1, 1, 'Engineering Drawing Kit', 'A complete drawing kit with compass, set squares, mini-drafter clips, and all essentials. In great shape!', 'Study Materials', 'Good', 'SELL', 300.00, NULL, 'AVAILABLE'),
(2, 4, 'Scientific Calculator (Casio fx-991EX)', 'Reliable scientific calculator with high-resolution LCD. Perfect for math and engineering coursework.', 'Electronics', 'Very good', 'SELL', 450.00, NULL, 'AVAILABLE'),
(3, 3, 'Lab Coat — Clean & Freshly Washed', 'White cotton lab coat (Medium size), washed and pressed. Free for any junior who needs it for practicals.', 'Lab Equipment', 'Good', 'DONATE', 0.00, NULL, 'AVAILABLE'),
(4, 2, 'DSA in Java — Textbook & Problem Sets', 'Covers Trees, Graphs, Dynamic Programming, and sorting algorithms. Minimal pencil notes, clean pages.', 'Books', 'Like new', 'SELL', 280.00, NULL, 'AVAILABLE'),
(5, 1, 'Java: The Complete Reference (12th Ed)', 'In depth reference for Java SE fundamentals, Swing, streams, and concurrency.', 'Books', 'Very good', 'EXCHANGE', 0.00, 'Seeking Operating Systems (Galvin) or Computer Networks (Tanenbaum)', 'AVAILABLE'),
(6, 4, 'Record Notebook & Mechanics Study Notes', 'Bound notebook with blank record sheets and solved assignment questions from previous semester.', 'Stationery', 'Good', 'DONATE', 0.00, NULL, 'AVAILABLE'),
(7, 3, 'Warm LED Desk Lamp in Blush Pink', 'Compact flexible desk lamp with warm 3-level dimming. Perfect for hostel dorm tables.', 'Other', 'Like new', 'SELL', 350.00, NULL, 'AVAILABLE');

INSERT IGNORE INTO item_requests (id, item_id, requester_id, status, notes) VALUES
(1, 1, 2, 'PENDING', 'Hi Aadhya, I need this drawing kit for graphics class this semester! Can we meet at the campus library?');
