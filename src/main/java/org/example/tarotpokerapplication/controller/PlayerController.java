package org.example.tarotpokerapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tarotpokerapplication.dto.PlayerCreateDto;
import org.example.tarotpokerapplication.dto.PlayerResponseDto;
import org.example.tarotpokerapplication.dto.PlayerUpdateDto;
import org.example.tarotpokerapplication.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public List<PlayerResponseDto> getAll(@RequestParam(required = false) String name) {
        return playerService.findAll(name);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponseDto> getById(@PathVariable String id) {
        return playerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlayerResponseDto> create(@Valid @RequestBody PlayerCreateDto dto) {
        return ResponseEntity.ok(playerService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponseDto> update(
            @PathVariable String id,
            @Valid @RequestBody PlayerUpdateDto dto) {
        return playerService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return playerService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
