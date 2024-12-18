package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        logger.debug("ログインページにアクセスしました");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            logger.debug("既に認証済みのユーザー: {}", auth.getName());
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.debug("新規登録ページにアクセスしました");
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.debug("新規ユーザー登録処理開始: {}", user.getUsername());

        // パスワードの確認チェック
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            bindingResult.addError(new FieldError("user", "confirmPassword", 
                "パスワードが一致しません"));
            logger.warn("パスワード不一致エラー");
        }

        // バリデーションエラーがある場合
        if (bindingResult.hasErrors()) {
            logger.warn("バリデーションエラー: {}", bindingResult.getAllErrors());
            return "register";
        }

        // ユーザー名とメールアドレスの重複チェック
        if (!userService.validateNewUser(user)) {
            model.addAttribute("error", "ユーザー名またはメールアドレスが既に使用されています");
            logger.warn("重複エラー: username={}, email={}", user.getUsername(), user.getEmail());
            return "register";
        }

        try {
            // デフォルト設定
            user.setRole("ROLE_USER");
            user.setMaxBorrowCount(5);
            user.setIsBlocked(false);

            // ユーザー登録
            userService.save(user);
            logger.info("ユーザー登録成功: {}", user.getUsername());
            
            // 登録成功メッセージをセット
            redirectAttributes.addFlashAttribute("success", 
                "アカウントが正常に作成されました。ログインしてください。");
            
            return "redirect:/login";
            
        } catch (Exception e) {
            logger.error("ユーザー登録エラー", e);
            model.addAttribute("error", "アカウント作成中にエラーが発生しました");
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("ログアウト処理開始");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            logger.info("ログアウト成功: {}", auth.getName());
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        logger.warn("アクセス拒否ページにアクセスしました");
        return "error/access-denied";
    }

    // デバッグ用エンドポイント
    @GetMapping("/api/auth/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> status = new HashMap<>();
        
        if (auth != null) {
            status.put("authenticated", auth.isAuthenticated());
            status.put("principal", auth.getPrincipal());
            status.put("authorities", auth.getAuthorities());
            status.put("details", auth.getDetails());
            status.put("name", auth.getName());
            
            if (!auth.getName().equals("anonymousUser")) {
                User user = userService.findByUsername(auth.getName());
                if (user != null) {
                    status.put("user", user);
                }
            }
        } else {
            status.put("authenticated", false);
        }
        
        return ResponseEntity.ok(status);
    }

    // パスワードハッシュ確認用エンドポイント（開発環境のみ）
    @GetMapping("/api/auth/check-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkPassword(
            @RequestParam String username,
            @RequestParam String password) {
        
        User user = userService.findByUsername(username);
        Map<String, Object> result = new HashMap<>();
        
        if (user != null) {
            result.put("found", true);
            result.put("storedHash", user.getPassword());
            result.put("matches", userService.isValidPassword(password, user.getPassword()));
        } else {
            result.put("found", false);
        }
        
        return ResponseEntity.ok(result);
    }
}
