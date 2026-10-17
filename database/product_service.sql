USE user;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS products (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT DEFAULT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO products (id, name, description, price, quantity) VALUES
    (1, 'Bàn phím cơ AKKO 3068B', 'Bàn phím cơ không dây Bluetooth 5.0 / 2.4Ghz', 1650000.00, 20),
    (2, 'Chuột Logitech G Pro X Superlight', 'Chuột chơi game siêu nhẹ 63g, cảm biến HERO 25K', 2990000.00, 15),
    (3, 'Màn hình Dell UltraSharp U2723QE', 'Màn hình 27 inch 4K IPS Black, USB-C 90W', 12490000.00, 8),
    (4, 'Tai nghe Sony WH-1000XM5', 'Tai nghe chống ồn chủ động không dây cao cấp', 7990000.00, 12),
    (5, 'Laptop Apple MacBook Air M2', 'RAM 8GB, SSD 256GB, Màn hình Retina 13.6 inch', 24500000.00, 5)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    quantity = VALUES(quantity);
