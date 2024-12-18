package com.example.demo.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Reservation {
    private Long reservationId;
    private Long bookId;
    private Long userId;
    private LocalDateTime reservationDate;
    private Integer status; // 0:予約中, 1:貸出済み, 2:キャンセル

    // 関連エンティティ
    private Book book;
    private User user;
}
