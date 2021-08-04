package com.github.njuro.jard.ban;

import com.github.njuro.jard.ban.dto.BanDto;
import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.utils.PathVariableArgumentResolver;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/** Resolver for mapping ban id (path variable) to respective {@link BanDto} object */
@Component
@RequiredArgsConstructor
public class BanResolver implements PathVariableArgumentResolver {

  private final BanFacade banFacade;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(BanDto.class);
  }

  @Override
  public Object resolveArgument(
      @NotNull MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      @NotNull NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {
    String id = getPathVariable(Mappings.PLACEHOLDER_BAN, webRequest);

    return banFacade.resolveBan(UUID.fromString(id));
  }
}
