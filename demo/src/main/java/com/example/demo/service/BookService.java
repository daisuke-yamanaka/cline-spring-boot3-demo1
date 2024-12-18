package com.example.demo.service;

import com.example.demo.mapper.BookMapper;
import com.example.demo.mapper.BorrowingHistoryMapper;
import com.example.demo.model.Book;
import com.example.demo.model.BorrowingHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowingHistoryMapper borrowingHistoryMapper;

    public Book findById(Long bookId) {
        return bookMapper.findById(bookId);
    }

    public Book findByIsbn(String isbn) {
        return bookMapper.findByIsbn(isbn);
    }

    public List<Book> findAll() {
        return bookMapper.findAll();
    }

    public List<Book> findByCategory(String category) {
        return bookMapper.findByCategory(category);
    }

    public List<Book> findByStatus(Integer status) {
        return bookMapper.findByStatus(status);
    }

    public List<Book> findByCategoryAndStatus(String category, Integer status) {
        return bookMapper.findByCategoryAndStatus(category, status);
    }

    public List<Book> findOutOfStockBooks() {
        // status = 1（貸出中）の本を検索
        return bookMapper.findByStatus(1);
    }

    public List<Book> searchBooksWithFilters(String keyword, String category, Integer status) {
        // キーワードがnullまたは空文字の場合は、そのままnullを渡す
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = "%" + keyword.trim() + "%";
        } else {
            keyword = null;
        }
        
        // カテゴリが空文字の場合はnullを設定
        if (category != null && category.trim().isEmpty()) {
            category = null;
        }
        
        return bookMapper.searchBooksWithFilters(keyword, category, status);
    }

    public List<Book> searchBooksWithFiltersPaged(String keyword, String category, Integer status, int page, int pageSize) {
        // キーワードがnullまたは空文字の場合は、そのままnullを渡す
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = "%" + keyword.trim() + "%";
        } else {
            keyword = null;
        }
        
        // カテゴリが空文字の場合はnullを設定
        if (category != null && category.trim().isEmpty()) {
            category = null;
        }
        
        int offset = (page - 1) * pageSize;
        return bookMapper.searchBooksWithFiltersPaged(keyword, category, status, pageSize, offset);
    }

    public int countBooksWithFilters(String keyword, String category, Integer status) {
        // キーワードがnullまたは空文字の場合は、そのままnullを渡す
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = "%" + keyword.trim() + "%";
        } else {
            keyword = null;
        }
        
        // カテゴリが空文字の場合はnullを設定
        if (category != null && category.trim().isEmpty()) {
            category = null;
        }
        
        return bookMapper.countBooksWithFilters(keyword, category, status);
    }

    public List<Book> findBorrowedByUser(String username) {
        return bookMapper.findBorrowedByUser(username);
    }

    public List<Book> findBorrowedByUserWithFilters(String username, Integer status, LocalDate startDate) {
        return bookMapper.findBorrowedByUserWithFilters(username, status, startDate);
    }

    public List<BorrowingHistory> findAllBorrowingHistories() {
        return borrowingHistoryMapper.findAllWithDetails();
    }

    public List<BorrowingHistory> findBorrowingHistoriesWithFilters(String keyword, Integer status, LocalDate startDate) {
        // キーワードがnullまたは空文字の場合は、そのままnullを渡す
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = "%" + keyword.trim() + "%";
        } else {
            keyword = null;
        }
        
        return borrowingHistoryMapper.findAllWithFilters(status, startDate, keyword);
    }

    public List<BorrowingHistory> findBorrowingHistoriesWithFiltersPaged(String keyword, Integer status, LocalDate startDate, int page, int pageSize) {
        // キーワードがnullまたは空文字の場合は、そのままnullを渡す
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = "%" + keyword.trim() + "%";
        } else {
            keyword = null;
        }
        
        int offset = (page - 1) * pageSize;
        return borrowingHistoryMapper.findAllWithFiltersPaged(status, startDate, keyword, pageSize, offset);
    }

    public int countBorrowingHistoriesWithFilters(String keyword, Integer status, LocalDate startDate) {
        // キーワードがnullまたは空文字の場合は、そのままnullを渡す
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = "%" + keyword.trim() + "%";
        } else {
            keyword = null;
        }
        
        return borrowingHistoryMapper.countAllWithFilters(status, startDate, keyword);
    }

    @Transactional
    public int save(Book book) {
        if (book.getBookId() == null) {
            return bookMapper.insert(book);
        } else {
            return bookMapper.update(book);
        }
    }

    @Transactional
    public int delete(Long bookId) {
        return bookMapper.delete(bookId);
    }

    public List<Book> findOverdueBooks() {
        return bookMapper.findOverdueBooks(LocalDate.now());
    }

    public List<Book> findMostPopularBooks(int limit) {
        return bookMapper.findMostPopularBooks(limit);
    }

    @Transactional
    public boolean borrowBook(Long bookId, String borrower, LocalDate expectedReturnDate) {
        Book book = findById(bookId);
        if (book == null || book.getStatus() != 0) {
            return false;
        }

        int borrowResult = bookMapper.updateBorrowStatus(
            bookId, 
            1, // 貸出中ステータス
            LocalDate.now(),
            borrower,
            expectedReturnDate
        );

        if (borrowResult <= 0) {
            return false;
        }

        // 貸出履歴を追加
        BorrowingHistory history = new BorrowingHistory();
        history.setBookId(bookId);
        history.setUserId(getUserIdByUsername(borrower)); // ユーザーIDを取得
        history.setBorrowedDate(LocalDate.now());
        history.setExpectedReturnDate(expectedReturnDate);
        history.setExtensionCount(0);
        history.setStatus(BorrowingHistory.STATUS_BORROWED);

        int historyResult = borrowingHistoryMapper.insert(history);
        if (historyResult <= 0) {
            // 貸出履歴の追加に失敗した場合、トランザクションがロールバックされます
            return false;
        }

        return true;
    }

    @Transactional
    public boolean returnBook(Long bookId) {
        try {
            Book book = findById(bookId);
            if (book == null || book.getStatus() != 1) {
                return false;
            }

            // まず、貸出履歴を取得して更新
            BorrowingHistory history = borrowingHistoryMapper.findByBookId(bookId)
                .stream()
                .filter(h -> h.getReturnedDate() == null)
                .findFirst()
                .orElse(null);

            if (history == null) {
                return false;
            }

            // 貸出履歴を返却済みに更新
            LocalDate now = LocalDate.now();
            int historyResult = borrowingHistoryMapper.updateReturn(
                history.getHistoryId(),
                now,
                BorrowingHistory.STATUS_RETURNED
            );
            
            if (historyResult <= 0) {
                throw new RuntimeException("貸出履歴の更新に失敗しました");
            }

            // 書籍のステータスを更新
            int returnResult = bookMapper.updateBorrowStatus(
                bookId,
                0, // 利用可能ステータス
                null,
                null,
                null
            );
            if (returnResult <= 0) {
                throw new RuntimeException("書籍ステータスの更新に失敗しました");
            }

            return true;
        } catch (Exception e) {
            // 例外が発生した場合、トランザクションは自動的にロールバックされます
            return false;
        }
    }

    public List<Book> findBooksNeedingInventory(LocalDate date) {
        return bookMapper.findBooksNeedingInventory(date);
    }

    @Transactional
    public boolean updateInventory(Long bookId, Integer condition) {
        Book book = findById(bookId);
        if (book == null) {
            return false;
        }

        book.setCondition(condition);
        book.setLastInventoryDate(LocalDate.now());

        return bookMapper.update(book) > 0;
    }

    public boolean isAvailableForBorrowing(Long bookId) {
        Book book = findById(bookId);
        return book != null && book.getStatus() == 0;
    }

    public boolean isOverdue(Book book) {
        if (book.getStatus() != 1 || book.getExpectedReturnDate() == null) {
            return false;
        }
        return LocalDate.now().isAfter(book.getExpectedReturnDate());
    }

    @Transactional
    public boolean extendBorrowing(Long bookId, LocalDate newExpectedReturnDate) {
        Book book = findById(bookId);
        if (book == null || book.getStatus() != 1) {
            return false;
        }

        // 貸出履歴を更新
        BorrowingHistory history = borrowingHistoryMapper.findByBookId(bookId)
            .stream()
            .filter(h -> h.getReturnedDate() == null)
            .findFirst()
            .orElse(null);

        if (history != null) {
            history.setExpectedReturnDate(newExpectedReturnDate);
            history.setStatus(BorrowingHistory.STATUS_EXTENDED);
            borrowingHistoryMapper.updateExtension(history.getHistoryId(), newExpectedReturnDate);
        }

        book.setExpectedReturnDate(newExpectedReturnDate);
        return bookMapper.update(book) > 0;
    }

    @Transactional
    public boolean updateBookCondition(Long bookId, Integer newCondition, String notes) {
        Book book = findById(bookId);
        if (book == null) {
            return false;
        }

        book.setCondition(newCondition);
        book.setNotes(notes);
        return bookMapper.update(book) > 0;
    }

    // ユーザー名からユーザーIDを取得するヘルパーメソッド
    private Long getUserIdByUsername(String username) {
        return bookMapper.findUserIdByUsername(username);
    }
}
