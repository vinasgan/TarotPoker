package org.example.tarotpokerapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tarotpokerapplication.dto.GameActionDto;
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
        return ResponseEntity.ok(gameSessionService.createPrivateSession(dto.getUserId(), null));
    }

    @PostMapping("/match/create/public")
    public ResponseEntity<GameSessionResponseDto> createPublic(
            @Valid @RequestBody GameSessionCreateDto dto) {
        return ResponseEntity.ok(gameSessionService.createPublicSession(dto.getUserId(), null));
    }

    @PostMapping("/match/create/bot")
    public ResponseEntity<GameSessionResponseDto> createBot(
            @Valid @RequestBody GameSessionCreateDto dto) {
        return ResponseEntity.ok(gameSessionService.createBotSession(dto.getUserId(), null));
    }

    @PostMapping("/match/join/{code}")
    public ResponseEntity<GameSessionResponseDto> joinByCode(
            @PathVariable String code,
            @Valid @RequestBody GameSessionCreateDto dto) {
        return ResponseEntity.ok(gameSessionService.joinByCode(code, dto.getUserId(), null));
    }

    @PostMapping("/match/join")
    public ResponseEntity<GameSessionResponseDto> joinPublic(
            @Valid @RequestBody GameSessionCreateDto dto) {
        return ResponseEntity.ok(gameSessionService.joinPublicSession(dto.getUserId(), null));
    }

    @GetMapping("/match")
    public List<GameSessionResponseDto> getAll() {
        return gameSessionService.getAllSessions();
    }

    @GetMapping("/match/{sessionId}")
    public ResponseEntity<GameSessionResponseDto> getState(
            @PathVariable String sessionId,
            @RequestParam String userId) {
        return ResponseEntity.ok(gameSessionService.getSession(sessionId, userId));
    }

    @PutMapping("/match/{sessionId}/pass")
    public ResponseEntity<GameSessionResponseDto> pass(
            @PathVariable String sessionId,
            @Valid @RequestBody GameActionDto dto) {
        return ResponseEntity.ok(gameSessionService.pass(sessionId, dto.getUserId()));
    }

    @PutMapping("/match/{sessionId}/trigger")
    public ResponseEntity<GameSessionResponseDto> trigger(
            @PathVariable String sessionId,
            @Valid @RequestBody GameSessionUpdateDto dto) {
        return ResponseEntity.ok(gameSessionService.triggerEvent(sessionId, dto.getUserId(), dto.getCardIndex()));
    }

    @PutMapping("/match/{sessionId}/next-round")
    public ResponseEntity<GameSessionResponseDto> nextRound(
            @PathVariable String sessionId,
            @Valid @RequestBody GameActionDto dto) {
        return ResponseEntity.ok(gameSessionService.nextRound(sessionId, dto.getUserId()));
    }

    @DeleteMapping("/match/{sessionId}")
    public ResponseEntity<Void> abandon(@PathVariable String sessionId) {
        gameSessionService.abandonSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
