-- Create the database
CREATE DATABASE IF NOT EXISTS subscription_manager;
USE subscription_manager;

-- Create user and grant privileges
CREATE USER IF NOT EXISTS 'subscription_manager'@'localhost' IDENTIFIED BY 'subscription_manager';
GRANT ALL PRIVILEGES ON subscription_manager.* TO 'subscription_manager'@'localhost';
FLUSH PRIVILEGES;

-- Create the subscriptions table
CREATE TABLE IF NOT EXISTS subscriptions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phonenumber VARCHAR(50) NOT NULL,
    subscription VARCHAR(50),
    services VARCHAR(255)
);

-- Insert dummy data
INSERT INTO subscriptions (firstname, lastname, email, phonenumber, subscription, services) VALUES
('John', 'Doe', 'john.doe@example.com', '1234567890', 'basic', 'ufc,hbo'),
('Jane', 'Smith', 'jane.smith@example.com', '0987654321', 'super', 'netflix'),
('Alice', 'Johnson', 'alice.johnson@example.com', '1122334455', 'lite', 'hbo,netflix'),
('Bob', 'Brown', 'bob.brown@example.com', '5566778899', 'basic', ''),
('Charlie', 'Davis', 'charlie.davis@example.com', '6677889900', 'super', 'ufc,netflix');
