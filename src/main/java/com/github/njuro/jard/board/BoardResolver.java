package com.github.njuro.jard.board;

import com.github.njuro.jard.board.dto.BoardDto;
import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.utils.PathVariableArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/** Resolver for mapping board label from path variables to respective {@link BoardDto} object. */
@Component
@RequiredArgsConstructor
public class BoardResolver implements PathVariableArgumentResolver {

  private final BoardFacade boardFacade;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(BoardDto.class);
  }

  @Override
  public Object resolveArgument(
      @NotNull MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      @NotNull NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {
    String label = getPathVariable(Mappings.PLACEHOLDER_BOARD, webRequest);

    return boardFacade.resolveBoard(label);
  }
}
