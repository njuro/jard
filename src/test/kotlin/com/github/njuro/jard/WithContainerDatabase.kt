package com.github.njuro.jard

import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.flywaydb.test.annotation.FlywayTest

/**
 * Test annotated with this annotation is provided with PostgreSQL database running inside Docker container.
 *
 * Flyway migrations are applied.
 * **/
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@AutoConfigureEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
@FlywayTest
annotation class WithContainerDatabase