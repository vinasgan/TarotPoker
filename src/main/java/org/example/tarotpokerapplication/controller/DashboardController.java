package org.example.tarotpokerapplication.controller;

import lombok.RequiredArgsConstructor;
import org.example.tarotpokerapplication.db.GameSessionRepository;
import org.example.tarotpokerapplication.db.PlayerRepository;
import org.example.tarotpokerapplication.db.PlayerEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final PlayerRepository playerRepository;
    private final GameSessionRepository gameSessionRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .filter(r -> List.of("ADMIN", "USER", "GUEST").contains(r))
                .findFirst().orElse("UNKNOWN");

        return ResponseEntity.ok(Map.of(
                "username", username,
                "role", role
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats(Authentication authentication) {
        String username = authentication.getName();

        List<PlayerEntity> players = playerRepository.findByNameContainingIgnoreCase(username);

        int totalMatches = 0;
        int wins = 0;

        if (!players.isEmpty()) {
            PlayerEntity player = players.get(0);
            var sessions = gameSessionRepository.findByPlayerId(player.getId());
            totalMatches = sessions.size();
            wins = (int) sessions.stream()
                    .filter(s -> player.getId().equals(s.getWinnerId()))
                    .count();
        }

        double winRate = totalMatches > 0 ? (double) wins / totalMatches * 100 : 0;

        return ResponseEntity.ok(Map.of(
                "totalMatches", totalMatches,
                "wins", wins,
                "winRate", Math.round(winRate)
        ));
    }
    
}
