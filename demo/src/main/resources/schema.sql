-- テーブルの削除（依存関係の順序に従って削除）
DROP TABLE IF EXISTS borrowing_history;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS book_reviews;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS notification_settings;
DROP TABLE IF EXISTS system_logs;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

-- ユーザーテーブル
-- システムのユーザー情報を管理するテーブル
-- user_id: ユーザーの一意識別子
-- username: ユーザーのログイン名（20文字以内）
-- display_name: ユーザーの表示名（50文字以内）
-- email: ユーザーのメールアドレス（一意）
-- password: ハッシュ化されたパスワード
-- role: ユーザーの役割（ADMIN, USER等）
-- max_borrow_count: 同時に借りられる最大冊数
-- is_blocked: アカウントのブロック状態
-- created_at: アカウント作成日時
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    max_borrow_count INT NOT NULL DEFAULT 5,
    is_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 書籍テーブル
-- 図書館の蔵書情報を管理するテーブル
-- book_id: 書籍の一意識別子
-- isbn: 国際標準図書番号（13桁）
-- title: 書籍のタイトル
-- author: 著者名
-- category: 書籍のカテゴリ
-- publish_date: 出版日
-- price: 価格
-- stock_quantity: 在庫数
-- condition: 本の状態（0:良好, 1:普通, 2:傷あり等）
-- is_ebook: 電子書籍かどうか
-- borrowed_date: 現在の貸出日
-- borrower: 現在の借り手
-- expected_return_date: 予定返却日
-- status: 書籍の状態（0:利用可能, 1:貸出中, 2:予約中等）
-- rating: 平均評価点
-- borrow_count: 累計貸出回数
-- disposal_date: 廃棄日
-- last_inventory_date: 最終棚卸日
-- notes: 備考
CREATE TABLE IF NOT EXISTS books (
    book_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(13),
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    category VARCHAR(50),
    publish_date DATE,
    price INT,
    stock_quantity INT NOT NULL DEFAULT 0,
    condition INT NOT NULL DEFAULT 0,
    is_ebook BOOLEAN NOT NULL DEFAULT FALSE,
    borrowed_date DATE,
    borrower VARCHAR(255),
    expected_return_date DATE,
    status INT NOT NULL DEFAULT 0,
    rating INT,
    borrow_count INT NOT NULL DEFAULT 0,
    disposal_date DATE,
    last_inventory_date DATE,
    notes TEXT
);

-- 書籍レビューテーブル
-- ユーザーによる書籍のレビュー情報を管理するテーブル
-- review_id: レビューの一意識別子
-- book_id: レビュー対象の書籍ID
-- user_id: レビューを投稿したユーザーID
-- rating: 評価点（1-5等）
-- comment: レビューコメント
-- review_date: レビュー投稿日時
CREATE TABLE IF NOT EXISTS book_reviews (
    review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    review_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- お気に入りテーブル
-- ユーザーのお気に入り書籍を管理するテーブル
-- favorite_id: お気に入りの一意識別子
-- user_id: お気に入りを登録したユーザーID
-- book_id: お気に入りに登録された書籍ID
-- added_date: お気に入り登録日時
CREATE TABLE IF NOT EXISTS favorites (
    favorite_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    added_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    UNIQUE (user_id, book_id)
);

-- 通知設定テーブル
-- ユーザーごとの通知設定を管理するテーブル
-- setting_id: 設定の一意識別子
-- user_id: 設定を持つユーザーID
-- new_arrival_notify: 新着図書通知の有効/無効
-- due_date_notify: 返却期限通知の有効/無効
-- review_notify: レビュー関連通知の有効/無効
CREATE TABLE IF NOT EXISTS notification_settings (
    setting_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    new_arrival_notify BOOLEAN NOT NULL DEFAULT TRUE,
    due_date_notify BOOLEAN NOT NULL DEFAULT TRUE,
    review_notify BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE (user_id)
);

-- システムログテーブル
-- システム内で発生した重要な操作のログを記録するテーブル
-- log_id: ログの一意識別子
-- user_id: 操作を行ったユーザーID（NULL可：システム操作の場合）
-- action_type: 操作の種類（LOGIN, CHECKOUT, RETURN等）
-- action_detail: 操作の詳細情報
-- ip_address: 操作元IPアドレス
-- created_at: ログ記録日時
CREATE TABLE IF NOT EXISTS system_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action_type VARCHAR(50) NOT NULL,
    action_detail TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 予約テーブル
-- 書籍の予約情報を管理するテーブル
-- reservation_id: 予約の一意識別子
-- book_id: 予約対象の書籍ID
-- user_id: 予約したユーザーID
-- reservation_date: 予約日時
-- status: 予約状態（0:予約中, 1:貸出可能, 2:キャンセル等）
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reservation_date TIMESTAMP NOT NULL,
    status INT NOT NULL DEFAULT 0,
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 貸出履歴テーブル
-- 書籍の貸出履歴を管理するテーブル
-- history_id: 履歴の一意識別子
-- book_id: 貸出書籍ID
-- user_id: 借り手のユーザーID
-- borrowed_date: 貸出日時
-- expected_return_date: 返却予定日時
-- returned_date: 実際の返却日時
-- extension_count: 貸出期間延長回数
-- status: 貸出状態（0:貸出中, 1:返却済, 2:延滞等）
-- created_at: 記録作成日時
-- updated_at: 記録更新日時
CREATE TABLE IF NOT EXISTS borrowing_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    borrowed_date TIMESTAMP NOT NULL,
    expected_return_date TIMESTAMP NOT NULL,
    returned_date TIMESTAMP,
    extension_count INT NOT NULL DEFAULT 0,
    status INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- インデックス
-- 予約と貸出履歴の検索効率化
CREATE INDEX IF NOT EXISTS idx_reservations_date ON reservations(reservation_date);
CREATE INDEX IF NOT EXISTS idx_borrowing_history_user ON borrowing_history(user_id);
CREATE INDEX IF NOT EXISTS idx_borrowing_history_book ON borrowing_history(book_id);
CREATE INDEX IF NOT EXISTS idx_borrowing_history_status ON borrowing_history(status);
CREATE INDEX IF NOT EXISTS idx_borrowing_history_dates ON borrowing_history(borrowed_date, expected_return_date, returned_date);

-- 追加のインデックス
-- 書籍検索の効率化
CREATE INDEX IF NOT EXISTS idx_books_isbn ON books(isbn);
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_author ON books(author);
CREATE INDEX IF NOT EXISTS idx_books_category ON books(category);
CREATE INDEX IF NOT EXISTS idx_books_status ON books(status);
-- ユーザー検索の効率化
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_display_name ON users(display_name);
-- レビュー検索の効率化
CREATE INDEX IF NOT EXISTS idx_book_reviews_book ON book_reviews(book_id);
CREATE INDEX IF NOT EXISTS idx_book_reviews_user ON book_reviews(user_id);
-- システムログ検索の効率化
CREATE INDEX IF NOT EXISTS idx_system_logs_user ON system_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_system_logs_action ON system_logs(action_type);
CREATE INDEX IF NOT EXISTS idx_system_logs_date ON system_logs(created_at);
