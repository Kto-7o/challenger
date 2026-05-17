package com.innovationCampus.challenger.controllers;

import com.innovationCampus.challenger.dto.UserProfileDto;
import com.innovationCampus.challenger.dto.UserSearchDto;
import com.innovationCampus.challenger.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileDto getMyProfile() {
        return userService.getMyProfile();
    }

    @GetMapping("/{id}")
    public UserProfileDto getUserProfile(@PathVariable Long id) {
        return userService.getUserProfile(id);
    }

    @GetMapping("/search")
    public List<UserSearchDto> searchUsers(@RequestParam String q) {
        return userService.searchUsers(q);
    }
}
