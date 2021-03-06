package com.github.njuro.jard.search.dto;

import com.github.njuro.jard.base.BaseDto;
import com.github.njuro.jard.search.SearchFacade;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Result of searching via {@link SearchFacade}. */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultsDto<T extends BaseDto> {

  /** DTOs matched for given query. */
  private List<T> resultList;

  /** Size of {@link #resultList}. */
  private int resultsCount;

  /** Total matches for given query (can be higher than {@link #resultsCount} */
  private int totalResultsCount;
}
