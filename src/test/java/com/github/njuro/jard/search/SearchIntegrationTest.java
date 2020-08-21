package com.github.njuro.jard.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.common.MockRequestTest;
import com.github.njuro.jard.database.UseMockDatabase;
import com.github.njuro.jard.post.dto.PostDto;
import com.github.njuro.jard.search.dto.SearchResultsDto;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@UseMockDatabase
class SearchIntegrationTest extends MockRequestTest {

  @Autowired private SearchFacade searchFacade;

  private static final String API_ROOT = Mappings.API_ROOT_SEARCH;

  @Test
  @DirtiesContext
  void testSearchPosts() throws Exception {
    searchFacade.rebuildIndexes();

    String query = "First";
    var result =
        performMockRequest(HttpMethod.GET, API_ROOT + "?query=" + query)
            .andExpect(status().isOk())
            .andExpect(nonEmptyBody())
            .andReturn();

    @SuppressWarnings("unchecked")
    var searchResults =
        (SearchResultsDto<PostDto>) getResponse(result, SearchResultsDto.class, PostDto.class);

    assertThat(searchResults.getResultList())
        .isNotEmpty()
        .extracting(PostDto::getBody)
        .element(0, InstanceOfAssertFactories.STRING)
        .containsSubsequence(
            Constants.SEARCH_RESULT_HIGHLIGHT_START, query, Constants.SEARCH_RESULT_HIGHLIGHT_END);
  }
}
