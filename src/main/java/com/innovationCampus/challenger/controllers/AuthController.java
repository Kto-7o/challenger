package com.innovationCampus.challenger.controllers;

import com.innovationCampus.challenger.dto.JwtAuthenticationResponse;
import com.innovationCampus.challenger.dto.SignInRequest;
import com.innovationCampus.challenger.dto.SignUpRequest;
import com.innovationCampus.challenger.dto.TagCheckDto;
import com.innovationCampus.challenger.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public JwtAuthenticationResponse signup(@RequestBody SignUpRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/register")
    public JwtAuthenticationResponse register(@RequestBody SignUpRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/signin")
    public JwtAuthenticationResponse signin(@RequestBody SignInRequest request) {
        return authService.signin(request);
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse login(@RequestBody SignInRequest request) {
        return authService.signin(request);
    }

    @GetMapping("/check-tag")
    public TagCheckDto checkTagAvailability(@RequestParam String tag) {
        log.debug("AuthController.checkTagAvailability called with tag={}", tag);
        TagCheckDto result = authService.checkTagAvailability(tag);
        log.debug("AuthController.checkTagAvailability returning {}", result);
        return result;
    }
}
