package org.example.tarotpokerapplication.exception;

public class GameSessionNotFoundException extends RuntimeException {
    public GameSessionNotFoundException(String sessionId) {
        super("Session '" + sessionId + "' not found");
    }
}
