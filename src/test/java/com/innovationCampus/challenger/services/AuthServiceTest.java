package com.innovationCampus.challenger.services;

import com.innovationCampus.challenger.dto.JwtAuthenticationResponse;
import com.innovationCampus.challenger.dto.SignInRequest;
import com.innovationCampus.challenger.dto.SignUpRequest;
import com.innovationCampus.challenger.entities.User;
import com.innovationCampus.challenger.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthService authService;

    @Test
    void signup_shouldCreateUserAndReturnToken() {
        // Given
        SignUpRequest signUpRequest = new SignUpRequest("testuser", "testtag", "test@test.com", "password");

        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        // The generateToken method will be called with a User object created inside the service.
        // So, we should stub it to accept any User object.
        when(jwtService.generateToken(any(User.class))).thenReturn("test_token");

        // When
        JwtAuthenticationResponse response = authService.signup(signUpRequest);

        // Then
        assertEquals("test_token", response.getAccessToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signin_shouldReturnToken_whenCredentialsAreValid() {
        // Given
        SignInRequest signInRequest = new SignInRequest("test@test.com", "password");
        User user = new User();
        user.setEmail(signInRequest.email());

        when(userRepository.findByEmail(signInRequest.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("test_token");

        // When
        JwtAuthenticationResponse response = authService.signin(signInRequest);

        // Then
        assertEquals("test_token", response.getAccessToken());
        verify(authenticationManager).authenticate(any());
    }
}
