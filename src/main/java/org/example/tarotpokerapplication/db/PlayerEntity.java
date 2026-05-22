package org.example.tarotpokerapplication.db;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
public class PlayerEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private int wins = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "player_minor_cards", joinColumns = @JoinColumn(name = "player_id"))
    @Column(name = "card_name")
    private List<String> minorCards = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "player_major_cards", joinColumns = @JoinColumn(name = "player_id"))
    @Column(name = "card_name")
    private List<String> majorCards = new ArrayList<>();
}
