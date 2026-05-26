package com.innovationCampus.challenger.services;

import com.innovationCampus.challenger.dto.JwtAuthenticationResponse;
import com.innovationCampus.challenger.dto.SignInRequest;
import com.innovationCampus.challenger.dto.SignUpRequest;
import com.innovationCampus.challenger.dto.TagCheckDto;
import com.innovationCampus.challenger.entities.Role;
import com.innovationCampus.challenger.entities.User;
import com.innovationCampus.challenger.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = new User();
        user.setUsername(request.username());
        user.setTag(request.tag());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.AUTHTORIZED);

        userRepository.save(user);

        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().accessToken(jwt).build();
    }

    public JwtAuthenticationResponse signin(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().accessToken(jwt).build();
    }

    public TagCheckDto checkTagAvailability(String tag) {
        log.debug("AuthService.checkTagAvailability starting for tag={}", tag);
        boolean available = userRepository.findByTag(tag).isEmpty();
        TagCheckDto result = new TagCheckDto(available);
        log.debug("AuthService.checkTagAvailability result for tag={}: {}", tag, result.available());
        return result;
    }
}