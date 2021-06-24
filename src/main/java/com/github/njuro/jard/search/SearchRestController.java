package com.github.njuro.jard.search;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.config.security.methods.HasAuthorities;
import com.github.njuro.jard.post.dto.PostDto;
import com.github.njuro.jard.search.dto.SearchResultsDto;
import com.github.njuro.jard.user.UserAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Mappings.API_ROOT_SEARCH)
public class SearchRestController {

  private final SearchFacade searchFacade;

  @Autowired
  public SearchRestController(SearchFacade searchFacade) {
    this.searchFacade = searchFacade;
  }

  @PostMapping("/rebuild-indexes")
  @HasAuthorities(UserAuthority.ACTUATOR_ACCESS)
  public ResponseEntity<String> rebuildIndexes() {
    boolean result = searchFacade.rebuildIndexes();
    return result
        ? ResponseEntity.ok("Search indexes rebuilt")
        : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Rebuilding serach indexes failed");
  }

  @GetMapping
  public SearchResultsDto<PostDto> searchPosts(@RequestParam(name = "query") String query) {
    return searchFacade.searchPosts(query);
  }
}
