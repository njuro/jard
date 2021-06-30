package com.github.njuro.jard.attachment

import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.attachment
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional

@DataJpaTest
@WithContainerDatabase
@Transactional
internal class AttachmentRepositoryTest {

    @Autowired
    private lateinit var attachmentRepository: AttachmentRepository

    @Test
    fun `save attachment`() {
        attachmentRepository.save(attachment()).shouldNotBeNull()
    }
}
