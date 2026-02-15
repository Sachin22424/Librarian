package com.example.day_04_project_01.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String password;

    private LocalDateTime joinedAt;

    @OneToMany(mappedBy = "member")
    private List<LendingRecord> lendingRecords;

    @PrePersist
    public void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }
}
