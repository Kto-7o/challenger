package com.innovationCampus.challenger.dto;

import lombok.Builder;

@Builder
public record JwtAuthenticationResponse(String token) {
}
