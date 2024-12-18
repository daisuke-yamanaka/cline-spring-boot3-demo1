package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.BorrowingHistory;
import com.example.demo.model.User;
import com.example.demo.service.BookService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    private static final int PAGE_SIZE = 5;

    @GetMapping("/search")
    public String searchForm(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            Model model,
            HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        
        List<Book> searchResults;
        Integer statusValue = status != null && !status.isEmpty() ? Integer.parseInt(status) : null;
        
        // キーワード、カテゴリ、状態を組み合わせた検索（ページング対応）
        searchResults = bookService.searchBooksWithFiltersPaged(
            keyword, 
            category != null && !category.isEmpty() ? category : null, 
            statusValue,
            page,
            PAGE_SIZE
        );
        
        // 総件数を取得
        int totalBooks = bookService.countBooksWithFilters(
            keyword,
            category != null && !category.isEmpty() ? category : null,
            statusValue
        );
        
        // 総ページ数を計算
        int totalPages = (int) Math.ceil((double) totalBooks / PAGE_SIZE);
        
        model.addAttribute("books", searchResults);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", PAGE_SIZE);
        
        return "books/search";
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        
        Book book = bookService.findById(id);
        if (book == null) {
            return "redirect:/books/search";
        }

        // 本が貸出中の場合、現在の貸出情報を取得
        if (book.getStatus() == 1) {
            BorrowingHistory currentBorrowing = bookService.findBorrowingHistoriesWithFilters(null, BorrowingHistory.STATUS_BORROWED, null)
                .stream()
                .filter(history -> history.getBookId().equals(id))
                .findFirst()
                .orElse(null);

            if (currentBorrowing != null) {
                // ユーザー情報を取得
                User borrower = userService.findById(currentBorrowing.getUserId());
                if (borrower != null) {
                    book.setBorrower(borrower.getDisplayName()); // usernameからdisplayNameに変更
                    book.setBorrowedDate(currentBorrowing.getBorrowedDate());
                    book.setExpectedReturnDate(currentBorrowing.getExpectedReturnDate());
                }
            }
        }

        model.addAttribute("book", book);
        return "books/view";
    }

    @GetMapping("/borrowed")
    public String borrowedBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String period,
            @RequestParam(required = false, defaultValue = "1") int page,
            Model model, 
            HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        
        // 期間の計算
        LocalDate startDate = null;
        if (period != null) {
            LocalDate now = LocalDate.now();
            switch (period) {
                case "month":
                    startDate = now.minusMonths(1);
                    break;
                case "3months":
                    startDate = now.minusMonths(3);
                    break;
                case "6months":
                    startDate = now.minusMonths(6);
                    break;
            }
        }

        // 状態の変換
        Integer historyStatus = null;
        if (status != null && !status.equals("all")) {
            switch (status) {
                case "current":
                    historyStatus = BorrowingHistory.STATUS_BORROWED;
                    break;
                case "returned":
                    historyStatus = BorrowingHistory.STATUS_RETURNED;
                    break;
                case "overdue":
                    historyStatus = BorrowingHistory.STATUS_OVERDUE;
                    break;
            }
        }
        
        List<BorrowingHistory> histories;
        int totalHistories;
        
        if (isAdmin) {
            // 管理者の場合は全ユーザーの貸出履歴を表示（ページング対応）
            histories = bookService.findBorrowingHistoriesWithFiltersPaged(keyword, historyStatus, startDate, page, PAGE_SIZE);
            totalHistories = bookService.countBorrowingHistoriesWithFilters(keyword, historyStatus, startDate);
            model.addAttribute("isAdmin", true);
        } else {
            // 一般ユーザーの場合は自分の貸出履歴のみを表示（ページング対応）
            User user = userService.findByUsername(auth.getName());
            histories = bookService.findBorrowingHistoriesWithFiltersPaged(keyword, historyStatus, startDate, page, PAGE_SIZE)
                .stream()
                .filter(history -> history.getUserId().equals(user.getUserId()))
                .toList();
            totalHistories = (int) bookService.findBorrowingHistoriesWithFilters(keyword, historyStatus, startDate)
                .stream()
                .filter(history -> history.getUserId().equals(user.getUserId()))
                .count();
            model.addAttribute("isAdmin", false);
        }
        
        // 総ページ数を計算
        int totalPages = (int) Math.ceil((double) totalHistories / PAGE_SIZE);
        
        model.addAttribute("histories", histories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPeriod", period);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", PAGE_SIZE);
        
        return "books/borrowed";
    }

    @PostMapping("/{id}/borrow")
    public String borrowBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        
        if (!userService.canBorrow(user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "貸出可能な上限に達しています");
            return "redirect:/books/" + id;
        }

        LocalDate expectedReturnDate = LocalDate.now().plusDays(14);
        boolean success = bookService.borrowBook(id, user.getUsername(), expectedReturnDate);
        
        if (success) {
            redirectAttributes.addFlashAttribute("success", "書籍を借りました");
        } else {
            redirectAttributes.addFlashAttribute("error", "貸出処理に失敗しました");
        }
        
        return "redirect:/books/" + id;
    }

    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean success = bookService.returnBook(id);
        
        if (success) {
            redirectAttributes.addFlashAttribute("success", "書籍を返却しました");
        } else {
            redirectAttributes.addFlashAttribute("error", "返却処理に失敗しました");
        }
        
        return "redirect:/books/" + id;
    }
}
