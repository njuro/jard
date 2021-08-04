package com.github.njuro.jard.user;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.user.dto.UserDto;
import com.github.njuro.jard.utils.PathVariableArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/** Resolver for mapping username (path variable) to respective {@link UserDto} object */
@Component
@RequiredArgsConstructor
public class UserResolver implements PathVariableArgumentResolver {

  private final UserFacade userFacade;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(UserDto.class);
  }

  @Override
  public Object resolveArgument(
      @NotNull MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      @NotNull NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {
    String username = getPathVariable(Mappings.PLACEHOLDER_USER, webRequest);

    return userFacade.resolveUser(username);
  }
}
