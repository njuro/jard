package com.github.njuro.jard.user;

import com.github.njuro.jard.ban.BanService;
import com.github.njuro.jard.user.token.UserTokenService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final BanService banService;
  private final UserTokenService userTokenService;

  @Autowired
  public UserService(
      UserRepository userRepository, BanService banService, UserTokenService userTokenService) {
    this.userRepository = userRepository;
    this.banService = banService;
    this.userTokenService = userTokenService;
  }

  /**
   * Saves new user to database.
   *
   * @param user user to be saved
   * @return saved user
   */
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  /** @return all users in database */
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  /**
   * Resolves user by given identifier.
   *
   * @param username name of the user
   * @return resolved user
   * @throws UserNotFoundException if such user is not found in database
   */
  public User resolveUser(String username) {
    return userRepository
        .findByUsernameIgnoreCase(username)
        .orElseThrow(UserNotFoundException::new);
  }

  /**
   * Resolves user by given email
   *
   * @param email e-mail address to check
   * @return resolved user or {@code null} if no such user is found
   */
  public User findUserByEmail(String email) {
    return userRepository.findByEmailIgnoreCase(email);
  }

  /**
   * @param username name of the user
   * @return true if user with such username exists, false otherwise
   */
  public boolean doesUserExists(String username) {
    try {
      resolveUser(username);
      return true;
    } catch (UserNotFoundException ex) {
      return false;
    }
  }

  /**
   * @param email e-mail address of the user
   * @return true if user with such email exists, {@code null} otherwise
   */
  public boolean doesEmailExists(String email) {
    return findUserByEmail(email) != null;
  }

  /** @return user currently logged in the system */
  public User getCurrentUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return null;
    }

    Object principal = authentication.getPrincipal();
    if (!(principal instanceof User)) {
      return null;
    }

    return (User) principal;
  }

  /**
   * Checks if currently logged user has given authority
   *
   * @param authority user authority to check
   * @return true if current user has given authority, false otherwise
   */
  public boolean hasCurrentUserAuthority(UserAuthority authority) {
    User current = getCurrentUser();
    if (current == null) {
      return false;
    }

    return current.getAuthorities().contains(authority);
  }

  /**
   * Deletes given user. Also deletes him/her from all of the bans he/she created - however the bans
   * remain active. Additionaly, deletes all tokens issued to given user.
   *
   * @param user user to delete
   */
  public void deleteUser(User user) {
    banService
        .getBansBannedByUser(user)
        .forEach(
            ban -> {
              ban.setBannedBy(null);
              banService.saveBan(ban);
            });
    userTokenService.deleteTokensForUser(user);
    userRepository.delete(user);
  }
}
