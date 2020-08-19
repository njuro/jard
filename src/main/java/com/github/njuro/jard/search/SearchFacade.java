package com.github.njuro.jard.search;

import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.post.PostMapper;
import com.github.njuro.jard.post.Post_;
import com.github.njuro.jard.post.dto.PostDto;
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

  public boolean createIndexes() {
    try {
      searchService.createIndexes();
      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }

  public List<PostDto> searchPosts(String query) {
    SearchResults<Post> searchResults =
        searchService.search(query, Post.class, Post_.BODY, Post_.NAME, Post_.TRIPCODE);

    List<PostDto> posts = postMapper.toDtoList(searchResults.getResults());
    posts.forEach(
        post ->
            post.setBody(
                searchService.getHighlightedSearchResult(
                    searchResults.getHighlighter(),
                    searchResults.getAnalyzer(),
                    Post_.BODY,
                    post.getBody())));

    return posts;
  }
}
