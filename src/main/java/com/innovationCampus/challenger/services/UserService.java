package com.innovationCampus.challenger.services;

import com.innovationCampus.challenger.dto.FriendshipStatus;
import com.innovationCampus.challenger.dto.UserProfileDto;
import com.innovationCampus.challenger.dto.UserSearchDto;
import com.innovationCampus.challenger.entities.ChallengeResult;
import com.innovationCampus.challenger.entities.FriendRequestStatus;
import com.innovationCampus.challenger.entities.User;
import com.innovationCampus.challenger.exceptions.UserNotFoundException;
import com.innovationCampus.challenger.repositories.FriendRequestRepository;
import com.innovationCampus.challenger.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public UserProfileDto getMyProfile() {
        User user = getCurrentUser();
        return buildUserProfileDto(user);
    }

    public UserProfileDto getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        return buildUserProfileDto(user);
    }

    public List<UserSearchDto> searchUsers(String query) {
        User currentUser = getCurrentUser();
        List<User> users = userRepository.findByUsernameContainingIgnoreCaseOrTagContainingIgnoreCase(query, query);

        return users.stream()
                .map(user -> UserSearchDto.builder()
                        .id(user.getId().toString())
                        .username(user.getUsername())
                        .tag(user.getTag())
                        .relation(getFriendshipStatus(currentUser, user))
                        .build())
                .collect(Collectors.toList());
    }

    private FriendshipStatus getFriendshipStatus(User currentUser, User otherUser) {
        if (currentUser.getFriends().contains(otherUser)) {
            return FriendshipStatus.FRIEND;
        }
        if (friendRequestRepository.findBySenderAndReceiverAndStatus(currentUser, otherUser, FriendRequestStatus.PENDING).isPresent() ||
                friendRequestRepository.findBySenderAndReceiverAndStatus(otherUser, currentUser, FriendRequestStatus.PENDING).isPresent()) {
            return FriendshipStatus.PENDING;
        }
        return FriendshipStatus.NONE;
    }

    private User getCurrentUser() {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userTag = principal.getClaim("preferred_username");
        return userRepository.findByTag(userTag)
                .orElseThrow(() -> new UserNotFoundException("User with tag " + userTag + " not found"));
    }

    private UserProfileDto buildUserProfileDto(User user) {
        int created = user.getChallenges().size();
        int completed = user.getHistory().size();
        long successCount = user.getHistory().stream()
                .filter(h -> h.getResult() == ChallengeResult.SUCCESS)
                .count();
        int successRate = completed > 0 ? (int) ((double) successCount / completed * 100) : 0;

        UserProfileDto.Stats stats = UserProfileDto.Stats.builder()
                .created(created)
                .completed(completed)
                .successRate(successRate)
                .build();

        List<UserProfileDto.HistoryItem> history = user.getHistory().stream()
                .map(h -> UserProfileDto.HistoryItem.builder()
                        .challengeTitle(h.getChallenge().getName())
                        .result(h.getResult())
                        .date(h.getDate())
                        .build())
                .collect(Collectors.toList());

        return UserProfileDto.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .tag(user.getTag())
                .email(user.getEmail())
                .stats(stats)
                .history(history)
                .build();
    }
}
