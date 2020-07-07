package com.github.njuro.jard.ban;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.utils.PathVariableArgumentResolver;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Resolver for mapping board label (path variable) to respective {@link Ban} object
 *
 * @author njuro
 */
@Component
@RequiredArgsConstructor
public class BanResolver implements PathVariableArgumentResolver {

  private final BanFacade banFacade;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(Ban.class);
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {
    String id = getPathVariable(Mappings.PLACEHOLDER_BAN, webRequest);

    return banFacade.resolveBan(UUID.fromString(id));
  }
}
