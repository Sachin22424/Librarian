package com.example.day_04_project_01.repository;

import com.example.day_04_project_01.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 🔎 Find member by email
    Optional<Member> findByEmail(String email);

    // 🔎 Find member by username
    Optional<Member> findByUsername(String username);

    // 🔎 Check if email already exists
    boolean existsByEmail(String email);

    // 🔎 Check if username already exists
    boolean existsByUsername(String username);
}
