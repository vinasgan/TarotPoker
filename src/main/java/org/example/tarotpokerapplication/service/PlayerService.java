package org.example.tarotpokerapplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tarotpokerapplication.db.PlayerEntity;
import org.example.tarotpokerapplication.db.PlayerRepository;
import org.example.tarotpokerapplication.dto.PlayerCreateDto;
import org.example.tarotpokerapplication.dto.PlayerResponseDto;
import org.example.tarotpokerapplication.dto.PlayerUpdateDto;
import org.example.tarotpokerapplication.exception.InvalidGameActionException;
import org.example.tarotpokerapplication.exception.PlayerAlreadyExistsException;
import org.example.tarotpokerapplication.exception.PlayerNotFoundException;
import org.example.tarotpokerapplication.security.FallbackHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.authorization.method.HandleAuthorizationDenied;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService {

    private static final Pattern MINOR_CARD_PATTERN = Pattern.compile(
            "^(2|3|4|5|6|7|8|9|10|Page|Knight|Queen|King|Ace) of (Wands|Cups|Swords|Pentacles)$"
    );

    private final PlayerRepository playerRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @HandleAuthorizationDenied(handlerClass = FallbackHandler.class)
    @Transactional(readOnly = true)
    public List<PlayerResponseDto> findAll() {
        return playerRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @AuthorizeReturnObject
    @Transactional(readOnly = true)
    public Optional<PlayerResponseDto> findById(String id) {
        return playerRepository.findById(id).map(this::toResponse);
    }

    @Transactional
    public PlayerResponseDto create(PlayerCreateDto dto) {
        if (playerRepository.existsById(dto.getId())) {
            throw new PlayerAlreadyExistsException(dto.getId());
        }
        PlayerEntity entity = new PlayerEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return toResponse(playerRepository.save(entity));
    }

    @Transactional
    public PlayerResponseDto update(String id, PlayerUpdateDto dto) {
        PlayerEntity existing = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));

        log.debug("update player id={} wins={} minorCards={} majorCards={}",
                id, dto.getWins(),
                dto.getMinorCards() != null ? dto.getMinorCards().size() : 0,
                dto.getMajorCards() != null ? dto.getMajorCards().size() : 0);
        if (dto.getMinorCards() != null) {
            List<String> invalid = dto.getMinorCards().stream()
                    .filter(card -> !MINOR_CARD_PATTERN.matcher(card).matches())
                    .toList();
            if (!invalid.isEmpty()) {
                throw new InvalidGameActionException(
                        "Invalid minor card(s): " + invalid + ". Expected format: '<rank> of <suit>' " +
                        "(ranks: 2-10, Page, Knight, Queen, King, Ace; suits: Wands, Cups, Swords, Pentacles)"
                );
            }
        }

        existing.setWins(dto.getWins());
        existing.getMinorCards().clear();
        if (dto.getMinorCards() != null) existing.getMinorCards().addAll(dto.getMinorCards());
        existing.getMajorCards().clear();
        if (dto.getMajorCards() != null) existing.getMajorCards().addAll(dto.getMajorCards());
        return toResponse(playerRepository.save(existing));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @HandleAuthorizationDenied(handlerClass = FallbackHandler.class)
    @Transactional
    public void delete(String id) {
        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException(id);
        }
        playerRepository.deleteById(id);
    }

    private PlayerResponseDto toResponse(PlayerEntity entity) {
        PlayerResponseDto dto = new PlayerResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setWins(entity.getWins());
        dto.setMinorCards(entity.getMinorCards());
        dto.setMajorCards(entity.getMajorCards());
        return dto;
    }
}
