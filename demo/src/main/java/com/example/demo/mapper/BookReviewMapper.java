package com.example.demo.mapper;

import com.example.demo.model.BookReview;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookReviewMapper {
    @Select("SELECT * FROM book_reviews WHERE review_id = #{reviewId}")
    BookReview findById(Long reviewId);

    @Select("SELECT * FROM book_reviews WHERE book_id = #{bookId}")
    List<BookReview> findByBookId(Long bookId);

    @Select("SELECT * FROM book_reviews WHERE user_id = #{userId}")
    List<BookReview> findByUserId(Long userId);

    @Insert("INSERT INTO book_reviews (book_id, user_id, rating, comment) " +
            "VALUES (#{bookId}, #{userId}, #{rating}, #{comment})")
    @Options(useGeneratedKeys = true, keyProperty = "reviewId")
    int insert(BookReview review);

    @Update("UPDATE book_reviews SET rating = #{rating}, comment = #{comment} " +
            "WHERE review_id = #{reviewId}")
    int update(BookReview review);

    @Delete("DELETE FROM book_reviews WHERE review_id = #{reviewId}")
    int delete(Long reviewId);

    @Select("SELECT AVG(rating) FROM book_reviews WHERE book_id = #{bookId}")
    Double getAverageRating(Long bookId);

    @Select("SELECT COUNT(*) FROM book_reviews WHERE book_id = #{bookId}")
    int getReviewCount(Long bookId);

    @Select("SELECT * FROM book_reviews WHERE book_id = #{bookId} " +
            "ORDER BY review_date DESC LIMIT #{limit}")
    List<BookReview> getRecentReviews(@Param("bookId") Long bookId, @Param("limit") int limit);

    @Select("SELECT * FROM book_reviews WHERE rating >= #{minRating}")
    List<BookReview> findByMinimumRating(int minRating);

    @Select("SELECT EXISTS(SELECT 1 FROM book_reviews " +
            "WHERE book_id = #{bookId} AND user_id = #{userId})")
    boolean hasUserReviewed(@Param("bookId") Long bookId, @Param("userId") Long userId);
}
