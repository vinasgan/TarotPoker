package org.example.tarotpokerapplication.entity;

import lombok.Getter;
import org.example.tarotpokerapplication.service.CardDeckRefillService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameDeck {

    private final CardDeckRefillService refillService;
    @Getter
    private final List<MinorArcanaCard> activeMinor = new ArrayList<>();
    @Getter
    private final List<MajorArcanaCard> activeMajor = new ArrayList<>();

    public GameDeck(CardDeckRefillService refillService) {
        this.refillService = refillService;
        refillMinor();
        refillMajor();
    }

    public void refillMinor() {
        activeMinor.clear();
        activeMinor.addAll(refillService.getMasterMinor());
        Collections.shuffle(activeMinor);
    }

    public void refillMajor() {
        activeMajor.clear();
        activeMajor.addAll(refillService.getMasterMajor());
        Collections.shuffle(activeMajor);
    }

    public List<MinorArcanaCard> drawMinor(int num) {
        List<MinorArcanaCard> drawn = new ArrayList<>();
        while (drawn.size() < num) {
            if (activeMinor.isEmpty()) refillMinor();
            drawn.add(activeMinor.remove(0));
        }
        return drawn;
    }

    public List<MajorArcanaCard> drawMajor(int num) {
        List<MajorArcanaCard> drawn = new ArrayList<>();
        while (drawn.size() < num) {
            if (activeMajor.isEmpty()) refillMajor();
            drawn.add(activeMajor.remove(0));
        }
        return drawn;
    }
}
