-- 書籍テーブルの作成
CREATE TABLE IF NOT EXISTS books (
    book_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    publish_date DATE NOT NULL,
    price INTEGER NOT NULL,
    borrowed_date DATE,
    borrower VARCHAR(100),
    expected_return_date DATE,
    status INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- インデックスの作成
CREATE INDEX IF NOT EXISTS idx_books_status ON books(status);
CREATE INDEX IF NOT EXISTS idx_books_category ON books(category);
CREATE INDEX IF NOT EXISTS idx_books_author ON books(author);

-- コメント
COMMENT ON TABLE books IS '書籍管理テーブル';
COMMENT ON COLUMN books.book_id IS '書籍ID';
COMMENT ON COLUMN books.title IS 'タイトル';
COMMENT ON COLUMN books.author IS '著者';
COMMENT ON COLUMN books.category IS 'カテゴリ';
COMMENT ON COLUMN books.publish_date IS '出版日';
COMMENT ON COLUMN books.price IS '価格';
COMMENT ON COLUMN books.borrowed_date IS '貸出日';
COMMENT ON COLUMN books.borrower IS '借り手';
COMMENT ON COLUMN books.expected_return_date IS '返却予定日';
COMMENT ON COLUMN books.status IS 'ステータス（0:未貸出,1:貸出中）';
COMMENT ON COLUMN books.created_at IS '作成日時';
COMMENT ON COLUMN books.updated_at IS '更新日時';
