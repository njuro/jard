package com.github.njuro.jard

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.njuro.jard.attachment.AttachmentCategory
import com.github.njuro.jard.attachment.AttachmentCategory.AttachmentCategoryDeserializer
import com.github.njuro.jard.attachment.AttachmentCategory.AttachmentCategorySerializer
import com.github.njuro.jard.utils.validation.ValidationErrors
import io.kotest.assertions.withClue
import io.kotest.matchers.maps.shouldContainKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.*
import java.nio.charset.StandardCharsets
import javax.annotation.PostConstruct

@AutoConfigureMockMvc
internal abstract class MockMvcTest : MapperTest() {

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @PostConstruct
    protected fun initModules() {
        objectMapper.registerModule(
            SimpleModule("AttachmentCategoryModule")
                .addSerializer(AttachmentCategory::class.java, AttachmentCategorySerializer())
                .addDeserializer(AttachmentCategory::class.java, AttachmentCategoryDeserializer())
        )
    }

    protected fun MockHttpServletRequestDsl.setUp() {
        contentType = MediaType.APPLICATION_JSON
        accept = MediaType.APPLICATION_JSON
        with(csrf())
    }

    protected fun MockMultipartHttpServletRequestDsl.setUp() {
        accept = MediaType.APPLICATION_JSON
        with(csrf())
    }


    protected fun MockHttpServletRequestDsl.body(body: Any) {
        setUp()
        content = objectMapper.writeValueAsString(body)
    }


    protected fun MockMultipartHttpServletRequestDsl.part(name: String, requestBody: Any) {
        setUp()
        file(
            MockMultipartFile(
                name,
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(requestBody)
            )
        )
    }

    protected fun ResultActionsDsl.andExpectValidationError(field: String) {
        andExpect {
            status { isBadRequest() }
            match(validationError(field))
        }
    }

    protected fun validationError(field: String): ResultMatcher {
        return ResultMatcher { result ->
            val errors = convertResult<ValidationErrors>(result).errors
            withClue("There should be error on field \"$field\"") {
                errors shouldContainKey field
            }
        }
    }

    protected final inline fun <reified T> ResultActionsDsl.andReturnConverted(): T {
        val result = this.andReturn()
        return convertResult(result)
    }

    private inline fun <reified T> convertResult(result: MvcResult): T {
        val typeReference = object : TypeReference<T>() {}
        return objectMapper.readValue(result.response.getContentAsString(StandardCharsets.UTF_8), typeReference)
    }
}