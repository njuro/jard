package com.github.njuro.controllers;

import com.github.njuro.models.dto.PostForm;
import com.github.njuro.models.dto.RegisterForm;
import com.github.njuro.models.dto.ThreadForm;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute(name = "postForm")
    public PostForm postForm() {
        return new PostForm();
    }

    @ModelAttribute(name = "threadForm")
    public ThreadForm threadForm() {
        return new ThreadForm();
    }

    @ModelAttribute(name = "registerForm")
    public RegisterForm registerForm() {
        return new RegisterForm();
    }
}
