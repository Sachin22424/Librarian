package com.example.day_04_project_01.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final UserAuthInterceptor userAuthInterceptor;

    public WebConfig(AuthInterceptor authInterceptor,
                     UserAuthInterceptor userAuthInterceptor) {
        this.authInterceptor = authInterceptor;
        this.userAuthInterceptor = userAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/", "/search", "/category", "/available", "/lent", "/ui/**")
                .excludePathPatterns("/login", "/logout", "/error", "/h2-console/**", "/api/**", "/css/**", "/js/**", "/images/**", "/webjars/**");

        registry.addInterceptor(userAuthInterceptor)
            .addPathPatterns("/user/**")
            .excludePathPatterns("/user/login", "/user/register", "/user/logout", "/error", "/h2-console/**", "/api/**", "/css/**", "/js/**", "/images/**", "/webjars/**");
    }
}
