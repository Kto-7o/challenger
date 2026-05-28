package com.innovationCampus.challenger.dto;

import lombok.Builder;

@Builder
public record UserSearchDto(
        long id,
        String username,
        String tag,
        FriendshipStatus relation
) {
}
