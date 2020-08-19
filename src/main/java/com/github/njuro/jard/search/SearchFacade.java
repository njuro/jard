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
    return postMapper.toDtoList(
        searchService.search(query, Post.class, Post_.BODY, Post_.NAME, Post_.TRIPCODE));
  }
}
