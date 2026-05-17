package com.innovationCampus.challenger.dto;

import com.innovationCampus.challenger.entities.ChallengeStatus;
import com.innovationCampus.challenger.entities.ProofType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ChallengeDto(
        String id,
        String title,
        String description,
        String creatorId,
        String creatorName,
        long deadline,
        ProofType proofType,
        ChallengeStatus status,
        int participantCount,
        List<PendingProofDto> pendingProofs
) {
}
