package com.innovationCampus.challenger.dto;

import lombok.Builder;

@Builder
public record FriendDto(
        long id,
        String username,
        String tag,
        int activeChallenges,
        FriendshipStatus relation
) {
}
