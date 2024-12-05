package com.example.demo.mapper;

import com.example.demo.model.Book;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 書籍情報のデータベース操作を行うマッパーインターフェース
 */
@Mapper
public interface BookMapper {
    
    /**
     * 全書籍情報を取得
     * @return 書籍情報のリスト
     */
    @Select("SELECT * FROM books")
    List<Book> findAll();
    
    /**
     * 指定されたIDの書籍情報を取得
     * @param bookId 書籍ID
     * @return 書籍情報
     */
    @Select("SELECT * FROM books WHERE book_id = #{bookId}")
    Book findById(Long bookId);
    
    /**
     * 新規書籍を登録
     * @param book 書籍情報
     * @return 影響を受けた行数
     */
    @Insert("INSERT INTO books (title, author, category, publish_date, price, status) " +
            "VALUES (#{title}, #{author}, #{category}, #{publishDate}, #{price}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "bookId")
    int insert(Book book);
    
    /**
     * 書籍情報を更新
     * @param book 書籍情報
     * @return 影響を受けた行数
     */
    @Update("UPDATE books SET title = #{title}, author = #{author}, category = #{category}, " +
            "publish_date = #{publishDate}, price = #{price} WHERE book_id = #{bookId}")
    int update(Book book);
    
    /**
     * 書籍を削除
     * @param bookId 書籍ID
     * @return 影響を受けた行数
     */
    @Delete("DELETE FROM books WHERE book_id = #{bookId}")
    int delete(Long bookId);
    
    /**
     * 書籍を貸し出し
     * @param book 貸出情報を含む書籍情報
     * @return 影響を受けた行数
     */
    @Update("UPDATE books SET status = 1, borrower = #{borrower}, " +
            "borrowed_date = #{borrowedDate}, expected_return_date = #{expectedReturnDate} " +
            "WHERE book_id = #{bookId} AND status = 0")
    int borrow(Book book);
    
    /**
     * 書籍を返却
     * @param bookId 書籍ID
     * @return 影響を受けた行数
     */
    @Update("UPDATE books SET status = 0, borrower = null, " +
            "borrowed_date = null, expected_return_date = null " +
            "WHERE book_id = #{bookId} AND status = 1")
    int returnBook(Long bookId);
}
