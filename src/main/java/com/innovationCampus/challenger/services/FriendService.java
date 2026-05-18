package com.innovationCampus.challenger.services;

import com.innovationCampus.challenger.dto.FriendDto;
import com.innovationCampus.challenger.dto.FriendRequestDto;
import com.innovationCampus.challenger.dto.FriendRequestsResponseDto;
import com.innovationCampus.challenger.dto.FriendshipStatus;
import com.innovationCampus.challenger.entities.FriendRequest;
import com.innovationCampus.challenger.entities.FriendRequestStatus;
import com.innovationCampus.challenger.entities.User;
import com.innovationCampus.challenger.exceptions.ConflictException;
import com.innovationCampus.challenger.exceptions.ForbiddenException;
import com.innovationCampus.challenger.exceptions.UserNotFoundException;
import com.innovationCampus.challenger.repositories.FriendRequestRepository;
import com.innovationCampus.challenger.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Transactional(readOnly = true)
    public List<FriendDto> getFriends() {
        User currentUser = getCurrentUser();
        return currentUser.getFriends().stream()
                .map(this::mapUserToFriendDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FriendRequestsResponseDto getFriendRequests() {
        User currentUser = getCurrentUser();

        List<FriendRequestDto> incoming = friendRequestRepository.findByReceiverAndStatus(currentUser, FriendRequestStatus.PENDING)
                .stream()
                .map(this::mapFriendRequestToDto)
                .collect(Collectors.toList());

        List<FriendRequestDto> outgoing = friendRequestRepository.findBySenderAndStatus(currentUser, FriendRequestStatus.PENDING)
                .stream()
                .map(this::mapFriendRequestToDto)
                .collect(Collectors.toList());

        return FriendRequestsResponseDto.builder()
                .incoming(incoming)
                .outgoing(outgoing)
                .build();
    }

    @Transactional
    public void sendFriendRequest(Long userId) {
        User sender = getCurrentUser();
        User receiver = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        if (sender.equals(receiver)) {
            throw new ConflictException("You cannot send a friend request to yourself");
        }

        if (sender.getFriends().contains(receiver)) {
            throw new ConflictException("You are already friends");
        }

        friendRequestRepository.findBySenderAndReceiverAndStatus(sender, receiver, FriendRequestStatus.PENDING)
                .ifPresent(request -> {
                    throw new ConflictException("Friend request already sent");
                });

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(friendRequest);
    }

    @Transactional
    public void respondToFriendRequest(Long requestId, boolean accepted) {
        User currentUser = getCurrentUser();
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException("Friend request with id " + requestId + " not found"));

        if (!friendRequest.getReceiver().equals(currentUser)) {
            throw new ForbiddenException("You are not authorized to respond to this request");
        }

        if (accepted) {
            friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
            User sender = friendRequest.getSender();
            currentUser.getFriends().add(sender);
            sender.getFriends().add(currentUser);
            userRepository.save(currentUser);
            userRepository.save(sender);
        } else {
            friendRequest.setStatus(FriendRequestStatus.REJECTED);
        }
        friendRequestRepository.save(friendRequest);
    }

    @Transactional
    public void removeFriend(Long userId) {
        User currentUser = getCurrentUser();
        User friend = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        if (!currentUser.getFriends().contains(friend)) {
            throw new ConflictException("You are not friends");
        }

        currentUser.getFriends().remove(friend);
        friend.getFriends().remove(currentUser);
        userRepository.save(currentUser);
        userRepository.save(friend);
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername(); // This now correctly returns the email
        } else {
            throw new IllegalStateException("User not authenticated");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email " + email));
    }

    private FriendDto mapUserToFriendDto(User user) {
        return FriendDto.builder()
                .id(user.getId().toString())
                .username(user.getDisplayName()) // Use getDisplayName() for the actual username
                .tag(user.getTag())
                .activeChallenges(user.getChallenges().size())
                .relation(FriendshipStatus.FRIEND)
                .build();
    }

    private FriendRequestDto mapFriendRequestToDto(FriendRequest request) {
        return FriendRequestDto.builder()
                .id(request.getId().toString())
                .fromUser(mapUserToRequestUserDto(request.getSender()))
                .toUser(mapUserToRequestUserDto(request.getReceiver()))
                .build();
    }

    private FriendRequestDto.UserDto mapUserToRequestUserDto(User user) {
        return FriendRequestDto.UserDto.builder()
                .id(user.getId().toString())
                .username(user.getDisplayName()) // Use getDisplayName()
                .tag(user.getTag())
                .build();
    }
}
