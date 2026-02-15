package com.example.day_04_project_01.repository;

import com.example.day_04_project_01.model.Book;
import com.example.day_04_project_01.model.BookRequest;
import com.example.day_04_project_01.model.BookRequestStatus;
import com.example.day_04_project_01.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {

    boolean existsByBookAndMemberAndStatus(Book book, Member member, BookRequestStatus status);

    List<BookRequest> findByMemberAndStatus(Member member, BookRequestStatus status);

    List<BookRequest> findByStatus(BookRequestStatus status);
}
