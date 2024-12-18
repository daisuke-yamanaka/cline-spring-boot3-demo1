package com.example.demo.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BorrowingHistory {
    // ステータス定数
    public static final int STATUS_BORROWED = 1;
    public static final int STATUS_RETURNED = 2;
    public static final int STATUS_EXTENDED = 3;
    public static final int STATUS_OVERDUE = 4;

    private Long historyId;
    private Long bookId;
    private Long userId;
    private LocalDate borrowedDate;
    private LocalDate returnedDate;
    private LocalDate expectedReturnDate;
    private Integer extensionCount;
    private Integer status; // 1:BORROWED, 2:RETURNED, 3:EXTENDED, 4:OVERDUE

    // 関連エンティティ
    private Book book;
    private User user;
}
