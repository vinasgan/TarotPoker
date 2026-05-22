package org.example.tarotpokerapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.tarotpokerapplication.db.PlayerEntity;
import org.example.tarotpokerapplication.db.PlayerRepository;
import org.example.tarotpokerapplication.dto.PlayerCreateDto;
import org.example.tarotpokerapplication.dto.PlayerResponseDto;
import org.example.tarotpokerapplication.dto.PlayerUpdateDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Transactional(readOnly = true)
    public List<PlayerResponseDto> findAll(String name) {
        List<PlayerEntity> entities = (name != null && !name.isBlank())
                ? playerRepository.findByNameContainingIgnoreCase(name)
                : playerRepository.findAll();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PlayerResponseDto> findById(String id) {
        return playerRepository.findById(id).map(this::toResponse);
    }

    @Transactional
    public PlayerResponseDto create(PlayerCreateDto dto) {
        if (playerRepository.existsById(dto.getId())) {
            throw new IllegalArgumentException("Player with ID '" + dto.getId() + "' already exists");
        }
        PlayerEntity entity = new PlayerEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return toResponse(playerRepository.save(entity));
    }

    @Transactional
    public Optional<PlayerResponseDto> update(String id, PlayerUpdateDto dto) {
        return playerRepository.findById(id).map(existing -> {
            existing.setWins(dto.getWins());
            existing.getMinorCards().clear();
            existing.getMinorCards().addAll(dto.getMinorCards());
            existing.getMajorCards().clear();
            if (dto.getMajorCards() != null) existing.getMajorCards().addAll(dto.getMajorCards());
            return toResponse(playerRepository.save(existing));
        });
    }

    @Transactional
    public boolean delete(String id) {
        if (!playerRepository.existsById(id)) return false;
        playerRepository.deleteById(id);
        return true;
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
