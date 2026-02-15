package com.example.day_04_project_01.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class UiExceptionHandler {

    @ExceptionHandler({BadRequestException.class, ResourceNotFoundException.class})
    public ModelAndView handleUiExceptions(RuntimeException ex) {
        String encoded = UriUtils.encode(ex.getMessage(), StandardCharsets.UTF_8);
        return new ModelAndView("redirect:/?error=" + encoded);
    }
}
