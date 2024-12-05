package com.example.demo.model;

import lombok.Data;
import java.time.LocalDate;

/**
 * 書籍情報を管理するエンティティクラス
 */
@Data
public class Book {
    /** 書籍ID */
    private Long bookId;
    
    /** タイトル */
    private String title;
    
    /** 著者 */
    private String author;
    
    /** カテゴリ */
    private String category;
    
    /** 出版日 */
    private LocalDate publishDate;
    
    /** 価格 */
    private Integer price;
    
    /** 貸出日 */
    private LocalDate borrowedDate;
    
    /** 借り手 */
    private String borrower;
    
    /** 返却予定日 */
    private LocalDate expectedReturnDate;
    
    /** ステータス（0:未貸出,1:貸出中） */
    private Integer status;
}
