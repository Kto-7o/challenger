package com.innovationCampus.challenger.dto;

import lombok.Builder;

@Builder
public record FriendRequestDto(
        long id,
        UserDto fromUser,
        UserDto toUser
) {
    @Builder
    public record UserDto(
            long id,
            String username,
            String tag
    ) {
    }
}
