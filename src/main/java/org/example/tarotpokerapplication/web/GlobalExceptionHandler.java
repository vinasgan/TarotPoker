package org.example.tarotpokerapplication.web;

import org.example.tarotpokerapplication.exception.GameNotAcceptingPlayersException;
import org.example.tarotpokerapplication.exception.PlayerAlreadyExistsException;
import org.example.tarotpokerapplication.exception.GameSessionNotFoundException;
import org.example.tarotpokerapplication.exception.InvalidCardIndexException;
import org.example.tarotpokerapplication.exception.InvalidGameActionException;
import org.example.tarotpokerapplication.exception.InvalidHandException;
import org.example.tarotpokerapplication.exception.InvalidTableException;
import org.example.tarotpokerapplication.exception.PlayerNotFoundInSessionException;
import org.example.tarotpokerapplication.exception.PlayerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GameSessionNotFoundException.class)
    public Map<String, Object> handleSessionNotFound(GameSessionNotFoundException ex) {
        return errorBody(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PlayerNotFoundException.class)
    public Map<String, Object> handlePlayerNotFound(PlayerNotFoundException ex) {
        return errorBody(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PlayerAlreadyExistsException.class)
    public Map<String, Object> handlePlayerAlreadyExists(PlayerAlreadyExistsException ex) {
        return errorBody(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(PlayerNotFoundInSessionException.class)
    public Map<String, Object> handlePlayerNotInSession(PlayerNotFoundInSessionException ex) {
        return errorBody(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(GameNotAcceptingPlayersException.class)
    public Map<String, Object> handleGameNotAccepting(GameNotAcceptingPlayersException ex) {
        return errorBody(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidCardIndexException.class)
    public Map<String, Object> handleInvalidCardIndex(InvalidCardIndexException ex) {
        return errorBody(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidHandException.class)
    public Map<String, Object> handleInvalidHand(InvalidHandException ex) {
        return errorBody(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTableException.class)
    public Map<String, Object> handleInvalidTable(InvalidTableException ex) {
        return errorBody(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidGameActionException.class)
    public Map<String, Object> handleInvalidAction(InvalidGameActionException ex) {
        return errorBody(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> body.put(e.getField(), e.getDefaultMessage()));
        return body;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String, Object> handleUnreadable(HttpMessageNotReadableException ex) {
        return errorBody(HttpStatus.BAD_REQUEST,
                "Request body is malformed or contains invalid field types. " + ex.getMostSpecificCause().getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public Map<String, Object> handleNoResource(NoResourceFoundException ex) {
        return errorBody(HttpStatus.NOT_FOUND, "No endpoint: " + ex.getResourcePath());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleGeneral() {
        return errorBody(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private static Map<String, Object> errorBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", message);
        return body;
    }
}
