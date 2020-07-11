package com.github.njuro.jard.common;

import lombok.experimental.UtilityClass;

/** Mappings for REST endpoints used in controllers. */
@SuppressWarnings("RedundantModifiersUtilityClassLombok")
@UtilityClass
public class Mappings {

  public static final String PLACEHOLDER_BOARD = "board";
  public static final String PATH_VARIABLE_BOARD = "/{" + PLACEHOLDER_BOARD + "}";

  public static final String PLACEHOLDER_THREAD = "thread";
  public static final String PATH_VARIABLE_THREAD = "/{" + PLACEHOLDER_THREAD + "}";

  public static final String PLACEHOLDER_POST = "post";
  public static final String PATH_VARIABLE_POST = "/{" + PLACEHOLDER_POST + "}";

  public static final String PLACEHOLDER_USER = "user";
  public static final String PATH_VARIABLE_USER = "/{" + PLACEHOLDER_USER + "}";

  public static final String PLACEHOLDER_BAN = "ban";
  public static final String PATH_VARIABLE_BAN = "/{" + PLACEHOLDER_BAN + "}";

  public static final String API_ROOT = "/api";
  public static final String API_ROOT_BOARDS = API_ROOT + "/boards";
  public static final String API_ROOT_THREADS = API_ROOT_BOARDS + PATH_VARIABLE_BOARD + "/thread";
  public static final String API_ROOT_USERS = API_ROOT + "/users";
  public static final String API_ROOT_BANS = API_ROOT + "/bans";
  public static final String API_ROOT_USERCONTENT = API_ROOT + "/usercontent";
}
