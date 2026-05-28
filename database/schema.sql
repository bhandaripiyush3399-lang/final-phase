-- ============================================================
-- College Ride Share System - Database Schema
-- ============================================================
-- Run this script in MySQL to set up the database.
-- Usage: mysql -u root -p < database/schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS college_rideshare;
USE college_rideshare;

-- -----------------------------------------------------------
-- Table: colleges
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS colleges (
    college_id      INT AUTO_INCREMENT PRIMARY KEY,
    college_name    VARCHAR(200) NOT NULL,
    college_code    VARCHAR(20)  NOT NULL UNIQUE,
    address         VARCHAR(500),
    emergency_contact VARCHAR(20),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Table: users (students)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id         INT AUTO_INCREMENT PRIMARY KEY,
    roll_number     VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    phone           VARCHAR(15),
    college_id      INT NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (college_id) REFERENCES colleges(college_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Table: rides
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS rides (
    ride_id         INT AUTO_INCREMENT PRIMARY KEY,
    provider_id     INT NOT NULL,
    vehicle_type    ENUM('BIKE', 'CAR', 'LIGHT_VEHICLE') NOT NULL,
    route_from      VARCHAR(200) NOT NULL,
    route_to        VARCHAR(200) NOT NULL,
    departure_time  DATETIME NOT NULL,
    total_seats     INT NOT NULL,
    available_seats INT NOT NULL,
    fare            DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status          ENUM('ACTIVE', 'FULL', 'CANCELLED', 'COMPLETED') DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (provider_id) REFERENCES users(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CHECK (available_seats >= 0),
    CHECK (available_seats <= total_seats),
    CHECK (total_seats > 0)
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Table: bookings
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    booking_id      INT AUTO_INCREMENT PRIMARY KEY,
    ride_id         INT NOT NULL,
    seeker_id       INT NOT NULL,
    status          ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
    booking_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ride_id) REFERENCES rides(ride_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (seeker_id) REFERENCES users(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Table: notifications
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    message         VARCHAR(500) NOT NULL,
    type            ENUM('BOOKING_REQUEST', 'RIDE_ACCEPTED', 'RIDE_REJECTED',
                         'RIDE_CANCELLED', 'BOOKING_CANCELLED', 'GENERAL') DEFAULT 'GENERAL',
    is_read         BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Sample Data: Colleges
-- -----------------------------------------------------------
INSERT INTO colleges (college_name, college_code, address, emergency_contact) VALUES
('Pune Institute of Computer Technology', 'PICT', 'Dhankawadi, Pune', '020-24371101'),
('College of Engineering Pune', 'COEP', 'Shivajinagar, Pune', '020-25507000'),
('Vishwakarma Institute of Technology', 'VIT', 'Bibwewadi, Pune', '020-24202124'),
('MIT World Peace University', 'MITWPU', 'Kothrud, Pune', '020-30273400'),
('Symbiosis Institute of Technology', 'SIT', 'Lavale, Pune', '020-39116100');

-- -----------------------------------------------------------
-- Sample Data: Users (password is 'password123' hashed with BCrypt)
-- -----------------------------------------------------------
INSERT INTO users (roll_number, password_hash, full_name, email, phone, college_id) VALUES
('PICT001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Rahul Sharma', 'rahul@pict.edu', '9876543210', 1),
('COEP002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Priya Patil', 'priya@coep.edu', '9876543211', 2),
('VIT003',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Amit Deshmukh', 'amit@vit.edu', '9876543212', 3);
