-- 外部キー制約を考慮して、関連テーブルのデータを事前にクリア
DELETE FROM book_reviews;
DELETE FROM borrowing_history;
DELETE FROM reservations;
DELETE FROM favorites;
DELETE FROM system_logs;
DELETE FROM notification_settings;

-- サンプル書籍データ
DELETE FROM books;

-- Spring Boot入門（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date) 
SELECT '9784123456789', 'Spring Boot入門', '山田太郎', '技術書', '2023-01-01', 3000, 0, false, 0, 4, 12, CURRENT_DATE
FROM generate_series(1, 2);

-- Java プログラミング（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784567890123', 'Java プログラミング', '鈴木一郎', '技術書', '2023-02-01', 3500, 0, false, 0, 5, 8, CURRENT_DATE
FROM generate_series(1, 2);

-- データベース設計（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784890123456', 'データベース設計', '佐藤花子', '技術書', '2023-03-01', 4000, 0, false, 0, 4, 15, CURRENT_DATE
FROM generate_series(1, 2);

-- Python機械学習入門（1冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784111222333', 'Python機械学習入門', '田中誠', '技術書', '2023-04-15', 3800, 1, true, 0, 5, 25, CURRENT_DATE
FROM generate_series(1, 1);

-- クラウドインフラ構築実践（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784222333444', 'クラウドインフラ構築実践', '高橋実', '技術書', '2023-05-20', 4200, 0, true, 0, 4, 18, CURRENT_DATE
FROM generate_series(1, 2);

-- 月光の森（3冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784333444556', '月光の森', '村上陽子', '小説', '2023-03-10', 1600, 0, false, 0, 5, 30, CURRENT_DATE
FROM generate_series(1, 3);

-- 永遠の約束（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784444555667', '永遠の約束', '木村達也', '小説', '2023-04-01', 1500, 1, false, 0, 4, 22, CURRENT_DATE
FROM generate_series(1, 2);

-- 青い鳥の唄（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784555666778', '青い鳥の唄', '小林美咲', '小説', '2023-05-15', 1800, 0, true, 0, 5, 15, CURRENT_DATE
FROM generate_series(1, 2);

-- 夜明けの街（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784666777889', '夜明けの街', '伊藤隆', '小説', '2023-06-01', 1700, 2, false, 0, 3, 10, CURRENT_DATE
FROM generate_series(1, 2);

-- 紅葉の季節（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784777888990', '紅葉の季節', '中村香', '小説', '2023-06-15', 1900, 0, true, 0, 4, 8, CURRENT_DATE
FROM generate_series(1, 2);

-- ビジネス戦略入門（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784888999000', 'ビジネス戦略入門', '山本一郎', 'ビジネス', '2023-02-15', 2500, 0, false, 0, 4, 20, CURRENT_DATE
FROM generate_series(1, 2);

-- マーケティング実践講座（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784999000111', 'マーケティング実践講座', '鈴木真理', 'ビジネス', '2023-03-20', 2800, 1, true, 0, 5, 28, CURRENT_DATE
FROM generate_series(1, 2);

-- 経営戦略の基礎（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784000111223', '経営戦略の基礎', '佐藤健一', 'ビジネス', '2023-05-01', 3000, 0, true, 0, 4, 12, CURRENT_DATE
FROM generate_series(1, 2);

-- 組織マネジメント入門（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784222333445', '組織マネジメント入門', '高橋道子', 'ビジネス', '2023-05-25', 2700, 1, false, 0, 3, 8, CURRENT_DATE
FROM generate_series(1, 2);

-- 現代物理学の基礎（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784333444557', '現代物理学の基礎', '山田博士', '学術書', '2023-01-20', 5000, 0, false, 0, 5, 10, CURRENT_DATE
FROM generate_series(1, 2);

-- 量子力学入門（1冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784444555668', '量子力学入門', '鈴木教授', '学術書', '2023-02-28', 4800, 0, true, 0, 4, 8, CURRENT_DATE
FROM generate_series(1, 1);

-- 分子生物学の展開（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784555666779', '分子生物学の展開', '田中研究員', '学術書', '2023-03-15', 5200, 1, false, 0, 5, 12, CURRENT_DATE
FROM generate_series(1, 2);

-- 宇宙物理学概論（2冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784666777890', '宇宙物理学概論', '佐藤博士', '学術書', '2023-04-20', 4500, 0, true, 0, 4, 6, CURRENT_DATE
FROM generate_series(1, 2);

-- 進化生物学研究（1冊）
INSERT INTO books (isbn, title, author, category, publish_date, price, condition, is_ebook, status, rating, borrow_count, last_inventory_date)
SELECT '9784777888991', '進化生物学研究', '高橋教授', '学術書', '2023-05-10', 5500, 0, false, 0, 5, 9, CURRENT_DATE
FROM generate_series(1, 1);

-- サンプルユーザーデータ
DELETE FROM users;
INSERT INTO users (username, display_name, email, password, role, max_borrow_count, is_blocked, created_at)
VALUES
('user1', 'ユーザー1', 'user1@example.com', '$2a$10$qC5yEyCaHVgBdzM8Nonutu/myjJsQt6Ks0AfEuNIMa5WpWYfkFPda', 'ROLE_USER', 5, false, CURRENT_TIMESTAMP),
('user2', 'ユーザー2', 'user2@example.com', '$2a$10$qC5yEyCaHVgBdzM8Nonutu/myjJsQt6Ks0AfEuNIMa5WpWYfkFPda', 'ROLE_USER', 5, false, CURRENT_TIMESTAMP),
('admin', '管理者', 'admin@example.com', '$2a$10$qC5yEyCaHVgBdzM8Nonutu/myjJsQt6Ks0AfEuNIMa5WpWYfkFPda', 'ROLE_ADMIN', 10, false, CURRENT_TIMESTAMP);
