package com.innovationCampus.challenger.dto;

import com.innovationCampus.challenger.entities.ChallengeResult;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UserProfileDto(
        long id,
        String username,
        String tag,
        String email,
        Stats stats,
        List<HistoryItem> history
) {
    @Builder
    public record Stats(
            int created,
            int completed,
            int successRate
    ) {
    }

    @Builder
    public record HistoryItem(
            String challengeTitle,
            ChallengeResult result,
            LocalDate date
    ) {
    }
}
