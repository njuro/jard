package com.github.njuro.jard.post.decorators

import com.github.njuro.jard.post.Post
import mu.KotlinLogging
import org.junit.jupiter.api.BeforeAll

private val log = KotlinLogging.logger { }

internal abstract class PostDecoratorTest {

    private val post: Post = Post.builder().build()
    private lateinit var decorator: PostDecorator

    @BeforeAll
    fun initAll() {
        decorator = initDecorator()
    }

    protected abstract fun initDecorator(): PostDecorator

    protected fun decorate(body: String) = decorate(post, body)

    protected fun decorate(post: Post, body: String): String {
        post.body = body
        decorator.decorate(post)
        log.info { "$body -> ${post.body}" }
        return post.body
    }

}