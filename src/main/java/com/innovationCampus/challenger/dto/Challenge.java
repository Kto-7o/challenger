package com.innovationCampus.challenger.dto;

import java.time.LocalDate;

public record Challenge (
        Long id,
        String name,
        String description,
        String img,
        Long usersId,
        LocalDate start
){
}
