package org.example.tarotpokerapplication.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.tarotpokerapplication.dto.GameSessionResponseDto;
import org.example.tarotpokerapplication.dto.PlayerResponseDto;
import org.example.tarotpokerapplication.entity.GamePhase;
import org.example.tarotpokerapplication.entity.GameSession;
import org.example.tarotpokerapplication.entity.MajorArcanaCard;
import org.example.tarotpokerapplication.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class ServiceLoggingAspect {

    private static final String MDC_MATCH = "matchId";

    @Pointcut("execution(* org.example.tarotpokerapplication.service.GameSessionService.create*(String, String))")
    public void sessionCreate() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.GameSessionService.joinByCode(String, String, String))")
    public void sessionJoinByCode() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.GameSessionService.joinPublicSession(String, String))")
    public void sessionJoinPublic() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.GameSessionService.abandonSession(String))")
    public void sessionAbandon() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.GameSessionService.pass(String, String))")
    public void sessionPass() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.GameSessionService.triggerEvent(String, String, int))")
    public void sessionTriggerEvent() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.GameSessionService.nextRound(String, String))")
    public void sessionNextRound() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.EventEffectService.applyEffect(..))")
    public void effectApply() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.WinnerDeterminerService.determineWinner(..))")
    public void winnerDetermine() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.CardDeckRefillService.getMasterMinor())")
    public void deckRefillMinor() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.CardDeckRefillService.getMasterMajor())")
    public void deckRefillMajor() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.PlayerSyncService.syncPlayer(..))")
    public void playerSyncPlayer() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.PlayerSyncService.syncSession(..))")
    public void playerSyncSession() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.GameSessionService.getAllSessions())")
    public void sessionGetAll() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.GameSessionService.getSession(String, String))")
    public void sessionGetOne() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.PlayerService.findAll())")
    public void playerFindAll() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.PlayerService.findById(String))")
    public void playerFindById() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.PlayerService.create(..))")
    public void playerCreate() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.PlayerService.update(String, ..))")
    public void playerUpdate() {}

    @Pointcut("execution(* org.example.tarotpokerapplication.service.PlayerService.delete(String))")
    public void playerDelete() {}

    @Pointcut("within(org.example.tarotpokerapplication.service..*)")
    public void serviceLayer() {}

    @Pointcut("within(org.example.tarotpokerapplication.controller..*)")
    public void controllerLayer() {}

    @Around("controllerLayer()")
    public Object logRequest(ProceedingJoinPoint pjp) throws Throwable {
        String cls    = pjp.getTarget().getClass().getSimpleName();
        String method = pjp.getSignature().getName();
        logger(pjp).debug("→ {}.{}()", cls, method);
        try {
            Object result = pjp.proceed();
            logger(pjp).debug("← {}.{}() completed", cls, method);
            return result;
        } catch (Throwable ex) {
            logger(pjp).warn("{}.{}() — {}: {}", cls, method,
                    ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }

    @Around("sessionCreate()")
    public Object logSessionCreate(ProceedingJoinPoint pjp) throws Throwable {
        String userId   = str(pjp, 0);
        String username = str(pjp, 1);
        String type = switch (pjp.getSignature().getName()) {
            case "createPrivateSession" -> "private";
            case "createPublicSession"  -> "public";
            case "createBotSession"     -> "vs Bot";
            default                     -> "unknown";
        };
        GameSessionResponseDto dto = (GameSessionResponseDto) pjp.proceed();
        logger(pjp).info("Session {} created ({}) — Player: {} (id={})",
                dto.getSessionId(), type, name(username, userId), userId);
        return dto;
    }

    @Around("sessionJoinByCode()")
    public Object logJoinByCode(ProceedingJoinPoint pjp) throws Throwable {
        String inviteCode = str(pjp, 0);
        String userId     = str(pjp, 1);
        String username   = str(pjp, 2);
        GameSessionResponseDto dto = (GameSessionResponseDto) pjp.proceed();
        logger(pjp).info("Player {} (id={}) joined session {} via code {}",
                name(username, userId), userId, dto.getSessionId(), inviteCode);
        return dto;
    }

    @Around("sessionJoinPublic()")
    public Object logJoinPublic(ProceedingJoinPoint pjp) throws Throwable {
        String userId   = str(pjp, 0);
        String username = str(pjp, 1);
        GameSessionResponseDto dto = (GameSessionResponseDto) pjp.proceed();
        logger(pjp).info("Player {} (id={}) joined public session {}",
                name(username, userId), userId, dto.getSessionId());
        return dto;
    }

    @Around("sessionAbandon()")
    public Object logAbandon(ProceedingJoinPoint pjp) throws Throwable {
        String sessionId = str(pjp, 0);
        MDC.put(MDC_MATCH, sessionId);
        try {
            Object result = pjp.proceed();
            logger(pjp).info("Session {} abandoned", sessionId);
            return result;
        } finally {
            MDC.remove(MDC_MATCH);
        }
    }

    @Around("sessionPass()")
    public Object logPass(ProceedingJoinPoint pjp) throws Throwable {
        String sessionId = str(pjp, 0);
        String userId    = str(pjp, 1);
        MDC.put(MDC_MATCH, sessionId);
        try {
            GameSessionResponseDto dto = (GameSessionResponseDto) pjp.proceed();
            logger(pjp).info("Player {} passed in session {} (round {})", userId, sessionId, dto.getRound());
            logPhaseTransition(logger(pjp), dto, sessionId);
            return dto;
        } finally {
            MDC.remove(MDC_MATCH);
        }
    }

    @Around("sessionTriggerEvent()")
    public Object logTriggerEvent(ProceedingJoinPoint pjp) throws Throwable {
        String sessionId = str(pjp, 0);
        String userId    = str(pjp, 1);
        int cardIndex    = (int) pjp.getArgs()[2];
        MDC.put(MDC_MATCH, sessionId);
        try {
            GameSessionResponseDto dto = (GameSessionResponseDto) pjp.proceed();
            String effect = dto.getLastEffectMessage() != null ? dto.getLastEffectMessage() : "none";
            logger(pjp).info("Player {} triggered event in session {} (card slot {}) → {}",
                    userId, sessionId, cardIndex, effect);
            logPhaseTransition(logger(pjp), dto, sessionId);
            return dto;
        } finally {
            MDC.remove(MDC_MATCH);
        }
    }

    @Around("sessionNextRound()")
    public Object logNextRound(ProceedingJoinPoint pjp) throws Throwable {
        String sessionId = str(pjp, 0);
        MDC.put(MDC_MATCH, sessionId);
        try {
            GameSessionResponseDto dto = (GameSessionResponseDto) pjp.proceed();
            logger(pjp).info("Round {} started in session {}", dto.getRound(), sessionId);
            return dto;
        } finally {
            MDC.remove(MDC_MATCH);
        }
    }

    @Around("effectApply()")
    public Object logEffect(ProceedingJoinPoint pjp) throws Throwable {
        GameSession session = (GameSession) pjp.getArgs()[0];
        boolean isPlayer1   = (boolean)     pjp.getArgs()[1];
        int cardIndex       = (int)         pjp.getArgs()[2];
        Player player       = isPlayer1 ? session.getPlayer1() : session.getPlayer2();
        List<MajorArcanaCard> hand = player.getMajorCards();
        String cardName = cardIndex >= 0 && cardIndex < hand.size() ? hand.get(cardIndex).getName() : "?";
        Object result = pjp.proceed();
        logger(pjp).info("Effect in session {} — Player {} ({}) played '{}' → {}",
                session.getSessionId(), isPlayer1 ? 1 : 2, player.getName(),
                cardName, session.getLastEffectMessage());
        return result;
    }

    @Around("winnerDetermine()")
    public Object logWinner(ProceedingJoinPoint pjp) throws Throwable {
        int result = (int) pjp.proceed();
        String matchId = MDC.get(MDC_MATCH);
        String outcome = switch (result) {
            case 1  -> "Player 1 wins";
            case 2  -> "Player 2 wins";
            default -> "draw";
        };
        logger(pjp).info("Round result in session {} — {}", matchId != null ? matchId : "?", outcome);
        return result;
    }

    @Around("deckRefillMinor()")
    public Object logRefillMinor(ProceedingJoinPoint pjp) throws Throwable {
        Object result  = pjp.proceed();
        String matchId = MDC.get(MDC_MATCH);
        if (matchId != null) logger(pjp).info("Minor deck refilled — match: {}", matchId);
        return result;
    }

    @Around("deckRefillMajor()")
    public Object logRefillMajor(ProceedingJoinPoint pjp) throws Throwable {
        Object result  = pjp.proceed();
        String matchId = MDC.get(MDC_MATCH);
        if (matchId != null) logger(pjp).info("Major deck refilled — match: {}", matchId);
        return result;
    }

    @Around("playerSyncPlayer()")
    public Object logSyncPlayer(ProceedingJoinPoint pjp) throws Throwable {
        Player player = (Player) pjp.getArgs()[0];
        Object result = pjp.proceed();
        logger(pjp).info("Player synced to DB — {} (id={}), wins: {}",
                player.getName(), player.getId(), player.getWins());
        return result;
    }

    @Around("playerSyncSession()")
    public Object logSyncSession(ProceedingJoinPoint pjp) throws Throwable {
        GameSession session = (GameSession) pjp.getArgs()[0];
        Object result = pjp.proceed();
        if (session.getPhase() == GamePhase.MATCH_END) {
            Player p1 = session.getPlayer1();
            Player p2 = session.getPlayer2();
            String winnerId = (p1 != null && (p2 == null || p1.getWins() > p2.getWins()))
                    ? p1.getId() : (p2 != null ? p2.getId() : "unknown");
            logger(pjp).info("Match ended — session: {}, winner id: {}",
                    session.getSessionId(), winnerId);
        } else {
            logger(pjp).info("Session {} synced to DB — phase: {}, round: {}",
                    session.getSessionId(), session.getPhase(), session.getRound());
        }
        return result;
    }

    @Around("sessionGetAll()")
    public Object logGetAll(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        logger(pjp).debug("getAllSessions — {} active sessions", ((List<?>) result).size());
        return result;
    }

    @Around("sessionGetOne()")
    public Object logGetOne(ProceedingJoinPoint pjp) throws Throwable {
        String sessionId = str(pjp, 0);
        String userId    = str(pjp, 1);
        MDC.put(MDC_MATCH, sessionId);
        try {
            Object result = pjp.proceed();
            logger(pjp).debug("getSession({}) for userId={}", sessionId, userId);
            return result;
        } finally {
            MDC.remove(MDC_MATCH);
        }
    }

    @Around("playerFindAll()")
    public Object logPlayerFindAll(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        logger(pjp).debug("findAll players — {} records", ((List<?>) result).size());
        return result;
    }

    @Around("playerFindById()")
    public Object logPlayerFindById(ProceedingJoinPoint pjp) throws Throwable {
        String id = str(pjp, 0);
        java.util.Optional<?> result = (java.util.Optional<?>) pjp.proceed();
        logger(pjp).debug("findById id={} — found: {}", id, result.isPresent());
        return result;
    }

    @Around("playerCreate()")
    public Object logPlayerCreate(ProceedingJoinPoint pjp) throws Throwable {
        PlayerResponseDto dto = (PlayerResponseDto) pjp.proceed();
        logger(pjp).info("Player created — {} (id={})", dto.getName(), dto.getId());
        return dto;
    }

    @Around("playerUpdate()")
    public Object logPlayerUpdate(ProceedingJoinPoint pjp) throws Throwable {
        PlayerResponseDto dto = (PlayerResponseDto) pjp.proceed();
        logger(pjp).info("Player updated — {} (id={}), wins: {}",
                dto.getName(), dto.getId(), dto.getWins());
        return dto;
    }

    @Around("playerDelete()")
    public Object logPlayerDelete(ProceedingJoinPoint pjp) throws Throwable {
        String id = str(pjp, 0);
        Object result = pjp.proceed();
        logger(pjp).info("Player deleted — id: {}", id);
        return result;
    }

    @AfterThrowing(pointcut = "serviceLayer() && !effectApply()", throwing = "ex")
    public void logError(JoinPoint jp, Throwable ex) {
        String matchId = MDC.get(MDC_MATCH);
        String prefix  = matchId != null ? "[session=" + matchId + "] " : "";
        LoggerFactory.getLogger(jp.getTarget().getClass())
                .error("{}{}() — {}: {}",
                        prefix, jp.getSignature().getName(),
                        ex.getClass().getSimpleName(), ex.getMessage());
    }

    private static Logger logger(ProceedingJoinPoint pjp) {
        return LoggerFactory.getLogger(pjp.getTarget().getClass());
    }

    private static String str(ProceedingJoinPoint pjp, int i) {
        Object[] args = pjp.getArgs();
        return (args.length > i && args[i] != null) ? args[i].toString() : null;
    }

    private static String name(String username, String userId) {
        return (username != null && !username.isBlank()) ? username : userId;
    }

    private static void logPhaseTransition(Logger log, GameSessionResponseDto dto, String sessionId) {
        if (dto.getPhase() == GamePhase.MATCH_END) {
            log.info("Match ended — session: {}, match winner: {}", sessionId,
                    dto.getRoundWinnerName() != null ? dto.getRoundWinnerName() : "unknown");
        } else if (dto.getPhase() == GamePhase.ROUND_END) {
            log.info("Round {} ended — session: {}, round winner: {}", dto.getRound(), sessionId,
                    dto.getRoundWinnerName() != null ? dto.getRoundWinnerName() : "draw");
        }
    }
}
