package com.example.day_04_project_01.controller;

import com.example.day_04_project_01.model.Book;
import com.example.day_04_project_01.model.BookCategory;
import com.example.day_04_project_01.model.Member;
import com.example.day_04_project_01.service.BookService;
import com.example.day_04_project_01.service.LendingService;
import com.example.day_04_project_01.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookRestController {

    private final BookService bookService;
    private final MemberService memberService;
    private final LendingService lendingService;

    public BookRestController(BookService bookService,
                              MemberService memberService,
                              LendingService lendingService) {
        this.bookService = bookService;
        this.memberService = memberService;
        this.lendingService = lendingService;
    }

    // 📘 Add Book
    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        return ResponseEntity.ok(bookService.addBook(book));
    }

    // 📘 Get All Books
    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // 🔎 Search Book by Title
    @GetMapping("/books/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchByTitle(title));
    }

    // 📘 Get Available Books
    @GetMapping("/books/available")
    public ResponseEntity<List<Book>> availableBooks() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    // 📘 Get Books by Category
    @GetMapping("/books/category/{category}")
    public ResponseEntity<List<Book>> getByCategory(@PathVariable BookCategory category) {
        return ResponseEntity.ok(bookService.getByCategory(category));
    }

    // 👤 Register Member
    @PostMapping("/members")
    public ResponseEntity<Member> registerMember(@Valid @RequestBody Member member) {
        return ResponseEntity.ok(memberService.registerMember(member));
    }

    // 👤 Get All Members
    @GetMapping("/members")
    public ResponseEntity<List<Member>> getMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    // 📚 Lend Book
    @PostMapping("/lend/{isbn}/{memberId}")
    public ResponseEntity<String> lendBook(@PathVariable String isbn,
                                           @PathVariable Long memberId) {
        lendingService.lendBook(isbn, memberId);
        return ResponseEntity.ok("Book lent successfully");
    }

    // 📚 Return Book
    @PostMapping("/return/{isbn}")
    public ResponseEntity<String> returnBook(@PathVariable String isbn) {
        lendingService.returnBook(isbn);
        return ResponseEntity.ok("Book returned successfully");
    }
}
