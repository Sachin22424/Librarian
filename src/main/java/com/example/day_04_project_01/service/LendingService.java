package com.example.day_04_project_01.service;

public interface LendingService {

    void lendBook(String isbn, Long memberId);

    void returnBook(String isbn);
}
