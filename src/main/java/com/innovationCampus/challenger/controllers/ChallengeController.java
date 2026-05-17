package com.innovationCampus.challenger.controllers;

import com.innovationCampus.challenger.dto.ChallengeDto;
import com.innovationCampus.challenger.dto.CreateChallengeRequestDto;
import com.innovationCampus.challenger.dto.ProofDto;
import com.innovationCampus.challenger.dto.VerdictRequestDto;
import com.innovationCampus.challenger.services.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    public List<ChallengeDto> getChallenges(@RequestParam(defaultValue = "active") String tab) {
        return challengeService.getChallenges(tab);
    }

    @PostMapping
    public ResponseEntity<ChallengeDto> createChallenge(@RequestBody CreateChallengeRequestDto request) {
        ChallengeDto challenge = challengeService.createChallenge(request);
        return new ResponseEntity<>(challenge, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/proof")
    public ProofDto uploadProof(@PathVariable Long id, @RequestParam("photo") MultipartFile file) {
        return challengeService.uploadProof(id, file);
    }

    @PostMapping("/{id}/verdict")
    public ResponseEntity<Void> giveVerdict(@PathVariable Long id, @RequestBody VerdictRequestDto request) {
        challengeService.giveVerdict(id, request);
        return ResponseEntity.ok().build();
    }
}
