package org.example.tarotpokerapplication.exception;

public class GameNotAcceptingPlayersException extends RuntimeException {
    public GameNotAcceptingPlayersException(String inviteCode) {
        super("Session with invite code '" + inviteCode + "' is not accepting players");
    }
}
