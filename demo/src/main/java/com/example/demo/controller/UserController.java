package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 管理者用のユーザー一覧画面
    @GetMapping("/manage")
    @PreAuthorize("hasRole('ADMIN')")
    public String manageUsers(Model model) {
        // currentPathの設定
        model.addAttribute("currentPath", "/users/manage");
        
        // 統計情報の取得
        model.addAttribute("totalUsers", userService.getTotalUsers());
        model.addAttribute("adminCount", userService.getAdminCount());
        model.addAttribute("activeUsers", userService.getActiveUsers());
        model.addAttribute("blockedUsers", userService.getBlockedUsers());
        
        return "users/manage";
    }

    // 以下はREST API用のエンドポイント
    @GetMapping("/api/list")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/api/create")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (!userService.validateNewUser(user)) {
            return ResponseEntity.badRequest()
                .body("Invalid user data or username/email already exists");
        }

        userService.save(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/api/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setUserId(id);
        if (userService.save(user) > 0) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.delete(id) > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/api/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<Void> blockUser(@PathVariable Long id) {
        if (userService.updateBlockStatus(id, true)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/api/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<Void> unblockUser(@PathVariable Long id) {
        if (userService.updateBlockStatus(id, false)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/{id}/can-borrow")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkCanBorrow(@PathVariable Long id) {
        boolean canBorrow = userService.canBorrow(id);
        return ResponseEntity.ok(Map.of("canBorrow", canBorrow));
    }

    @PostMapping("/api/{id}/change-password")
    @ResponseBody
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwords) {
        
        String currentPassword = passwords.get("currentPassword");
        String newPassword = passwords.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Both current and new passwords are required");
        }

        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest().body("New password must be at least 8 characters long");
        }

        if (userService.changePassword(id, currentPassword, newPassword)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().body("Invalid current password");
    }

    @PutMapping("/api/{id}/max-borrow-count")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> updateMaxBorrowCount(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        Integer newMaxBorrowCount = request.get("maxBorrowCount");
        if (newMaxBorrowCount == null || newMaxBorrowCount < 0) {
            return ResponseEntity.badRequest().body("Invalid max borrow count");
        }

        if (userService.updateMaxBorrowCount(id, newMaxBorrowCount)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/api/search")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        
        List<User> users;
        if (username != null && !username.isEmpty()) {
            users = List.of(userService.findByUsername(username));
        } else if (email != null && !email.isEmpty()) {
            users = List.of(userService.findByEmail(email));
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/api/{id}/borrowing-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserBorrowingStatus(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> status = Map.of(
            "userId", user.getUserId(),
            "username", user.getUsername(),
            "maxBorrowCount", user.getMaxBorrowCount(),
            "currentBorrowCount", userService.getCurrentBorrowCount(id),
            "isBlocked", user.getIsBlocked()
        );

        return ResponseEntity.ok(status);
    }
}
