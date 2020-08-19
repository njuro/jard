package com.github.njuro.jard.search;

import com.github.njuro.jard.base.BaseEntity;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
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
  public <T extends BaseEntity> List<T> search(
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

    return (List<T>)
        fullTextEntityManager
            .createFullTextQuery(query, resultClass)
            .setMaxResults(50)
            .getResultList();
  }
}
