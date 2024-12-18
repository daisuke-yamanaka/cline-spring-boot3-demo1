package com.example.demo.mapper;

import com.example.demo.model.Book;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BookMapper {
    @Select("SELECT * FROM books WHERE book_id = #{bookId}")
    Book findById(Long bookId);

    @Select("SELECT * FROM books WHERE isbn = #{isbn}")
    Book findByIsbn(String isbn);

    @Select("SELECT * FROM books")
    List<Book> findAll();

    @Select("SELECT * FROM books WHERE category = #{category}")
    List<Book> findByCategory(String category);

    @Select("SELECT * FROM books WHERE status = #{status}")
    List<Book> findByStatus(Integer status);

    @Select("SELECT * FROM books WHERE category = #{category} AND status = #{status}")
    List<Book> findByCategoryAndStatus(@Param("category") String category, @Param("status") Integer status);

    @Select("SELECT * FROM books WHERE " +
            "(#{keyword} IS NULL OR #{keyword} = '' OR " +
            "LOWER(TRANSLATE(title, '０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ', " +
            "'0123456789abcdefghijklmnopqrstuvwxyz')) LIKE LOWER(TRANSLATE(#{keyword}, " +
            "'０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ', " +
            "'0123456789abcdefghijklmnopqrstuvwxyz')) " +
            "OR LOWER(TRANSLATE(author, '０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ', " +
            "'0123456789abcdefghijklmnopqrstuvwxyz')) LIKE LOWER(TRANSLATE(#{keyword}, " +
            "'０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ', " +
            "'0123456789abcdefghijklmnopqrstuvwxyz')) " +
            "OR LOWER(TRANSLATE(isbn, '０１２３４５６７８９', '0123456789')) LIKE LOWER(TRANSLATE(#{keyword}, " +
            "'０１２３４５６７８９', '0123456789'))) " +
            "AND (#{category} IS NULL OR category = #{category}) " +
            "AND (#{status} IS NULL OR status = #{status})")
    List<Book> searchBooksWithFilters(
        @Param("keyword") String keyword,
        @Param("category") String category,
        @Param("status") Integer status
    );

    @Select("SELECT * FROM books WHERE " +
            "(#{keyword} IS NULL OR #{keyword} = '' OR " +
            "LOWER(TRANSLATE(title, '０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ', " +
            "'0123456789abcdefghijklmnopqrstuvwxyz')) LIKE LOWER(TRANSLATE(#{keyword}, " +
            "'０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ', " +
            "'0123456789abcdefghijklmnopqrstuvwxyz')) " +
            "OR LOWER(TRANSLATE(author, '０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ', " +
            "'0123456789abcdefghijklmnopqrstuvwxyz')) LIKE LOWER(TRANSLATE(#{keyword}, " +
            "'０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ', " +
            "'0123456789abcdefghijklmnopqrstuvwxyz')) " +
            "OR LOWER(TRANSLATE(isbn, '０１２３４５６７８９', '0123456789')) LIKE LOWER(TRANSLATE(#{keyword}, " +
            "'０１２３４５６７８９', '0123456789'))) " +
            "AND (#{category} IS NULL OR category = #{category}) " +
            "AND (#{status} IS NULL OR status = #{status}) " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<Book> searchBooksWithFiltersPaged(
        @Param("keyword") String keyword,
        @Param("category") String category,
        @Param("status") Integer status,
        @Param("pageSize") int pageSize,
        @Param("offset") int offset
    );

    @Select("SELECT COUNT(*) FROM books WHERE " +
            "(#{keyword} IS NULL OR #{keyword} = '' OR " +
            "LOWER(TRANSLATE(title, '０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ', " +
            "'0123456789abcdefghijklmnopqrstuvwxyz')) LIKE LOWER(TRANSLATE(#{keyword}, " +
            "'０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ', " +
            "'0123456789abcdefghijklmnopqrstuvwxyz')) " +
            "OR LOWER(TRANSLATE(author, '０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ', " +
            "'0123456789abcdefghijklmnopqrstuvwxyz')) LIKE LOWER(TRANSLATE(#{keyword}, " +
            "'０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ', " +
            "'0123456789abcdefghijklmnopqrstuvwxyz')) " +
            "OR LOWER(TRANSLATE(isbn, '０１２３４５６７８９', '0123456789')) LIKE LOWER(TRANSLATE(#{keyword}, " +
            "'０１２３４５６７８９', '0123456789'))) " +
            "AND (#{category} IS NULL OR category = #{category}) " +
            "AND (#{status} IS NULL OR status = #{status})")
    int countBooksWithFilters(
        @Param("keyword") String keyword,
        @Param("category") String category,
        @Param("status") Integer status
    );

    @Select("SELECT * FROM books WHERE borrower = #{username} AND status = 1")
    List<Book> findBorrowedByUser(String username);

    @Select("SELECT b.* FROM books b " +
            "JOIN borrowing_histories bh ON b.book_id = bh.book_id " +
            "WHERE bh.user_id = (SELECT user_id FROM users WHERE username = #{username}) " +
            "AND (#{status} IS NULL OR bh.status = #{status}) " +
            "AND (#{startDate} IS NULL OR bh.borrowed_date >= #{startDate})")
    List<Book> findBorrowedByUserWithFilters(
        @Param("username") String username,
        @Param("status") Integer status,
        @Param("startDate") LocalDate startDate
    );

    @Insert("INSERT INTO books (isbn, title, author, category, publish_date, price, " +
            "stock_quantity, condition, is_ebook, status, rating, borrow_count, " +
            "disposal_date, last_inventory_date, notes) " +
            "VALUES (#{isbn}, #{title}, #{author}, #{category}, #{publishDate}, #{price}, " +
            "#{stockQuantity}, #{condition}, #{isEbook}, #{status}, #{rating}, #{borrowCount}, " +
            "#{disposalDate}, #{lastInventoryDate}, #{notes})")
    @Options(useGeneratedKeys = true, keyProperty = "bookId")
    int insert(Book book);

    @Update("UPDATE books SET isbn = #{isbn}, title = #{title}, author = #{author}, " +
            "category = #{category}, publish_date = #{publishDate}, price = #{price}, " +
            "stock_quantity = #{stockQuantity}, condition = #{condition}, " +
            "is_ebook = #{isEbook}, borrowed_date = #{borrowedDate}, " +
            "borrower = #{borrower}, expected_return_date = #{expectedReturnDate}, " +
            "status = #{status}, rating = #{rating}, borrow_count = #{borrowCount}, " +
            "disposal_date = #{disposalDate}, last_inventory_date = #{lastInventoryDate}, " +
            "notes = #{notes} WHERE book_id = #{bookId}")
    int update(Book book);

    @Delete("DELETE FROM books WHERE book_id = #{bookId}")
    int delete(Long bookId);

    @Select("SELECT * FROM books WHERE expected_return_date <= #{date} AND status = 1")
    List<Book> findOverdueBooks(LocalDate date);

    @Select("SELECT * FROM books WHERE stock_quantity < #{minStock}")
    List<Book> findLowStockBooks(int minStock);

    @Select("SELECT * FROM books WHERE stock_quantity = 0")
    List<Book> findOutOfStockBooks();

    @Select("SELECT * FROM books ORDER BY borrow_count DESC LIMIT #{limit}")
    List<Book> findMostPopularBooks(int limit);

    @Update("UPDATE books SET stock_quantity = stock_quantity - 1 WHERE book_id = #{bookId}")
    int decrementStock(Long bookId);

    @Update("UPDATE books SET stock_quantity = stock_quantity + 1 WHERE book_id = #{bookId}")
    int incrementStock(Long bookId);

    @Update("UPDATE books SET status = #{status}, borrowed_date = #{borrowedDate}, " +
            "borrower = #{borrower}, expected_return_date = #{expectedReturnDate}, " +
            "borrow_count = borrow_count + 1 WHERE book_id = #{bookId}")
    int updateBorrowStatus(@Param("bookId") Long bookId, 
                          @Param("status") Integer status,
                          @Param("borrowedDate") LocalDate borrowedDate,
                          @Param("borrower") String borrower,
                          @Param("expectedReturnDate") LocalDate expectedReturnDate);

    @Select("SELECT * FROM books WHERE last_inventory_date < #{date} OR last_inventory_date IS NULL")
    List<Book> findBooksNeedingInventory(LocalDate date);

    @Select("SELECT user_id FROM users WHERE username = #{username}")
    Long findUserIdByUsername(String username);
}
