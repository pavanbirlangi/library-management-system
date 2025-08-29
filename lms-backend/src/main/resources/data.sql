-- data.sql
-- Mock data for Library Management System
-- Passwords are BCrypt hashed version of 'password123'

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Clear existing data in dependency order
DELETE FROM fines;
DELETE FROM reservations;
DELETE FROM loans;
DELETE FROM members;
DELETE FROM books;
DELETE FROM users;

-- Reset auto increment
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE members AUTO_INCREMENT = 1;
ALTER TABLE books AUTO_INCREMENT = 1;
ALTER TABLE loans AUTO_INCREMENT = 1;
ALTER TABLE fines AUTO_INCREMENT = 1;
ALTER TABLE reservations AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- USERS (with hashed passwords)
-- Password for all users: 'password'
-- Note: Email and phone fields are optional during registration
-- =========================  
INSERT INTO users (id, username, password_hash, role, status, created_at, updated_at) VALUES
(1, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'ACTIVE', NOW(), NOW()),
(2, 'librarian1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'LIBRARIAN', 'ACTIVE', NOW(), NOW()),
(3, 'librarian2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'LIBRARIAN', 'ACTIVE', NOW(), NOW()),
(4, 'member1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'MEMBER', 'ACTIVE', NOW(), NOW()),
(5, 'member2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'MEMBER', 'ACTIVE', NOW(), NOW()),
(6, 'member3', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'MEMBER', 'ACTIVE', NOW(), NOW()),
(7, 'member4', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'MEMBER', 'SUSPENDED', NOW(), NOW());

-- =========================
-- MEMBERS (library patrons)
-- =========================
INSERT INTO members (id, user_id, full_name, email, phone, status, joined_at, updated_at) VALUES
(1, 4, 'John Doe', 'john.doe@email.com', '+91-9876543210', 'ACTIVE', NOW(), NOW()),
(2, 5, 'Jane Smith', 'jane.smith@email.com', '+91-9876543211', 'ACTIVE', NOW(), NOW()),
(3, 6, 'Bob Johnson', 'bob.johnson@email.com', '+91-9876543212', 'ACTIVE', NOW(), NOW()),
(4, 7, 'Alice Brown', 'alice.brown@email.com', '+91-9876543213', 'SUSPENDED', NOW(), NOW());

-- =========================
-- BOOKS (catalog)
-- =========================
INSERT INTO books (id, isbn, title, author, category, total_copies, available_copies, created_at, updated_at) VALUES
(1, '978-0-14-143951-8', 'To Kill a Mockingbird', 'Harper Lee', 'Fiction', 5, 3, NOW(), NOW()),
(2, '978-0-7432-7356-5', 'The Great Gatsby', 'F. Scott Fitzgerald', 'Fiction', 4, 2, NOW(), NOW()),
(3, '978-0-452-28423-4', '1984', 'George Orwell', 'Science Fiction', 6, 4, NOW(), NOW()),
(4, '978-0-316-76948-0', 'The Catcher in the Rye', 'J.D. Salinger', 'Fiction', 3, 1, NOW(), NOW()),
(5, '978-0-06-112008-4', 'Where the Crawdads Sing', 'Delia Owens', 'Fiction', 4, 4, NOW(), NOW()),
(6, '978-1-250-30168-0', 'Educated', 'Tara Westover', 'Biography', 3, 2, NOW(), NOW()),
(7, '978-0-525-47895-4', 'Atomic Habits', 'James Clear', 'Self-Help', 5, 5, NOW(), NOW()),
(8, '978-0-13-468599-1', 'Clean Code', 'Robert C. Martin', 'Technology', 4, 3, NOW(), NOW()),
(9, '978-0-321-35668-0', 'Effective Java', 'Joshua Bloch', 'Technology', 3, 2, NOW(), NOW()),
(10, '978-0-596-00968-9', 'Head First Design Patterns', 'Eric Freeman', 'Technology', 2, 1, NOW(), NOW());

-- =========================
-- LOANS (some active, some returned)
-- =========================
INSERT INTO loans (id, book_id, member_id, issued_at, due_at, returned_at, status, issued_by_user_id, returned_by_user_id) VALUES
-- Active loans
(1, 1, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 5 DAY), INTERVAL 14 DAY), NULL, 'ACTIVE', 2, NULL),
(2, 2, 2, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 DAY), INTERVAL 14 DAY), NULL, 'ACTIVE', 2, NULL),
(3, 4, 3, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 7 DAY), INTERVAL 14 DAY), NULL, 'ACTIVE', 3, NULL),
(4, 8, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 14 DAY), NULL, 'ACTIVE', 2, NULL),
(5, 9, 2, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 14 DAY), NULL, 'ACTIVE', 3, NULL),
-- Overdue loan (due yesterday)
(6, 1, 3, DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, 'ACTIVE', 2, NULL),
(7, 10, 2, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), NULL, 'ACTIVE', 2, NULL),
-- Returned loans
(8, 3, 1, DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 'RETURNED', 2, 3),
(9, 6, 2, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), 'RETURNED', 3, 2),
(10, 3, 3, DATE_SUB(NOW(), INTERVAL 35 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY), 'RETURNED', 2, 2);

-- =========================
-- FINES (for overdue books)
-- =========================
INSERT INTO fines (id, loan_id, member_id, amount, status, calculated_at, settled_at, settled_by_user_id, payment_method, payment_ref) VALUES
-- Pending fines
(1, 6, 3, 20.00, 'PENDING', NOW(), NULL, NULL, NULL, NULL),
(2, 7, 2, 60.00, 'PENDING', NOW(), NULL, NULL, NULL, NULL),
-- Settled fine
(3, 8, 1, 10.00, 'SETTLED', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), 2, 'CASH', 'TXN001');

-- =========================
-- RESERVATIONS (queue for unavailable books)
-- =========================
INSERT INTO reservations (id, book_id, member_id, status, queue_position, created_at, updated_at) VALUES
-- Active reservations
(1, 2, 1, 'ACTIVE', 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 4, 2, 'ACTIVE', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 10, 1, 'ACTIVE', 1, NOW(), NOW()),
-- Fulfilled reservation
(4, 1, 2, 'FULFILLED', 1, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

-- Final consistency check
SET FOREIGN_KEY_CHECKS = 1;
