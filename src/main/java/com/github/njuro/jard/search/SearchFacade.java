package com.github.njuro.jard.search;

import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.post.PostMapper;
import com.github.njuro.jard.post.Post_;
import com.github.njuro.jard.post.dto.PostDto;
import com.github.njuro.jard.search.dto.SearchResultsDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchFacade {

  private final SearchService searchService;
  private final PostMapper postMapper;

  @Autowired
  public SearchFacade(SearchService searchService, PostMapper postMapper) {
    this.searchService = searchService;
    this.postMapper = postMapper;
  }

  /**
   * Rebuilds search indexes.
   *
   * @return true if rebuilding was finished successfully, false otherwise.
   */
  public boolean rebuildIndexes() {
    try {
      searchService.rebuildIndexes();
      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }

  /**
   * Searches across all posts.
   *
   * @param query user query to search by
   * @return list of matched posts for given query with highlighted matches, ordered by relevance
   *     (top 50 results)
   * @see SearchResultsDto
   */
  public SearchResultsDto<PostDto> searchPosts(String query) {
    SearchResults<Post> searchResults =
        searchService.search(query, Post.class, Post_.BODY, Post_.NAME, Post_.TRIPCODE);

    List<PostDto> posts = postMapper.toDtoList(searchResults.getResultList());
    posts.forEach(
        post ->
            post.setBody(
                searchService.getHighlightedSearchResult(
                    searchResults.getHighlighter(),
                    searchResults.getAnalyzer(),
                    Post_.BODY,
                    post.getBody())));

    return SearchResultsDto.<PostDto>builder()
        .resultList(posts)
        .resultsCount(posts.size())
        .totalResultsCount(searchResults.getTotalResultsCount())
        .build();
  }
}
