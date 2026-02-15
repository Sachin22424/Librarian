package com.example.day_04_project_01.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lending_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LendingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime lentDate;

    private LocalDateTime returnDate;

    private boolean returned = false;

    @PrePersist
    public void onLend() {
        this.lentDate = LocalDateTime.now();
        this.returned = false;
    }

    public void markAsReturned() {
        this.returnDate = LocalDateTime.now();
        this.returned = true;
    }
}
