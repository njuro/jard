package com.github.njuro.jard.rewrite.common


/** Mappings for REST endpoints used in controllers.  */

const val PLACEHOLDER_BOARD = "board"
const val PATH_VARIABLE_BOARD = "/{$PLACEHOLDER_BOARD}"
const val PLACEHOLDER_THREAD = "thread"
const val PATH_VARIABLE_THREAD = "/{$PLACEHOLDER_THREAD}"
const val PLACEHOLDER_POST = "post"
const val PATH_VARIABLE_POST = "/{$PLACEHOLDER_POST}"
const val PLACEHOLDER_USER = "user"
const val PATH_VARIABLE_USER = "/{$PLACEHOLDER_USER}"
const val PLACEHOLDER_BAN = "ban"
const val PATH_VARIABLE_BAN = "/{$PLACEHOLDER_BAN}"
const val API_ROOT = "/api"
const val API_ROOT_BOARDS = "$API_ROOT/boards"
const val API_ROOT_THREADS = "$API_ROOT_BOARDS$PATH_VARIABLE_BOARD/thread"
const val API_ROOT_USERS = "$API_ROOT/users"
const val API_ROOT_BANS = "$API_ROOT/bans"
const val API_ROOT_USERCONTENT = "$API_ROOT/usercontent"
const val API_ROOT_SEARCH = "$API_ROOT/search"
