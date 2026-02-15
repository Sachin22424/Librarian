package com.example.day_04_project_01.repository;

import com.example.day_04_project_01.model.Book;
import com.example.day_04_project_01.model.LendingRecord;
import com.example.day_04_project_01.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LendingRecordRepository extends JpaRepository<LendingRecord, Long> {

    // 📌 Find latest active lending record for a book
    Optional<LendingRecord> findTopByBookAndReturnedFalseOrderByLentDateDesc(Book book);

    // 📌 Get all records for a member
    List<LendingRecord> findByMember(Member member);

    // 📌 Get all active lendings for a member
    List<LendingRecord> findByMemberAndReturnedFalse(Member member);

    // 📌 Get all records for a book
    List<LendingRecord> findByBook(Book book);
}
