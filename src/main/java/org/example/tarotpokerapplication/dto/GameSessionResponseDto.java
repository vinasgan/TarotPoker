package org.example.tarotpokerapplication.dto;

import lombok.Data;
import org.example.tarotpokerapplication.entity.*;

import java.util.List;

@Data
public class GameSessionResponseDto {

    private Long id;
    private String winnerId;
    private String player1Id;
    private String player1Name;
    private String player2Id;
    private String player2Name;
    private String sessionId;
    private String inviteCode;

    private int round;
    private GamePhase phase;
    private int windowNumber;
    private long windowRemainingMs;

    private List<MinorArcanaCard> communityCards;

    private String lastEffectMessage;
    private String roundWinnerName;

    private boolean botMode;
    private MajorArcanaCard botLastEventCard;
    private boolean firstActorIsPlayer1;
    private boolean publicMatch;

    private int playerNumber;
    private Player player1;
    private Player player2;

    public static GameSessionResponseDto from(GameSession session, String userId) {
        int playerNumber = 0;
        if (session.getPlayer1() != null && session.getPlayer1().getId().equals(userId)) playerNumber = 1;
        else if (session.getPlayer2() != null && session.getPlayer2().getId().equals(userId)) playerNumber = 2;
        return from(session, playerNumber);
    }

    public static GameSessionResponseDto from(GameSession session, int playerNumber) {
        GameSessionResponseDto dto = new GameSessionResponseDto();
        dto.setSessionId(session.getSessionId());
        dto.setInviteCode(session.getInviteCode());
        dto.setRound(session.getRound());
        dto.setPhase(session.getPhase());
        dto.setWindowNumber(session.getWindowNumber());
        dto.setCommunityCards(session.getCommunityCards());
        dto.setLastEffectMessage(session.getLastEffectMessage());
        dto.setRoundWinnerName(session.getRoundWinnerName());
        dto.setBotMode(session.isBotMode());
        dto.setFirstActorIsPlayer1(session.isFirstActorIsPlayer1());
        dto.setPublicMatch(session.isPublicMatch());
        dto.setPlayerNumber(playerNumber);
        dto.setPlayer1(playerNumber == 1 ? session.getPlayer1() : hideCards(session.getPlayer1()));
        dto.setPlayer2(playerNumber == 2 ? session.getPlayer2() : hideCards(session.getPlayer2()));
        dto.setBotLastEventCard(playerNumber == 1 ? session.getBotLastEventCard() : null);
        if (session.getPhase() == GamePhase.EVENT_WINDOW && session.getWindowOpenedAt() > 0) {
            dto.setWindowRemainingMs(Math.max(0, 20_000 - (System.currentTimeMillis() - session.getWindowOpenedAt())));
        }
        return dto;
    }

    private static Player hideCards(Player p) {
        if (p == null) return null;
        Player hidden = new Player(p.getId(), p.getName());
        hidden.setWins(p.getWins());
        hidden.setEventsUsed(p.getEventsUsed());
        hidden.setActed(p.isActed());
        hidden.setHoleCards(null);
        hidden.setMajorCards(null);
        return hidden;
    }
}
