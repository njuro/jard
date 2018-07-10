package com.github.njuro.jboard.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for authenticated section (dashboard)
 *
 * @author njuro
 */
@Controller
@RequestMapping("/auth")
public class AdminController {

    @GetMapping
    public String showAdminDashboard() {
        return "auth";
    }
}
