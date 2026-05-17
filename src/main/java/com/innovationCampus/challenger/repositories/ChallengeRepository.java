package com.innovationCampus.challenger.repositories;

import com.innovationCampus.challenger.entities.Challenge;
import com.innovationCampus.challenger.entities.ChallengeStatus;
import com.innovationCampus.challenger.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByUsersContainsAndStatus(User user, ChallengeStatus status);
    List<Challenge> findByUsersContainsAndStatusIn(User user, List<ChallengeStatus> statuses);
    List<Challenge> findAllByStatus(ChallengeStatus status);
}
