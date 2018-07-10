package com.github.njuro.jboard.services;


import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.models.dto.RegisterForm;
import com.github.njuro.jboard.models.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);

    User findByEmailIgnoreCase(String email);
}

/**
 * Service methods for manipulating {@link User users}
 *
 * @author njuro
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
     * Creates user from {@link RegisterForm}. Encrypts password with provided {@link PasswordEncoder}. Sets default
     * {@link UserRole} of user and enables it.
     *
     * @param registerForm    with user details
     * @param passwordEncoder to be used for password encryption
     * @return created user
     */
    public User createUser(RegisterForm registerForm, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setUsername(registerForm.getUsername());
        user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        user.setEmail(registerForm.getEmail());
        user.setRole(UserRole.USER);
        user.setEnabled(true);

        return user;
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
}

