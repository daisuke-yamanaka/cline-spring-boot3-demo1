package com.example.demo.controller;

import com.example.demo.service.BookService;
import com.example.demo.service.UserService;
import com.example.demo.model.Book;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, HttpServletRequest request) {
        // 現在のパスを設定
        String currentPath = request.getRequestURI();
        if (currentPath.equals("/")) {
            currentPath = "/dashboard";
        }
        model.addAttribute("currentPath", currentPath);

        // 現在のユーザー情報を取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            User currentUser = userService.findByUsername(auth.getName());
            if (currentUser != null) {
                model.addAttribute("currentUser", currentUser);
            } else {
                // ユーザーが見つからない場合のフォールバック
                model.addAttribute("currentUser", new User());
            }
        } else {
            // 認証情報がない場合のフォールバック
            model.addAttribute("currentUser", new User());
        }

        // 統計情報を取得
        List<Book> allBooks = bookService.findAll();
        List<Book> overdueBooks = bookService.findOverdueBooks();
        List<Book> popularBooks = bookService.findMostPopularBooks(5);
        List<Book> outOfStockBooks = bookService.findOutOfStockBooks();

        // ダッシュボードの統計情報を設定
        model.addAttribute("totalBooks", allBooks.size());
        model.addAttribute("availableBooks", allBooks.stream()
                .filter(b -> b.getStatus() == 0).count());
        model.addAttribute("borrowedBooks", allBooks.stream()
                .filter(b -> b.getStatus() == 1).count());
        model.addAttribute("overdueBooks", overdueBooks.size());
        model.addAttribute("popularBooks", popularBooks);
        model.addAttribute("outOfStockBooks", outOfStockBooks);

        // 管理者向けの追加情報
        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            model.addAttribute("isAdmin", true);
            // 在庫切れ警告
            model.addAttribute("outOfStockAlert", outOfStockBooks.size() > 0);
            // 延滞警告
            model.addAttribute("overdueAlert", overdueBooks.size() > 0);
        }

        return "dashboard";
    }
}
