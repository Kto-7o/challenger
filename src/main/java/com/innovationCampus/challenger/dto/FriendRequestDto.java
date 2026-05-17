package com.innovationCampus.challenger.dto;

import lombok.Builder;

@Builder
public record FriendRequestDto(
        String id,
        UserDto fromUser,
        UserDto toUser
) {
    @Builder
    public record UserDto(
            String id,
            String username,
            String tag
    ) {
    }
}
