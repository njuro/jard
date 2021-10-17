package com.github.njuro.jard.search;

import com.github.njuro.jard.base.BaseEntity;
import com.github.njuro.jard.common.Constants;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.highlight.*;
import org.hibernate.search.backend.lucene.LuceneBackend;
import org.hibernate.search.backend.lucene.search.spi.LuceneMigrationUtils;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@ConditionalOnProperty(
    value = "spring.jpa.properties.hibernate.search.enabled",
    havingValue = "true")
public class SearchService {

  private final SearchSession searchSession;
  private final Formatter highlightFormatter;

  @Autowired
  public SearchService(EntityManagerFactory entityManagerFactory) {
    searchSession = Search.session(entityManagerFactory.createEntityManager());
    highlightFormatter =
        new SimpleHTMLFormatter(
            Constants.SEARCH_RESULT_HIGHLIGHT_START, Constants.SEARCH_RESULT_HIGHLIGHT_END);
  }

  /**
   * Creates / rebuilds search indexes.
   *
   * @throws InterruptedException when thread is interrupted before finishing the indexing.
   */
  @PostConstruct
  public void rebuildIndexes() throws InterruptedException {
    searchSession.massIndexer().startAndWait();
  }

  /**
   * Searches in entities annotated by {@link Indexed}.
   *
   * @param specification for this search
   * @return search results for given query ordered by their relevance (top 50 results)
   * @see SearchSpecification
   * @see SearchResults
   */
  public <T extends BaseEntity> SearchResults<T> search(SearchSpecification<T> specification) {

    var predicate = getPredicate(specification);
    var results =
        searchSession
            .search(specification.getEntityClass())
            .where(factory -> factory.bool(clause -> clause.must(predicate)))
            .fetch(Constants.MAX_SEARCH_RESULTS_COUNT);

    return SearchResults.<T>builder()
        .entityList(results.hits())
        .totalResultsCount(results.total().hitCount())
        .analyzer(getAnalyzer(specification.getAnalyzerName()))
        .highlighter(getHighlighter(predicate))
        .build();
  }

  /**
   * Marks searched term(s) in given text. Highlighter and Analyzer should be obtained from {@link
   * SearchResults}.
   *
   * @param highlighter {@link Highlighter} to use
   * @param analyzer {@link Analyzer} to user
   * @param fieldName field of entity to highlight
   * @param originalContent original content of entity field
   * @return source with marked searched terms
   * @throws IllegalArgumentException if highlighting process fails
   */
  public String getHighlightedSearchResult(
      Highlighter highlighter, Analyzer analyzer, String fieldName, String originalContent) {
    try {
      String highlighted = highlighter.getBestFragment(analyzer, fieldName, originalContent);
      return highlighted != null ? highlighted : originalContent;
    } catch (IOException | InvalidTokenOffsetsException ex) {
      throw new IllegalArgumentException("Failed to highlight results", ex);
    }
  }

  private <T extends BaseEntity> SearchPredicate getPredicate(
      SearchSpecification<T> specification) {
    var predicateFactory = searchSession.scope(specification.getEntityClass()).predicate();
    return predicateFactory
        .simpleQueryString()
        .field(specification.getPrimaryField())
        .boost(2f)
        .fields(specification.getAdditionalFields().toArray(String[]::new))
        .matching(specification.getQuery())
        .toPredicate();
  }

  private Analyzer getAnalyzer(String analyzerName) {
    return Search.mapping(searchSession.toEntityManager().getEntityManagerFactory())
        .backend()
        .unwrap(LuceneBackend.class)
        .analyzer(analyzerName)
        .orElseThrow(
            () -> new IllegalStateException("Analyzer %s is not defined".formatted(analyzerName)));
  }

  private Highlighter getHighlighter(SearchPredicate predicate) {
    var queryScorer = new QueryScorer(LuceneMigrationUtils.toLuceneQuery(predicate));
    return new Highlighter(highlightFormatter, queryScorer);
  }
}
