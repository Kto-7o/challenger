package com.innovationCampus.challenger.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FriendRequestsResponseDto(
        List<FriendRequestDto> incoming,
        List<FriendRequestDto> outgoing
) {
}
