package com.example.demo.service;

import com.example.demo.mapper.BookMapper;
import com.example.demo.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 書籍管理に関するビジネスロジックを提供するサービスクラス
 */
@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    /**
     * 全書籍情報を取得
     * @return 書籍情報のリスト
     */
    public List<Book> getAllBooks() {
        return bookMapper.findAll();
    }

    /**
     * 指定されたIDの書籍情報を取得
     * @param bookId 書籍ID
     * @return 書籍情報
     * @throws IllegalArgumentException 書籍が存在しない場合
     */
    public Book getBookById(Long bookId) {
        Book book = bookMapper.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("指定された書籍が見つかりません：" + bookId);
        }
        return book;
    }

    /**
     * 新規書籍を登録
     * @param book 書籍情報
     * @return 登録された書籍情報
     */
    @Transactional
    public Book createBook(Book book) {
        validateBook(book);
        bookMapper.insert(book);
        return book;
    }

    /**
     * 書籍情報を更新
     * @param book 書籍情報
     * @return 更新された書籍情報
     */
    @Transactional
    public Book updateBook(Book book) {
        validateBook(book);
        if (bookMapper.update(book) == 0) {
            throw new IllegalArgumentException("指定された書籍が見つかりません：" + book.getBookId());
        }
        return book;
    }

    /**
     * 書籍を削除
     * @param bookId 書籍ID
     * @throws IllegalArgumentException 書籍が存在しない場合
     */
    @Transactional
    public void deleteBook(Long bookId) {
        if (bookMapper.delete(bookId) == 0) {
            throw new IllegalArgumentException("指定された書籍が見つかりません：" + bookId);
        }
    }

    /**
     * 書籍を貸し出し
     * @param book 貸出情報を含む書籍情報
     * @return 更新された書籍情報
     */
    @Transactional
    public Book borrowBook(Book book) {
        if (bookMapper.borrow(book) == 0) {
            throw new IllegalStateException("書籍の貸出に失敗しました。既に貸出中の可能性があります：" + book.getBookId());
        }
        return book;
    }

    /**
     * 書籍を返却
     * @param bookId 書籍ID
     */
    @Transactional
    public void returnBook(Long bookId) {
        if (bookMapper.returnBook(bookId) == 0) {
            throw new IllegalStateException("書籍の返却に失敗しました。貸出中でない可能性があります：" + bookId);
        }
    }

    /**
     * 書籍情報の入力値検証
     * @param book 検証対象の書籍情報
     * @throws IllegalArgumentException 入力値が不正な場合
     */
    private void validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須です");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("著者は必須です");
        }
        if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("カテゴリは必須です");
        }
        if (book.getPublishDate() == null) {
            throw new IllegalArgumentException("出版日は必須です");
        }
        if (book.getPrice() == null || book.getPrice() < 0) {
            throw new IllegalArgumentException("価格は0以上の値を指定してください");
        }
    }
}
