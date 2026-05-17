package com.innovationCampus.challenger.repositories;

import com.innovationCampus.challenger.entities.Challenge;
import com.innovationCampus.challenger.entities.User;
import com.innovationCampus.challenger.entities.UserChallengeStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserChallengeStatsRepository extends JpaRepository<UserChallengeStats, Long> {
    Optional<UserChallengeStats> findByUserAndChallenge(User user, Challenge challenge);
}
