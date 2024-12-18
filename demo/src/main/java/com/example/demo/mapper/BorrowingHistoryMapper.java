package com.example.demo.mapper;

import com.example.demo.model.BorrowingHistory;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BorrowingHistoryMapper {
    @Select("SELECT * FROM borrowing_history WHERE history_id = #{historyId}")
    BorrowingHistory findById(Long historyId);

    @Select("SELECT * FROM borrowing_history WHERE book_id = #{bookId}")
    List<BorrowingHistory> findByBookId(Long bookId);

    @Select("SELECT * FROM borrowing_history WHERE user_id = #{userId}")
    List<BorrowingHistory> findByUserId(Long userId);

    @Select("SELECT * FROM borrowing_history WHERE status = #{status}")
    List<BorrowingHistory> findByStatus(Integer status);

    @Results({
        @Result(property = "historyId", column = "history_id"),
        @Result(property = "bookId", column = "book_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "book.title", column = "book_title"),
        @Result(property = "book.author", column = "book_author"),
        @Result(property = "book.isbn", column = "book_isbn"),
        @Result(property = "user.username", column = "user_username"),
        @Result(property = "user.displayName", column = "user_display_name")
    })
    @Select("SELECT bh.*, " +
            "b.title as book_title, b.author as book_author, b.isbn as book_isbn, " +
            "u.username as user_username, u.display_name as user_display_name " +
            "FROM borrowing_history bh " +
            "INNER JOIN books b ON bh.book_id = b.book_id " +
            "INNER JOIN users u ON bh.user_id = u.user_id " +
            "ORDER BY bh.borrowed_date DESC")
    List<BorrowingHistory> findAllWithDetails();

    @Results({
        @Result(property = "historyId", column = "history_id"),
        @Result(property = "bookId", column = "book_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "book.title", column = "book_title"),
        @Result(property = "book.author", column = "book_author"),
        @Result(property = "book.isbn", column = "book_isbn"),
        @Result(property = "user.username", column = "user_username"),
        @Result(property = "user.displayName", column = "user_display_name")
    })
    @Select("SELECT bh.*, " +
            "b.title as book_title, b.author as book_author, b.isbn as book_isbn, " +
            "u.username as user_username, u.display_name as user_display_name " +
            "FROM borrowing_history bh " +
            "INNER JOIN books b ON bh.book_id = b.book_id " +
            "INNER JOIN users u ON bh.user_id = u.user_id " +
            "WHERE (#{status} IS NULL OR bh.status = #{status}) " +
            "AND (#{startDate} IS NULL OR bh.borrowed_date >= #{startDate}) " +
            "AND (#{keyword} IS NULL OR " +
            "    LOWER(b.title) LIKE LOWER(#{keyword}) OR " +
            "    LOWER(b.author) LIKE LOWER(#{keyword}) OR " +
            "    LOWER(b.isbn) LIKE LOWER(#{keyword})) " +
            "ORDER BY bh.borrowed_date DESC")
    List<BorrowingHistory> findAllWithFilters(
        @Param("status") Integer status,
        @Param("startDate") LocalDate startDate,
        @Param("keyword") String keyword
    );

    @Results({
        @Result(property = "historyId", column = "history_id"),
        @Result(property = "bookId", column = "book_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "book.title", column = "book_title"),
        @Result(property = "book.author", column = "book_author"),
        @Result(property = "book.isbn", column = "book_isbn"),
        @Result(property = "user.username", column = "user_username"),
        @Result(property = "user.displayName", column = "user_display_name")
    })
    @Select("SELECT bh.*, " +
            "b.title as book_title, b.author as book_author, b.isbn as book_isbn, " +
            "u.username as user_username, u.display_name as user_display_name " +
            "FROM borrowing_history bh " +
            "INNER JOIN books b ON bh.book_id = b.book_id " +
            "INNER JOIN users u ON bh.user_id = u.user_id " +
            "WHERE (#{status} IS NULL OR bh.status = #{status}) " +
            "AND (#{startDate} IS NULL OR bh.borrowed_date >= #{startDate}) " +
            "AND (#{keyword} IS NULL OR " +
            "    LOWER(b.title) LIKE LOWER(#{keyword}) OR " +
            "    LOWER(b.author) LIKE LOWER(#{keyword}) OR " +
            "    LOWER(b.isbn) LIKE LOWER(#{keyword})) " +
            "ORDER BY bh.borrowed_date DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<BorrowingHistory> findAllWithFiltersPaged(
        @Param("status") Integer status,
        @Param("startDate") LocalDate startDate,
        @Param("keyword") String keyword,
        @Param("pageSize") int pageSize,
        @Param("offset") int offset
    );

    @Select("SELECT COUNT(*) FROM borrowing_history bh " +
            "INNER JOIN books b ON bh.book_id = b.book_id " +
            "WHERE (#{status} IS NULL OR bh.status = #{status}) " +
            "AND (#{startDate} IS NULL OR bh.borrowed_date >= #{startDate}) " +
            "AND (#{keyword} IS NULL OR " +
            "    LOWER(b.title) LIKE LOWER(#{keyword}) OR " +
            "    LOWER(b.author) LIKE LOWER(#{keyword}) OR " +
            "    LOWER(b.isbn) LIKE LOWER(#{keyword}))")
    int countAllWithFilters(
        @Param("status") Integer status,
        @Param("startDate") LocalDate startDate,
        @Param("keyword") String keyword
    );

    @Insert("INSERT INTO borrowing_history (book_id, user_id, borrowed_date, " +
            "expected_return_date, extension_count, status) " +
            "VALUES (#{bookId}, #{userId}, #{borrowedDate}, " +
            "#{expectedReturnDate}, #{extensionCount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "historyId")
    int insert(BorrowingHistory history);

    @Update("UPDATE borrowing_history SET " +
            "returned_date = #{returnedDate}, " +
            "status = #{status}, " +
            "extension_count = 0 " +
            "WHERE history_id = #{historyId} " +
            "AND returned_date IS NULL")
    int updateReturn(@Param("historyId") Long historyId, 
                    @Param("returnedDate") LocalDate returnedDate, 
                    @Param("status") Integer status);

    @Update("UPDATE borrowing_history SET expected_return_date = #{expectedReturnDate}, " +
            "extension_count = extension_count + 1 " +
            "WHERE history_id = #{historyId}")
    int updateExtension(@Param("historyId") Long historyId, 
                       @Param("expectedReturnDate") LocalDate expectedReturnDate);

    @Select("SELECT * FROM borrowing_history WHERE returned_date IS NULL " +
            "AND expected_return_date < #{date}")
    List<BorrowingHistory> findOverdue(LocalDate date);

    @Select("SELECT * FROM borrowing_history WHERE user_id = #{userId} " +
            "AND returned_date IS NULL")
    List<BorrowingHistory> findCurrentBorrowings(Long userId);

    @Select("SELECT COUNT(*) FROM borrowing_history WHERE user_id = #{userId} " +
            "AND returned_date IS NULL")
    int getCurrentBorrowCount(Long userId);

    @Select("SELECT * FROM borrowing_history " +
            "ORDER BY borrowed_date DESC LIMIT #{limit}")
    List<BorrowingHistory> findRecentBorrowings(int limit);

    @Select("SELECT book_id, COUNT(*) as borrow_count " +
            "FROM borrowing_history " +
            "GROUP BY book_id " +
            "ORDER BY borrow_count DESC " +
            "LIMIT #{limit}")
    List<Object[]> findMostBorrowedBooks(int limit);

    @Select("SELECT user_id, COUNT(*) as borrow_count " +
            "FROM borrowing_history " +
            "GROUP BY user_id " +
            "ORDER BY borrow_count DESC " +
            "LIMIT #{limit}")
    List<Object[]> findMostActiveUsers(int limit);
}
