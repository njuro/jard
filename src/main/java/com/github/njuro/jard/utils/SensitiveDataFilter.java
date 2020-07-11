package com.github.njuro.jard.utils;

import com.comparator.Comparator;
import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.user.UserAuthority;
import com.github.njuro.jard.user.UserFacade;
import com.jfilter.filter.DynamicFilterComponent;
import com.jfilter.filter.DynamicFilterEvent;
import com.jfilter.filter.FilterFields;
import com.jfilter.request.RequestSession;
import java.util.Collections;
import lombok.RequiredArgsConstructor;

/** Filter to dynamically remove sensitive data from JSON responses for certain users. */
@DynamicFilterComponent
@RequiredArgsConstructor
public class SensitiveDataFilter implements DynamicFilterEvent {

  private final UserFacade userFacade;

  @Override
  public void onRequest(Comparator<RequestSession, FilterFields> comparator) {
    // only authenticated users with VIEW_IP authority can view posters IP addresses
    comparator.compare(
        request -> !userFacade.hasCurrentUserAuthority(UserAuthority.VIEW_IP),
        result -> FilterFields.getFieldsBy(Post.class, Collections.singletonList("ip")));
  }
}
