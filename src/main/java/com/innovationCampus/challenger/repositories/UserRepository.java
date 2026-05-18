package com.innovationCampus.challenger.repositories;

import com.innovationCampus.challenger.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTag(String tag);
    Optional<User> findByEmail(String email);
    List<User> findByUsernameContainingIgnoreCaseOrTagContainingIgnoreCase(String username, String tag);
}
