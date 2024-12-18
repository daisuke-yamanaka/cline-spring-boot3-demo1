package com.example.demo.mapper;

import com.example.demo.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    User findById(Long userId);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    @Select("SELECT * FROM users")
    List<User> findAll();

    @Insert("INSERT INTO users (username, display_name, email, password, role, max_borrow_count, is_blocked) " +
            "VALUES (#{username}, #{displayName}, #{email}, #{password}, #{role}, #{maxBorrowCount}, #{isBlocked})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);

    @Update("UPDATE users SET username = #{username}, display_name = #{displayName}, email = #{email}, " +
            "password = #{password}, role = #{role}, max_borrow_count = #{maxBorrowCount}, " +
            "is_blocked = #{isBlocked} WHERE user_id = #{userId}")
    int update(User user);

    @Delete("DELETE FROM users WHERE user_id = #{userId}")
    int delete(Long userId);

    @Select("SELECT COUNT(*) FROM borrowing_history " +
            "WHERE user_id = #{userId} AND returned_date IS NULL")
    int getCurrentBorrowCount(Long userId);

    @Update("UPDATE users SET is_blocked = #{blocked} WHERE user_id = #{userId}")
    int updateBlockStatus(@Param("userId") Long userId, @Param("blocked") boolean blocked);

    // 統計情報用のメソッド
    @Select("SELECT COUNT(*) FROM users")
    int getTotalUsers();

    @Select("SELECT COUNT(*) FROM users WHERE role = 'ROLE_ADMIN'")
    int getAdminCount();

    @Select("SELECT COUNT(*) FROM users WHERE is_blocked = false")
    int getActiveUsers();

    @Select("SELECT COUNT(*) FROM users WHERE is_blocked = true")
    int getBlockedUsers();
}
