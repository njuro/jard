package com.github.njuro.jard.database;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@DBRider
@DataSet(
    value = "/datasets/test.yml",
    useSequenceFiltering = false,
    disableConstraints = true,
    transactional = true)
public @interface UseMockDatabase {

  @AliasFor(annotation = DataSet.class, attribute = "value")
  String[] value() default "/datasets/test.yml";
}
