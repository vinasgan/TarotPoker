package org.example.tarotpokerapplication.exception;

public class PlayerAlreadyExistsException extends RuntimeException {
    public PlayerAlreadyExistsException(String id) {
        super("Player with ID '" + id + "' already exists");
    }
}
