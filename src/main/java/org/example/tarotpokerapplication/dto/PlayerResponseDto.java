package org.example.tarotpokerapplication.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.example.tarotpokerapplication.security.FallbackHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.HandleAuthorizationDenied;

import java.util.List;

@Data
public class PlayerResponseDto {
    private String id;
    private String name;
    private int wins;

    @Getter(AccessLevel.NONE)
    private List<String> minorCards;

    @Getter(AccessLevel.NONE)
    private List<String> majorCards;

    @PreAuthorize("hasRole('ADMIN')")
    @HandleAuthorizationDenied(handlerClass = FallbackHandler.class)
    public List<String> getMinorCards() { return minorCards; }

    @PreAuthorize("hasRole('ADMIN')")
    @HandleAuthorizationDenied(handlerClass = FallbackHandler.class)
    public List<String> getMajorCards() { return majorCards; }
}
