package com.github.njuro.jard

import org.springframework.context.annotation.Import

/**
 * Annotation which enables to autowire [TestDataRepository] into test.
 * */
@Target(AnnotationTarget.CLASS)
@Import(TestDataRepository::class)
internal annotation class WithTestDataRepository
