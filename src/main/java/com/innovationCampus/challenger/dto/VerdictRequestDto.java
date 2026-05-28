package com.innovationCampus.challenger.dto;

public record VerdictRequestDto(
        long proofId,
        boolean accepted
) {
}
