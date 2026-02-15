package com.example.day_04_project_01.controller;

import com.example.day_04_project_01.exception.BadRequestException;
import com.example.day_04_project_01.exception.ResourceNotFoundException;
import com.example.day_04_project_01.model.Book;
import com.example.day_04_project_01.model.BookCategory;
import com.example.day_04_project_01.model.BookRequest;
import com.example.day_04_project_01.model.BookRequestStatus;
import com.example.day_04_project_01.model.LendingRecord;
import com.example.day_04_project_01.repository.BookRequestRepository;
import com.example.day_04_project_01.repository.LendingRecordRepository;
import com.example.day_04_project_01.service.BookService;
import com.example.day_04_project_01.service.LendingService;
import com.example.day_04_project_01.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BookViewController {

    private final BookService bookService;
    private final MemberService memberService;
    private final LendingService lendingService;
    private final LendingRecordRepository lendingRecordRepository;
    private final BookRequestRepository bookRequestRepository;

    public BookViewController(BookService bookService,
                              MemberService memberService,
                              LendingService lendingService,
                              LendingRecordRepository lendingRecordRepository,
                              BookRequestRepository bookRequestRepository) {
        this.bookService = bookService;
        this.memberService = memberService;
        this.lendingService = lendingService;
        this.lendingRecordRepository = lendingRecordRepository;
        this.bookRequestRepository = bookRequestRepository;
    }

    // 🏠 Home Page - Show All
    @GetMapping("/")
    public String home(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        addStats(model, books);
        addLendingInfo(model, books);
        addPendingRequests(model);
        model.addAttribute("members", memberService.getAllMembers());
        model.addAttribute("categories", BookCategory.values());
        return "book-list";
    }

    // 🔎 Search
    @GetMapping("/search")
    public String search(@RequestParam String title, Model model) {
        List<Book> books = bookService.searchByTitle(title);
        model.addAttribute("books", books);
        addStats(model, books);
        addLendingInfo(model, books);
        addPendingRequests(model);
        model.addAttribute("members", memberService.getAllMembers());
        model.addAttribute("categories", BookCategory.values());
        return "book-list";
    }

    // 📂 Filter by Category
    @GetMapping("/category")
    public String filterByCategory(
            @RequestParam(required = false) BookCategory category,
            Model model) {

        if (category == null) {
            List<Book> books = bookService.getAllBooks();
            model.addAttribute("books", books);
            addStats(model, books);
            addLendingInfo(model, books);
            addPendingRequests(model);
        } else {
            List<Book> books = bookService.getByCategory(category);
            model.addAttribute("books", books);
            addStats(model, books);
            addLendingInfo(model, books);
            addPendingRequests(model);
        }

        model.addAttribute("members", memberService.getAllMembers());
        model.addAttribute("categories", BookCategory.values());

        return "book-list";
    }


    // ✅ Available Books
    @GetMapping("/available")
    public String availableBooks(Model model) {
        List<Book> books = bookService.getAvailableBooks();
        model.addAttribute("books", books);
        addStats(model, books);
        addLendingInfo(model, books);
        addPendingRequests(model);
        model.addAttribute("members", memberService.getAllMembers());
        model.addAttribute("categories", BookCategory.values());
        return "book-list";
    }

    // 📕 Lent Books
    @GetMapping("/lent")
    public String lentBooks(Model model) {
        List<Book> lentBooks = bookService.getAllBooks()
                .stream()
                .filter(book -> !book.isAvailable())
                .toList();

        model.addAttribute("books", lentBooks);
        addStats(model, lentBooks);
        addLendingInfo(model, lentBooks);
        addPendingRequests(model);
        model.addAttribute("members", memberService.getAllMembers());
        model.addAttribute("categories", BookCategory.values());
        return "book-list";
    }

    private void addStats(Model model, List<Book> books) {
        int totalBooks = books.size();
        long availableCount = books.stream().filter(Book::isAvailable).count();
        long lentCount = totalBooks - availableCount;

        int availablePercent = totalBooks == 0 ? 0 : (int) Math.round((availableCount * 100.0) / totalBooks);
        int lentPercent = totalBooks == 0 ? 0 : 100 - availablePercent;

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("availableCount", availableCount);
        model.addAttribute("lentCount", lentCount);
        model.addAttribute("availablePercent", availablePercent);
        model.addAttribute("lentPercent", lentPercent);
    }

    private void addLendingInfo(Model model, List<Book> books) {
        Map<String, LendingRecord> activeLendings = new HashMap<>();
        Map<String, LocalDateTime> expectedReturns = new HashMap<>();

        for (Book book : books) {
            if (!book.isAvailable()) {
                lendingRecordRepository.findTopByBookAndReturnedFalseOrderByLentDateDesc(book)
                        .ifPresent(record -> {
                            activeLendings.put(book.getIsbn(), record);
                            if (record.getLentDate() != null) {
                                expectedReturns.put(book.getIsbn(), record.getLentDate().plusDays(7));
                            }
                        });
            }
        }

        model.addAttribute("activeLendings", activeLendings);
        model.addAttribute("expectedReturns", expectedReturns);
    }

    private void addPendingRequests(Model model) {
        List<BookRequest> pendingRequests = bookRequestRepository.findByStatus(BookRequestStatus.PENDING);
        model.addAttribute("pendingRequests", pendingRequests);
    }

    // Lend
    @PostMapping("/ui/lend")
    public String lend(@RequestParam String isbn,
                       @RequestParam Long memberId,
                       RedirectAttributes redirectAttributes) {
        try {
            lendingService.lendBook(isbn, memberId);
            redirectAttributes.addFlashAttribute("message", "Book lent successfully.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/";
    }

    // Return
    @PostMapping("/ui/return")
    public String returnBook(@RequestParam String isbn,
                             RedirectAttributes redirectAttributes) {
        try {
            lendingService.returnBook(isbn);
            redirectAttributes.addFlashAttribute("message", "Book returned successfully.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/ui/requests/approve")
    public String approveRequest(@RequestParam Long requestId,
                                 RedirectAttributes redirectAttributes) {
        try {
            BookRequest request = bookRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

            if (request.getStatus() != BookRequestStatus.PENDING) {
                throw new BadRequestException("Request already processed");
            }

            if (!request.getBook().isAvailable()) {
                throw new BadRequestException("Book is no longer available");
            }

            lendingService.lendBook(request.getBook().getIsbn(), request.getMember().getId());

            request.setStatus(BookRequestStatus.APPROVED);
            request.setProcessedAt(LocalDateTime.now());
            bookRequestRepository.save(request);

            redirectAttributes.addFlashAttribute("message", "Request approved and book lent.");
        } catch (BadRequestException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/";
    }
}
