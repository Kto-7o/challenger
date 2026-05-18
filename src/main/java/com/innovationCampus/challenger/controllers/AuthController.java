package com.innovationCampus.challenger.controllers;

import com.innovationCampus.challenger.dto.JwtAuthenticationResponse;
import com.innovationCampus.challenger.dto.SignInRequest;
import com.innovationCampus.challenger.dto.SignUpRequest;
import com.innovationCampus.challenger.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public JwtAuthenticationResponse signup(@RequestBody SignUpRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/signin")
    public JwtAuthenticationResponse signin(@RequestBody SignInRequest request) {
        return authService.signin(request);
    }
}
