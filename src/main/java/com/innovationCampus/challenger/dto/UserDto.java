package com.innovationCampus.challenger.dto;

import java.time.LocalDate;

public record UserDto(
        Long id,
        String username,
        String email,
        String image,
        LocalDate birthday,
        RoleDto roleDto,
        Long friendsId,
        Long challengesId
) {
}
