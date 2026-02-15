package com.example.day_04_project_01.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null
                && Boolean.TRUE.equals(session.getAttribute("userLoggedIn"));

        if (loggedIn) {
            return true;
        }

        response.sendRedirect("/user/login");
        return false;
    }
}
