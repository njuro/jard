package com.github.njuro.jard.base;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Base abstract JPA entity with auto-generated {@link UUID}. */
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
public abstract class BaseEntity implements Serializable {
  private static final long serialVersionUID = -8206522330125773358L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false)
  @Getter
  @Setter
  protected UUID id;
}
