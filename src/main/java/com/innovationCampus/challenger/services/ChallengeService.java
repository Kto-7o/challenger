package com.innovationCampus.challenger.services;

import com.innovationCampus.challenger.dto.*;
import com.innovationCampus.challenger.entities.*;
import com.innovationCampus.challenger.exceptions.ForbiddenException;
import com.innovationCampus.challenger.exceptions.UserNotFoundException;
import com.innovationCampus.challenger.repositories.ChallengeHistoryRepository;
import com.innovationCampus.challenger.repositories.ChallengeRepository;
import com.innovationCampus.challenger.repositories.ProofRepository;
import com.innovationCampus.challenger.repositories.UserRepository;
import com.innovationCampus.challenger.repositories.UserChallengeStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final ProofRepository proofRepository;
    private final StorageService storageService;
    private final UserChallengeStatsRepository userChallengeStatsRepository;
    private final ChallengeHistoryRepository challengeHistoryRepository;

    public List<ChallengeDto> getChallenges(String tab) {
        User currentUser = getCurrentUser();
        List<Challenge> challenges;

        switch (tab) {
            case "active":
                challenges = challengeRepository.findByUsersContainsAndStatus(currentUser, ChallengeStatus.ACTIVE);
                break;
            case "incoming":
                challenges = challengeRepository.findByUsersContainsAndStatus(currentUser, ChallengeStatus.INCOMING);
                break;
            case "archive":
                challenges = challengeRepository.findByUsersContainsAndStatusIn(currentUser, List.of(ChallengeStatus.COMPLETED, ChallengeStatus.EXPIRED));
                break;
            default:
                throw new IllegalArgumentException("Invalid tab value");
        }

        return challenges.stream()
                .map(this::mapChallengeToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChallengeDto createChallenge(CreateChallengeRequestDto request) {
        User creator = getCurrentUser();
        Set<User> invitedUsers = request.invitedUserIds().stream()
    //            .map(Long::parseLong)
                .map(id -> userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found")))
                .collect(Collectors.toSet());
        invitedUsers.add(creator);

        Challenge challenge = new Challenge();
        challenge.setName(request.title());
        challenge.setDescription(request.description());
        challenge.setCreator(creator);
        challenge.setDeadline(LocalDateTime.now().plusDays(request.deadlineDays()));
        challenge.setProofType(request.proofType());
        challenge.setStatus(ChallengeStatus.INCOMING);
        challenge.setUsers(invitedUsers);

        Set<UserChallengeStats> stats = new HashSet<>();
        for (User user : invitedUsers) {
            UserChallengeStats userStats = new UserChallengeStats(null, user, challenge, 0);
            stats.add(userStats);
        }
        challenge.setUserStats(stats);

        Challenge savedChallenge = challengeRepository.save(challenge);
        return mapChallengeToDto(savedChallenge);
    }

    public ProofDto uploadProof(Long challengeId, MultipartFile file) {
        User currentUser = getCurrentUser();
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new UserNotFoundException("Challenge with id " + challengeId + " not found"));

        if (!challenge.getUsers().contains(currentUser)) {
            throw new ForbiddenException("You are not a participant of this challenge");
        }

        String mediaUrl = storageService.uploadFile(file);

        Proof proof = new Proof();
        proof.setChallenge(challenge);
        proof.setUser(currentUser);
        proof.setMediaUrl(mediaUrl);
        proof.setCreatedAt(LocalDateTime.now());
        Proof savedProof = proofRepository.save(proof);

        return mapProofToDto(savedProof);
    }

    @Transactional
    public void giveVerdict(Long challengeId, VerdictRequestDto request) {
        User currentUser = getCurrentUser();
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new UserNotFoundException("Challenge with id " + challengeId + " not found"));

        if (!challenge.getCreator().equals(currentUser)) {
            throw new ForbiddenException("Only the creator of the challenge can give a verdict");
        }

        Proof proof = proofRepository.findById(request.proofId())
                .orElseThrow(() -> new UserNotFoundException("Proof with id " + request.proofId() + " not found"));

        User proofOwner = proof.getUser();
        UserChallengeStats stats = userChallengeStatsRepository.findByUserAndChallenge(proofOwner, challenge)
                .orElseThrow(() -> new IllegalStateException("User stats not found"));

        ChallengeHistory history = new ChallengeHistory();
        history.setUser(proofOwner);
        history.setChallenge(challenge);
        history.setDate(LocalDate.now());

        if (request.accepted()) {
            stats.setStreak(stats.getStreak() + 1);
            challenge.setDeadline(challenge.getDeadline().plusDays(1));
            history.setResult(ChallengeResult.SUCCESS);
        } else {
            stats.setStreak(0);
            history.setResult(ChallengeResult.FAIL);
        }

        userChallengeStatsRepository.save(stats);
        challengeHistoryRepository.save(history);
        proofRepository.delete(proof);
    }

    @Scheduled(cron = "0 0 * * * *") // Run every hour
    @Transactional
    public void updateChallengeStatuses() {
        List<Challenge> activeChallenges = challengeRepository.findAllByStatus(ChallengeStatus.ACTIVE);
        for (Challenge challenge : activeChallenges) {
            if (challenge.getDeadline().isBefore(LocalDateTime.now())) {
                challenge.setStatus(ChallengeStatus.EXPIRED);
                User winner = challenge.getUserStats().stream()
                        .max(Comparator.comparing(UserChallengeStats::getStreak))
                        .map(UserChallengeStats::getUser)
                        .orElse(null);
                challenge.setWinner(winner);
                challengeRepository.save(challenge);
            }
        }

        List<Challenge> incomingChallenges = challengeRepository.findAllByStatus(ChallengeStatus.INCOMING);
        for (Challenge challenge : incomingChallenges) {
            if (challenge.getDeadline().minusDays(7).toLocalDate().isEqual(LocalDate.now())) {
                 challenge.setStatus(ChallengeStatus.ACTIVE);
                 challengeRepository.save(challenge);
            }
        }
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private ChallengeDto mapChallengeToDto(Challenge challenge) {
        List<PendingProofDto> pendingProofs = challenge.getProofs().stream()
                .map(this::mapProofToPendingProofDto)
                .collect(Collectors.toList());

        return ChallengeDto.builder()
                .id(challenge.getId())//.toString())
                .title(challenge.getName())
                .description(challenge.getDescription())
                .creatorId(challenge.getCreator().getId())//.toString())
                .creatorName(challenge.getCreator().getUsername())
                .deadline(challenge.getDeadline().toEpochSecond(ZoneOffset.UTC))
                .proofType(challenge.getProofType())
                .status(challenge.getStatus())
                .participantCount(challenge.getUsers().size())
                .pendingProofs(pendingProofs)
                .build();
    }

    private PendingProofDto mapProofToPendingProofDto(Proof proof) {
        return PendingProofDto.builder()
                .id(proof.getId())
                .userId(proof.getUser().getId())
                .userName(proof.getUser().getUsername())
                .mediaUrl(proof.getMediaUrl())
                .createdAt(proof.getCreatedAt().toEpochSecond(ZoneOffset.UTC))
                .build();
    }

    private ProofDto mapProofToDto(Proof proof) {
        return ProofDto.builder()
                .id(proof.getId())
                .userId(proof.getUser().getId())
                .mediaUrl(proof.getMediaUrl())
                .createdAt(proof.getCreatedAt().toEpochSecond(ZoneOffset.UTC))
                .build();
    }
}
