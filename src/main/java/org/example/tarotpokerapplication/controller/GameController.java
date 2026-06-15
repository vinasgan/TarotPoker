package org.example.tarotpokerapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tarotpokerapplication.dto.GameActionDto;
import org.example.tarotpokerapplication.dto.GameSessionCreateDto;
import org.example.tarotpokerapplication.dto.GameSessionResponseDto;
import org.example.tarotpokerapplication.dto.GameSessionUpdateDto;
import org.example.tarotpokerapplication.service.GameSessionService;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestBody GameSessionCreateDto dto) {
        log.debug("POST /game/match/create/private userId={}", dto.getUserId());
        return ResponseEntity.ok(gameSessionService.createPrivateSession(dto.getUserId(), null));
    }

    @PostMapping("/match/create/public")
    public ResponseEntity<GameSessionResponseDto> createPublic(
            @Valid @RequestBody GameSessionCreateDto dto) {
        log.debug("POST /game/match/create/public userId={}", dto.getUserId());
        return ResponseEntity.ok(gameSessionService.createPublicSession(dto.getUserId(), null));
    }

    @PostMapping("/match/create/bot")
    public ResponseEntity<GameSessionResponseDto> createBot(
            @Valid @RequestBody GameSessionCreateDto dto) {
        log.debug("POST /game/match/create/bot userId={}", dto.getUserId());
        return ResponseEntity.ok(gameSessionService.createBotSession(dto.getUserId(), null));
    }

    @PostMapping("/match/join/{code}")
    public ResponseEntity<GameSessionResponseDto> joinByCode(
            @PathVariable String code,
            @Valid @RequestBody GameSessionCreateDto dto) {
        log.debug("POST /game/match/join/{} userId={}", code, dto.getUserId());
        return ResponseEntity.ok(gameSessionService.joinByCode(code, dto.getUserId(), null));
    }

    @PostMapping("/match/join")
    public ResponseEntity<GameSessionResponseDto> joinPublic(
            @Valid @RequestBody GameSessionCreateDto dto) {
        log.debug("POST /game/match/join userId={}", dto.getUserId());
        return ResponseEntity.ok(gameSessionService.joinPublicSession(dto.getUserId(), null));
    }

    @GetMapping("/match")
    public List<GameSessionResponseDto> getAll() {
        log.debug("GET /game/match");
        return gameSessionService.getAllSessions();
    }

    @GetMapping("/match/{sessionId}")
    public ResponseEntity<GameSessionResponseDto> getState(
            @PathVariable String sessionId,
            @RequestParam String userId) {
        log.debug("GET /game/match/{} userId={}", sessionId, userId);
        return ResponseEntity.ok(gameSessionService.getSession(sessionId, userId));
    }

    @PutMapping("/match/{sessionId}/pass")
    public ResponseEntity<GameSessionResponseDto> pass(
            @PathVariable String sessionId,
            @Valid @RequestBody GameActionDto dto) {
        log.debug("PUT /game/match/{}/pass userId={}", sessionId, dto.getUserId());
        return ResponseEntity.ok(gameSessionService.pass(sessionId, dto.getUserId()));
    }

    @PutMapping("/match/{sessionId}/trigger")
    public ResponseEntity<GameSessionResponseDto> trigger(
            @PathVariable String sessionId,
            @Valid @RequestBody GameSessionUpdateDto dto) {
        log.debug("PUT /game/match/{}/trigger userId={} cardIndex={}", sessionId, dto.getUserId(), dto.getCardIndex());
        return ResponseEntity.ok(gameSessionService.triggerEvent(sessionId, dto.getUserId(), dto.getCardIndex()));
    }

    @PutMapping("/match/{sessionId}/next-round")
    public ResponseEntity<GameSessionResponseDto> nextRound(
            @PathVariable String sessionId,
            @Valid @RequestBody GameActionDto dto) {
        log.debug("PUT /game/match/{}/next-round userId={}", sessionId, dto.getUserId());
        return ResponseEntity.ok(gameSessionService.nextRound(sessionId, dto.getUserId()));
    }

    @DeleteMapping("/match/{sessionId}")
    public ResponseEntity<Void> abandon(@PathVariable String sessionId) {
        log.debug("DELETE /game/match/{}", sessionId);
        gameSessionService.abandonSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
