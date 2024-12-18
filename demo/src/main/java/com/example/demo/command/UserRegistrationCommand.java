package com.example.demo.command;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@Profile("command")
public class UserRegistrationCommand implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== ユーザー管理システム ===");
            System.out.println("1: 新規ユーザー登録");
            System.out.println("2: ユーザー更新");
            System.out.println("3: ユーザー削除");
            System.out.println("4: 終了");
            System.out.print("選択してください (1-4): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    registerNewUser(scanner);
                    break;
                case "2":
                    updateUser(scanner);
                    break;
                case "3":
                    deleteUser(scanner);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("無効な選択です。もう一度お試しください。");
            }
        }
    }

    private void registerNewUser(Scanner scanner) {
        User user = new User();

        // ユーザー名の入力
        while (true) {
            System.out.print("ユーザー名を入力してください: ");
            String username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("ユーザー名は必須です");
                continue;
            }
            if (userService.findByUsername(username) != null) {
                System.out.println("このユーザー名は既に使用されています");
                continue;
            }
            user.setUsername(username);
            break;
        }

        // 表示名の入力
        while (true) {
            System.out.print("表示名を入力してください（50文字以内）: ");
            String displayName = scanner.nextLine().trim();
            if (displayName.isEmpty()) {
                System.out.println("表示名は必須です");
                continue;
            }
            if (displayName.length() > 50) {
                System.out.println("表示名は50文字以内で入力してください");
                continue;
            }
            user.setDisplayName(displayName);
            break;
        }

        // メールアドレスの入力
        while (true) {
            System.out.print("メールアドレスを入力してください: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("メールアドレスは必須です");
                continue;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.out.println("有効なメールアドレスを入力してください");
                continue;
            }
            if (userService.findByEmail(email) != null) {
                System.out.println("このメールアドレスは既に使用されています");
                continue;
            }
            user.setEmail(email);
            break;
        }

        // パスワードの入力
        while (true) {
            System.out.print("パスワードを入力してください（8文字以上）: ");
            String password = scanner.nextLine().trim();
            if (password.length() < 8) {
                System.out.println("パスワードは8文字以上である必要があります");
                continue;
            }
            
            System.out.print("パスワードを再入力してください: ");
            String confirmPassword = scanner.nextLine().trim();
            if (!password.equals(confirmPassword)) {
                System.out.println("パスワードが一致しません");
                continue;
            }
            
            user.setPassword(password);
            break;
        }

        // 権限の選択
        while (true) {
            System.out.print("権限を選択してください（1: 一般ユーザー, 2: 管理者）: ");
            String roleChoice = scanner.nextLine().trim();
            if ("1".equals(roleChoice)) {
                user.setRole("ROLE_USER");
                break;
            } else if ("2".equals(roleChoice)) {
                user.setRole("ROLE_ADMIN");
                break;
            }
            System.out.println("1または2を選択してください");
        }

        // デフォルト値の設定
        user.setMaxBorrowCount("ROLE_ADMIN".equals(user.getRole()) ? 10 : 5);
        user.setIsBlocked(false);

        try {
            userService.save(user);
            System.out.println("\n✔ ユーザーの登録が完了しました");
            System.out.println("ユーザー名: " + user.getUsername());
            System.out.println("表示名: " + user.getDisplayName());
            System.out.println("メールアドレス: " + user.getEmail());
            System.out.println("権限: " + ("ROLE_ADMIN".equals(user.getRole()) ? "管理者" : "一般ユーザー"));
        } catch (Exception e) {
            System.out.println("\n❌ ユーザーの登録に失敗しました: " + e.getMessage());
        }
    }

    private void updateUser(Scanner scanner) {
        System.out.print("更新するユーザーのユーザー名を入力してください: ");
        String username = scanner.nextLine().trim();
        
        User user = userService.findByUsername(username);
        if (user == null) {
            System.out.println("指定されたユーザーが見つかりません");
            return;
        }

        System.out.println("\n=== ユーザー情報の更新 ===");
        System.out.println("1: メールアドレスの更新");
        System.out.println("2: パスワードの更新");
        System.out.println("3: 表示名の更新");
        System.out.println("4: 権限の更新");
        System.out.print("更新する項目を選択してください (1-4): ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                while (true) {
                    System.out.print("新しいメールアドレスを入力してください: ");
                    String newEmail = scanner.nextLine().trim();
                    if (newEmail.isEmpty()) {
                        System.out.println("メールアドレスは必須です");
                        continue;
                    }
                    if (!newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                        System.out.println("有効なメールアドレスを入力してください");
                        continue;
                    }
                    User existingUser = userService.findByEmail(newEmail);
                    if (existingUser != null && !existingUser.getUsername().equals(user.getUsername())) {
                        System.out.println("このメールアドレスは既に使用されています");
                        continue;
                    }
                    user.setEmail(newEmail);
                    break;
                }
                break;

            case "2":
                while (true) {
                    System.out.print("新しいパスワードを入力してください（8文字以上）: ");
                    String newPassword = scanner.nextLine().trim();
                    if (newPassword.length() < 8) {
                        System.out.println("パスワードは8文字以上である必要があります");
                        continue;
                    }
                    
                    System.out.print("新しいパスワードを再入力してください: ");
                    String confirmPassword = scanner.nextLine().trim();
                    if (!newPassword.equals(confirmPassword)) {
                        System.out.println("パスワードが一致しません");
                        continue;
                    }
                    
                    user.setPassword(newPassword);
                    break;
                }
                break;

            case "3":
                while (true) {
                    System.out.print("新しい表示名を入力してください（50文字以内）: ");
                    String newDisplayName = scanner.nextLine().trim();
                    if (newDisplayName.isEmpty()) {
                        System.out.println("表示名は必須です");
                        continue;
                    }
                    if (newDisplayName.length() > 50) {
                        System.out.println("表示名は50文字以内で入力してください");
                        continue;
                    }
                    user.setDisplayName(newDisplayName);
                    break;
                }
                break;

            case "4":
                while (true) {
                    System.out.print("新しい権限を選択してください（1: 一般ユーザー, 2: 管理者）: ");
                    String roleChoice = scanner.nextLine().trim();
                    if ("1".equals(roleChoice)) {
                        user.setRole("ROLE_USER");
                        user.setMaxBorrowCount(5);
                        break;
                    } else if ("2".equals(roleChoice)) {
                        user.setRole("ROLE_ADMIN");
                        user.setMaxBorrowCount(10);
                        break;
                    }
                    System.out.println("1または2を選択してください");
                }
                break;

            default:
                System.out.println("無効な選択です");
                return;
        }

        try {
            userService.save(user);
            System.out.println("\n✔ ユーザー情報の更新が完了しました");
        } catch (Exception e) {
            System.out.println("\n❌ ユーザー情報の更新に失敗しました: " + e.getMessage());
        }
    }

    private void deleteUser(Scanner scanner) {
        System.out.print("削除するユーザーのユーザー名を入力してください: ");
        String username = scanner.nextLine().trim();
        
        User user = userService.findByUsername(username);
        if (user == null) {
            System.out.println("指定されたユーザーが見つかりません");
            return;
        }

        System.out.println("\nユーザー情報:");
        System.out.println("ユーザー名: " + user.getUsername());
        System.out.println("表示名: " + user.getDisplayName());
        System.out.println("メールアドレス: " + user.getEmail());
        System.out.println("権限: " + ("ROLE_ADMIN".equals(user.getRole()) ? "管理者" : "一般ユーザー"));

        System.out.print("\n本当にこのユーザーを削除しますか？ (y/n): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if ("y".equals(confirmation)) {
            try {
                userService.delete(user.getUserId());
                System.out.println("\n✔ ユーザーの削除が完了しました");
            } catch (Exception e) {
                System.out.println("\n❌ ユーザーの削除に失敗しました: " + e.getMessage());
            }
        } else {
            System.out.println("\n削除をキャンセルしました");
        }
    }
}
