package org.example.tarotpokerapplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tarotpokerapplication.db.GameSessionEntity;
import org.example.tarotpokerapplication.db.GameSessionRepository;
import org.example.tarotpokerapplication.db.PlayerEntity;
import org.example.tarotpokerapplication.db.PlayerRepository;
import org.example.tarotpokerapplication.entity.GamePhase;
import org.example.tarotpokerapplication.entity.GameSession;
import org.example.tarotpokerapplication.entity.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerSyncService {

    private static final String BOT_ID = "BOT";

    private final PlayerRepository playerRepository;
    private final GameSessionRepository gameSessionRepository;

    @Transactional
    public PlayerEntity syncPlayer(Player player) {
        PlayerEntity entity = playerRepository.findById(player.getId())
                .orElseGet(() -> {
                    PlayerEntity e = new PlayerEntity();
                    e.setId(player.getId());
                    return e;
                });
        entity.setName(player.getName());
        entity.setWins(player.getWins());
        entity.getMinorCards().clear();
        player.getHoleCards().stream().map(c -> c.getName()).forEach(entity.getMinorCards()::add);
        entity.getMajorCards().clear();
        player.getMajorCards().stream().map(c -> c.getName()).forEach(entity.getMajorCards()::add);
        PlayerEntity saved = playerRepository.save(entity);
        log.debug("Player synced to DB id={} wins={}", player.getId(), player.getWins());
        return saved;
    }

    @Transactional
    public void syncSession(GameSession session) {
        GameSessionEntity entity = gameSessionRepository.findBySessionId(session.getSessionId())
                .orElseGet(() -> {
                    GameSessionEntity e = new GameSessionEntity();
                    e.setSessionId(session.getSessionId());
                    return e;
                });

        entity.setPhase(session.getPhase().name());
        entity.setRound(session.getRound());

        if (session.getPlayer1() != null) {
            playerRepository.findById(session.getPlayer1().getId()).ifPresent(entity::setPlayer1);
        }
        if (session.getPlayer2() != null && !BOT_ID.equals(session.getPlayer2().getId())) {
            playerRepository.findById(session.getPlayer2().getId()).ifPresent(entity::setPlayer2);
        }

        if (session.getPhase() == GamePhase.MATCH_END) {
            Player p1 = session.getPlayer1();
            Player p2 = session.getPlayer2();
            if (p1 != null && (p2 == null || p1.getWins() > p2.getWins())) {
                entity.setWinnerId(p1.getId());
                log.info("Match winner persisted sessionId={} winnerId={}", session.getSessionId(), p1.getId());
            } else if (p2 != null && p2.getWins() > p1.getWins()) {
                entity.setWinnerId(p2.getId());
                log.info("Match winner persisted sessionId={} winnerId={}", session.getSessionId(), p2.getId());
            }
        }

        gameSessionRepository.save(entity);
        log.debug("Session synced to DB sessionId={} phase={} round={}", session.getSessionId(),
                session.getPhase(), session.getRound());
    }
}
