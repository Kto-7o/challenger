package com.innovationCampus.challenger.repositories;

import com.innovationCampus.challenger.entities.Proof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProofRepository extends JpaRepository<Proof, Long> {
}
