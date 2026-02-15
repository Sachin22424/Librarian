package com.example.day_04_project_01.service;

import com.example.day_04_project_01.model.Member;

import java.util.List;

public interface MemberService {

    Member registerMember(Member member);

    Member registerUser(Member member);

    Member authenticateUser(String username, String password);

    List<Member> getAllMembers();
}
