package org.example.tarotpokerapplication.service;

import org.example.tarotpokerapplication.entity.MajorArcanaCard;
import org.example.tarotpokerapplication.entity.MinorArcanaCard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CardDeckRefillService {

    private final List<MinorArcanaCard> masterMinor;
    private final List<MajorArcanaCard> masterMajor;

    public CardDeckRefillService() {
        String[] suits = {"Wands", "Cups", "Swords", "Pentacles"};
        List<MinorArcanaCard> minor = new ArrayList<>();
        for (String suit : suits) {
            for (int power = 2; power <= 15; power++) {
                minor.add(new MinorArcanaCard(rankName(power) + " of " + suit, suit, power));
            }
        }
        this.masterMinor = Collections.unmodifiableList(minor);

        this.masterMajor = List.of(
                new MajorArcanaCard("The Fool", 0),
                new MajorArcanaCard("The Magician", 1),
                new MajorArcanaCard("The High Priestess", 2),
                new MajorArcanaCard("The Empress", 3),
                new MajorArcanaCard("The Emperor", 4),
                new MajorArcanaCard("The Hierophant", 5),
                new MajorArcanaCard("The Lovers", 6),
                new MajorArcanaCard("The Chariot", 7),
                new MajorArcanaCard("Strength", 8),
                new MajorArcanaCard("The Hermit", 9),
                new MajorArcanaCard("Wheel of Fortune", 10),
                new MajorArcanaCard("Justice", 11),
                new MajorArcanaCard("The Hanged Man", 12),
                new MajorArcanaCard("Death", 13),
                new MajorArcanaCard("Temperance", 14),
                new MajorArcanaCard("The Devil", 15),
                new MajorArcanaCard("The Tower", 16),
                new MajorArcanaCard("The Star", 17),
                new MajorArcanaCard("The Moon", 18),
                new MajorArcanaCard("The Sun", 19),
                new MajorArcanaCard("Judgement", 20),
                new MajorArcanaCard("The World", 21)
        );
    }

    public List<MinorArcanaCard> getMasterMinor() {
        return masterMinor;
    }

    public List<MajorArcanaCard> getMasterMajor() {
        return masterMajor;
    }

    private String rankName(int power) {
        return switch (power) {
            case 15 -> "Ace";
            case 14 -> "King";
            case 13 -> "Queen";
            case 12 -> "Knight";
            case 11 -> "Page";
            default -> String.valueOf(power);
        };
    }
}
