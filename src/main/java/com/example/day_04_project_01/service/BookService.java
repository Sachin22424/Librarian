package com.example.day_04_project_01.service;

import com.example.day_04_project_01.model.Book;
import com.example.day_04_project_01.model.BookCategory;

import java.util.List;

public interface BookService {

    Book addBook(Book book);

    List<Book> getAllBooks();

    List<Book> searchByTitle(String title);

    List<Book> getAvailableBooks();

    List<Book> getByCategory(BookCategory category);
}
