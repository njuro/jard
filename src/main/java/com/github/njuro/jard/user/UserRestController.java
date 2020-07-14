package com.github.njuro.jard.user;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.config.security.methods.HasAuthorities;
import com.jfilter.filter.FieldFilterSetting;
import com.jfilter.filter.FilterBehaviour;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Mappings.API_ROOT_USERS)
public class UserRestController {

  private final UserFacade userFacade;

  @Autowired
  public UserRestController(UserFacade userFacade) {
    this.userFacade = userFacade;
  }

  @PutMapping
  @HasAuthorities(UserAuthority.MANAGE_USERS)
  public User createUser(@RequestBody @Valid UserForm userForm, HttpServletRequest request) {
    userForm.setRegistrationIp(request.getRemoteAddr());
    return userFacade.createUser(userForm);
  }

  @GetMapping
  @FieldFilterSetting(
      className = User.class,
      fields = {"username", "email", "enabled", "role", "authorities"},
      behaviour = FilterBehaviour.KEEP_FIELDS)
  @HasAuthorities(UserAuthority.MANAGE_USERS)
  public List<User> getAllUsers() {
    return userFacade.getAllUsers();
  }

  @GetMapping("/current")
  @FieldFilterSetting(
      className = User.class,
      fields = {"username", "role", "authorities"},
      behaviour = FilterBehaviour.KEEP_FIELDS)
  public User getCurrentUser() {
    return userFacade.getCurrentUser();
  }

  @PostMapping(Mappings.PATH_VARIABLE_USER + "/edit")
  @HasAuthorities(UserAuthority.MANAGE_USERS)
  public User editUser(User oldUser, @RequestBody UserForm userForm) {
    return userFacade.editUser(oldUser, userForm);
  }

  @DeleteMapping(Mappings.PATH_VARIABLE_USER)
  @HasAuthorities(UserAuthority.MANAGE_USERS)
  public ResponseEntity<Object> deleteUser(User user) {
    userFacade.deleteUser(user);
    return ResponseEntity.ok().build();
  }
}
