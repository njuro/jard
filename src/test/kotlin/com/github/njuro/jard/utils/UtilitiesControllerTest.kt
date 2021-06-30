package com.github.njuro.jard.utils

import com.github.njuro.jard.MockMvcTest
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.common.InputConstraints
import com.github.njuro.jard.common.Mappings
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

@WithContainerDatabase
internal class UtilitiesControllerTest : MockMvcTest() {

    @Test
    fun heartbeat() {
        mockMvc.get("/") { setUp() }.andExpect {
            status { isOk() }
            content { string("\"jard API is running\"") }
        }
    }

    @Test
    fun `input constraints`() {
        mockMvc.get("${Mappings.API_ROOT}/input-constraints") { setUp() }.andExpect { status { isOk() } }
            .andReturnConverted<InputConstraints.Values>().shouldNotBeNull()
    }
}
