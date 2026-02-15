package com.example.day_04_project_01.controller;

import com.example.day_04_project_01.exception.BadRequestException;
import com.example.day_04_project_01.model.Member;
import com.example.day_04_project_01.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserAuthController {

    private final MemberService memberService;

    public UserAuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/user/register")
    public String registerPage() {
        return "user-register";
    }

    @PostMapping("/user/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String username,
                           @RequestParam String password,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        try {
            Member member = Member.builder()
                    .name(name)
                    .email(email)
                    .username(username)
                    .password(password)
                    .build();

            memberService.registerUser(member);
            redirectAttributes.addFlashAttribute("message", "Account created. Please log in.");
            return "redirect:/user/login";
        } catch (BadRequestException ex) {
            model.addAttribute("error", ex.getMessage());
            return "user-register";
        }
    }

    @GetMapping("/user/login")
    public String loginPage() {
        return "user-login";
    }

    @PostMapping("/user/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {
        Member member = memberService.authenticateUser(username, password);
        if (member != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("userLoggedIn", true);
            session.setAttribute("userId", member.getId());
            session.setAttribute("username", member.getUsername());
            return "redirect:/user";
        }

        model.addAttribute("error", "Invalid username or password");
        return "user-login";
    }

    @GetMapping("/user/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/user/login";
    }
}
