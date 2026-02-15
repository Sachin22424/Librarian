package com.example.day_04_project_01.service.impl;

import com.example.day_04_project_01.exception.BadRequestException;
import com.example.day_04_project_01.model.Member;
import com.example.day_04_project_01.repository.MemberRepository;
import com.example.day_04_project_01.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Member registerMember(Member member) {

        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        return memberRepository.save(member);
    }

    @Override
    public Member registerUser(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        if (member.getUsername() == null || member.getUsername().isBlank()) {
            throw new BadRequestException("Username is required");
        }

        if (member.getPassword() == null || member.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        if (memberRepository.existsByUsername(member.getUsername())) {
            throw new BadRequestException("Username already taken");
        }

        return memberRepository.save(member);
    }

    @Override
    public Member authenticateUser(String username, String password) {
        return memberRepository.findByUsername(username)
                .filter(member -> password != null && password.equals(member.getPassword()))
                .orElse(null);
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}
