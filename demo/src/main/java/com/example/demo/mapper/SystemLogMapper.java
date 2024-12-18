package com.example.demo.mapper;

import com.example.demo.model.SystemLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SystemLogMapper {
    @Select("SELECT * FROM system_logs WHERE log_id = #{logId}")
    SystemLog findById(Long logId);

    @Select("SELECT * FROM system_logs WHERE user_id = #{userId}")
    List<SystemLog> findByUserId(Long userId);

    @Select("SELECT * FROM system_logs WHERE action_type = #{actionType}")
    List<SystemLog> findByActionType(String actionType);

    @Insert("INSERT INTO system_logs (user_id, action_type, action_detail, ip_address) " +
            "VALUES (#{userId}, #{actionType}, #{actionDetail}, #{ipAddress})")
    @Options(useGeneratedKeys = true, keyProperty = "logId")
    int insert(SystemLog log);

    @Delete("DELETE FROM system_logs WHERE log_id = #{logId}")
    int delete(Long logId);

    @Select("SELECT * FROM system_logs " +
            "WHERE created_at BETWEEN #{startDate} AND #{endDate}")
    List<SystemLog> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);

    @Select("SELECT * FROM system_logs " +
            "ORDER BY created_at DESC LIMIT #{limit}")
    List<SystemLog> findRecentLogs(int limit);

    @Select("SELECT * FROM system_logs " +
            "WHERE action_type LIKE '%error%' OR action_type LIKE '%fail%' " +
            "ORDER BY created_at DESC LIMIT #{limit}")
    List<SystemLog> findRecentErrors(int limit);

    @Select("SELECT action_type, COUNT(*) as action_count " +
            "FROM system_logs " +
            "WHERE created_at BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY action_type")
    List<Object[]> getActionStatistics(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    @Select("SELECT user_id, COUNT(*) as action_count " +
            "FROM system_logs " +
            "WHERE created_at BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY user_id " +
            "ORDER BY action_count DESC " +
            "LIMIT #{limit}")
    List<Object[]> getMostActiveUsers(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate, 
                                    @Param("limit") int limit);

    @Delete("DELETE FROM system_logs WHERE created_at < #{date}")
    int deleteOldLogs(LocalDateTime date);
}
