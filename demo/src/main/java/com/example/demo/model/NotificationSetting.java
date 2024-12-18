package com.example.demo.model;

import lombok.Data;

@Data
public class NotificationSetting {
    private Long settingId;
    private Long userId;
    private Boolean newArrivalNotify;
    private Boolean dueDateNotify;
    private Boolean reviewNotify;

    // 関連エンティティ
    private User user;
}
