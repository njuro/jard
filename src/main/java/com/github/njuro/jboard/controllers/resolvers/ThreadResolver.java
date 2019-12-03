package com.github.njuro.jboard.controllers.resolvers;

import com.github.njuro.jboard.helpers.Mappings;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Resolver for mapping thread number and board label (path variables) to respective {@link Thread}
 * object
 *
 * @author njuro
 */
@Component
public class ThreadResolver implements PathVariableArgumentResolver {

  private final ThreadService threadService;

  @Autowired
  public ThreadResolver(ThreadService threadService) {
    this.threadService = threadService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(Thread.class);
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {
    Long threadNumber = Long.valueOf(getPathVariable(Mappings.PLACEHOLDER_THREAD, webRequest));
    String board = getPathVariable(Mappings.PLACEHOLDER_BOARD, webRequest);

    return threadService.resolveThread(board, threadNumber);
  }
}
