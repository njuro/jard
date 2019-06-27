package com.github.njuro.jboard.controllers;

import com.github.njuro.jboard.models.dto.BanForm;
import com.github.njuro.jboard.models.enums.UserRole;
import com.github.njuro.jboard.services.BanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class BanController {

    private final BanService banService;

    @Autowired
    public BanController(BanService banService) {
        this.banService = banService;
    }

    @Secured(UserRole.Roles.MODERATOR_ROLE)
    @PostMapping("/ban")
    public String banUser(@Valid @ModelAttribute(name = "banForm") BanForm banForm) {
        return "/";
    }
}
