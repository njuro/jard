package com.github.njuro.jard.user;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.config.security.methods.HasAuthorities;
import com.github.njuro.jard.user.dto.UserDto;
import com.github.njuro.jard.user.dto.UserForm;
import com.jfilter.filter.FieldFilterSetting;
import com.jfilter.filter.FilterBehaviour;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  public UserDto createUser(@RequestBody @Valid UserForm userForm, HttpServletRequest request) {
    userForm.setRegistrationIp(request.getRemoteAddr());
    return userFacade.createUser(userForm);
  }

  @GetMapping
  @FieldFilterSetting(
      className = UserDto.class,
      fields = {"username", "email", "enabled", "role", "authorities"},
      behaviour = FilterBehaviour.KEEP_FIELDS)
  @HasAuthorities(UserAuthority.MANAGE_USERS)
  public List<UserDto> getAllUsers() {
    return userFacade.getAllUsers();
  }

  @GetMapping("/current")
  @FieldFilterSetting(
      className = UserDto.class,
      fields = {"username", "role", "authorities"},
      behaviour = FilterBehaviour.KEEP_FIELDS)
  public UserDto getCurrentUser() {
    return userFacade.getCurrentUser();
  }

  @PostMapping(Mappings.PATH_VARIABLE_USER + "/edit")
  @HasAuthorities(UserAuthority.MANAGE_USERS)
  public UserDto editUser(UserDto oldUser, @RequestBody UserForm userForm) {
    return userFacade.editUser(oldUser, userForm);
  }

  @DeleteMapping(Mappings.PATH_VARIABLE_USER)
  @HasAuthorities(UserAuthority.MANAGE_USERS)
  public ResponseEntity<Object> deleteUser(UserDto user) {
    userFacade.deleteUser(user);
    return ResponseEntity.ok().build();
  }
}
