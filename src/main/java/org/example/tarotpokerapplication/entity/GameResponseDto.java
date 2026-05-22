package org.example.tarotpokerapplication.entity;

import lombok.Data;

import java.util.List;

@Data
public class GameResponseDto {

    private String sessionId;
    private String inviteCode;
    private String phase;
    private int round;

    private PlayerDto player1;
    private PlayerDto player2;

    private List<CardDto> communityCards;
    private List<CardDto> myHoleCards;
    private List<CardDto> myMajorCards;
    private List<CardDto> opponentHoleCards; // populated only at ROUND_END / MATCH_END

    private long windowRemainingMs;
    private boolean firstActorIsPlayer1;
    private String lastEffectMessage;
    private String roundWinnerName;
    private CardDto botLastEventCard;

    // ── Factory ──────────────────────────────────────────────────────────────

    public static GameResponseDto from(GameSession s, String userId) {
        GameResponseDto dto = new GameResponseDto();
        dto.sessionId = s.getSessionId();
        dto.inviteCode = s.getInviteCode();
        dto.phase = s.getPhase().name();
        dto.round = s.getRound();
        dto.communityCards = toCardDtos(s.getCommunityCards());
        dto.lastEffectMessage = s.getLastEffectMessage();
        dto.roundWinnerName = s.getRoundWinnerName();
        dto.firstActorIsPlayer1 = s.isFirstActorIsPlayer1();

        Player p1 = s.getPlayer1();
        Player p2 = s.getPlayer2();
        dto.player1 = toPlayerDto(p1);
        dto.player2 = toPlayerDto(p2);

        GamePhase phase = s.getPhase();
        boolean inWindow = phase == GamePhase.EVENT_WINDOW_1
                || phase == GamePhase.EVENT_WINDOW_2
                || phase == GamePhase.EVENT_WINDOW_3;
        if (inWindow && s.getWindowOpenedAt() > 0)
            dto.windowRemainingMs = Math.max(0, 20_000 - (System.currentTimeMillis() - s.getWindowOpenedAt()));

        boolean isP1 = p1 != null && userId != null && userId.equals(p1.getId());
        boolean isP2 = p2 != null && userId != null && userId.equals(p2.getId());
        Player me = isP1 ? p1 : isP2 ? p2 : null;
        Player opponent = isP1 ? p2 : isP2 ? p1 : null;

        if (me != null) {
            dto.myHoleCards = toCardDtos(me.getHoleCards());
            dto.myMajorCards = toCardDtos(me.getMajorCards());
            boolean reveal = phase == GamePhase.ROUND_END || phase == GamePhase.MATCH_END;
            if (reveal && opponent != null) dto.opponentHoleCards = toCardDtos(opponent.getHoleCards());
        }
        if (isP1 && s.isBotMode() && s.getBotLastEventCard() != null)
            dto.botLastEventCard = toCardDto(s.getBotLastEventCard());

        return dto;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static PlayerDto toPlayerDto(Player p) {
        if (p == null) return null;
        PlayerDto dto = new PlayerDto();
        dto.name = p.getName();
        dto.wins = p.getWins();
        dto.eventsUsed = p.getEventsUsed();
        dto.acted = p.isActed();
        return dto;
    }

    private static List<CardDto> toCardDtos(List<?> cards) {
        if (cards == null) return List.of();
        return cards.stream().map(GameResponseDto::toCardDto).toList();
    }

    private static CardDto toCardDto(Object c) {
        CardDto d = new CardDto();
        if (c instanceof MinorArcanaCard m) {
            d.type = "minor";
            d.name = m.getName();
            d.suit = m.getSuit();
            d.power = m.getPower();
        } else if (c instanceof MajorArcanaCard mj) {
            d.type = "major";
            d.name = mj.getName();
            d.eventId = mj.getEventId();
        }
        return d;
    }

    @Data
    public static class PlayerDto {
        String name;
        int wins;
        int eventsUsed;
        boolean acted;
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
