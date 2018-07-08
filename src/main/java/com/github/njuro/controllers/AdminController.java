package com.github.njuro.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AdminController {

    @GetMapping
    public String getAdminDashboard() {
        return "auth";
    }
}
