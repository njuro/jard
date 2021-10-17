package com.github.njuro.jard.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Base DTO for JPA entities inheriting from {@link BaseEntity}. */
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseDto implements Serializable {
  @Serial private static final long serialVersionUID = 2437438129744212745L;

  @JsonIgnore @Getter @Setter protected UUID id;
}
