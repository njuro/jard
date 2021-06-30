package com.github.njuro.jard.search

import org.springframework.test.context.TestPropertySource

@Target(AnnotationTarget.CLASS)
@TestPropertySource(properties = ["spring.jpa.properties.hibernate.search.enabled=true"])
annotation class EnableSearch
