package com.innovationCampus.challenger.dto;

import lombok.Builder;

@Builder
public record ProofDto(
        String id,
        String userId,
        String mediaUrl,
        long createdAt
) {
}
