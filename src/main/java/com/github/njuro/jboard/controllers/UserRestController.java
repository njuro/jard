package com.github.njuro.jboard.controllers;

import com.github.njuro.jboard.config.security.SensitiveDataFilter;
import com.github.njuro.jboard.facades.UserFacade;
import com.github.njuro.jboard.helpers.Mappings;
import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.models.dto.RegisterForm;
import com.jfilter.filter.DynamicFilter;
import com.jfilter.filter.FieldFilterSetting;
import com.jfilter.filter.FilterBehaviour;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(Mappings.API_ROOT_USERS)
@DynamicFilter(SensitiveDataFilter.class)
public class UserRestController {

    private final UserFacade userFacade;

    @Autowired
    public UserRestController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping("/current")
    @FieldFilterSetting(className = User.class, fields = {"username", "role", "authorities"}, behaviour = FilterBehaviour.KEEP_FIELDS)
    public User getCurrentUser() {
        return userFacade.getCurrentUser();
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody @Valid RegisterForm registerForm, HttpServletRequest request) {
        registerForm.setRegistrationIp(request.getRemoteAddr());
        return userFacade.registerUser(registerForm);
    }
}
