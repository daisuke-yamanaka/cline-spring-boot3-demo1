package com.example.demo.service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User findById(Long userId) {
        logger.debug("IDによるユーザー検索: {}", userId);
        User user = userMapper.findById(userId);
        if (user == null) {
            logger.debug("ユーザーが見つかりません。ID: {}", userId);
        } else {
            logger.debug("ユーザーが見つかりました。ID: {}, Username: {}", userId, user.getUsername());
        }
        return user;
    }

    public User findByUsername(String username) {
        logger.debug("ユーザー名によるユーザー検索: {}", username);
        User user = userMapper.findByUsername(username);
        if (user == null) {
            logger.debug("ユーザーが見つかりません。Username: {}", username);
        } else {
            logger.debug("ユーザーが見つかりました。Username: {}, Role: {}", username, user.getRole());
        }
        return user;
    }

    public User findByEmail(String email) {
        logger.debug("メールアドレスによるユーザー検索: {}", email);
        return userMapper.findByEmail(email);
    }

    public List<User> findAll() {
        logger.debug("全ユーザーの取得");
        return userMapper.findAll();
    }

    @Transactional
    public int save(User user) {
        logger.debug("ユーザー保存処理開始: {}", user.getUsername());
        if (user.getUserId() == null) {
            // 新規ユーザーの場合
            logger.debug("新規ユーザーの登録: {}", user.getUsername());
            String rawPassword = user.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
            logger.debug("パスワードをハッシュ化しました");
            
            // デフォルト値の設定
            if (user.getRole() == null) {
                user.setRole("ROLE_USER");
            }
            if (user.getMaxBorrowCount() == null) {
                user.setMaxBorrowCount(5);
            }
            if (user.getIsBlocked() == null) {
                user.setIsBlocked(false);
            }
            
            return userMapper.insert(user);
        } else {
            // 既存ユーザーの更新
            logger.debug("既存ユーザーの更新: {}", user.getUsername());
            User existingUser = findById(user.getUserId());
            if (existingUser == null) {
                logger.warn("更新対象のユーザーが見つかりません: {}", user.getUserId());
                return 0;
            }

            // パスワードが指定されていない場合は既存のパスワードを使用
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                logger.debug("パスワードが指定されていないため、既存のパスワードを使用");
                user.setPassword(existingUser.getPassword());
            } else if (!user.getPassword().equals(existingUser.getPassword())) {
                // パスワードが変更されている場合のみハッシュ化
                logger.debug("パスワードの更新を検出");
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            return userMapper.update(user);
        }
    }

    @Transactional
    public int delete(Long userId) {
        logger.debug("ユーザー削除: {}", userId);
        return userMapper.delete(userId);
    }

    @Transactional
    public boolean updateBlockStatus(Long userId, boolean blocked) {
        logger.debug("ユーザーブロック状態の更新: userId={}, blocked={}", userId, blocked);
        return userMapper.updateBlockStatus(userId, blocked) > 0;
    }

    public boolean canBorrow(Long userId) {
        logger.debug("貸出可能状態チェック: {}", userId);
        User user = findById(userId);
        if (user == null || user.getIsBlocked()) {
            logger.debug("貸出不可: ユーザーが存在しないまたはブロックされています。userId={}", userId);
            return false;
        }

        int currentBorrowCount = getCurrentBorrowCount(userId);
        boolean canBorrow = currentBorrowCount < user.getMaxBorrowCount();
        logger.debug("貸出可能判定: userId={}, 現在の貸出数={}, 最大貸出可能数={}, 結果={}", 
                    userId, currentBorrowCount, user.getMaxBorrowCount(), canBorrow);
        return canBorrow;
    }

    public int getCurrentBorrowCount(Long userId) {
        return userMapper.getCurrentBorrowCount(userId);
    }

    public boolean isValidPassword(String rawPassword, String encodedPassword) {
        logger.debug("パスワード検証");
        boolean isValid = passwordEncoder.matches(rawPassword, encodedPassword);
        logger.debug("パスワード検証結果: {}", isValid);
        return isValid;
    }

    @Transactional
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        logger.debug("パスワード変更処理開始: userId={}", userId);
        User user = findById(userId);
        if (user == null) {
            logger.warn("パスワード変更失敗: ユーザーが見つかりません。userId={}", userId);
            return false;
        }

        if (!isValidPassword(currentPassword, user.getPassword())) {
            logger.warn("パスワード変更失敗: 現在のパスワードが一致しません。userId={}", userId);
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        boolean success = userMapper.update(user) > 0;
        logger.debug("パスワード変更完了: userId={}, success={}", userId, success);
        return success;
    }

    @Transactional
    public boolean updateMaxBorrowCount(Long userId, int newMaxBorrowCount) {
        logger.debug("最大貸出数の更新: userId={}, newMaxBorrowCount={}", userId, newMaxBorrowCount);
        User user = findById(userId);
        if (user == null) {
            return false;
        }

        user.setMaxBorrowCount(newMaxBorrowCount);
        return userMapper.update(user) > 0;
    }

    public boolean validateNewUser(User user) {
        logger.debug("新規ユーザーのバリデーション開始: {}", user.getUsername());
        
        if (findByUsername(user.getUsername()) != null) {
            logger.warn("バリデーション失敗: ユーザー名が既に存在します: {}", user.getUsername());
            return false;
        }

        if (findByEmail(user.getEmail()) != null) {
            logger.warn("バリデーション失敗: メールアドレスが既に存在します: {}", user.getEmail());
            return false;
        }

        boolean isValid = user.getUsername() != null && !user.getUsername().trim().isEmpty() &&
                         user.getEmail() != null && !user.getEmail().trim().isEmpty() &&
                         user.getPassword() != null && user.getPassword().length() >= 8;
                         
        logger.debug("バリデーション結果: {}", isValid);
        return isValid;
    }

    // 統計情報取得メソッド
    public int getTotalUsers() {
        logger.debug("総ユーザー数の取得");
        return userMapper.getTotalUsers();
    }

    public int getAdminCount() {
        logger.debug("管理者数の取得");
        return userMapper.getAdminCount();
    }

    public int getActiveUsers() {
        logger.debug("アクティブユーザー数の取得");
        return userMapper.getActiveUsers();
    }

    public int getBlockedUsers() {
        logger.debug("ブロック中ユーザー数の取得");
        return userMapper.getBlockedUsers();
    }
}
