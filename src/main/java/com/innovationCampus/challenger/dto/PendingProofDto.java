package com.innovationCampus.challenger.dto;

import lombok.Builder;

@Builder
public record PendingProofDto(
        long id,
        long userId,
        String userName,
        String mediaUrl,
        long createdAt
) {
}
