package com.example.demo.mapper;

import com.example.demo.model.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    @Select("SELECT * FROM favorites WHERE favorite_id = #{favoriteId}")
    Favorite findById(Long favoriteId);

    @Select("SELECT * FROM favorites WHERE user_id = #{userId}")
    List<Favorite> findByUserId(Long userId);

    @Select("SELECT * FROM favorites WHERE book_id = #{bookId}")
    List<Favorite> findByBookId(Long bookId);

    @Insert("INSERT INTO favorites (user_id, book_id) " +
            "VALUES (#{userId}, #{bookId})")
    @Options(useGeneratedKeys = true, keyProperty = "favoriteId")
    int insert(Favorite favorite);

    @Delete("DELETE FROM favorites WHERE favorite_id = #{favoriteId}")
    int delete(Long favoriteId);

    @Delete("DELETE FROM favorites WHERE user_id = #{userId} AND book_id = #{bookId}")
    int deleteByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Select("SELECT EXISTS(SELECT 1 FROM favorites " +
            "WHERE user_id = #{userId} AND book_id = #{bookId})")
    boolean isFavorite(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Select("SELECT COUNT(*) FROM favorites WHERE book_id = #{bookId}")
    int getFavoriteCount(Long bookId);

    @Select("SELECT b.* FROM books b " +
            "JOIN favorites f ON b.book_id = f.book_id " +
            "WHERE f.user_id = #{userId}")
    List<com.example.demo.model.Book> findFavoriteBooks(Long userId);

    @Select("SELECT * FROM favorites " +
            "WHERE user_id = #{userId} " +
            "ORDER BY added_date DESC " +
            "LIMIT #{limit}")
    List<Favorite> findRecentFavorites(@Param("userId") Long userId, 
                                     @Param("limit") int limit);

    @Select("SELECT book_id, COUNT(*) as favorite_count " +
            "FROM favorites " +
            "GROUP BY book_id " +
            "ORDER BY favorite_count DESC " +
            "LIMIT #{limit}")
    List<Object[]> findMostFavoritedBooks(int limit);
}
