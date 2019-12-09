package com.github.njuro.jboard.config.security;

import com.comparator.Comparator;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.enums.UserAuthority;
import com.github.njuro.jboard.services.UserService;
import com.jfilter.filter.DynamicFilterComponent;
import com.jfilter.filter.DynamicFilterEvent;
import com.jfilter.filter.FilterFields;
import com.jfilter.request.RequestSession;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;

@DynamicFilterComponent
public class SensitiveDataFilter implements DynamicFilterEvent {

  private final UserService userService;

  @Autowired
  public SensitiveDataFilter(final UserService userService) {
    this.userService = userService;
  }

  @Override
  public void onRequest(final Comparator<RequestSession, FilterFields> comparator) {
    comparator.compare(
        request -> !UserService.hasCurrentUserAuthority(UserAuthority.VIEW_IP),
        result -> FilterFields.getFieldsBy(Post.class, Collections.singletonList("ip")));
  }
}
