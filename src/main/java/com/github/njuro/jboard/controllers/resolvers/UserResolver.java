package com.github.njuro.jboard.controllers.resolvers;

import com.github.njuro.jboard.helpers.Mappings;
import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserResolver implements PathVariableArgumentResolver {

  private final UserService userService;

  @Autowired
  public UserResolver(UserService userService) {
    this.userService = userService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(User.class);
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {
    String username = getPathVariable(Mappings.PLACEHOLDER_USER, webRequest);

    return userService.resolveUser(username);
  }
}
