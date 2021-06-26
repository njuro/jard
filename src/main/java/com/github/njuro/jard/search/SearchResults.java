package com.github.njuro.jard.search;

import com.github.njuro.jard.base.BaseEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.highlight.Highlighter;

/** Result of searching via {@link SearchService}. */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResults<T extends BaseEntity> {

  /** List of entities matched by given query. */
  private List<T> entityList;

  /** Total results found for given query (can be higher than size of {@link #entityList}. */
  private long totalResultsCount;

  /** Lucene {@link Analyzer} for this search. */
  private Analyzer analyzer;

  /** Lucene {@link Highlighter} for this search. */
  private Highlighter highlighter;
}
