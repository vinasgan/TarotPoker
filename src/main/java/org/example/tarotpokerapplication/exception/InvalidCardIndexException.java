package org.example.tarotpokerapplication.exception;

public class InvalidCardIndexException extends RuntimeException {
    public InvalidCardIndexException(int index, int handSize) {
        super("Card index " + index + " is out of range — hand has " + handSize + " card(s)");
    }
}
