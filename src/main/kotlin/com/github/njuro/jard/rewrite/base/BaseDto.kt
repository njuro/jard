package com.github.njuro.jard.rewrite.base

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.UUID

/** Base DTO for JPA entities inheriting from [BaseEntity].  */
abstract class BaseDto (@JsonIgnore val id: UUID?)
