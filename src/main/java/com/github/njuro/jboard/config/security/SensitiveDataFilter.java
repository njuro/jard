package com.github.njuro.jboard.config.security;

import com.comparator.Comparator;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.enums.UserAuthority;
import com.github.njuro.jboard.services.UserService;
import com.jfilter.filter.DynamicFilterComponent;
import com.jfilter.filter.DynamicFilterEvent;
import com.jfilter.filter.FilterFields;
import com.jfilter.request.RequestSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

@DynamicFilterComponent
public class SensitiveDataFilter implements DynamicFilterEvent {

    private final UserService userService;

    @Autowired
    public SensitiveDataFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onRequest(Comparator<RequestSession, FilterFields> comparator) {
        comparator.compare(
                request -> !userService.hasCurrentUserAuthority(UserAuthority.VIEW_IP),
                result -> FilterFields.getFieldsBy(Post.class, Collections.singletonList("ip"))
        );
    }
}
