package com.example.demo.mapper;

import com.example.demo.model.NotificationSetting;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationSettingMapper {
    @Select("SELECT * FROM notification_settings WHERE setting_id = #{settingId}")
    NotificationSetting findById(Long settingId);

    @Select("SELECT * FROM notification_settings WHERE user_id = #{userId}")
    NotificationSetting findByUserId(Long userId);

    @Insert("INSERT INTO notification_settings (user_id, new_arrival_notify, " +
            "due_date_notify, review_notify) " +
            "VALUES (#{userId}, #{newArrivalNotify}, #{dueDateNotify}, #{reviewNotify})")
    @Options(useGeneratedKeys = true, keyProperty = "settingId")
    int insert(NotificationSetting setting);

    @Update("UPDATE notification_settings SET " +
            "new_arrival_notify = #{newArrivalNotify}, " +
            "due_date_notify = #{dueDateNotify}, " +
            "review_notify = #{reviewNotify} " +
            "WHERE setting_id = #{settingId}")
    int update(NotificationSetting setting);

    @Delete("DELETE FROM notification_settings WHERE setting_id = #{settingId}")
    int delete(Long settingId);

    @Select("SELECT * FROM notification_settings WHERE new_arrival_notify = true")
    List<NotificationSetting> findUsersWantingNewArrivalNotifications();

    @Select("SELECT * FROM notification_settings WHERE due_date_notify = true")
    List<NotificationSetting> findUsersWantingDueDateNotifications();

    @Select("SELECT * FROM notification_settings WHERE review_notify = true")
    List<NotificationSetting> findUsersWantingReviewNotifications();
}
