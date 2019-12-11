package com.github.njuro.jboard.user;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service methods for manipulating {@link User users}
 *
 * @author njuro
 */
@Service
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new UsernameNotFoundException("User " + username + "does not exists"));
  }

  public User saveUser(User user) {
    return userRepository.save(user);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User resolveUser(String username) {
    return userRepository.findByUsernameIgnoreCase(username).orElse(null);
  }

  public User findUserByEmail(String email) {
    return userRepository.findByEmailIgnoreCase(email);
  }

  public boolean doesUserExists(String username) {
    try {
      loadUserByUsername(username);
      return true;
    } catch (UsernameNotFoundException unfe) {
      return false;
    }
  }

  public boolean doesEmailExists(String email) {
    return findUserByEmail(email) != null;
  }

  public User getCurrentUser() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (!(principal instanceof User)) {
      return null;
    }

    return (User) principal;
  }

  public boolean hasCurrentUserAuthority(UserAuthority authority) {
    User current = getCurrentUser();
    if (current == null) {
      return false;
    }

    return current.getAuthorities().contains(authority);
  }

  public User updateUser(User user) {
    return userRepository.save(user);
  }

  public void deleteUser(User user) {
    // TODO delete all bans from user
    userRepository.delete(user);
  }
}
