package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        
        // 現在のユーザー情報を取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            // 現在のユーザー情報を取得
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(auth.getName());
            
            // 更新可能なフィールドのみを更新
            currentUser.setEmail(user.getEmail());
            
            userService.save(currentUser);
            redirectAttributes.addFlashAttribute("success", "プロフィールを更新しました");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "プロフィールの更新に失敗しました");
        }

        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes) {
        
        try {
            // 現在のユーザー情報を取得
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName());

            if (userService.changePassword(user.getUserId(), currentPassword, newPassword)) {
                redirectAttributes.addFlashAttribute("success", "パスワードを変更しました");
            } else {
                redirectAttributes.addFlashAttribute("error", "現在のパスワードが正しくありません");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "パスワードの変更に失敗しました");
        }

        return "redirect:/profile";
    }
}
