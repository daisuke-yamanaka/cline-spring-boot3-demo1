package com.example.demo.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * 書籍情報を管理するエンティティクラス
 */
@Data
public class Book {
    /** 書籍ID */
    private Long bookId;
    
    /** ISBN */
    private String isbn;
    
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
    
    /** 在庫数 */
    private Integer stockQuantity;
    
    /** 書籍の状態（0:新品, 1:良好, 2:傷あり, 3:修理必要） */
    private Integer condition;
    
    /** 電子書籍フラグ */
    private Boolean isEbook;
    
    /** 貸出日 */
    private LocalDate borrowedDate;
    
    /** 借り手 */
    private String borrower;
    
    /** 返却予定日 */
    private LocalDate expectedReturnDate;
    
    /** ステータス（0:未貸出,1:貸出中,2:予約あり,3:メンテナンス中） */
    private Integer status;
    
    /** 評価点（5段階） */
    private Integer rating;
    
    /** 貸出回数 */
    private Integer borrowCount;
    
    /** 廃棄予定日 */
    private LocalDate disposalDate;
    
    /** 最終棚卸日 */
    private LocalDate lastInventoryDate;
    
    /** 備考 */
    private String notes;
}
