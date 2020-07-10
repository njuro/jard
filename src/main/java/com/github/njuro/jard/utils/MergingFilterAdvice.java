package com.github.njuro.jard.utils;

import com.jfilter.components.DynamicFilterProvider;
import com.jfilter.components.FilterConfiguration;
import com.jfilter.components.FilterProvider;
import com.jfilter.converter.FilterClassWrapper;
import com.jfilter.converter.MethodParameterDetails;
import com.jfilter.filter.BaseFilter;
import com.jfilter.filter.FilterFields;
import com.jfilter.request.RequestSession;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * This class is a customized version of jfilter {@link com.jfilter.components.FilterAdvice}.
 * Contrary to original version it enables merging of filter fields from both static and dynamic
 * filters. This is a temporary workaround until jfilter releases new version enabling this
 * behaviour by default. (watch <a href="https://github.com/rkonovalov/jfilter/issues/16">issue on
 * GitHub</a>).
 *
 * @author rkonovalov, njuro
 */
@RestControllerAdvice
public class MergingFilterAdvice implements ResponseBodyAdvice<Object> {

  private FilterProvider filterProvider;
  private DynamicFilterProvider dynamicFilterProvider;
  private FilterConfiguration filterConfiguration;

  @Autowired
  public void setFilterProvider(FilterProvider filterProvider) {
    this.filterProvider = filterProvider;
  }

  @Autowired
  public MergingFilterAdvice setDynamicFilterProvider(DynamicFilterProvider dynamicFilterProvider) {
    this.dynamicFilterProvider = dynamicFilterProvider;
    return this;
  }

  @Autowired
  public MergingFilterAdvice setFilterConfiguration(FilterConfiguration filterConfiguration) {
    this.filterConfiguration = filterConfiguration;
    return this;
  }

  /**
   * Attempt to find annotations in method and associated filter
   *
   * @param methodParameter {@link MethodParameter}
   * @param aClass {@link HttpMessageConverter}
   * @return true if found, else false
   */
  @Override
  public boolean supports(
      MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
    return filterConfiguration.isEnabled()
        && (filterProvider.isAccept(methodParameter)
            || DynamicFilterProvider.isAccept(methodParameter));
  }

  /**
   * Attempt to find filter and extract ignorable fields from methodParameter
   *
   * @param obj {@link Object} object sent from response of Spring Web Service
   * @param methodParameter {@link MethodParameter}
   * @param mediaType {@link MediaType}
   * @param aClass {@link HttpMessageConverter}
   * @param serverHttpRequest {@link ServerHttpRequest}
   * @param serverHttpResponse {@link ServerHttpResponse}
   * @return {@link FilterClassWrapper} if BaseFilter is found FilterClassWrapper contains list of
   *     ignorable fields, else returns FilterClassWrapper with HashMap zero length
   */
  @Override
  public Serializable beforeBodyWrite(
      Object obj,
      MethodParameter methodParameter,
      MediaType mediaType,
      Class<? extends HttpMessageConverter<?>> aClass,
      ServerHttpRequest serverHttpRequest,
      ServerHttpResponse serverHttpResponse) {
    FilterFields filterFields = FilterFields.EMPTY_FIELDS;

    // Getting HttpServletRequest from serverHttpRequest
    HttpServletRequest servletServerHttpRequest =
        ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
    RequestSession requestSession = new RequestSession(servletServerHttpRequest);

    // Proccess filters
    BaseFilter filter = filterProvider.getFilter(methodParameter);
    if (filter != null) {
      // Get fields from static filter
      filterFields = filter.getFields(obj, requestSession);
    }

    // Get fields from dynamic filter
    FilterFields dynamicFilterFields =
        dynamicFilterProvider.getFields(methodParameter, requestSession);
    dynamicFilterFields.getFieldsMap().forEach(filterFields::appendToMap);

    MethodParameterDetails methodParameterDetails =
        new MethodParameterDetails(methodParameter, mediaType, filterFields);

    return new FilterClassWrapper(obj, methodParameterDetails);
  }
}
