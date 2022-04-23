package com.github.njuro.jard.rewrite.utils

import com.comparator.Comparator
import com.github.njuro.jard.post.Post_
import com.github.njuro.jard.post.dto.PostDto
import com.github.njuro.jard.user.UserAuthority
import com.github.njuro.jard.user.UserFacade
import com.jfilter.filter.DynamicFilterComponent
import com.jfilter.filter.DynamicFilterEvent
import com.jfilter.filter.FilterFields
import com.jfilter.filter.FilterFields.getFieldsBy
import com.jfilter.request.RequestSession
import lombok.RequiredArgsConstructor

/** Filter to dynamically remove sensitive data from JSON responses for certain users.  */
@DynamicFilterComponent
@RequiredArgsConstructor
class SensitiveDataFilter(private val userFacade: UserFacade) : DynamicFilterEvent {
    override fun onRequest(comparator: Comparator<RequestSession, FilterFields>) {
        // only authenticated users with VIEW_IP authority can view posters IP addresses
        comparator.compare(
            { request -> !userFacade.hasCurrentUserAuthority(UserAuthority.VIEW_IP) },
            { response -> getFieldsBy(PostDto::class.java, listOf(Post_.IP)) }
        )
    }
}
