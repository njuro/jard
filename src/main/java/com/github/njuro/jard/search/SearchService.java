package com.github.njuro.jard.search;

import com.github.njuro.jard.base.BaseEntity;
import com.github.njuro.jard.common.Constants;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.highlight.*;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SearchService {

  private final FullTextEntityManager fullTextEntityManager;
  private static final Formatter HIGHLIGHT_MARKER =
      new SimpleHTMLFormatter(
          Constants.SEARCH_RESULT_HIGHLIGHT_START, Constants.SEARCH_RESULT_HIGHLIGHT_END);

  @Autowired
  public SearchService(EntityManagerFactory entityManagerFactory) {
    fullTextEntityManager =
        Search.getFullTextEntityManager(entityManagerFactory.createEntityManager());
  }

  /**
   * Creates / rebuilds search indexes.
   *
   * @throws InterruptedException when thread is interrupted before finishing the indexing.
   */
  @PostConstruct
  public void rebuildIndexes() throws InterruptedException {
    fullTextEntityManager.createIndexer().startAndWait();
  }

  /**
   * Searches in entities annotated by {@link org.hibernate.search.annotations.Indexed}.
   *
   * @param queryString user query to search by
   * @param resultClass class of entity to search in
   * @param searchField primary field of entity to search in (must be annotated by {@link
   *     org.hibernate.search.annotations.Field}
   * @param additionalFields additional fields of entity to search in (all must be annotated by
   *     {@link org.hibernate.search.annotations.Field}
   * @return search results for given query ordered by their relevance (top 50 results)
   * @see SearchResults
   */
  @SuppressWarnings("unchecked")
  public <T extends BaseEntity> SearchResults<T> search(
      String queryString, Class<T> resultClass, String searchField, String... additionalFields) {
    var queryBuilder =
        fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(resultClass).get();

    var query =
        queryBuilder
            .simpleQueryString()
            .onFields(searchField)
            .boostedTo(2f)
            .andFields(additionalFields)
            .matching(queryString)
            .createQuery();

    var results = fullTextEntityManager.createFullTextQuery(query, resultClass).setMaxResults(50);

    return SearchResults.<T>builder()
        .highlighter(new Highlighter(HIGHLIGHT_MARKER, new QueryScorer(query)))
        .analyzer(fullTextEntityManager.getSearchFactory().getAnalyzer(resultClass))
        .resultList(results.getResultList())
        .totalResultsCount(results.getResultSize())
        .build();
  }

  /**
   * Marks searched term(s) in given text. Highlighter and Analyzer should be obtained from {@link
   * SearchResults}.
   *
   * @param highlighter {@link Highlighter} to use
   * @param analyzer {@link Analyzer} to use
   * @param fieldName field of entity to highlight
   * @param source original content of entity field
   * @return source with marked searched terms
   * @throws IllegalArgumentException if highlighting process fails
   */
  public String getHighlightedSearchResult(
      Highlighter highlighter, Analyzer analyzer, String fieldName, String source) {
    try {
      String highlighted = highlighter.getBestFragment(analyzer, fieldName, source);
      return highlighted != null ? highlighted : source;
    } catch (IOException | InvalidTokenOffsetsException ex) {
      throw new IllegalArgumentException("Failed to highlight results", ex);
    }
  }
}
