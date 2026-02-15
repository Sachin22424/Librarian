package com.example.day_04_project_01.config;

import com.example.day_04_project_01.model.Book;
import com.example.day_04_project_01.model.BookCategory;
import com.example.day_04_project_01.model.LendingRecord;
import com.example.day_04_project_01.model.Member;
import com.example.day_04_project_01.repository.BookRepository;
import com.example.day_04_project_01.repository.LendingRecordRepository;
import com.example.day_04_project_01.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class DataLoader implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
        private final LendingRecordRepository lendingRecordRepository;

    public DataLoader(BookRepository bookRepository,
                                          MemberRepository memberRepository,
                                          LendingRecordRepository lendingRecordRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
                this.lendingRecordRepository = lendingRecordRepository;
    }

    @Override
    public void run(String... args) {

        // Insert Books only if table is empty
        if (bookRepository.count() == 0) {

            bookRepository.saveAll(List.of(
                    Book.builder()
                            .isbn("ISBN001")
                            .title("Spring Boot Basics")
                            .category(BookCategory.TECHNOLOGY)
                            .available(true)
                            .build(),
                    Book.builder()
                            .isbn("ISBN002")
                            .title("Data Structures in Java")
                            .category(BookCategory.TECHNOLOGY)
                            .available(true)
                            .build(),
                    Book.builder()
                            .isbn("ISBN003")
                            .title("World History")
                            .category(BookCategory.HISTORY)
                            .available(true)
                            .build(),
                    Book.builder()
                            .isbn("ISBN004")
                            .title("Clean Code")
                            .category(BookCategory.TECHNOLOGY)
                            .available(true)
                            .build(),
                    Book.builder()
                            .isbn("ISBN005")
                            .title("Effective Java")
                            .category(BookCategory.TECHNOLOGY)
                            .available(true)
                            .build(),
                    Book.builder()
                            .isbn("ISBN006")
                            .title("Astrophysics 101")
                            .category(BookCategory.SCIENCE)
                            .available(true)
                            .build(),
                    Book.builder()
                            .isbn("ISBN007")
                            .title("Modern Economics")
                            .category(BookCategory.BUSINESS)
                            .available(true)
                            .build(),
                    Book.builder()
                            .isbn("ISBN008")
                            .title("The Great Novel")
                            .category(BookCategory.FICTION)
                            .available(true)
                            .build(),
                    Book.builder()
                            .isbn("ISBN009")
                            .title("Ancient Civilizations")
                            .category(BookCategory.HISTORY)
                            .available(true)
                            .build(),
                    Book.builder()
                            .isbn("ISBN010")
                            .title("Project Management Basics")
                            .category(BookCategory.BUSINESS)
                            .available(true)
                            .build(),
                    Book.builder()
                            .isbn("ISBN011")
                            .title("Algorithms Illustrated")
                            .category(BookCategory.TECHNOLOGY)
                            .available(true)
                            .build(),
                    Book.builder()
                            .isbn("ISBN012")
                            .title("Chemistry Essentials")
                            .category(BookCategory.SCIENCE)
                            .available(true)
                            .build(),
                    Book.builder()
                            .isbn("ISBN013")
                            .title("Short Stories Vol 1")
                            .category(BookCategory.FICTION)
                            .available(true)
                            .build()
            ));

            System.out.println("📚 Sample books inserted!");
        }

        // Insert Members only if empty
        if (memberRepository.count() == 0) {

            memberRepository.saveAll(List.of(
                    Member.builder()
                            .name("Sachin")
                            .email("sachin@gmail.com")
                            .username("sachin")
                            .password("Sachin@1234")
                            .build(),
                    Member.builder()
                            .name("Rahul")
                            .email("rahul@gmail.com")
                            .build(),
                    Member.builder()
                            .name("Anita")
                            .email("anita@gmail.com")
                            .build(),
                    Member.builder()
                            .name("Meera")
                            .email("meera@gmail.com")
                            .build(),
                    Member.builder()
                            .name("Vikram")
                            .email("vikram@gmail.com")
                            .build(),
                    Member.builder()
                            .name("John")
                            .email("john@gmail.com")
                            .build(),
                    Member.builder()
                            .name("Sara")
                            .email("sara@gmail.com")
                            .build(),
                    Member.builder()
                            .name("Priya")
                            .email("priya@gmail.com")
                            .build()
            ));

            System.out.println("👤 Sample members inserted!");
        }

        // Insert Lending Records only if empty
        if (lendingRecordRepository.count() == 0) {
            List<Book> booksForLending = bookRepository.findAll();
            List<Member> membersForLending = memberRepository.findAll();

            if (booksForLending.size() >= 6 && membersForLending.size() >= 3) {
                Book book1 = booksForLending.get(0);
                Book book2 = booksForLending.get(1);
                Book book3 = booksForLending.get(2);
                Book book4 = booksForLending.get(3);
                Book book5 = booksForLending.get(4);
                Book book6 = booksForLending.get(5);

                Member member1 = membersForLending.get(0);
                Member member2 = membersForLending.get(1);
                Member member3 = membersForLending.get(2);

                LendingRecord active1 = LendingRecord.builder()
                        .book(book1)
                        .member(member1)
                        .build();
                LendingRecord active2 = LendingRecord.builder()
                        .book(book2)
                        .member(member2)
                        .build();
                LendingRecord active3 = LendingRecord.builder()
                        .book(book3)
                        .member(member3)
                        .build();

                lendingRecordRepository.saveAll(List.of(active1, active2, active3));

                book1.setAvailable(false);
                book2.setAvailable(false);
                book3.setAvailable(false);
                bookRepository.saveAll(List.of(book1, book2, book3));

                LendingRecord returned1 = LendingRecord.builder()
                        .book(book4)
                        .member(member1)
                        .build();
                returned1.markAsReturned();

                LendingRecord returned2 = LendingRecord.builder()
                        .book(book5)
                        .member(member2)
                        .build();
                returned2.markAsReturned();

                LendingRecord returned3 = LendingRecord.builder()
                        .book(book6)
                        .member(member3)
                        .build();
                returned3.markAsReturned();

                lendingRecordRepository.saveAll(List.of(returned1, returned2, returned3));
            }
        }
    }
}
