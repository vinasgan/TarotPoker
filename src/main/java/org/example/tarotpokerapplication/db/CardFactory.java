package org.example.tarotpokerapplication.entity.db;

import org.example.tarotpokerapplication.entity.MajorArcanaCard;
import org.example.tarotpokerapplication.entity.MinorArcanaCard;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardFactory {

    public List<MinorArcanaCard> createMinorArcana() {
        List<MinorArcanaCard> cards = new ArrayList<>();
        String[] suits = {"Wands", "Cups", "Swords", "Pentacles"};
        String[] names = {"Two", "Three", "Four", "Five", "Six", "Seven",
                "Eight", "Nine", "Ten", "Page", "Knight", "Queen", "King", "Ace"};
        int[] powers = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        for (String suit : suits) {
            for (int i = 0; i < names.length; i++) {
                cards.add(new MinorArcanaCard(names[i] + " of " + suit, suit, powers[i]));
            }
        }
        return cards;
    }

    public List<MajorArcanaCard> createMajorArcana() {
        return List.of(
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
}
