package com.innovationCampus.challenger.repositories;

import com.innovationCampus.challenger.entities.ChallengeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeHistoryRepository extends JpaRepository<ChallengeHistory, Long> {
}
