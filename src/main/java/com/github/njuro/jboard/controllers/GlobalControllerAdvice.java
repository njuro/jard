package com.github.njuro.jboard.controllers;

import com.github.njuro.jboard.models.dto.PostForm;
import com.github.njuro.jboard.models.dto.RegisterForm;
import com.github.njuro.jboard.models.dto.ThreadForm;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Global controller advice for initializing common model attributes (such as forms)
 *
 * @author njuro
 */
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
