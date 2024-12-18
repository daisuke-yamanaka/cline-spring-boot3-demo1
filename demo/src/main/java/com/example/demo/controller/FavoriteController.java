package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.User;
import com.example.demo.service.FavoriteService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Controller
@RequestMapping("/books/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    private Long getUserId(UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new RuntimeException("ユーザーが見つかりません: " + userDetails.getUsername());
        }
        return user.getUserId();
    }

    @GetMapping
    public String getFavorites(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpServletRequest request) {
        // 現在のパスを設定
        model.addAttribute("currentPath", request.getRequestURI());
        
        Long userId = getUserId(userDetails);
        List<Book> favoriteBooks = favoriteService.getFavoriteBooks(userId);
        model.addAttribute("books", favoriteBooks);
        return "books/favorites";
    }

    @PostMapping("/add/{bookId}")
    @ResponseBody
    public String addFavorite(@AuthenticationPrincipal UserDetails userDetails, 
                            @PathVariable Long bookId) {
        Long userId = getUserId(userDetails);
        favoriteService.addFavorite(userId, bookId);
        return "お気に入りに追加しました";
    }

    @DeleteMapping("/remove/{bookId}")
    @ResponseBody
    public String removeFavorite(@AuthenticationPrincipal UserDetails userDetails, 
                               @PathVariable Long bookId) {
        Long userId = getUserId(userDetails);
        favoriteService.removeFavorite(userId, bookId);
        return "お気に入りから削除しました";
    }

    @GetMapping("/check/{bookId}")
    @ResponseBody
    public boolean isFavorite(@AuthenticationPrincipal UserDetails userDetails, 
                            @PathVariable Long bookId) {
        Long userId = getUserId(userDetails);
        return favoriteService.isFavorite(userId, bookId);
    }

    @GetMapping("/count/{bookId}")
    @ResponseBody
    public int getFavoriteCount(@PathVariable Long bookId) {
        return favoriteService.getFavoriteCount(bookId);
    }

    @GetMapping("/popular")
    public String getPopularBooks(Model model, HttpServletRequest request) {
        // 現在のパスを設定
        model.addAttribute("currentPath", request.getRequestURI());
        
        List<Object[]> popularBooks = favoriteService.getMostFavoritedBooks(10);
        model.addAttribute("popularBooks", popularBooks);
        return "books/popular";
    }
}
