package com.example.demo.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookReview {
    private Long reviewId;
    private Long bookId;
    private Long userId;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;
    
    // 関連エンティティ
    private Book book;
    private User user;
}
