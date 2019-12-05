package com.github.njuro.jboard.controllers;

import com.github.njuro.jboard.config.security.methods.HasAuthorities;
import com.github.njuro.jboard.facades.UserFacade;
import com.github.njuro.jboard.helpers.Mappings;
import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.models.dto.forms.RegisterForm;
import com.github.njuro.jboard.models.enums.UserAuthority;
import com.jfilter.filter.FieldFilterSetting;
import com.jfilter.filter.FilterBehaviour;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Mappings.API_ROOT_USERS)
public class UserRestController {

  private final UserFacade userFacade;

  @Autowired
  public UserRestController(final UserFacade userFacade) {
    this.userFacade = userFacade;
  }

  @GetMapping
  @FieldFilterSetting(
      className = User.class,
      fields = {"username", "email", "enabled", "role", "authorities"},
      behaviour = FilterBehaviour.KEEP_FIELDS)
  public List<User> getAllUsers() {
    return this.userFacade.getAllUsers();
  }

  @GetMapping("/current")
  @FieldFilterSetting(
      className = User.class,
      fields = {"username", "role", "authorities"},
      behaviour = FilterBehaviour.KEEP_FIELDS)
  public User getCurrentUser() {
    return this.userFacade.getCurrentUser();
  }

  @PostMapping("/create")
  @HasAuthorities(UserAuthority.MANAGE_USERS)
  public User registerUser(
      @RequestBody @Valid final RegisterForm registerForm, final HttpServletRequest request) {
    registerForm.setRegistrationIp(request.getRemoteAddr());
    return this.userFacade.registerUser(registerForm);
  }
}
