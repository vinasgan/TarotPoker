package org.example.tarotpokerapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.tarotpokerapplication.dto.GameSessionResponseDto;
import org.example.tarotpokerapplication.entity.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GameSessionService {

    private static final int WINS_TO_WIN_MATCH = 3;
    private static final String BOT_ID = "BOT";
    private final CardDeckRefillService cardDeckRefillService;
    private final EventEffectService eventEffectService;
    private final WinnerDeterminerService winnerDeterminer;
    private final PlayerSyncService playerPersistenceService;
    private static final long MOVE_TIMEOUT_MS = 20_000;
    private final Random random = new Random();
    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> codeIndex = new ConcurrentHashMap<>();

    public GameSessionResponseDto createPrivateSession(String userId, String username) {
        String sessionId = newSessionId();
        String inviteCode = generateCode();
        GameSession session = new GameSession(sessionId, inviteCode);
        Player player1 = new Player(userId, resolvedName(userId, username));
        session.setPlayer1(player1);
        session.setDeck(new GameDeck(cardDeckRefillService));
        sessions.put(sessionId, session);
        codeIndex.put(inviteCode, sessionId);
        playerPersistenceService.syncPlayer(player1);
        playerPersistenceService.syncSession(session);
        return GameSessionResponseDto.from(session, userId);
    }

    public GameSessionResponseDto createPublicSession(String userId, String username) {
        String sessionId = newSessionId();
        String inviteCode = generateCode();
        GameSession session = new GameSession(sessionId, inviteCode);
        Player player1 = new Player(userId, resolvedName(userId, username));
        session.setPlayer1(player1);
        session.setPublicMatch(true);
        session.setDeck(new GameDeck(cardDeckRefillService));
        sessions.put(sessionId, session);
        codeIndex.put(inviteCode, sessionId);
        playerPersistenceService.syncPlayer(player1);
        playerPersistenceService.syncSession(session);
        return GameSessionResponseDto.from(session, userId);
    }

    public GameSessionResponseDto createBotSession(String userId, String username) {
        String sessionId = newSessionId();
        String inviteCode = generateCode();
        GameSession session = new GameSession(sessionId, inviteCode);
        Player player1 = new Player(userId, resolvedName(userId, username));
        session.setPlayer1(player1);
        session.setPlayer2(new Player(BOT_ID, "Bot"));
        session.setBotMode(true);
        session.setDeck(new GameDeck(cardDeckRefillService));
        sessions.put(sessionId, session);
        playerPersistenceService.syncPlayer(player1);
        startNewRound(session);
        return GameSessionResponseDto.from(session, userId);
    }

    public GameSessionResponseDto joinByCode(String inviteCode, String userId, String username) {
        String sessionId = codeIndex.get(inviteCode.toUpperCase());
        if (sessionId == null) throw new NoSuchElementException("Invite code '" + inviteCode + "' not found");
        GameSession session = sessions.get(sessionId);
        if (session == null || session.getPhase() != GamePhase.WAITING_FOR_PLAYER)
            throw new IllegalArgumentException("Session is not accepting players");
        if (userId.equals(session.getPlayer1().getId()))
            throw new IllegalArgumentException("Cannot join your own session");
        Player player2 = new Player(userId, resolvedName(userId, username));
        session.setPlayer2(player2);
        playerPersistenceService.syncPlayer(player2);
        startNewRound(session);
        playerPersistenceService.syncSession(session);
        return GameSessionResponseDto.from(session, userId);
    }

    public GameSessionResponseDto joinPublicSession(String userId, String username) {
        GameSession existing = sessions.values().stream()
                .filter(s -> s.getPhase() == GamePhase.WAITING_FOR_PLAYER
                        && s.isPublicMatch()
                        && !s.isBotMode()
                        && !s.getPlayer1().getId().equals(userId))
                .findFirst()
                .orElse(null);
        if (existing != null) {
            Player player2 = new Player(userId, resolvedName(userId, username));
            existing.setPlayer2(player2);
            playerPersistenceService.syncPlayer(player2);
            startNewRound(existing);
            playerPersistenceService.syncSession(existing);
            return GameSessionResponseDto.from(existing, userId);
        }
        return createPublicSession(userId, username);
    }

    public List<GameSessionResponseDto> getAllSessions() {
        return sessions.values().stream()
                .map(s -> GameSessionResponseDto.from(s, 0))
                .collect(java.util.stream.Collectors.toList());
    }

    public GameSessionResponseDto getSession(String sessionId, String userId) {
        GameSession session = requireSession(sessionId);
        requireParticipant(session, userId);
        autoPassTimedOut(session);
        return GameSessionResponseDto.from(session, userId);
    }

    public GameSessionResponseDto pass(String sessionId, String userId) {
        GameSession session = requireSession(sessionId);
        requireParticipant(session, userId);
        autoPassTimedOut(session);
        session.setBotLastEventCard(null);
        markActed(session, userId);
        if (session.isBotMode() && !session.getPlayer2().isActed()) botActForPlayer2(session);
        advanceIfBothActed(session);
        playerPersistenceService.syncSession(session);
        return GameSessionResponseDto.from(session, userId);
    }

    public GameSessionResponseDto triggerEvent(String sessionId, String userId, int cardIndex) {
        GameSession session = requireSession(sessionId);
        requireParticipant(session, userId);
        autoPassTimedOut(session);
        boolean isPlayer1 = userId.equals(session.getPlayer1().getId());
        Player actingPlayer = isPlayer1 ? session.getPlayer1() : session.getPlayer2();
        List<MajorArcanaCard> hand = actingPlayer.getMajorCards();
        if (hand.isEmpty() || cardIndex >= hand.size()) {
            throw new IllegalArgumentException(
                    "Card index " + cardIndex + " is out of range — player has " + hand.size() + " major card(s)");
        }
        if (actingPlayer.getEventsUsed() >= 2) {
            session.setLastEffectMessage("You have already used 2 events this round.");
            return GameSessionResponseDto.from(session, userId);
        }
        session.setBotLastEventCard(null);
        eventEffectService.applyEffect(session, isPlayer1, cardIndex);
        markActed(session, userId);
        if (session.isBotMode() && !session.getPlayer2().isActed()) botActForPlayer2(session);
        advanceIfBothActed(session);
        playerPersistenceService.syncSession(session);
        return GameSessionResponseDto.from(session, userId);
    }

    public void abandonSession(String sessionId) {
        GameSession session = requireSession(sessionId);
        sessions.remove(sessionId);
        if (session.getInviteCode() != null) codeIndex.remove(session.getInviteCode());
    }

    public GameSessionResponseDto nextRound(String sessionId, String userId) {
        GameSession session = requireSession(sessionId);
        requireParticipant(session, userId);
        if (session.getPhase() != GamePhase.ROUND_END) return GameSessionResponseDto.from(session, userId);
        startNewRound(session);
        return GameSessionResponseDto.from(session, userId);
    }

    private void botActForPlayer2(GameSession session) {
        Player bot = session.getPlayer2();
        List<MajorArcanaCard> botHand = bot.getMajorCards();
        if (!botHand.isEmpty() && bot.getEventsUsed() < 2 && random.nextInt(100) < 30) {
            int idx = random.nextInt(botHand.size());
            session.setBotLastEventCard(botHand.get(idx));
            eventEffectService.applyEffect(session, false, idx);
        }
        markActed(session, BOT_ID);
    }

    private void startNewRound(GameSession session) {
        session.setRound(session.getRound() + 1);
        session.setLastEffectMessage(null);
        session.setRoundWinnerName(null);
        session.setCommunityCards(new ArrayList<>());
        session.getPlayer1().resetForRound();
        session.getPlayer2().resetForRound();
        session.getDeck().refillMinor();
        session.getDeck().refillMajor();
        int majorToDeal = (session.getRound() == 1) ? 3 : 1;
        session.getPlayer1().getMajorCards().addAll(session.getDeck().drawMajor(majorToDeal));
        session.getPlayer2().getMajorCards().addAll(session.getDeck().drawMajor(majorToDeal));
        session.getPlayer1().setHoleCards(new ArrayList<>(session.getDeck().drawMinor(2)));
        session.getPlayer2().setHoleCards(new ArrayList<>(session.getDeck().drawMinor(2)));
        session.getCommunityCards().addAll(session.getDeck().drawMinor(3));
        session.setFirstActorIsPlayer1(random.nextBoolean());
        openWindow(session, 1);
    }

    private void openWindow(GameSession session, int windowNumber) {
        session.setPhase(GamePhase.EVENT_WINDOW);
        session.setWindowNumber(windowNumber);
        session.getPlayer1().setActed(false);
        session.getPlayer2().setActed(false);
        session.setWindowOpenedAt(System.currentTimeMillis());
    }

    private void autoPassTimedOut(GameSession session) {
        if (session.getPhase() != GamePhase.EVENT_WINDOW) return;
        if (session.getWindowOpenedAt() == 0) return;
        long elapsed = System.currentTimeMillis() - session.getWindowOpenedAt();
        if (elapsed < MOVE_TIMEOUT_MS) return;
        Player p2 = session.getPlayer2();
        if (!session.getPlayer1().isActed()) markActed(session, session.getPlayer1().getId());
        if (p2 != null && !p2.isActed()) markActed(session, p2.getId());
        advanceIfBothActed(session);
    }

    private void markActed(GameSession session, String userId) {
        if (userId.equals(session.getPlayer1().getId())) session.getPlayer1().setActed(true);
        else if (session.getPlayer2() != null && userId.equals(session.getPlayer2().getId()))
            session.getPlayer2().setActed(true);
    }

    private void advanceIfBothActed(GameSession session) {
        if (!session.getPlayer1().isActed() || !session.getPlayer2().isActed()) return;
        int window = session.getWindowNumber();
        if (window < 3) {
            session.getCommunityCards().addAll(session.getDeck().drawMinor(1));
            session.setFirstActorIsPlayer1(!session.isFirstActorIsPlayer1());
            openWindow(session, window + 1);
        } else {
            endRound(session);
        }
    }

    private void endRound(GameSession session) {
        session.setPhase(GamePhase.ROUND_END);
        List<MinorArcanaCard> community = session.getCommunityCards();
        while (community.size() < 5) community.addAll(session.getDeck().drawMinor(1));
        int result = winnerDeterminer.determineWinner(
                session.getPlayer1().getHoleCards(),
                session.getPlayer2().getHoleCards(),
                community);
        if (result == 1) {
            session.getPlayer1().setWins(session.getPlayer1().getWins() + 1);
            session.setRoundWinnerName(session.getPlayer1().getName());
            playerPersistenceService.syncPlayer(session.getPlayer1());
        } else if (result == 2) {
            session.getPlayer2().setWins(session.getPlayer2().getWins() + 1);
            session.setRoundWinnerName(session.getPlayer2().getName());
            if (!BOT_ID.equals(session.getPlayer2().getId())) {
                playerPersistenceService.syncPlayer(session.getPlayer2());
            }
        } else {
            session.setRoundWinnerName(null);
        }
        if (session.getPlayer1().getWins() >= WINS_TO_WIN_MATCH
                || session.getPlayer2().getWins() >= WINS_TO_WIN_MATCH) {
            session.setPhase(GamePhase.MATCH_END);
        }
        playerPersistenceService.syncSession(session);
    }

    private String newSessionId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private String generateCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) sb.append(chars.charAt(random.nextInt(chars.length())));
        String code = sb.toString();
        return codeIndex.containsKey(code) ? generateCode() : code;
    }

    private GameSession requireSession(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null) throw new NoSuchElementException("Session '" + sessionId + "' not found");
        return session;
    }

    private void requireParticipant(GameSession session, String userId) {
        boolean isP1 = session.getPlayer1() != null && userId.equals(session.getPlayer1().getId());
        boolean isP2 = session.getPlayer2() != null && userId.equals(session.getPlayer2().getId());
        if (!isP1 && !isP2)
            throw new IllegalArgumentException("User '" + userId + "' is not a participant in this session");
    }

    private static String resolvedName(String userId, String username) {
        return (username != null && !username.isBlank()) ? username : userId;
    }
}
