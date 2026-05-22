package org.example.tarotpokerapplication.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSessionEntity, Long> {
    Optional<GameSessionEntity> findBySessionId(String sessionId);

    @Query("SELECT s FROM GameSessionEntity s WHERE s.player1.id = :playerId OR s.player2.id = :playerId")
    List<GameSessionEntity> findByPlayerId(@Param("playerId") String playerId);
}
