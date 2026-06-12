package org.example.tarotpokerapplication.service;

import org.example.tarotpokerapplication.entity.GameSession;
import org.example.tarotpokerapplication.entity.MajorArcanaCard;
import org.example.tarotpokerapplication.entity.MinorArcanaCard;
import org.example.tarotpokerapplication.entity.Player;
import org.example.tarotpokerapplication.exception.InvalidCardIndexException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
public class EventEffectService {

    private final Random random = new Random();

    public void applyEffect(GameSession session, boolean isPlayer1, int cardIndex) {
        Player player = isPlayer1 ? session.getPlayer1() : session.getPlayer2();
        List<MajorArcanaCard> hand = player.getMajorCards();

        if (cardIndex < 0 || cardIndex >= hand.size()) {
            throw new InvalidCardIndexException(cardIndex, hand.size());
        }

        MajorArcanaCard card = hand.remove(cardIndex);

        switch (card.getEventId()) {
            case 0, 17, 19 -> addCommunityCard(session, card);
            case 3, 1      -> swapWeakestHole(session, player, card);
            case 10        -> shuffleCommunity(session, card);
            case 13        -> removeLowestCommunity(session, card);
            case 16, 18    -> removeRandomCommunity(session, card);
            default        -> session.setLastEffectMessage(
                    card.getName() + " — A mystic vision passes through the veil...");
        }

        player.setEventsUsed(player.getEventsUsed() + 1);
    }

    private void addCommunityCard(GameSession session, MajorArcanaCard card) {
        List<MinorArcanaCard> community = session.getCommunityCards();
        if (community.size() >= 7) {
            session.setLastEffectMessage(card.getName() + " — The table is full, fate cannot add more.");
            return;
        }
        MinorArcanaCard drawn = session.getDeck().drawMinor(1).get(0);
        community.add(drawn);
        session.setLastEffectMessage(card.getName() + " — An extra card rises: " + drawn.getName() + "!");
    }

    private void swapWeakestHole(GameSession session, Player player, MajorArcanaCard card) {
        List<MinorArcanaCard> hole = new ArrayList<>(player.getHoleCards());

        int weakestIdx = 0;
        for (int i = 1; i < hole.size(); i++) {
            if (hole.get(i).getPower() < hole.get(weakestIdx).getPower()) weakestIdx = i;
        }
        MinorArcanaCard removed = hole.remove(weakestIdx);
        MinorArcanaCard drawn = session.getDeck().drawMinor(1).get(0);
        hole.add(drawn);

        player.setHoleCards(hole);
        session.setLastEffectMessage(card.getName() + " — Your " + removed.getName()
                + " transforms into " + drawn.getName() + "!");
    }

    private void shuffleCommunity(GameSession session, MajorArcanaCard card) {
        int count = session.getCommunityCards().size();
        session.getDeck().refillMinor();
        session.setCommunityCards(new ArrayList<>(session.getDeck().drawMinor(count)));
        session.setLastEffectMessage(card.getName() + " — The Wheel turns! Community cards reshuffled.");
    }

    private void removeLowestCommunity(GameSession session, MajorArcanaCard card) {
        List<MinorArcanaCard> community = session.getCommunityCards();
        if (community.isEmpty()) {
            session.setLastEffectMessage(card.getName() + " — Nothing to reap.");
            return;
        }
        MinorArcanaCard lowest = community.stream()
                .min(Comparator.comparingInt(MinorArcanaCard::getPower)).orElseThrow();
        community.remove(lowest);
        session.setLastEffectMessage(card.getName() + " — Death claims " + lowest.getName() + " from the table!");
    }

    private void removeRandomCommunity(GameSession session, MajorArcanaCard card) {
        List<MinorArcanaCard> community = session.getCommunityCards();
        if (community.isEmpty()) {
            session.setLastEffectMessage(card.getName() + " — Nothing to destroy.");
            return;
        }
        MinorArcanaCard removed = community.remove(random.nextInt(community.size()));
        session.setLastEffectMessage(card.getName() + " — The Tower strikes! " + removed.getName() + " is removed!");
    }
}
