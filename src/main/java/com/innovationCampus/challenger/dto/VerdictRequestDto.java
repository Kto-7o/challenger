package com.innovationCampus.challenger.dto;

public record VerdictRequestDto(
        String proofId,
        boolean accepted
) {
}
