package com.github.njuro.jard.search;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.highlight.Highlighter;

@Getter
@AllArgsConstructor
public class SearchResults<T> {

  private final List<T> results;
  private final Highlighter highlighter;
  private final Analyzer analyzer;
}
