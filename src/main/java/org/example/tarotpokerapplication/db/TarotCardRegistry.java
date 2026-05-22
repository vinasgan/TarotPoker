package org.example.tarotpokerapplication.entity.db;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.tarotpokerapplication.entity.MajorArcanaCard;
import org.example.tarotpokerapplication.entity.MinorArcanaCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TarotCardRegistry {

    @Getter
    private List<MinorArcanaCard> masterMinor;
    @Getter
    private List<MajorArcanaCard> masterMajor;

    @Autowired(required = false)
    private CardRepository cardRepository;

    @PostConstruct
    public void loadLibrary() {
        if (cardRepository != null) {
            try {
                List<MinorArcanaCard> minor = cardRepository.findByArcana("Minor").stream()
                        .map(e -> new MinorArcanaCard(e.getName(), e.getSuit(), e.getPower()))
                        .toList();
                List<MajorArcanaCard> major = cardRepository.findByArcana("Major").stream()
                        .map(e -> new MajorArcanaCard(e.getName(), e.getEventId()))
                        .toList();
                if (!minor.isEmpty() && !major.isEmpty()) {
                    this.masterMinor = minor;
                    this.masterMajor = major;
                    log.info("TarotCardRegistry: loaded {} minor, {} major cards from MongoDB.",
                            minor.size(), major.size());
                    return;
                }
                log.warn("TarotCardRegistry: DB returned empty card lists, using built-in deck.");
            } catch (Exception e) {
                log.warn("TarotCardRegistry: MongoDB error — {}", e.getMessage());
            }
        }
        log.info("TarotCardRegistry: using built-in deck.");
        loadFallback();
    }

    private void loadFallback() {
        String[] suits = {"Wands", "Cups", "Swords", "Pentacles"};
        List<MinorArcanaCard> minor = new ArrayList<>();
        for (String suit : suits) {
            for (int power = 2; power <= 15; power++) {
                minor.add(new MinorArcanaCard(rankName(power) + " of " + suit, suit, power));
            }
        }
        this.masterMinor = minor;

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
