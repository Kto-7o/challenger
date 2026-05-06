package com.innovationCampus.challenger.dto;

import java.time.LocalDate;

public record User(
        Long id,
        String username,
        String email,
        String image,
        LocalDate birthday,
        Role role,
        Long friendsId,
        Long challengesId
) {
}
