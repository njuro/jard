package com.github.njuro.jard.user;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.config.security.methods.HasAuthorities;
import com.github.njuro.jard.user.dto.*;
import com.github.njuro.jard.utils.HttpUtils;
import com.github.njuro.jard.utils.validation.FormValidationException;
import com.jfilter.filter.FieldFilterSetting;
import com.jfilter.filter.FilterBehaviour;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Mappings.API_ROOT_USERS)
@Slf4j
public class UserRestController {

  private final UserFacade userFacade;

  @Autowired
  public UserRestController(UserFacade userFacade) {
    this.userFacade = userFacade;
  }

  @PostMapping
  @HasAuthorities(UserAuthority.MANAGE_USERS)
  public ResponseEntity<UserDto> createUser(
      @RequestBody @Valid UserForm userForm, HttpServletRequest request) {
    userForm.setRegistrationIp(request.getRemoteAddr());
    return ResponseEntity.status(HttpStatus.CREATED).body(userFacade.createUser(userForm));
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
      fields = {"username", "email", "role", "authorities"},
      behaviour = FilterBehaviour.KEEP_FIELDS)
  public UserDto getCurrentUser() {
    return userFacade.getCurrentUser();
  }

  @PutMapping(Mappings.PATH_VARIABLE_USER)
  @HasAuthorities(UserAuthority.MANAGE_USERS)
  public UserDto editUser(UserDto oldUser, @RequestBody UserForm userForm) {
    return userFacade.editUser(oldUser, userForm);
  }

  @PatchMapping("/current")
  @FieldFilterSetting(
      className = UserDto.class,
      fields = {"username", "email", "role", "authorities"},
      behaviour = FilterBehaviour.KEEP_FIELDS)
  public UserDto editCurrentUser(@RequestBody @Valid CurrentUserEditDto userChange) {
    return userFacade.editCurrentUser(userChange);
  }

  @PatchMapping("/current/password")
  public ResponseEntity<Object> editCurrentUserPassword(
      @RequestBody @Valid CurrentUserPasswordEditDto passwordChange) {
    userFacade.editCurrentUserPassword(passwordChange);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Object> forgotPassword(
      @RequestBody ForgotPasswordDto forgotRequest, HttpServletRequest httpRequest) {
    forgotRequest.setIp(HttpUtils.getClientIp(httpRequest));
    forgotRequest.setUserAgent(httpRequest.getHeader(HttpHeaders.USER_AGENT));
    try {
      userFacade.sendPasswordResetLink(forgotRequest);
    } catch (FormValidationException | UserNotFoundException ex) {
      // exception is silenced and not propagated to client for security reasons.
      log.error("Request for password reset failed", ex);
    }

    return ResponseEntity.ok().build();
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Object> resetPassword(@RequestBody @Valid ResetPasswordDto resetRequest) {
    userFacade.resetPassword(resetRequest);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(Mappings.PATH_VARIABLE_USER)
  @HasAuthorities(UserAuthority.MANAGE_USERS)
  public ResponseEntity<Object> deleteUser(UserDto user) {
    userFacade.deleteUser(user);
    return ResponseEntity.ok().build();
  }
}
