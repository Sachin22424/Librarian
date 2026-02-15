package com.example.day_04_project_01.controller;

import com.example.day_04_project_01.exception.BadRequestException;
import com.example.day_04_project_01.model.Book;
import com.example.day_04_project_01.model.BookCategory;
import com.example.day_04_project_01.model.BookRequest;
import com.example.day_04_project_01.model.BookRequestStatus;
import com.example.day_04_project_01.model.LendingRecord;
import com.example.day_04_project_01.model.Member;
import com.example.day_04_project_01.repository.BookRepository;
import com.example.day_04_project_01.repository.BookRequestRepository;
import com.example.day_04_project_01.repository.LendingRecordRepository;
import com.example.day_04_project_01.repository.MemberRepository;
import com.example.day_04_project_01.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserBookViewController {

    private final BookService bookService;
    private final LendingRecordRepository lendingRecordRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BookRequestRepository bookRequestRepository;

    public UserBookViewController(BookService bookService,
                                  LendingRecordRepository lendingRecordRepository,
                                  BookRepository bookRepository,
                                  MemberRepository memberRepository,
                                  BookRequestRepository bookRequestRepository) {
        this.bookService = bookService;
        this.lendingRecordRepository = lendingRecordRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.bookRequestRepository = bookRequestRepository;
    }

    @GetMapping("/user")
    public String userHome(Model model, HttpServletRequest request) {
        return listBooks(bookService.getAllBooks(), model, request);
    }

    @GetMapping("/user/search")
    public String search(@RequestParam String title, Model model, HttpServletRequest request) {
        return listBooks(bookService.searchByTitle(title), model, request);
    }

    @GetMapping("/user/category")
    public String filterByCategory(@RequestParam(required = false) BookCategory category,
                                   Model model,
                                   HttpServletRequest request) {
        if (category == null) {
            return listBooks(bookService.getAllBooks(), model, request);
        }
        return listBooks(bookService.getByCategory(category), model, request);
    }

    @GetMapping("/user/available")
    public String availableBooks(Model model, HttpServletRequest request) {
        return listBooks(bookService.getAvailableBooks(), model, request);
    }

    @GetMapping("/user/lent")
    public String lentBooks(Model model, HttpServletRequest request) {
        List<Book> lentBooks = bookService.getAllBooks()
                .stream()
                .filter(book -> !book.isAvailable())
                .toList();
        return listBooks(lentBooks, model, request);
    }

    @PostMapping("/user/request")
    public String requestBook(@RequestParam String isbn,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in again.");
            return "redirect:/user/login";
        }

        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BadRequestException("Book not found"));

        if (!book.isAvailable()) {
            redirectAttributes.addFlashAttribute("error", "Only available books can be requested.");
            return "redirect:/user";
        }

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (bookRequestRepository.existsByBookAndMemberAndStatus(book, member, BookRequestStatus.PENDING)) {
            redirectAttributes.addFlashAttribute("error", "You already requested this book.");
            return "redirect:/user";
        }

        BookRequest bookRequest = BookRequest.builder()
                .book(book)
                .member(member)
                .status(BookRequestStatus.PENDING)
                .build();
        bookRequestRepository.save(bookRequest);

        redirectAttributes.addFlashAttribute("message", "Request sent to librarian.");
        return "redirect:/user";
    }

    private String listBooks(List<Book> books, Model model, HttpServletRequest request) {
        model.addAttribute("books", books);
        addStats(model, books);
        addLendingInfo(model, books);
        addRequestedInfo(model, request);
        model.addAttribute("categories", BookCategory.values());
        return "user-book-list";
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

    private void addRequestedInfo(Model model, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            model.addAttribute("requestedBooks", List.of());
            model.addAttribute("requestedIsbns", Set.of());
            return;
        }

        Member member = memberRepository.findById(userId).orElse(null);
        if (member == null) {
            model.addAttribute("requestedBooks", List.of());
            model.addAttribute("requestedIsbns", Set.of());
            return;
        }

        List<BookRequest> pendingRequests = bookRequestRepository.findByMemberAndStatus(
                member,
                BookRequestStatus.PENDING
        );

        Set<String> requestedIsbns = pendingRequests.stream()
                .map(requestItem -> requestItem.getBook().getIsbn())
                .collect(Collectors.toSet());

        model.addAttribute("requestedBooks", pendingRequests);
        model.addAttribute("requestedIsbns", requestedIsbns);
    }
}
