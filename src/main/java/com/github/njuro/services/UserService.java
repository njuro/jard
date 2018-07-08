package com.github.njuro.services;


import com.github.njuro.models.User;
import com.github.njuro.models.dto.RegisterForm;
import com.github.njuro.models.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

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
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user;
        }

        throw new UsernameNotFoundException("User '" + username + "' not found");
    }

    public User createUser(RegisterForm registerForm, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setUsername(registerForm.getUsername());
        user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        user.setEmail(registerForm.getEmail());
        user.setRole(UserRole.USER);
        user.setEnabled(true);

        return user;
    }

    public User saveUser(User user) {
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
}

