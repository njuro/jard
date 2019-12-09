package com.github.njuro.jboard.services;

import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.models.enums.UserAuthority;
import com.github.njuro.jboard.repositories.UserRepository;
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

  public User getUserByEmail(String email) {
    return userRepository.findByEmailIgnoreCase(email);
  }

  public User saveUser(User user) {
    return userRepository.save(user);
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
    return getUserByEmail(email) != null;
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public static User getCurrentUser() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (!(principal instanceof User)) {
      return null;
    }

    return (User) principal;
  }

  public static boolean hasCurrentUserAuthority(UserAuthority authority) {
    User current = UserService.getCurrentUser();
    if (current == null) {
      return false;
    }

    return current.getAuthorities().contains(authority);
  }
}
