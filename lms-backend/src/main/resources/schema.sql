-- schema.sql
-- Library Management System (LMS) – MySQL 8+
-- Single source of truth for users, members, books, loans, fines, reservations.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Drop in dependency order (children → parents)
DROP TABLE IF EXISTS fines;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- USERS (auth + roles)
-- =========================
CREATE TABLE users (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  username        VARCHAR(100) NOT NULL UNIQUE,
  password_hash   VARCHAR(255) NOT NULL,
  role            ENUM('ADMIN','LIBRARIAN','MEMBER') NOT NULL,
  status          ENUM('ACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- MEMBERS (library patrons)
-- 1:1 with users WHEN role = MEMBER
-- =========================
CREATE TABLE members (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT NOT NULL UNIQUE,
  full_name       VARCHAR(150) NOT NULL,
  email           VARCHAR(150),
  phone           VARCHAR(25),
  status          ENUM('ACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
  joined_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_members_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- BOOKS (catalog)
-- =========================
CREATE TABLE books (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  isbn             VARCHAR(20) UNIQUE,
  title            VARCHAR(200) NOT NULL,
  author           VARCHAR(150) NOT NULL,
  category         VARCHAR(100),
  total_copies     INT UNSIGNED NOT NULL DEFAULT 0,
  available_copies INT UNSIGNED NOT NULL DEFAULT 0,
  created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_books_title (title),
  INDEX idx_books_author (author),
  INDEX idx_books_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- LOANS (issue/return + due)
-- =========================
CREATE TABLE loans (
  id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
  book_id              BIGINT NOT NULL,
  member_id            BIGINT NOT NULL,
  issued_at            DATETIME NOT NULL,
  due_at               DATETIME NOT NULL,
  returned_at          DATETIME NULL,
  status               ENUM('ACTIVE','RETURNED') NOT NULL DEFAULT 'ACTIVE',
  issued_by_user_id    BIGINT NULL,
  returned_by_user_id  BIGINT NULL,
  CONSTRAINT fk_loans_book
    FOREIGN KEY (book_id) REFERENCES books(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_loans_member
    FOREIGN KEY (member_id) REFERENCES members(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_loans_issued_by
    FOREIGN KEY (issued_by_user_id) REFERENCES users(id)
    ON UPDATE RESTRICT ON DELETE SET NULL,
  CONSTRAINT fk_loans_returned_by
    FOREIGN KEY (returned_by_user_id) REFERENCES users(id)
    ON UPDATE RESTRICT ON DELETE SET NULL,
  INDEX idx_loans_member (member_id),
  INDEX idx_loans_book (book_id),
  INDEX idx_loans_status (status),
  INDEX idx_loans_member_status (member_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- FINES (created at return if late)
-- Only LIBRARIAN may mark as paid
-- =========================
CREATE TABLE fines (
  id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
  loan_id              BIGINT NOT NULL,
  member_id            BIGINT NOT NULL,
  amount               DECIMAL(10,2) NOT NULL,
  status               ENUM('PENDING','SETTLED') NOT NULL DEFAULT 'PENDING',
  calculated_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  settled_at           DATETIME NULL,
  settled_by_user_id   BIGINT NULL,
  payment_method       VARCHAR(30) NULL,     -- e.g., CASH/CARD/UPI
  payment_ref          VARCHAR(100) NULL,    -- optional reference/txn id
  CONSTRAINT fk_fines_loan
    FOREIGN KEY (loan_id) REFERENCES loans(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_fines_member
    FOREIGN KEY (member_id) REFERENCES members(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_fines_settled_by
    FOREIGN KEY (settled_by_user_id) REFERENCES users(id)
    ON UPDATE RESTRICT ON DELETE SET NULL,
  INDEX idx_fines_member (member_id),
  INDEX idx_fines_status (status),
  INDEX idx_fines_member_status (member_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- RESERVATIONS (queue for unavailable books)
-- =========================
CREATE TABLE reservations (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  book_id         BIGINT NOT NULL,
  member_id       BIGINT NOT NULL,
  status          ENUM('ACTIVE','CANCELLED','FULFILLED','EXPIRED') NOT NULL DEFAULT 'ACTIVE',
  queue_position  INT UNSIGNED NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_reservations_book
    FOREIGN KEY (book_id) REFERENCES books(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_reservations_member
    FOREIGN KEY (member_id) REFERENCES members(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  INDEX idx_res_book_status (book_id, status),
  INDEX idx_res_member_status (member_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- (Optional helper: prevent duplicate ACTIVE reservation per member/book)
-- Enforce in service layer; MySQL lacks partial unique indexes.

-- Done.
