package org.example.tarotpokerapplication.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tarotpokerapplication.dto.GameSessionCreateDto;
import org.example.tarotpokerapplication.dto.GameSessionResponseDto;
import org.example.tarotpokerapplication.dto.GameSessionUpdateDto;
import org.example.tarotpokerapplication.service.GameSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GameController {

    private final GameSessionService gameSessionService;

    @PostMapping("/match/create/private")
    public ResponseEntity<GameSessionResponseDto> createPrivate(
            @RequestBody GameSessionCreateDto dto, Authentication authentication) {
        String userId = authentication.getName();
        log.debug("POST /game/match/create/private userId={}", userId);
        return ResponseEntity.ok(gameSessionService.createPrivateSession(userId, dto.getUsername()));
    }

    @PostMapping("/match/create/public")
    public ResponseEntity<GameSessionResponseDto> createPublic(
            @RequestBody GameSessionCreateDto dto, Authentication authentication) {
        String userId = authentication.getName();
        log.debug("POST /game/match/create/public userId={}", userId);
        return ResponseEntity.ok(gameSessionService.createPublicSession(userId, dto.getUsername()));
    }

    @PostMapping("/match/create/bot")
    public ResponseEntity<GameSessionResponseDto> createBot(
            @RequestBody GameSessionCreateDto dto, Authentication authentication) {
        String userId = authentication.getName();
        log.debug("POST /game/match/create/bot userId={}", userId);
        return ResponseEntity.ok(gameSessionService.createBotSession(userId, dto.getUsername()));
    }

    @PostMapping("/match/join/{code}")
    public ResponseEntity<GameSessionResponseDto> joinByCode(
            @PathVariable String code,
            @RequestBody GameSessionCreateDto dto, Authentication authentication) {
        String userId = authentication.getName();
        log.debug("POST /game/match/join/{} userId={}", code, userId);
        return ResponseEntity.ok(gameSessionService.joinByCode(code, userId, dto.getUsername()));
    }

    @PostMapping("/match/join")
    public ResponseEntity<GameSessionResponseDto> joinPublic(
            @RequestBody GameSessionCreateDto dto, Authentication authentication) {
        String userId = authentication.getName();
        log.debug("POST /game/match/join userId={}", userId);
        return ResponseEntity.ok(gameSessionService.joinPublicSession(userId, dto.getUsername()));
    }

    @GetMapping("/match")
    public List<GameSessionResponseDto> getAll() {
        log.debug("GET /game/match");
        return gameSessionService.getAllSessions();
    }

    @GetMapping("/match/{sessionId}")
    public ResponseEntity<GameSessionResponseDto> getState(
            @PathVariable String sessionId, Authentication authentication) {
        String userId = authentication.getName();
        log.debug("GET /game/match/{} userId={}", sessionId, userId);
        return ResponseEntity.ok(gameSessionService.getSession(sessionId, userId));
    }

    @PutMapping("/match/{sessionId}/pass")
    public ResponseEntity<GameSessionResponseDto> pass(
            @PathVariable String sessionId, Authentication authentication) {
        String userId = authentication.getName();
        log.debug("PUT /game/match/{}/pass userId={}", sessionId, userId);
        return ResponseEntity.ok(gameSessionService.pass(sessionId, userId));
    }

    @PutMapping("/match/{sessionId}/trigger")
    public ResponseEntity<GameSessionResponseDto> trigger(
            @PathVariable String sessionId,
            @RequestBody GameSessionUpdateDto dto, Authentication authentication) {
        String userId = authentication.getName();
        log.debug("PUT /game/match/{}/trigger userId={} cardIndex={}", sessionId, userId, dto.getCardIndex());
        return ResponseEntity.ok(gameSessionService.triggerEvent(sessionId, userId, dto.getCardIndex()));
    }

    @PutMapping("/match/{sessionId}/next-round")
    public ResponseEntity<GameSessionResponseDto> nextRound(
            @PathVariable String sessionId, Authentication authentication) {
        String userId = authentication.getName();
        log.debug("PUT /game/match/{}/next-round userId={}", sessionId, userId);
        return ResponseEntity.ok(gameSessionService.nextRound(sessionId, userId));
    }

    @DeleteMapping("/match/{sessionId}")
    public ResponseEntity<Void> abandon(@PathVariable String sessionId) {
        log.debug("DELETE /game/match/{}", sessionId);
        gameSessionService.abandonSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
