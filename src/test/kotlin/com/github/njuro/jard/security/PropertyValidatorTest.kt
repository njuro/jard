package com.github.njuro.jard.security

import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.utils.validation.PropertyValidationException
import com.github.njuro.jard.utils.validation.PropertyValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.validation.constraints.Email
import javax.validation.constraints.Size

@SpringBootTest
@WithContainerDatabase
internal class PropertyValidatorTest {

    @Autowired
    private lateinit var propertyValidator: PropertyValidator

    @Test
    fun `validate whole object`() {
        val testEntity = TestEntity(email = "abcde", "ab")
        val result = shouldThrow<PropertyValidationException> {
            propertyValidator.validateObject(testEntity)
        }

        result.bindingResult.fieldErrors shouldHaveSize 2
    }

    @Test
    fun `validate single property`() {
        val testEntity = TestEntity(email = "abcde", "ab")
        val result = shouldThrow<PropertyValidationException> {
            propertyValidator.validateProperty(testEntity, "email")
        }
        result.message.shouldContain("email").shouldNotContain("password")
    }

    private data class TestEntity(@field:Email val email: String, @field:Size(min = 3) val password: String)
}
