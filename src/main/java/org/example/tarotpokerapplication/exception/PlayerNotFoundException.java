package org.example.tarotpokerapplication.exception;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String id) {
        super("Player with ID '" + id + "' not found");
    }
}
