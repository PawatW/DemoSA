-- ========================
-- Staff
-- ========================
CREATE TABLE Staff (
    staff_id SERIAL PRIMARY KEY,
    staff_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(100) NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

-- ========================
-- Customer
-- ========================
CREATE TABLE Customer (
    customer_id SERIAL PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(50),
    email VARCHAR(100) UNIQUE
);

-- ========================
-- Supplier
-- ========================
CREATE TABLE Supplier (
    supplier_id SERIAL PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(50),
    email VARCHAR(100) UNIQUE
);

-- ========================
-- Category
-- ========================
CREATE TABLE Category (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
);

-- ========================
-- Product
-- ========================
CREATE TABLE Product (
    product_id SERIAL PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    unit VARCHAR(50),
    price_per_unit DECIMAL(10,2) CHECK (price_per_unit > 0),
    quantity INT DEFAULT 0 CHECK (quantity >= 0),
    supplier_id INT REFERENCES Supplier(supplier_id),
    category_id INT REFERENCES Category(category_id),
    image_url      VARCHAR(255)
);

-- ========================
-- Order
-- ========================
CREATE TABLE "Order" (
    order_id SERIAL PRIMARY KEY,
    order_date DATE NOT NULL,
    total_amount DECIMAL(12,2) DEFAULT 0 CHECK (total_amount >= 0),
    status VARCHAR(50) DEFAULT 'Pending',
    customer_id INT REFERENCES Customer(customer_id),
    staff_id INT REFERENCES Staff(staff_id)
);

-- ========================
-- OrderItem
-- ========================
CREATE TABLE OrderItem (
    order_item_id SERIAL PRIMARY KEY,
    order_id INT REFERENCES "Order"(order_id),
    product_id INT REFERENCES Product(product_id),
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL,
    line_total DECIMAL(12,2) NOT NULL,
    fulfilled_qty INT DEFAULT 0,
    remaining_qty INT GENERATED ALWAYS AS (quantity - fulfilled_qty) STORED
);

-- ========================
-- Request
-- ========================
CREATE TABLE Request (
    request_id SERIAL PRIMARY KEY,
    request_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'Awaiting Approval',
    order_id INT REFERENCES "Order"(order_id),
    customer_id INT REFERENCES Customer(customer_id),
    staff_id INT REFERENCES Staff(staff_id),
    description TEXT,
    approved_by INT REFERENCES Staff(staff_id),
    approved_date TIMESTAMP
);

-- ========================
-- RequestItem
-- ========================
CREATE TABLE RequestItem (
    request_item_id SERIAL PRIMARY KEY,
    request_id INT REFERENCES Request(request_id),
    product_id INT REFERENCES Product(product_id),
    quantity INT NOT NULL CHECK (quantity > 0),
    fulfilled_qty INT DEFAULT 0,
    remaining_qty INT GENERATED ALWAYS AS (quantity - fulfilled_qty) STORED
);

-- ========================
-- StockTransaction
-- ========================
CREATE TABLE StockTransaction (
    transaction_id SERIAL PRIMARY KEY,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type VARCHAR(10) CHECK (type IN ('IN','OUT','ADJUST')),
    product_id INT REFERENCES Product(product_id),
    quantity INT NOT NULL CHECK (quantity > 0),
    staff_id INT REFERENCES Staff(staff_id),
    reference VARCHAR(255)
);


-- ===== SEED DATA (เล็กน้อยสำหรับทดสอบ) =====

INSERT INTO Product (product_name, description, unit, price_per_unit, quantity, supplier_id, category_id, image_url)
VALUES
('Hammer', 'ค้อนเหล็ก 1 ปอนด์', 'pcs', 150.00, 100, 1, 2, 'https://example.com/images/hammer.jpg'),
('Laptop', 'Notebook 15 inch i5', 'pcs', 25000.00, 20, 2, 1, 'https://example.com/images/laptop.png'),
('Printer Ink', 'หมึกพิมพ์สีดำ', 'bottle', 500.00, 50, 1, 3, 'https://example.com/images/ink.jpg');

INSERT INTO Staff (staff_name, role, phone, email, password, active)
VALUES
('Alice Admin', 'admin', '0811111111', 'alice@company.com', '$2a$10$u/9yFjX4EAcY...hashed...', true),
('Bob Foreman', 'foreman', '0822222222', 'bob@company.com', '$2a$10$u/9yFjX4EAcY...hashed...', true),
('Charlie Warehouse', 'warehouse', '0833333333', 'charlie@company.com', '$2a$10$u/9yFjX4EAcY...hashed...', true),
('Tom Tech', 'technician', '0844444444', 'tom@company.com', '$2a$10$u/9yFjX4EAcY...hashed...', true);

INSERT INTO Customer (customer_name, address, phone, email)
VALUES
('บริษัท สมชาย จำกัด', '123 ถนนสุขุมวิท กรุงเทพ', '021234567', 'somchai@example.com'),
('หจก. แสงไทย', '45/7 ถนนเพชรบุรี กรุงเทพ', '026789123', 'sangthai@example.com');

INSERT INTO Supplier (supplier_name, address, phone, email)
VALUES
('Supplier A', 'บางนา กรุงเทพ', '029876543', 'supA@example.com'),
('Supplier B', 'เชียงใหม่', '053111222', 'supB@example.com');

INSERT INTO Category (category_name)
VALUES
('Electronics'),
('Tools'),
('Office Supplies');

INSERT INTO "Order" (order_date, total_amount, status, customer_id, staff_id)
VALUES
(CURRENT_DATE, 25500.00, 'Confirmed', 1, 1);

INSERT INTO OrderItem (order_id, product_id, quantity, unit_price, line_total, fulfilled_qty)
VALUES
(1, 1, 10, 150.00, 1500.00, 0),
(1, 2, 1, 25000.00, 25000.00, 0);

INSERT INTO Request (request_date, status, order_id, customer_id, staff_id, description)
VALUES
(CURRENT_DATE, 'Awaiting Approval', 1, 1, 4, 'เบิกสินค้าเพื่อซ่อมบำรุง');

INSERT INTO RequestItem (request_id, product_id, quantity, fulfilled_qty)
VALUES
(1, 1, 5, 0);
