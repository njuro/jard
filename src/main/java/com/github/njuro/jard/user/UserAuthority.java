package com.github.njuro.jard.user;

import com.github.njuro.jard.ban.Ban;
import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.config.security.methods.HasAuthorities;
import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.thread.Thread;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;

/**
 * Enum representing authority, which can be granted to {@link User} and enable him/her additional
 * capabilities in the system.
 *
 * @see UserRole
 * @see HasAuthorities
 */
public enum UserAuthority implements GrantedAuthority {
  /** Allows to create/update/delete {@link Board} and their settings */
  MANAGE_BOARDS,
  /** Allows to toggle {@code stickied} status of {@link Thread} */
  TOGGLE_STICKY_THREAD,
  /** Allows to toggle {@code locked} status of {@link Thread} */
  TOGGLE_LOCK_THREAD,
  /** Allows to delete {@link Post} or whole {@link Thread} */
  DELETE_POST,
  /** Allows to create/update/delete {@link User} */
  MANAGE_USERS,
  /** Allows to view IP from which the {@link Post} was created. */
  VIEW_IP,
  /** Allows to create/update/delete {@link Ban} */
  MANAGE_BANS;

  @Override
  public String getAuthority() {
    return name();
  }

  public static Set<UserAuthority> getAllAuthorities() {
    return Set.of(values());
  }
}
