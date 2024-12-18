package com.example.demo.model;

import lombok.Data;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class User {
    private Long userId;

    @NotBlank(message = "ユーザー名は必須です")
    @Size(min = 4, max = 20, message = "ユーザー名は4文字以上20文字以内で入力してください")
    private String username;

    @NotBlank(message = "表示名は必須です")
    @Size(max = 50, message = "表示名は50文字以内で入力してください")
    private String displayName;

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;

    @NotBlank(message = "パスワードは必須です")
    @Size(min = 8, message = "パスワードは8文字以上で入力してください")
    private String password;

    // パスワード確認用フィールド（データベースには保存しない）
    private String confirmPassword;

    private String role;
    private Integer maxBorrowCount;
    private Boolean isBlocked;
    private LocalDateTime createdAt;

    // カスタムバリデーション用のメソッド
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}
