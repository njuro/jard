package com.github.njuro.jard.search;

import com.github.njuro.jard.base.BaseEntity;
import com.github.njuro.jard.common.Constants;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SearchService {

  private final FullTextEntityManager fullTextEntityManager;

  @Autowired
  public SearchService(EntityManagerFactory entityManagerFactory) {
    fullTextEntityManager =
        Search.getFullTextEntityManager(entityManagerFactory.createEntityManager());
  }

  @PostConstruct
  public void createIndexes() throws InterruptedException {
    fullTextEntityManager.createIndexer().startAndWait();
  }

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

    var results =
        (List<T>)
            fullTextEntityManager
                .createFullTextQuery(query, resultClass)
                .setMaxResults(50)
                .getResultList();

    var highlighter = makeHighlighter(query);
    var analyzer = fullTextEntityManager.getSearchFactory().getAnalyzer(resultClass);

    return new SearchResults<T>(results, highlighter, analyzer);
  }

  private Highlighter makeHighlighter(Query query) {
    var formatter =
        new SimpleHTMLFormatter(
            Constants.SEARCH_RESULT_HIGHLIGHT_START, Constants.SEARCH_RESULT_HIGHLIGHT_END);
    return new Highlighter(formatter, new QueryScorer(query));
  }

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
