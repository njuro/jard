package com.github.njuro.jard.search;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.config.security.methods.HasAuthorities;
import com.github.njuro.jard.post.dto.PostDto;
import com.github.njuro.jard.user.UserAuthority;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Mappings.API_ROOT_SEARCH)
public class SearchRestController {

  private final SearchFacade searchFacade;

  @Autowired
  public SearchRestController(SearchFacade searchFacade) {
    this.searchFacade = searchFacade;
  }

  @GetMapping("/create-indexes")
  @HasAuthorities(UserAuthority.ACTUATOR_ACCESS)
  public ResponseEntity<String> createIndexes() {
    boolean result = searchFacade.createIndexes();
    return result
        ? ResponseEntity.ok("Indexes created")
        : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Creating indexes failed");
  }

  @GetMapping
  public List<PostDto> searchPosts(@RequestParam(name = "query") String query) {
    return searchFacade.searchPosts(query);
  }
}
