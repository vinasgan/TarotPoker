package org.example.tarotpokerapplication.entity;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class GameStateDto {

    private String sessionId;
    private String inviteCode;
    private String phase;
    private int round;

    private String player1Name;
    private String player2Name;
    private int player1Wins;
    private int player2Wins;

    // Caller-relative fields
    private String myRole;          // "player1" | "player2" | "spectator"
    private List<CardDto> myHoleCards;
    private List<CardDto> myMajorCards;
    private List<CardDto> opponentHoleCards; // revealed only at ROUND_END / MATCH_END

    private List<CardDto> communityCards;

    private int myEventsUsedThisRound;
    private int opponentEventsUsedThisRound;
    private boolean myActedInWindow;
    private boolean opponentActedInWindow;

    private String lastEffectMessage;
    private String roundWinnerName;

    // ── Factory ──────────────────────────────────────────────────────────────

    public static GameStateDto from(GameSession s, String userId) {
        GameStateDto dto = new GameStateDto();
        dto.sessionId = s.getSessionId();
        dto.inviteCode = s.getInviteCode();
        dto.phase = s.getPhase().name();
        dto.round = s.getRound();
        dto.player1Name = s.getPlayer1Name();
        dto.player2Name = s.getPlayer2Name();
        dto.player1Wins = s.getPlayer1Wins();
        dto.player2Wins = s.getPlayer2Wins();
        dto.lastEffectMessage = s.getLastEffectMessage();
        dto.roundWinnerName = s.getRoundWinnerName();
        dto.communityCards = toCardDtos(s.getCommunityCards());

        boolean isP1 = userId != null && userId.equals(s.getPlayer1Id());
        boolean isP2 = userId != null && userId.equals(s.getPlayer2Id());
        boolean showOpponentCards = s.getPhase() == GamePhase.ROUND_END
                || s.getPhase() == GamePhase.MATCH_END;

        if (isP1) {
            dto.myRole = "player1";
            dto.myHoleCards = toCardDtos(s.getPlayer1HoleCards());
            dto.myMajorCards = toCardDtos(s.getPlayer1MajorCards());
            dto.opponentHoleCards = showOpponentCards ? toCardDtos(s.getPlayer2HoleCards()) : null;
            dto.myEventsUsedThisRound = s.getPlayer1EventsUsed();
            dto.opponentEventsUsedThisRound = s.getPlayer2EventsUsed();
            dto.myActedInWindow = s.isPlayer1Acted();
            dto.opponentActedInWindow = s.isPlayer2Acted();
        } else if (isP2) {
            dto.myRole = "player2";
            dto.myHoleCards = toCardDtos(s.getPlayer2HoleCards());
            dto.myMajorCards = toCardDtos(s.getPlayer2MajorCards());
            dto.opponentHoleCards = showOpponentCards ? toCardDtos(s.getPlayer1HoleCards()) : null;
            dto.myEventsUsedThisRound = s.getPlayer2EventsUsed();
            dto.opponentEventsUsedThisRound = s.getPlayer1EventsUsed();
            dto.myActedInWindow = s.isPlayer2Acted();
            dto.opponentActedInWindow = s.isPlayer1Acted();
        } else {
            dto.myRole = "spectator";
        }

        return dto;
    }

    private static List<CardDto> toCardDtos(List<?> cards) {
        if (cards == null) return null;
        return cards.stream().map(c -> {
            CardDto d = new CardDto();
            if (c instanceof MinorArcanaCard m) {
                d.name = m.getName();
                d.suit = m.getSuit();
                d.power = m.getPower();
                d.type = "minor";
            } else if (c instanceof MajorArcanaCard mj) {
                d.name = mj.getName();
                d.eventId = mj.getEventId();
                d.type = "major";
            }
            return d;
        }).collect(Collectors.toList());
    }

    @Data
    public static class CardDto {
        String type;
        String name;
        String suit;
        int power;
        int eventId;
    }
}
