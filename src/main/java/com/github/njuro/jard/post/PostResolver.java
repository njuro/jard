package com.github.njuro.jard.post;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.utils.PathVariableArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Resolver for mapping post number and board label (path variables) to respective {@link Post}
 * object
 *
 * @author njuro
 */
@Component
@RequiredArgsConstructor
public class PostResolver implements PathVariableArgumentResolver {

  private final PostFacade postFacade;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(Post.class);
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {
    String board = getPathVariable(Mappings.PLACEHOLDER_BOARD, webRequest);
    Long postNumber = Long.valueOf(getPathVariable(Mappings.PLACEHOLDER_POST, webRequest));

    return postFacade.resolvePost(board, postNumber);
  }
}
