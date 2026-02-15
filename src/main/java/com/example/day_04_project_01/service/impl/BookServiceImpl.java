package com.example.day_04_project_01.service.impl;

import com.example.day_04_project_01.exception.BadRequestException;
import com.example.day_04_project_01.exception.ResourceNotFoundException;
import com.example.day_04_project_01.model.Book;
import com.example.day_04_project_01.model.BookCategory;
import com.example.day_04_project_01.repository.BookRepository;
import com.example.day_04_project_01.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book addBook(Book book) {

        if (bookRepository.existsById(book.getIsbn())) {
            throw new BadRequestException("Book with this ISBN already exists");
        }

        book.setAvailable(true);
        return bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableTrue();
    }

    @Override
    public List<Book> getByCategory(BookCategory category) {
        return bookRepository.findByCategory(category);
    }
}
