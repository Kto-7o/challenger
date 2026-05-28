package com.innovationCampus.challenger.dto;

import lombok.Builder;

@Builder
public record ProofDto(
        long id,
        long userId,
        String mediaUrl,
        long createdAt
) {
}
