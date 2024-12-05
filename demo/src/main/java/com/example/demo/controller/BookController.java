package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

/**
 * 書籍管理のコントローラクラス
 */
@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 書籍一覧画面を表示
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "index";
    }

    /**
     * 書籍新規登録画面を表示
     */
    @GetMapping("/books/new")
    public String newBook(Model model) {
        model.addAttribute("book", new Book());
        return "book/new";
    }

    /**
     * 書籍編集画面を表示
     */
    @GetMapping("/books/edit/{id}")
    public String editBook(@PathVariable("id") Long bookId, Model model) {
        Book book = bookService.getBookById(bookId);
        model.addAttribute("book", book);
        return "book/edit";
    }

    /**
     * 指定されたIDの書籍情報を取得（Ajax）
     */
    @GetMapping("/api/books/{id}")
    @ResponseBody
    public ResponseEntity<Book> getBook(@PathVariable("id") Long bookId) {
        Book book = bookService.getBookById(bookId);
        return ResponseEntity.ok(book);
    }

    /**
     * 書籍情報を新規登録（Ajax）
     */
    @PostMapping("/api/books")
    @ResponseBody
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.createBook(book);
        return ResponseEntity.ok(createdBook);
    }

    /**
     * 書籍情報を更新（Ajax）
     */
    @PutMapping("/api/books/{id}")
    @ResponseBody
    public ResponseEntity<Book> updateBook(@PathVariable("id") Long bookId, @RequestBody Book book) {
        book.setBookId(bookId);
        Book updatedBook = bookService.updateBook(book);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * 書籍を削除（Ajax）
     */
    @DeleteMapping("/api/books/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteBook(@PathVariable("id") Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.ok().build();
    }

    /**
     * 書籍を貸し出し（Ajax）
     */
    @PostMapping("/api/books/{id}/borrow")
    @ResponseBody
    public ResponseEntity<Book> borrowBook(@PathVariable("id") Long bookId, @RequestBody Book book) {
        book.setBookId(bookId);
        Book borrowedBook = bookService.borrowBook(book);
        return ResponseEntity.ok(borrowedBook);
    }

    /**
     * 書籍を返却（Ajax）
     */
    @PostMapping("/api/books/{id}/return")
    @ResponseBody
    public ResponseEntity<Void> returnBook(@PathVariable("id") Long bookId) {
        bookService.returnBook(bookId);
        return ResponseEntity.ok().build();
    }

    /**
     * エラーハンドリング
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
