package com.github.njuro.jard.search

import com.github.njuro.jard.MockMvcTest
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithMockJardUser
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.post.dto.PostDto
import com.github.njuro.jard.search.dto.SearchResultsDto
import com.github.njuro.jard.user.UserAuthority
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.slot
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WithContainerDatabase
internal class SearchControllerTest : MockMvcTest() {

    @MockkBean
    private lateinit var searchFacade: SearchFacade

    @Nested
    @DisplayName("rebuild indexes")
    @WithMockJardUser(UserAuthority.ACTUATOR_ACCESS)
    inner class RebuildIndexes {
        private fun rebuildIndexes() = mockMvc.post("${Mappings.API_ROOT_SEARCH}/rebuild-indexes") { setUp() }

        @Test
        fun success() {
            every { searchFacade.rebuildIndexes() } returns true
            rebuildIndexes().andExpect { status { isOk() } }
        }

        @Test
        fun failure() {
            every { searchFacade.rebuildIndexes() } returns false
            rebuildIndexes().andExpect { status { isInternalServerError() } }
        }
    }

    @Test
    fun `search posts`() {
        val query = slot<String>()
        every { searchFacade.searchPosts(capture(query)) } returns SearchResultsDto.builder<PostDto>()
            .resultsCount(2)
            .totalResultsCount(5)
            .resultList(listOf(PostDto.builder().build()))
            .build()

        mockMvc.get("${Mappings.API_ROOT_SEARCH}?query=test-query") { setUp() }.andExpect { status { isOk() } }
            .andReturnConverted<SearchResultsDto<PostDto>>().shouldNotBeNull()
        query.captured shouldBe "test-query"
    }
}
