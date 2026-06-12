package org.example.tarotpokerapplication.exception;

public class PlayerNotFoundInSessionException extends RuntimeException {
    public PlayerNotFoundInSessionException(String userId, String sessionId) {
        super("Player '" + userId + "' is not a participant in session '" + sessionId + "'");
    }
}
