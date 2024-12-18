package com.example.demo.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Long favoriteId;
    private Long userId;
    private Long bookId;
    private LocalDateTime addedDate;

    // 関連エンティティ
    private Book book;
    private User user;
}
