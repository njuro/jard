package com.github.njuro.jard.search;

import com.github.njuro.jard.base.BaseEntity;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Query for searching via {@link SearchService} */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchSpecification<T extends BaseEntity> {

  /** Input for searching. */
  private String query;

  /** Class of entity to search. */
  private Class<T> entityClass;

  /** Name of Hibernate Search analyzer to use. */
  private String analyzerName;

  /** Primary field on which search should be conducted. */
  private String primaryField;

  /** Optional additional fields on which search should be conducted. */
  private Set<String> additionalFields;
}
