package com.github.njuro.jboard.helpers;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Mappings {

    public static final String PLACEHOLDER_BOARD = "board";
    public static final String PATH_VARIABLE_BOARD = "/{" + PLACEHOLDER_BOARD + "}";

    public static final String PLACEHOLDER_THREAD = "thread";
    public static final String PATH_VARIABLE_THREAD = "/{" + PLACEHOLDER_THREAD + "}";

    public static final String PLACEHOLDER_POST = "post";
    public static final String PATH_VARIABLE_POST = "/{" + PLACEHOLDER_POST + "}";

    public static final String API_ROOT = "/api";
    public static final String API_ROOT_BOARDS = API_ROOT + "/boards";
    public static final String API_ROOT_THREADS = API_ROOT_BOARDS + PATH_VARIABLE_BOARD;
    public static final String API_ROOT_USERS = API_ROOT + "/users";

}
