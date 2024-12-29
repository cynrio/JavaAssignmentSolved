create DATABASE `maybank-db`;

use `maybank-db`;

CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    short_name VARCHAR(50),
    full_name VARCHAR(100),
    city VARCHAR(50),
    postal_code VARCHAR(20)
);

CREATE TABLE addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    address_line1 VARCHAR(80),
    address_line2 VARCHAR(80),
    address_line3 VARCHAR(80),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);


-- Insert sample customers
INSERT INTO customers (short_name, full_name, city, postal_code) VALUES
('JDOE', 'John Doe', 'Springfield', '12345'),
('JSMITH', 'Jane Smith', 'Shelbyville', '54321'),
('AJOHNSON', 'Alice Johnson', 'Ogdenville', '67890'),
('BBROWN', 'Bob Brown', 'North Haverbrook', '98765');

-- Insert sample addresses for customers
INSERT INTO addresses (customer_id, address_line1, address_line2, address_line3) VALUES
-- Addresses for John Doe
(1, '123 Main St', 'Apt 4B', ''),
(1, '456 Elm St', '', ''),

-- Address for Jane Smith
(2, '789 Oak St', 'Suite 200', ''),

-- Address for Alice Johnson
(3, '321 Pine St', '', ''),

-- Addresses for Bob Brown
(4, '654 Maple St', '', ''),
(4, '987 Birch St', '', '');