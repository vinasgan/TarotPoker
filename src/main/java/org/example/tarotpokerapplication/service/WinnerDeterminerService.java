package org.example.tarotpokerapplication.service;

import org.example.tarotpokerapplication.entity.MinorArcanaCard;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WinnerDeterminerService {

    public int determineWinner(List<MinorArcanaCard> p1, List<MinorArcanaCard> p2, List<MinorArcanaCard> table) {
        List<Integer> score1 = calculateBestHandScore(p1, table);
        List<Integer> score2 = calculateBestHandScore(p2, table);
        if (score1.isEmpty() || score2.isEmpty()) return -1;
        for (int i = 0; i < Math.min(score1.size(), score2.size()); i++) {
            if (score1.get(i) > score2.get(i)) return 1;
            if (score1.get(i) < score2.get(i)) return 2;
        }
        return 0;
    }

    public List<Integer> getBestHandScore(List<MinorArcanaCard> hole, List<MinorArcanaCard> table) {
        return calculateBestHandScore(hole, table);
    }

    private List<Integer> calculateBestHandScore(List<MinorArcanaCard> hole, List<MinorArcanaCard> table) {
        if (hole.size() < 2 || table.size() < 4 || table.size() > 6) return new ArrayList<>();

        int[] powerCount = new int[16];
        Map<String, List<Integer>> suitMap = new HashMap<>();

        List<MinorArcanaCard> all = new ArrayList<>(hole);
        all.addAll(table);
        for (MinorArcanaCard c : all) {
            powerCount[c.getPower()]++;
            suitMap.computeIfAbsent(c.getSuit(), _ -> new ArrayList<>()).add(c.getPower());
        }

        List<Integer> allPowers = new ArrayList<>();
        for (int p = 15; p >= 2; p--)
            for (int i = 0; i < powerCount[p]; i++) allPowers.add(p);

        List<Integer> flushSuit = null;
        for (List<Integer> powers : suitMap.values())
            if (powers.size() >= 5) {
                flushSuit = powers;
                break;
            }

        // Royal Flush (10) / Straight Flush (9)
        if (flushSuit != null) {
            List<Integer> fd = flushSuit.stream().distinct().sorted(Comparator.reverseOrder()).toList();
            int sfHigh = -1;
            for (int i = 0; i <= fd.size() - 5; i++)
                if (fd.get(i) - fd.get(i + 4) == 4) {
                    sfHigh = fd.get(i);
                    break;
                }
            if (sfHigh == 15) return List.of(10);
            if (sfHigh != -1) return List.of(9, sfHigh);
        }

        // Four of a Kind (8)
        for (int p = 15; p >= 2; p--) {
            if (powerCount[p] >= 4) {
                int quad = p;
                List<Integer> score = new ArrayList<>(List.of(8, quad));
                allPowers.stream().filter(x -> x != quad).limit(1).forEach(score::add);
                return score;
            }
        }

        // Full House (7)
        int triple = -1, pairFH = -1;
        for (int p = 15; p >= 2; p--) {
            if (powerCount[p] >= 3) {
                triple = p;
                break;
            }
        }
        for (int p = 15; p >= 2; p--) {
            if (powerCount[p] >= 2) {
                if (p != triple) {
                    pairFH = p;
                    break;
                }
            }
        }
        if (triple != -1 && pairFH != -1) return List.of(7, triple, pairFH);

        // Flush (6)
        if (flushSuit != null) {
            List<Integer> score = new ArrayList<>(List.of(6));
            flushSuit.stream().sorted(Comparator.reverseOrder()).limit(5).forEach(score::add);
            return score;
        }

        // Straight (5)
        for (int high = 15; high >= 6; high--) {
            boolean isStraight = true;
            for (int p = high; p > high - 5; p--)
                if (powerCount[p] == 0) {
                    isStraight = false;
                    break;
                }
            if (isStraight) return List.of(5, high);
        }

        // Three of a Kind (4)
        if (triple != -1) {
            int t = triple;
            List<Integer> score = new ArrayList<>(List.of(4, t));
            allPowers.stream().filter(x -> x != t).limit(2).forEach(score::add);
            return score;
        }

        // Two Pair (3) / Pair (2)
        int pair1 = -1, pair2 = -1;
        for (int p = 15; p >= 2; p--) {
            if (powerCount[p] >= 2) {
                pair1 = p;
                break;
            }
        }
        for (int p = 15; p >= 2; p--) {
            if (powerCount[p] >= 2) {
                if (p != pair1) {
                    pair2 = p;
                    break;
                }
            }
        }
        if (pair2 != -1) {
            int p1 = pair1, p2 = pair2;
            List<Integer> score = new ArrayList<>(List.of(3, p1, p2));
            allPowers.stream().filter(x -> x != p1 && x != p2).limit(1).forEach(score::add);
            return score;
        }
        if (pair1 != -1) {
            int p1 = pair1;
            List<Integer> score = new ArrayList<>(List.of(2, p1));
            allPowers.stream().filter(x -> x != p1).limit(3).forEach(score::add);
            return score;
        }

        // High Card (1)
        List<Integer> score = new ArrayList<>(List.of(1));
        score.addAll(allPowers.subList(0, Math.min(5, allPowers.size())));
        return score;
    }
}
