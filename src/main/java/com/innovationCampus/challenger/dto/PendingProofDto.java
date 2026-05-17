package com.innovationCampus.challenger.dto;

import lombok.Builder;

@Builder
public record PendingProofDto(
        String id,
        String userId,
        String userName,
        String mediaUrl,
        long createdAt
) {
}
