package com.innovationCampus.challenger.services;

import com.innovationCampus.challenger.dto.CreateChallengeRequestDto;
import com.innovationCampus.challenger.entities.Challenge;
import com.innovationCampus.challenger.entities.ProofType;
import com.innovationCampus.challenger.entities.Role;
import com.innovationCampus.challenger.entities.User;
import com.innovationCampus.challenger.repositories.ChallengeRepository;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private StorageService storageService;

    @InjectMocks
    private ChallengeService challengeService;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("current");
        currentUser.setEmail("current@test.com");
        currentUser.setTag("current_tag");
        currentUser.setRole(Role.AUTHTORIZED);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities()));

        // **THE FIX**: Use lenient stubbing for mocks in @BeforeEach
        // that are not used by all tests in the class.
        lenient().when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
    }

    @Test
    void createChallenge_shouldSaveChallengeWithUsers() {
        CreateChallengeRequestDto request = new CreateChallengeRequestDto("Test Challenge", "Description", ProofType.PHOTO, 7, List.of());
        
        when(challengeRepository.save(any(Challenge.class))).thenAnswer(invocation -> {
            Challenge challenge = invocation.getArgument(0);
            challenge.setId(1L);
            challenge.setProofs(new HashSet<>());
            challenge.setCreator(currentUser);
            return challenge;
        });

        challengeService.createChallenge(request);

        verify(challengeRepository).save(any(Challenge.class));
    }
}
