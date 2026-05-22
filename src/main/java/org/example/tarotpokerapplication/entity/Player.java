package org.example.tarotpokerapplication.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Player {
    private final String id;
    private final String name;
    private int wins = 0;
    private List<MinorArcanaCard> holeCards = new ArrayList<>();
    private List<MajorArcanaCard> majorCards = new ArrayList<>();
    private int eventsUsed = 0;
    private boolean acted = false;

    public void resetForRound() {
        holeCards = new ArrayList<>();
        eventsUsed = 0;
        acted = false;
    }
}
