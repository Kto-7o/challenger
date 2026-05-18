package com.innovationCampus.challenger.services;

import com.innovationCampus.challenger.entities.FriendRequest;
import com.innovationCampus.challenger.entities.FriendRequestStatus;
import com.innovationCampus.challenger.entities.Role;
import com.innovationCampus.challenger.entities.User;
import com.innovationCampus.challenger.exceptions.ConflictException;
import com.innovationCampus.challenger.repositories.FriendRequestRepository;
import com.innovationCampus.challenger.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @InjectMocks
    private FriendService friendService;

    private User currentUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("current");
        currentUser.setEmail("current@test.com");
        currentUser.setTag("current_tag");
        currentUser.setRole(Role.AUTHTORIZED); // **THE FIX**
        currentUser.setFriends(new HashSet<>());

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("other");
        otherUser.setTag("other_tag");

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities()));

        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
    }

    @Test
    void sendFriendRequest_shouldSucceed() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(friendRequestRepository.findBySenderAndReceiverAndStatus(currentUser, otherUser, FriendRequestStatus.PENDING))
                .thenReturn(Optional.empty());

        friendService.sendFriendRequest(2L);

        verify(friendRequestRepository).save(any(FriendRequest.class));
    }

    @Test
    void sendFriendRequest_shouldThrowConflict_whenAlreadyFriends() {
        currentUser.getFriends().add(otherUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));

        assertThrows(ConflictException.class, () -> friendService.sendFriendRequest(2L));
    }
}
