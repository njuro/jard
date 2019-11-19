package com.github.njuro.jboard.services;


import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.models.dto.CurrentUser;
import com.github.njuro.jboard.models.dto.RegisterForm;
import com.github.njuro.jboard.models.enums.UserRole;
import com.github.njuro.jboard.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final RoleHierarchy roleHierarchy;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleHierarchy roleHierarchy) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleHierarchy = roleHierarchy;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("User " + username + "does not exists"));
    }

    /**
     * Gets user by its email. The search is case-insensitive
     *
     * @param email
     * @return user with given email, or null, if none was found
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    /**
     * Creates user from {@link RegisterForm}. Encrypts password with {@link PasswordEncoder}. Sets default
     * {@link UserRole} of user and enables it.
     *
     * @param registerForm    with user details
     * @return created user
     */
    public User createUser(RegisterForm registerForm) {
        User user = User.builder()
                .username(registerForm.getUsername())
                .password(passwordEncoder.encode(registerForm.getPassword()))
                .email(registerForm.getEmail())
                .registrationIp(registerForm.getRegistrationIp())
                .role(UserRole.USER)
                .enabled(true)
                .build();

        //log.debug("Created user {}", user);
        return saveUser(user);
    }

    /**
     * Saves user to database
     *
     * @param user to save
     * @return saved user
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Checks if user with given username exists
     *
     * @param username to check
     * @return true if user with this username exists, false otherwise
     */
    public boolean doesUserExists(String username) {
        try {
            loadUserByUsername(username);
            return true;
        } catch (UsernameNotFoundException unfe) {
            return false;
        }
    }

    /**
     * Checks if user with given e-mail exists
     *
     * @param email to check
     * @return true if user with this e-mail exists, false otherwise
     */
    public boolean doesEmailExists(String email) {
        return getUserByEmail(email) != null;
    }

    /**
     * Return current logged {@link User}
     *
     * @return Instance of {@link User} or {@code null}, if no user is logged in
     */
    public static User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof User)) {
            return null;
        }

        return (User) principal;
    }

    public CurrentUser getCurrentUserReduced() {
        User user = getCurrentUser();

        return (user == null) ? null : new CurrentUser(user.getUsername(), user.getRole(), roleHierarchy.getReachableGrantedAuthorities(user.getAuthorities()));
    }
}

