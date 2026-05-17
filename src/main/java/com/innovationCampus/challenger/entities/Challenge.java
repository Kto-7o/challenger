package com.innovationCampus.challenger.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "challenges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "proof_type", nullable = false)
    private ProofType proofType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus status;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;

    @ManyToMany
    @JoinTable(
            name = "user_challenges",
            joinColumns = @JoinColumn(name = "challenge_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;

    @OneToMany(mappedBy = "challenge")
    private Set<Proof> proofs;

    @OneToMany(mappedBy = "challenge")
    private Set<ChallengeHistory> history;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserChallengeStats> userStats;
}
