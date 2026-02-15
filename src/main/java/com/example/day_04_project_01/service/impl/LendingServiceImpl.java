package com.example.day_04_project_01.service.impl;

import com.example.day_04_project_01.exception.BadRequestException;
import com.example.day_04_project_01.exception.ResourceNotFoundException;
import com.example.day_04_project_01.model.Book;
import com.example.day_04_project_01.model.LendingRecord;
import com.example.day_04_project_01.model.Member;
import com.example.day_04_project_01.repository.BookRepository;
import com.example.day_04_project_01.repository.LendingRecordRepository;
import com.example.day_04_project_01.repository.MemberRepository;
import com.example.day_04_project_01.service.LendingService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LendingServiceImpl implements LendingService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LendingRecordRepository lendingRecordRepository;

    public LendingServiceImpl(BookRepository bookRepository,
                              MemberRepository memberRepository,
                              LendingRecordRepository lendingRecordRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.lendingRecordRepository = lendingRecordRepository;
    }

    @Override
    @Transactional
    public void lendBook(String isbn, Long memberId) {

        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (!book.isAvailable()) {
            throw new BadRequestException("Book is already lent");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        LendingRecord record = LendingRecord.builder()
                .book(book)
                .member(member)
                .build();

        book.setAvailable(false);

        lendingRecordRepository.save(record);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void returnBook(String isbn) {

        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        LendingRecord record = lendingRecordRepository
                .findTopByBookAndReturnedFalseOrderByLentDateDesc(book)
                .orElseThrow(() -> new BadRequestException("No active lending found"));

        record.markAsReturned();
        book.setAvailable(true);

        lendingRecordRepository.save(record);
        bookRepository.save(book);
    }
}
