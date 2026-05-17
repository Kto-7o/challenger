package com.innovationCampus.challenger.controllers;

import com.innovationCampus.challenger.dto.FriendDto;
import com.innovationCampus.challenger.dto.FriendRequestsResponseDto;
import com.innovationCampus.challenger.services.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public List<FriendDto> getFriends() {
        return friendService.getFriends();
    }

    @GetMapping("/requests")
    public FriendRequestsResponseDto getFriendRequests() {
        return friendService.getFriendRequests();
    }

    @PostMapping("/request/{userId}")
    public ResponseEntity<Void> sendFriendRequest(@PathVariable Long userId) {
        friendService.sendFriendRequest(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request/{id}/respond")
    public ResponseEntity<Void> respondToFriendRequest(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        friendService.respondToFriendRequest(id, body.get("accepted"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long userId) {
        friendService.removeFriend(userId);
        return ResponseEntity.ok().build();
    }
}
