package com.github.njuro.jard.rewrite

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters
import java.util.TimeZone
import javax.annotation.PostConstruct

/** Main entry point of the application.  */
@EntityScan(basePackageClasses = [JardApplication::class, Jsr310JpaConverters::class])
@SpringBootApplication
@EnableCaching
@EnableAdminServer
class JardApplication {
    @PostConstruct
    fun init() {
        // set default timezone to UTC
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    fun main(args: Array<String>) {
        runApplication<JardApplication>(*args)
    }
}
