package com.github.njuro.jard.rewrite.utils

import com.github.njuro.jard.common.InputConstraints
import com.github.njuro.jard.common.Mappings
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/** Controller for utilities endpoints.  */
@RestController
class UtilitiesRestController {

    @GetMapping("/")
    fun heartbeat(): ResponseEntity<String> = ResponseEntity.ok("jard API is running")

    @GetMapping(Mappings.API_ROOT + "/input-constraints")
    fun inputConstraints(): InputConstraints.Values = InputConstraints.Values.INSTANCE

    @GetMapping(Mappings.API_ROOT + "/secured")
    fun securedEndpoint(): ResponseEntity<Any> =  ResponseEntity.ok().build() // for testing purposes

}
