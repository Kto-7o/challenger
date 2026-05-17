package com.innovationCampus.challenger.dto;

import com.innovationCampus.challenger.entities.ProofType;

import java.util.List;

public record CreateChallengeRequestDto(
        String title,
        String description,
        ProofType proofType,
        int deadlineDays,
        List<String> invitedUserIds
) {
}
