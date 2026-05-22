package org.example.tarotpokerapplication.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<PlayerEntity, String> {
    List<PlayerEntity> findByNameContainingIgnoreCase(String name);
}
