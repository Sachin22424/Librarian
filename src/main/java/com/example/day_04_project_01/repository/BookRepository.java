package com.example.day_04_project_01.repository;

import com.example.day_04_project_01.model.Book;
import com.example.day_04_project_01.model.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {

    // 🔎 Search books by title (case insensitive)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // 🔎 Find all available books
    List<Book> findByAvailableTrue();

    // 🔎 Find books by category
    List<Book> findByCategory(BookCategory category);

    // 🔎 Find available books in specific category
    List<Book> findByCategoryAndAvailableTrue(BookCategory category);
}
