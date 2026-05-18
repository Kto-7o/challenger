package com.innovationCampus.challenger.security;

import com.innovationCampus.challenger.entities.Role;
import com.innovationCampus.challenger.entities.User;
import com.innovationCampus.challenger.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.HashSet;

public class WithAppUserSecurityContextFactory implements WithSecurityContextFactory<WithAppUser> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SecurityContext createSecurityContext(WithAppUser annotation) {
        String email = annotation.email();

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setId(annotation.id());
            newUser.setEmail(email);
            newUser.setUsername(annotation.username());
            newUser.setTag(annotation.tag());
            newUser.setPassword(passwordEncoder.encode(annotation.password()));
            newUser.setRole(Role.AUTHTORIZED);
            newUser.setFriends(new HashSet<>());
            newUser.setChallenges(new HashSet<>());
            newUser.setHistory(new HashSet<>());
            return userRepository.save(newUser);
        });

        var auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
