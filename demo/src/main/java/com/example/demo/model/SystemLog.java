package com.example.demo.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemLog {
    private Long logId;
    private Long userId;
    private String actionType;
    private String actionDetail;
    private String ipAddress;
    private LocalDateTime createdAt;

    // 関連エンティティ
    private User user;
}
