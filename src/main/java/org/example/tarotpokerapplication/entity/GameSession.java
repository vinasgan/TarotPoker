package org.example.tarotpokerapplication.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameSession {

    private final String sessionId;
    private final String inviteCode;

    private Player player1;
    private Player player2;

    private int round = 0;
    private GamePhase phase = GamePhase.WAITING_FOR_PLAYER;
    private int windowNumber = 0;
    private List<MinorArcanaCard> communityCards = new ArrayList<>();

    private GameDeck deck;

    private String lastEffectMessage;
    private String roundWinnerName;

    private boolean botMode = false;
    private MajorArcanaCard botLastEventCard = null;
    private boolean firstActorIsPlayer1 = true;
    private boolean publicMatch = false;
    private long windowOpenedAt = 0;
}
