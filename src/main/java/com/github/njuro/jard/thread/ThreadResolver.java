package com.github.njuro.jard.thread;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.thread.dto.ThreadDto;
import com.github.njuro.jard.utils.PathVariableArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Resolver for mapping thread number (= its original post's number) and board label from path
 * variables to respective {@link Thread} object
 */
@Component
@RequiredArgsConstructor
public class ThreadResolver implements PathVariableArgumentResolver {

  private final ThreadFacade threadFacade;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(ThreadDto.class);
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {
    String board = getPathVariable(Mappings.PLACEHOLDER_BOARD, webRequest);
    Long threadNumber = Long.valueOf(getPathVariable(Mappings.PLACEHOLDER_THREAD, webRequest));

    return threadFacade.resolveThread(board, threadNumber);
  }
}
