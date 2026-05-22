package org.example.tarotpokerapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tarotpokerapplication.dto.GameSessionCreateDto;
import org.example.tarotpokerapplication.dto.GameSessionResponseDto;
import org.example.tarotpokerapplication.dto.GameSessionUpdateDto;
import org.example.tarotpokerapplication.service.GameSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GameController {

    private final GameSessionService gameSessionService;

    @PostMapping("/match/create/private")
    public ResponseEntity<GameSessionResponseDto> createPrivate(
            @Valid @RequestBody GameSessionCreateDto dto) {
        return ResponseEntity.ok(gameSessionService.createPrivateSession(dto.getUserId(), dto.getUsername()));
    }

    @PostMapping("/match/create/public")
    public ResponseEntity<GameSessionResponseDto> createPublic(
            @Valid @RequestBody GameSessionCreateDto dto) {
        return ResponseEntity.ok(gameSessionService.createPublicSession(dto.getUserId(), dto.getUsername()));
    }

    @PostMapping("/match/create/bot")
    public ResponseEntity<GameSessionResponseDto> createBot(
            @Valid @RequestBody GameSessionCreateDto dto) {
        return ResponseEntity.ok(gameSessionService.createBotSession(dto.getUserId(), dto.getUsername()));
    }

    @PostMapping("/match/join/{code}")
    public ResponseEntity<GameSessionResponseDto> joinByCode(
            @PathVariable String code,
            @Valid @RequestBody GameSessionCreateDto dto) {
        GameSessionResponseDto result = gameSessionService.joinByCode(code, dto.getUserId(), dto.getUsername());
        return result == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(result);
    }

    @PostMapping("/match/join")
    public ResponseEntity<GameSessionResponseDto> joinPublic(
            @Valid @RequestBody GameSessionCreateDto dto) {
        return ResponseEntity.ok(gameSessionService.joinPublicSession(dto.getUserId(), dto.getUsername()));
    }

    @GetMapping("/match")
    public List<GameSessionResponseDto> getAll() {
        return gameSessionService.getAllSessions();
    }

    @GetMapping("/match/{sessionId}")
    public ResponseEntity<GameSessionResponseDto> getState(
            @PathVariable String sessionId,
            @RequestParam(required = false) String userId) {
        GameSessionResponseDto result = gameSessionService.getSession(sessionId, userId);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }

    @PutMapping("/match/{sessionId}/pass")
    public ResponseEntity<GameSessionResponseDto> pass(
            @PathVariable String sessionId,
            @Valid @RequestBody GameSessionUpdateDto dto) {
        GameSessionResponseDto result = gameSessionService.pass(sessionId, dto.getUserId());
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }

    @PutMapping("/match/{sessionId}/trigger")
    public ResponseEntity<GameSessionResponseDto> trigger(
            @PathVariable String sessionId,
            @Valid @RequestBody GameSessionUpdateDto dto) {
        GameSessionResponseDto result = gameSessionService.triggerEvent(sessionId, dto.getUserId(), dto.getCardIndex());
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }

    @PutMapping("/match/{sessionId}/next-round")
    public ResponseEntity<GameSessionResponseDto> nextRound(
            @PathVariable String sessionId,
            @Valid @RequestBody GameSessionUpdateDto dto) {
        GameSessionResponseDto result = gameSessionService.nextRound(sessionId, dto.getUserId());
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }

    @DeleteMapping("/match/{sessionId}")
    public ResponseEntity<Void> abandon(
            @PathVariable String sessionId,
            @RequestParam String userId) {
        gameSessionService.abandonSession(sessionId, userId);
        return ResponseEntity.ok().build();
    }
}
