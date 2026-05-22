package org.example.tarotpokerapplication;

import org.example.tarotpokerapplication.entity.MinorArcanaCard;
import org.example.tarotpokerapplication.service.WinnerDeterminerService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BestHandScoreTest {

        private final WinnerDeterminerService determiner = new WinnerDeterminerService();

        private static MinorArcanaCard c(int power, String suit) {
                return new MinorArcanaCard(suit + "-" + power, suit, power);
        }

        @Nested
        class BestHandScoreTestFiveCards {

                static Stream<Arguments> testCases() {
                        return Stream.of(

                                // ════════════════════════════════════════════════════════════
                                // ROYAL FLUSH  → score = [10]                    (tests 01-07)
                                // ════════════════════════════════════════════════════════════

                                /* 01 */ Arguments.of(
                                        "Royal Flush in Swords (A-K-Q-J-Page)",
                                        List.of(c(15, "Swords"), c(14, "Swords")),
                                        List.of(c(13, "Swords"), c(12, "Swords"), c(11, "Swords"),
                                                c(2, "Cups"), c(3, "Wands")),
                                        List.of(10)
                                ),

                                /* 02 */ Arguments.of(
                                        "Royal Flush in Cups — hole provides two RF cards",
                                        List.of(c(15, "Cups"), c(11, "Cups")),
                                        List.of(c(14, "Cups"), c(13, "Cups"), c(12, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(10)
                                ),

                                /* 03 */ Arguments.of(
                                        "Royal Flush in Wands — full RF on board, hole irrelevant",
                                        List.of(c(2, "Swords"), c(3, "Pentacles")),
                                        List.of(c(15, "Wands"), c(14, "Wands"), c(13, "Wands"),
                                                c(12, "Wands"), c(11, "Wands")),
                                        List.of(10)
                                ),

                                /* 04 */ Arguments.of(
                                        "Royal Flush in Pentacles — one hole card contributes",
                                        List.of(c(15, "Pentacles"), c(2, "Cups")),
                                        List.of(c(14, "Pentacles"), c(13, "Pentacles"), c(12, "Pentacles"),
                                                c(11, "Pentacles"), c(3, "Swords")),
                                        List.of(10)
                                ),

                                /* 05 */ Arguments.of(
                                        "Royal Flush — 7 cards available, RF is best 5",
                                        List.of(c(15, "Swords"), c(14, "Swords")),
                                        List.of(c(13, "Swords"), c(12, "Swords"), c(11, "Swords"),
                                                c(15, "Cups"), c(14, "Cups")),
                                        List.of(10)
                                ),

                                /* 06 */ Arguments.of(
                                        "Royal Flush — hole provides J and A, board provides rest",
                                        List.of(c(11, "Cups"), c(15, "Cups")),
                                        List.of(c(12, "Cups"), c(13, "Cups"), c(14, "Cups"),
                                                c(4, "Swords"), c(5, "Wands")),
                                        List.of(10)
                                ),

                                /* 07 */ Arguments.of(
                                        "Royal Flush in Wands — hole provides K and Q",
                                        List.of(c(14, "Wands"), c(13, "Wands")),
                                        List.of(c(15, "Wands"), c(12, "Wands"), c(11, "Wands"),
                                                c(9, "Cups"), c(8, "Swords")),
                                        List.of(10)
                                ),

                                // ════════════════════════════════════════════════════════════
                                // STRAIGHT FLUSH  → score = [9, highCard]        (tests 08-14)
                                // ════════════════════════════════════════════════════════════

                                /* 08 */ Arguments.of(
                                        "Straight Flush K-high (9-10-J-Q-K) in Swords",
                                        List.of(c(9, "Swords"), c(10, "Swords")),
                                        List.of(c(11, "Swords"), c(12, "Swords"), c(13, "Swords"),
                                                c(2, "Cups"), c(3, "Wands")),
                                        List.of(9, 13)
                                ),

                                /* 09 */ Arguments.of(
                                        "Straight Flush Q-high (8-9-10-J-Q) in Cups",
                                        List.of(c(8, "Cups"), c(12, "Cups")),
                                        List.of(c(9, "Cups"), c(10, "Cups"), c(11, "Cups"),
                                                c(4, "Swords"), c(5, "Wands")),
                                        List.of(9, 12)
                                ),

                                /* 10 */ Arguments.of(
                                        "Straight Flush J-high (7-8-9-10-J) in Wands",
                                        List.of(c(7, "Wands"), c(11, "Wands")),
                                        List.of(c(8, "Wands"), c(9, "Wands"), c(10, "Wands"),
                                                c(2, "Cups"), c(3, "Swords")),
                                        List.of(9, 11)
                                ),

                                /* 11 */ Arguments.of(
                                        "Straight Flush 10-high (6-7-8-9-10) in Pentacles",
                                        List.of(c(6, "Pentacles"), c(10, "Pentacles")),
                                        List.of(c(7, "Pentacles"), c(8, "Pentacles"), c(9, "Pentacles"),
                                                c(3, "Cups"), c(4, "Swords")),
                                        List.of(9, 10)
                                ),

                                /* 12 */ Arguments.of(
                                        "Straight Flush 9-high (5-6-7-8-9) in Cups",
                                        List.of(c(5, "Cups"), c(9, "Cups")),
                                        List.of(c(6, "Cups"), c(7, "Cups"), c(8, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(9, 9)
                                ),

                                /* 13 */ Arguments.of(
                                        "Straight Flush 8-high (4-5-6-7-8) in Swords",
                                        List.of(c(4, "Swords"), c(8, "Swords")),
                                        List.of(c(5, "Swords"), c(6, "Swords"), c(7, "Swords"),
                                                c(2, "Cups"), c(3, "Wands")),
                                        List.of(9, 8)
                                ),

                                /* 14 */ Arguments.of(
                                        "Straight Flush 7-high (3-4-5-6-7) in Wands — beats flush from other cards",
                                        List.of(c(3, "Wands"), c(7, "Wands")),
                                        List.of(c(4, "Wands"), c(5, "Wands"), c(6, "Wands"),
                                                c(15, "Cups"), c(14, "Cups")),
                                        List.of(9, 7)
                                ),

                                // ════════════════════════════════════════════════════════════
                                // FOUR OF A KIND  → score = [8, quadPower, kicker]
                                //                                                (tests 15-21)
                                // ════════════════════════════════════════════════════════════

                                /* 15 */ Arguments.of(
                                        "Four of a Kind — Aces (15), kicker King (14)",
                                        List.of(c(15, "Cups"), c(15, "Swords")),
                                        List.of(c(15, "Wands"), c(15, "Pentacles"), c(14, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(8, 15, 14)
                                ),

                                /* 16 */ Arguments.of(
                                        "Four of a Kind — Kings (14), kicker Ace (15)",
                                        List.of(c(14, "Cups"), c(14, "Swords")),
                                        List.of(c(14, "Wands"), c(14, "Pentacles"), c(15, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(8, 14, 15)
                                ),

                                /* 17 */ Arguments.of(
                                        "Four of a Kind — Queens (13), kicker King (14)",
                                        List.of(c(13, "Cups"), c(14, "Swords")),
                                        List.of(c(13, "Swords"), c(13, "Wands"), c(13, "Pentacles"),
                                                c(2, "Cups"), c(3, "Wands")),
                                        List.of(8, 13, 14)
                                ),

                                /* 18 */ Arguments.of(
                                        "Four of a Kind — Jacks (11), kicker Ace (15)",
                                        List.of(c(11, "Cups"), c(15, "Swords")),
                                        List.of(c(11, "Swords"), c(11, "Wands"), c(11, "Pentacles"),
                                                c(2, "Cups"), c(3, "Wands")),
                                        List.of(8, 11, 15)
                                ),

                                /* 19 */ Arguments.of(
                                        "Four of a Kind — 9s, kicker Queen (13)",
                                        List.of(c(9, "Cups"), c(9, "Swords")),
                                        List.of(c(9, "Wands"), c(9, "Pentacles"), c(13, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(8, 9, 13)
                                ),

                                /* 20 */ Arguments.of(
                                        "Four of a Kind — 7s, kicker is best remaining (Ace from board)",
                                        List.of(c(7, "Cups"), c(7, "Swords")),
                                        List.of(c(7, "Wands"), c(7, "Pentacles"), c(15, "Cups"),
                                                c(14, "Swords"), c(13, "Wands")),
                                        List.of(8, 7, 15)
                                ),

                                /* 21 */ Arguments.of(
                                        "Four of a Kind — 5s on board, hole provides kicker K (14)",
                                        List.of(c(14, "Cups"), c(2, "Swords")),
                                        List.of(c(5, "Cups"), c(5, "Swords"), c(5, "Wands"),
                                                c(5, "Pentacles"), c(3, "Wands")),
                                        List.of(8, 5, 14)
                                ),

                                // ════════════════════════════════════════════════════════════
                                // FULL HOUSE  → score = [7, tripsPower, pairPower]
                                //                                                (tests 22-28)
                                // ════════════════════════════════════════════════════════════

                                /* 22 */ Arguments.of(
                                        "Full House — Aces full of Kings (A-A-A + K-K)",
                                        List.of(c(15, "Cups"), c(15, "Swords")),
                                        List.of(c(15, "Wands"), c(14, "Cups"), c(14, "Swords"),
                                                c(2, "Pentacles"), c(3, "Wands")),
                                        List.of(7, 15, 14)
                                ),

                                /* 23 */ Arguments.of(
                                        "Full House — Kings full of Aces (K-K-K + A-A)",
                                        List.of(c(14, "Cups"), c(14, "Swords")),
                                        List.of(c(14, "Wands"), c(15, "Cups"), c(15, "Swords"),
                                                c(2, "Pentacles"), c(3, "Wands")),
                                        List.of(7, 14, 15)
                                ),

                                /* 24 */ Arguments.of(
                                        "Full House — Queens full of Jacks (Q-Q-Q + J-J)",
                                        List.of(c(13, "Cups"), c(13, "Swords")),
                                        List.of(c(13, "Wands"), c(11, "Cups"), c(11, "Swords"),
                                                c(2, "Pentacles"), c(3, "Wands")),
                                        List.of(7, 13, 11)
                                ),

                                /* 25 */ Arguments.of(
                                        "Full House — 10s full of 9s (10-10-10 + 9-9)",
                                        List.of(c(10, "Cups"), c(10, "Swords")),
                                        List.of(c(10, "Wands"), c(9, "Cups"), c(9, "Swords"),
                                                c(2, "Pentacles"), c(3, "Wands")),
                                        List.of(7, 10, 9)
                                ),

                                /* 26 */ Arguments.of(
                                        "Full House — trips from board, pair from hole",
                                        List.of(c(13, "Cups"), c(13, "Swords")),
                                        List.of(c(15, "Cups"), c(15, "Swords"), c(15, "Wands"),
                                                c(2, "Pentacles"), c(3, "Cups")),
                                        List.of(7, 15, 13)
                                ),

                                /* 27 */ Arguments.of(
                                        "Full House — two trips on board: best trips chosen (higher wins)",
                                        // board: A-A-A + K-K-K → FH = [7, 15, 14]
                                        List.of(c(2, "Cups"), c(3, "Swords")),
                                        List.of(c(15, "Cups"), c(15, "Swords"), c(15, "Wands"),
                                                c(14, "Cups"), c(14, "Swords")),
                                        List.of(7, 15, 14)
                                ),

                                /* 28 */ Arguments.of(
                                        "Full House — 8s full of 7s (8-8-8 + 7-7)",
                                        List.of(c(8, "Cups"), c(8, "Swords")),
                                        List.of(c(8, "Wands"), c(7, "Cups"), c(7, "Swords"),
                                                c(2, "Pentacles"), c(3, "Wands")),
                                        List.of(7, 8, 7)
                                ),

                                // ════════════════════════════════════════════════════════════
                                // FLUSH  → score = [6, c1, c2, c3, c4, c5] desc (tests 29-35)
                                // ════════════════════════════════════════════════════════════

                                /* 29 */ Arguments.of(
                                        "Flush in Cups — A-K-Q-J-9",
                                        List.of(c(15, "Cups"), c(14, "Cups")),
                                        List.of(c(13, "Cups"), c(11, "Cups"), c(9, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(6, 15, 14, 13, 11, 9)
                                ),

                                /* 30 */ Arguments.of(
                                        "Flush in Swords — K-J-9-7-5",
                                        List.of(c(14, "Swords"), c(11, "Swords")),
                                        List.of(c(9, "Swords"), c(7, "Swords"), c(5, "Swords"),
                                                c(2, "Cups"), c(3, "Wands")),
                                        List.of(6, 14, 11, 9, 7, 5)
                                ),

                                /* 31 */ Arguments.of(
                                        "Flush in Wands — Q-10-8-6-4",
                                        List.of(c(13, "Wands"), c(10, "Wands")),
                                        List.of(c(8, "Wands"), c(6, "Wands"), c(4, "Wands"),
                                                c(2, "Cups"), c(3, "Swords")),
                                        List.of(6, 13, 10, 8, 6, 4)
                                ),

                                /* 32 */ Arguments.of(
                                        "Flush in Pentacles — A-10-8-6-4",
                                        List.of(c(15, "Pentacles"), c(10, "Pentacles")),
                                        List.of(c(8, "Pentacles"), c(6, "Pentacles"), c(4, "Pentacles"),
                                                c(2, "Cups"), c(3, "Swords")),
                                        List.of(6, 15, 10, 8, 6, 4)
                                ),

                                /* 33 */ Arguments.of(
                                        "Flush — 6 suited cards available, picks best 5",
                                        // suited: A K Q J 10 9 → best flush A-K-Q-J-10
                                        List.of(c(7, "Cups"), c(14, "Cups")),
                                        List.of(c(13, "Cups"), c(5, "Cups"), c(11, "Cups"),
                                                c(5, "Cups"), c(2, "Swords")),
                                        List.of(6, 14, 13, 11, 7, 5)
                                ),

                                /* 34 */ Arguments.of(
                                        "Flush in Cups — J-9-7-5-3",
                                        List.of(c(11, "Cups"), c(9, "Cups")),
                                        List.of(c(7, "Cups"), c(5, "Cups"), c(3, "Cups"),
                                                c(2, "Swords"), c(4, "Wands")),
                                        List.of(6, 11, 9, 7, 5, 3)
                                ),

                                /* 35 */ Arguments.of(
                                        "Flush in Swords — A-Q-9-7-4",
                                        List.of(c(15, "Swords"), c(13, "Swords")),
                                        List.of(c(9, "Swords"), c(7, "Swords"), c(4, "Swords"),
                                                c(2, "Cups"), c(3, "Wands")),
                                        List.of(6, 15, 13, 9, 7, 4)
                                ),

                                // ════════════════════════════════════════════════════════════
                                // STRAIGHT  → score = [5, highCard]              (tests 36-42)
                                // ════════════════════════════════════════════════════════════

                                /* 36 */ Arguments.of(
                                        "Straight A-high (10-J-Q-K-A)",
                                        List.of(c(15, "Cups"), c(14, "Swords")),
                                        List.of(c(13, "Wands"), c(12, "Pentacles"), c(11, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(5, 15)
                                ),

                                /* 37 */ Arguments.of(
                                        "Straight K-high (9-10-J-Q-K)",
                                        List.of(c(14, "Cups"), c(13, "Swords")),
                                        List.of(c(12, "Wands"), c(11, "Pentacles"), c(10, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(5, 14)
                                ),

                                /* 38 */ Arguments.of(
                                        "Straight Q-high (8-9-10-J-Q)",
                                        List.of(c(13, "Cups"), c(12, "Swords")),
                                        List.of(c(11, "Wands"), c(10, "Pentacles"), c(9, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(5, 13)
                                ),

                                /* 39 */ Arguments.of(
                                        "Straight J-high (7-8-9-10-J)",
                                        List.of(c(11, "Cups"), c(10, "Swords")),
                                        List.of(c(9, "Wands"), c(8, "Pentacles"), c(7, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(5, 11)
                                ),

                                /* 40 */ Arguments.of(
                                        "Straight 10-high (6-7-8-9-10)",
                                        List.of(c(10, "Cups"), c(9, "Swords")),
                                        List.of(c(8, "Wands"), c(7, "Pentacles"), c(6, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(5, 10)
                                ),

                                /* 41 */ Arguments.of(
                                        "Straight 9-high (5-6-7-8-9)",
                                        List.of(c(9, "Cups"), c(8, "Swords")),
                                        List.of(c(7, "Wands"), c(6, "Pentacles"), c(5, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(5, 9)
                                ),

                                /* 42 */ Arguments.of(
                                        "Straight 8-high (4-5-6-7-8) — board has extra cards, still picks best",
                                        List.of(c(8, "Cups"), c(7, "Swords")),
                                        List.of(c(6, "Wands"), c(5, "Pentacles"), c(4, "Cups"),
                                                c(3, "Swords"), c(2, "Wands")),
                                        List.of(5, 8)
                                ),

                                // ════════════════════════════════════════════════════════════
                                // THREE OF A KIND  → score = [4, trips, k1, k2] (tests 43-49)
                                // ════════════════════════════════════════════════════════════

                                /* 43 */ Arguments.of(
                                        "Three of a Kind — Aces (15), kickers K(14) Q(13)",
                                        List.of(c(15, "Cups"), c(15, "Swords")),
                                        List.of(c(15, "Wands"), c(14, "Cups"), c(13, "Swords"),
                                                c(2, "Pentacles"), c(3, "Wands")),
                                        List.of(4, 15, 14, 13)
                                ),

                                /* 44 */ Arguments.of(
                                        "Three of a Kind — Kings (14), kickers A(15) Q(13)",
                                        List.of(c(14, "Cups"), c(14, "Swords")),
                                        List.of(c(14, "Wands"), c(15, "Cups"), c(13, "Swords"),
                                                c(2, "Pentacles"), c(3, "Wands")),
                                        List.of(4, 14, 15, 13)
                                ),

                                /* 45 */ Arguments.of(
                                        "Three of a Kind — Queens (13), kickers A(15) K(14)",
                                        List.of(c(13, "Cups"), c(15, "Swords")),
                                        List.of(c(13, "Swords"), c(13, "Wands"), c(14, "Pentacles"),
                                                c(2, "Cups"), c(3, "Wands")),
                                        List.of(4, 13, 15, 14)
                                ),

                                /* 46 */ Arguments.of(
                                        "Three of a Kind — 10s, kickers K(14) J(11)",
                                        List.of(c(10, "Cups"), c(10, "Swords")),
                                        List.of(c(10, "Wands"), c(14, "Cups"), c(11, "Swords"),
                                                c(2, "Pentacles"), c(3, "Wands")),
                                        List.of(4, 10, 14, 11)
                                ),

                                /* 47 */ Arguments.of(
                                        "Three of a Kind — 8s, kickers A(15) K(14)",
                                        List.of(c(8, "Cups"), c(8, "Swords")),
                                        List.of(c(8, "Wands"), c(15, "Cups"), c(14, "Swords"),
                                                c(2, "Pentacles"), c(3, "Wands")),
                                        List.of(4, 8, 15, 14)
                                ),

                                /* 48 */ Arguments.of(
                                        "Three of a Kind — 6s on board, hole provides kickers A(15) K(14)",
                                        List.of(c(15, "Cups"), c(14, "Swords")),
                                        List.of(c(6, "Cups"), c(6, "Swords"), c(6, "Wands"),
                                                c(2, "Pentacles"), c(3, "Wands")),
                                        List.of(4, 6, 15, 14)
                                ),

                                /* 49 */ Arguments.of(
                                        "Three of a Kind — 4s, picks best 2 kickers from 4 available",
                                        // available kickers: A(15) K(14) Q(13) J(11) → picks A, K
                                        List.of(c(4, "Cups"), c(4, "Swords")),
                                        List.of(c(4, "Wands"), c(15, "Cups"), c(14, "Swords"),
                                                c(13, "Pentacles"), c(11, "Wands")),
                                        List.of(4, 4, 15, 14)
                                ),

                                // ════════════════════════════════════════════════════════════
                                // TWO PAIR  → score = [3, highPair, lowPair, kicker]
                                //                                                (tests 50-56)
                                // ════════════════════════════════════════════════════════════

                                /* 50 */ Arguments.of(
                                        "Two Pair — A-A + K-K, kicker Q(13)",
                                        List.of(c(15, "Cups"), c(15, "Swords")),
                                        List.of(c(14, "Cups"), c(14, "Swords"), c(13, "Wands"),
                                                c(2, "Pentacles"), c(3, "Cups")),
                                        List.of(3, 15, 14, 13)
                                ),

                                /* 51 */ Arguments.of(
                                        "Two Pair — K-K + Q-Q, kicker A(15)",
                                        List.of(c(14, "Cups"), c(14, "Swords")),
                                        List.of(c(13, "Cups"), c(13, "Swords"), c(15, "Wands"),
                                                c(2, "Pentacles"), c(3, "Cups")),
                                        List.of(3, 14, 13, 15)
                                ),

                                /* 52 */ Arguments.of(
                                        "Two Pair — Q-Q + J-J, kicker K(14)",
                                        List.of(c(13, "Cups"), c(13, "Swords")),
                                        List.of(c(11, "Cups"), c(11, "Swords"), c(14, "Wands"),
                                                c(2, "Pentacles"), c(3, "Cups")),
                                        List.of(3, 13, 11, 14)
                                ),

                                /* 53 */ Arguments.of(
                                        "Two Pair — J-J + 10-10, kicker A(15)",
                                        List.of(c(11, "Cups"), c(11, "Swords")),
                                        List.of(c(10, "Cups"), c(10, "Swords"), c(15, "Wands"),
                                                c(2, "Pentacles"), c(3, "Cups")),
                                        List.of(3, 11, 10, 15)
                                ),

                                /* 54 */ Arguments.of(
                                        "Two Pair — 9-9 + 8-8, kicker A(15)",
                                        List.of(c(9, "Cups"), c(9, "Swords")),
                                        List.of(c(8, "Cups"), c(8, "Swords"), c(15, "Wands"),
                                                c(2, "Pentacles"), c(3, "Cups")),
                                        List.of(3, 9, 8, 15)
                                ),

                                /* 55 */ Arguments.of(
                                        "Two Pair — 3 pairs available (A-A, K-K, Q-Q): picks top 2 + best kicker",
                                        // pairs: A(15) K(14) Q(13); best two pair = A-A + K-K kicker Q(13)
                                        List.of(c(15, "Cups"), c(15, "Swords")),
                                        List.of(c(14, "Cups"), c(14, "Swords"), c(13, "Cups"),
                                                c(13, "Swords"), c(2, "Pentacles")),
                                        List.of(3, 15, 14, 13)
                                ),

                                /* 56 */ Arguments.of(
                                        "Two Pair — 7-7 + 6-6, kicker K(14)",
                                        List.of(c(7, "Cups"), c(7, "Swords")),
                                        List.of(c(6, "Cups"), c(6, "Swords"), c(14, "Wands"),
                                                c(2, "Pentacles"), c(3, "Cups")),
                                        List.of(3, 7, 6, 14)
                                ),

                                // ════════════════════════════════════════════════════════════
                                // ONE PAIR  → score = [2, pairPower, k1, k2, k3](tests 57-63)
                                // ════════════════════════════════════════════════════════════

                                /* 57 */ Arguments.of(
                                        "One Pair — Aces (15), kickers K(14) Q(13) J(11)",
                                        List.of(c(15, "Cups"), c(15, "Swords")),
                                        List.of(c(14, "Wands"), c(13, "Pentacles"), c(11, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(2, 15, 14, 13, 11)
                                ),

                                /* 58 */ Arguments.of(
                                        "One Pair — Kings (14), kickers A(15) Q(13) J(11)",
                                        List.of(c(14, "Cups"), c(14, "Swords")),
                                        List.of(c(15, "Wands"), c(13, "Pentacles"), c(11, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(2, 14, 15, 13, 11)
                                ),

                                /* 59 */ Arguments.of(
                                        "One Pair — Queens (13), kickers A(15) K(14) 10",
                                        List.of(c(13, "Cups"), c(13, "Swords")),
                                        List.of(c(15, "Wands"), c(14, "Pentacles"), c(10, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(2, 13, 15, 14, 10)
                                ),

                                /* 60 */ Arguments.of(
                                        "One Pair — 9s, kickers A(15) K(14) Q(13), ignores lower cards",
                                        List.of(c(9, "Cups"), c(9, "Swords")),
                                        List.of(c(15, "Wands"), c(14, "Pentacles"), c(13, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(2, 9, 15, 14, 13)
                                ),

                                /* 61 */ Arguments.of(
                                        "One Pair — 7s, kickers K(14) J(11) 10",
                                        List.of(c(7, "Cups"), c(7, "Swords")),
                                        List.of(c(14, "Wands"), c(11, "Pentacles"), c(10, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(2, 7, 14, 11, 10)
                                ),

                                /* 62 */ Arguments.of(
                                        "One Pair — 5s, 5 kicker candidates, picks best 3",
                                        // kickers available: A(15) K(14) Q(13) J(11) → picks 15 14 13
                                        List.of(c(5, "Cups"), c(5, "Swords")),
                                        List.of(c(15, "Wands"), c(14, "Pentacles"), c(13, "Cups"),
                                                c(11, "Swords"), c(3, "Wands")),
                                        List.of(2, 5, 15, 14, 13)
                                ),

                                /* 63 */ Arguments.of(
                                        "One Pair — 3s, kickers A(15) K(14) Q(13)",
                                        List.of(c(3, "Cups"), c(3, "Swords")),
                                        List.of(c(15, "Wands"), c(14, "Pentacles"), c(13, "Cups"),
                                                c(2, "Swords"), c(4, "Wands")),
                                        List.of(2, 3, 15, 14, 13)
                                ),

                                // ════════════════════════════════════════════════════════════
                                // HIGH CARD  → score = [1, c1, c2, c3, c4, c5] (tests 64-70)
                                // ════════════════════════════════════════════════════════════

                                /* 64 */ Arguments.of(
                                        "High Card (best possible high card)",
                                        List.of(c(15, "Cups"), c(8, "Swords")),
                                        List.of(c(13, "Wands"), c(12, "Pentacles"), c(11, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(1, 15, 13, 12, 11, 8)
                                ),

                                /* 65 */ Arguments.of(
                                        "High Card — A-K-Q-J-10",
                                        List.of(c(15, "Cups"), c(10, "Swords")),
                                        List.of(c(14, "Wands"), c(13, "Pentacles"), c(11, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(1, 15, 14, 13, 11, 10)
                                ),

                                /* 66 */ Arguments.of(
                                        "High Card — A-K-Q-10-9",
                                        List.of(c(15, "Cups"), c(9, "Swords")),
                                        List.of(c(14, "Wands"), c(13, "Pentacles"), c(10, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(1, 15, 14, 13, 10, 9)
                                ),

                                /* 67 */ Arguments.of(
                                        "High Card — A-K-J-9-7",
                                        List.of(c(15, "Cups"), c(7, "Swords")),
                                        List.of(c(14, "Wands"), c(11, "Pentacles"), c(9, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(1, 15, 14, 11, 9, 7)
                                ),

                                /* 68 */ Arguments.of(
                                        "High Card — 7 cards available, picks best 5",
                                        List.of(c(15, "Cups"), c(3, "Swords")),
                                        List.of(c(2, "Wands"), c(12, "Pentacles"), c(11, "Cups"),
                                                c(8, "Swords"), c(9, "Wands")),
                                        List.of(1, 15, 12, 11, 9, 8)
                                ),

                                /* 69 */ Arguments.of(
                                        "High Card",
                                        List.of(c(14, "Cups"), c(10, "Swords")),
                                        List.of(c(5, "Wands"), c(12, "Pentacles"), c(4, "Cups"),
                                                c(8, "Swords"), c(2, "Wands")),
                                        List.of(1, 14, 12, 10, 8, 5)
                                ),

                                /* 70 */ Arguments.of(
                                        "High Card — A-Q-J-9-8 (mixed gap hand)",
                                        List.of(c(15, "Cups"), c(8, "Swords")),
                                        List.of(c(13, "Wands"), c(11, "Pentacles"), c(9, "Cups"),
                                                c(2, "Swords"), c(3, "Wands")),
                                        List.of(1, 15, 13, 11, 9, 8)
                                )
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("testCases")
                void bestHandScore(
                        String description,
                        List<MinorArcanaCard> hole,
                        List<MinorArcanaCard> table,
                        List<Integer> expectedScore) {

                        assertEquals(
                                expectedScore,
                                determiner.getBestHandScore(hole, table),
                                description
                        );
                }
        }

        @Nested
        class BestHandScoreTestFourCards {

                // ── Helpers ──────────────────────────────────────────────────────────
                // (delegates to outer-class helpers; duplicate only if running standalone)

                private List<Integer> score(List<MinorArcanaCard> hole, List<MinorArcanaCard> table) {
                        return determiner.getBestHandScore(hole, table);
                }

                // ════════════════════════════════════════════════════════════════════
                // ROYAL FLUSH → [10]                                       tests 01-07
                //
                // With 4 table cards there are only two valid RF distributions:
                //   • 4 RF cards on table + 1 RF card in hole + 1 junk card in hole
                //   • 3 RF cards on table + 2 RF cards in hole + 1 junk card on table
                // Tests cover all four suits and both distribution patterns.
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> royalFlushCases() {
                        return Stream.of(
                                /* 01 — 4 table RF + 1 hole RF + junk (Swords) */
                                Arguments.of("RF: 4 table RF cards, A in hole (Swords)",
                                        List.of(c(15,"Swords"),  c(2,"Cups")),
                                        List.of(c(14,"Swords"),  c(13,"Swords"),  c(12,"Swords"),  c(11,"Swords")),
                                        List.of(10)),

                                /* 02 — 3 table RF + 2 hole RF + junk (Cups) */
                                Arguments.of("RF: 3 table RF cards, A+K in hole (Cups)",
                                        List.of(c(15,"Cups"),    c(14,"Cups")),
                                        List.of(c(13,"Cups"),    c(12,"Cups"),    c(11,"Cups"),    c(2,"Swords")),
                                        List.of(10)),

                                /* 03 — 4 table RF + 1 hole RF + junk (Cups) */
                                Arguments.of("RF: 4 table RF cards, Page in hole (Cups)",
                                        List.of(c(11,"Cups"),    c(2,"Swords")),
                                        List.of(c(15,"Cups"),    c(14,"Cups"),    c(13,"Cups"),    c(12,"Cups")),
                                        List.of(10)),

                                /* 04 — 3 table RF + 2 hole RF + junk (Swords) */
                                Arguments.of("RF: 3 table RF cards, K+Q in hole (Swords)",
                                        List.of(c(14,"Swords"),  c(13,"Swords")),
                                        List.of(c(15,"Swords"),  c(12,"Swords"),  c(11,"Swords"),  c(2,"Cups")),
                                        List.of(10)),

                                /* 05 — 4 table RF + 1 hole RF + junk (Wands) */
                                Arguments.of("RF: 4 table RF cards, Knight in hole (Wands)",
                                        List.of(c(12,"Wands"),   c(2,"Cups")),
                                        List.of(c(15,"Wands"),   c(14,"Wands"),   c(13,"Wands"),   c(11,"Wands")),
                                        List.of(10)),

                                /* 06 — 3 table RF + 2 hole RF + junk (Wands) */
                                Arguments.of("RF: 3 table RF cards, A+Page in hole (Wands)",
                                        List.of(c(15,"Wands"),   c(11,"Wands")),
                                        List.of(c(14,"Wands"),   c(13,"Wands"),   c(12,"Wands"),   c(2,"Cups")),
                                        List.of(10)),

                                /* 07 — 6 consecutive same-suit cards; best 5 = A-high = RF */
                                Arguments.of("RF: 6 consecutive Cups {10-15}, picks A-high SF = Royal Flush",
                                        List.of(c(15,"Cups"),    c(10,"Cups")),
                                        List.of(c(14,"Cups"),    c(13,"Cups"),    c(12,"Cups"),    c(11,"Cups")),
                                        // All 6 Cups {10,11,12,13,14,15}; best 5 = {11-15} = RF [10]
                                        List.of(10))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("royalFlushCases")
                void royalFlush(String desc, List<MinorArcanaCard> hole,
                                List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // STRAIGHT FLUSH → [9, highCard]                           tests 08-14
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> straightFlushCases() {
                        return Stream.of(
                                /* 08 — K-high (10-J-Q-K in Swords on table, hole K♠ + junk) */
                                Arguments.of("SF: K-high (10-J-Q-Kn-K) in Swords",
                                        List.of(c(14,"Swords"),  c(2,"Cups")),
                                        List.of(c(10,"Swords"),  c(11,"Swords"),  c(12,"Swords"),  c(13,"Swords")),
                                        List.of(9, 14)),

                                /* 09 — Q-high (9-10-J-Q-Kn) in Cups, junk hole card */
                                Arguments.of("SF: Q-high (9-10-11-12-13) in Cups",
                                        List.of(c(13,"Cups"),    c(2,"Swords")),
                                        List.of(c(9,"Cups"),     c(10,"Cups"),    c(11,"Cups"),    c(12,"Cups")),
                                        List.of(9, 13)),

                                /* 10 — 6 consecutive Cups {9-14}, picks K-high (10-14) */
                                Arguments.of("SF: 6 consecutive Cups {9-14}, evaluator picks K-high (10-14)",
                                        List.of(c(14,"Cups"),    c(9,"Cups")),
                                        List.of(c(10,"Cups"),    c(11,"Cups"),    c(12,"Cups"),    c(13,"Cups")),
                                        // {9,10,11,12,13,14} all Cups: best SF = 10-11-12-13-14 = K-high [9,14]
                                        List.of(9, 14)),

                                /* 11 — Kn-high (8-9-10-11-12) in Wands */
                                Arguments.of("SF: Knight-high (8-9-10-11-12) in Wands",
                                        List.of(c(8,"Wands"),    c(2,"Cups")),
                                        List.of(c(9,"Wands"),    c(10,"Wands"),   c(11,"Wands"),   c(12,"Wands")),
                                        List.of(9, 12)),

                                /* 12 — Page-high (7-8-9-10-11) in Swords */
                                Arguments.of("SF: Page-high (7-8-9-10-11) in Swords",
                                        List.of(c(11,"Swords"),  c(2,"Cups")),
                                        List.of(c(7,"Swords"),   c(8,"Swords"),   c(9,"Swords"),   c(10,"Swords")),
                                        List.of(9, 11)),

                                /* 13 — 10-high (6-7-8-9-10) in Wands */
                                Arguments.of("SF: 10-high (6-7-8-9-10) in Wands",
                                        List.of(c(10,"Wands"),   c(2,"Cups")),
                                        List.of(c(6,"Wands"),    c(7,"Wands"),    c(8,"Wands"),    c(9,"Wands")),
                                        List.of(9, 10)),

                                /* 14 — 8-high (4-5-6-7-8) in Cups */
                                Arguments.of("SF: 8-high (4-5-6-7-8) in Cups",
                                        List.of(c(8,"Cups"),     c(2,"Swords")),
                                        List.of(c(4,"Cups"),     c(5,"Cups"),     c(6,"Cups"),     c(7,"Cups")),
                                        List.of(9, 8))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("straightFlushCases")
                void straightFlush(String desc, List<MinorArcanaCard> hole,
                                   List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // FOUR OF A KIND → [8, quadPower, kickerPower]             tests 15-21
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> fourOfAKindCases() {
                        return Stream.of(
                                /* 15 — quad Aces, kicker K from table */
                                Arguments.of("Quads Aces: kicker K(14) from table",
                                        List.of(c(15,"Cups"),    c(15,"Swords")),
                                        List.of(c(15,"Wands"),   c(15,"Pentacles"), c(14,"Cups"),  c(2,"Swords")),
                                        List.of(8, 15, 14)),

                                /* 16 — quad Kings, kicker Ace from table */
                                Arguments.of("Quads Kings: kicker A(15) from table",
                                        List.of(c(14,"Cups"),    c(14,"Swords")),
                                        List.of(c(14,"Wands"),   c(14,"Pentacles"), c(15,"Cups"),  c(2,"Swords")),
                                        List.of(8, 14, 15)),

                                /* 17 — quad Queens, kicker K from hole beats Q from table */
                                Arguments.of("Quads Queens: kicker K(14) from hole beats 2 from table",
                                        List.of(c(13,"Cups"),    c(14,"Swords")),   // K in hole = kicker
                                        List.of(c(13,"Swords"),  c(13,"Wands"),     c(13,"Pentacles"), c(2,"Cups")),
                                        List.of(8, 13, 14)),

                                /* 18 — quad Jacks(11), kicker A from hole */
                                Arguments.of("Quads Jacks(11): kicker A(15) from hole",
                                        List.of(c(11,"Cups"),    c(15,"Swords")),
                                        List.of(c(11,"Swords"),  c(11,"Wands"),     c(11,"Pentacles"), c(3,"Cups")),
                                        List.of(8, 11, 15)),

                                /* 19 — quad 9s, K from hole beats Q from table as kicker */
                                Arguments.of("Quads 9s: K(14) from hole beats Q(13) from table",
                                        List.of(c(9,"Cups"),     c(14,"Swords")),   // K = best kicker
                                        List.of(c(9,"Swords"),   c(9,"Wands"),      c(9,"Pentacles"),  c(13,"Cups")),
                                        List.of(8, 9, 14)),

                                /* 20 — quad 7s, kicker A from table (both hole cards are the quads) */
                                Arguments.of("Quads 7s: kicker A(15) from table",
                                        List.of(c(7,"Cups"),     c(7,"Swords")),
                                        List.of(c(7,"Wands"),    c(7,"Pentacles"),  c(15,"Cups"),  c(2,"Swords")),
                                        List.of(8, 7, 15)),

                                /* 21 — quad 5s, picks best kicker from two remaining */
                                Arguments.of("Quads 5s: kicker 9 beats 6 from remaining cards",
                                        List.of(c(5,"Cups"),     c(5,"Swords")),
                                        List.of(c(5,"Wands"),    c(5,"Pentacles"),  c(9,"Cups"),   c(6,"Swords")),
                                        List.of(8, 5, 9))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("fourOfAKindCases")
                void fourOfAKind(String desc, List<MinorArcanaCard> hole,
                                 List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // FULL HOUSE → [7, tripsPower, pairPower]                  tests 22-28
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> fullHouseCases() {
                        return Stream.of(
                                /* 22 — Aces full of Kings */
                                Arguments.of("FH: Aces full of Kings",
                                        List.of(c(15,"Cups"),    c(15,"Swords")),
                                        List.of(c(15,"Wands"),   c(14,"Cups"),    c(14,"Swords"),  c(2,"Pentacles")),
                                        List.of(7, 15, 14)),

                                /* 23 — Kings full of Aces */
                                Arguments.of("FH: Kings full of Aces",
                                        List.of(c(14,"Cups"),    c(14,"Swords")),
                                        List.of(c(14,"Wands"),   c(15,"Cups"),    c(15,"Swords"),  c(2,"Pentacles")),
                                        List.of(7, 14, 15)),

                                /* 24 — Queens full of Jacks(11) */
                                Arguments.of("FH: Queens(13) full of Jacks(11)",
                                        List.of(c(13,"Cups"),    c(13,"Swords")),
                                        List.of(c(13,"Wands"),   c(11,"Cups"),    c(11,"Swords"),  c(2,"Pentacles")),
                                        List.of(7, 13, 11)),

                                /* 25 — 6 cards = two complete trips; picks better trips for FH */
                                Arguments.of("FH: two triples in 6 cards — Aces full of Kings [7,15,14]",
                                        List.of(c(15,"Cups"),    c(15,"Swords")),
                                        // board has one extra K to form 3 Kings, but 3 Aces rank higher as trips
                                        List.of(c(15,"Wands"),   c(14,"Cups"),    c(14,"Swords"),  c(14,"Wands")),
                                        List.of(7, 15, 14)),

                                /* 26 — 10s full of 9s */
                                Arguments.of("FH: 10s full of 9s",
                                        List.of(c(10,"Cups"),    c(10,"Swords")),
                                        List.of(c(10,"Wands"),   c(9,"Cups"),     c(9,"Swords"),   c(2,"Pentacles")),
                                        List.of(7, 10, 9)),

                                /* 27 — 8s full of 7s */
                                Arguments.of("FH: 8s full of 7s",
                                        List.of(c(8,"Cups"),     c(8,"Swords")),
                                        List.of(c(8,"Wands"),    c(7,"Cups"),     c(7,"Swords"),   c(2,"Pentacles")),
                                        List.of(7, 8, 7)),

                                /* 28 — trips from table, pair from hole */
                                Arguments.of("FH: Queens(13) full of Pages(11) — trips from table, pair from hole",
                                        List.of(c(11,"Cups"),    c(11,"Swords")),
                                        List.of(c(13,"Cups"),    c(13,"Swords"),  c(13,"Wands"),   c(2,"Pentacles")),
                                        List.of(7, 13, 11))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("fullHouseCases")
                void fullHouse(String desc, List<MinorArcanaCard> hole,
                               List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // FLUSH → [6, c1, c2, c3, c4, c5] desc                    tests 29-35
                //
                // Both hole cards or a mix with table cards reach 5 same-suit cards.
                // Table flush cards are always non-consecutive (gaps prevent SF).
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> flushCases() {
                        return Stream.of(
                                /* 29 — 4 Cups on table + 1 Cups in hole = 5 Cups flush */
                                Arguments.of("Flush: 4 table Cups + A in hole → A-Q-J(11)-9-7",
                                        List.of(c(15,"Cups"),    c(2,"Swords")),
                                        List.of(c(13,"Cups"),    c(11,"Cups"),    c(9,"Cups"),     c(7,"Cups")),
                                        // Cups: {7,9,11,13,15} — gaps at 8,10,12,14 prevent SF ✓
                                        List.of(6, 15, 13, 11, 9, 7)),

                                /* 30 — 3 Swords on table + 2 Swords in hole = 5 Swords flush */
                                Arguments.of("Flush: 3 table Swords + A+K in hole → A-K-Q(12)-10-8",
                                        List.of(c(15,"Swords"),  c(14,"Swords")),
                                        List.of(c(12,"Swords"),  c(10,"Swords"),  c(8,"Swords"),   c(2,"Cups")),
                                        // Swords: {8,10,12,14,15} — gaps prevent SF ✓
                                        List.of(6, 15, 14, 12, 10, 8)),

                                /* 31 — 6 Swords available, picks best 5 (drops lowest) */
                                Arguments.of("Flush: 6 Swords {7,9,11,13,14,15}, picks top 5 dropping 7",
                                        List.of(c(15,"Swords"),  c(9,"Swords")),
                                        List.of(c(14,"Swords"),  c(13,"Swords"),  c(11,"Swords"),  c(7,"Swords")),
                                        // No SF: {7,9,11,13,14,15} — not 5 consecutive (gaps at 8,10,12) ✓
                                        // Top 5: {9,11,13,14,15} → [6,15,14,13,11,9]
                                        List.of(6, 15, 14, 13, 11, 9)),

                                /* 32 — 4 Wands on table + 1 Wands in hole */
                                Arguments.of("Flush: 4 table Wands + K in hole → A-K-Q(12)-10-8",
                                        List.of(c(14,"Wands"),   c(3,"Cups")),
                                        List.of(c(15,"Wands"),   c(12,"Wands"),   c(10,"Wands"),   c(8,"Wands")),
                                        // Wands: {8,10,12,14,15} — gaps prevent SF ✓
                                        List.of(6, 15, 14, 12, 10, 8)),

                                /* 33 — K-high flush, 4 table Pentacles + K in hole */
                                Arguments.of("Flush: K-high — 4 table Pentacles + K in hole",
                                        List.of(c(14,"Pentacles"), c(2,"Cups")),
                                        List.of(c(12,"Pentacles"), c(10,"Pentacles"), c(8,"Pentacles"), c(6,"Pentacles")),
                                        // Pentacles: {6,8,10,12,14} — even values, no 5 consecutive ✓
                                        List.of(6, 14, 12, 10, 8, 6)),

                                /* 34 — Knight(12)-high flush */
                                Arguments.of("Flush: Knight(12)-high — 4 table Cups + Kn in hole",
                                        List.of(c(12,"Cups"),    c(2,"Swords")),
                                        List.of(c(9,"Cups"),     c(7,"Cups"),     c(5,"Cups"),     c(3,"Cups")),
                                        // Cups: {3,5,7,9,12} — gaps at 4,6,8,10,11 prevent SF ✓
                                        List.of(6, 12, 9, 7, 5, 3)),

                                /* 35 — Q(13)-high flush from 6 suited, picks best 5 */
                                Arguments.of("Flush: 6 Swords {3,5,7,9,11,13}, picks top 5 dropping 3",
                                        List.of(c(13,"Swords"),  c(5,"Swords")),
                                        List.of(c(11,"Swords"),  c(9,"Swords"),   c(7,"Swords"),   c(3,"Swords")),
                                        // All odd values — no 5 consecutive ✓; top 5 = {5,7,9,11,13}
                                        List.of(6, 13, 11, 9, 7, 5))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("flushCases")
                void flush(String desc, List<MinorArcanaCard> hole,
                           List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // STRAIGHT → [5, highCard]                                 tests 36-42
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> straightCases() {
                        return Stream.of(
                                /* 36 — A-high (11-12-13-14-15), one junk dropped */
                                Arguments.of("Straight: A-high (11-12-13-14-15) — junk 2 dropped",
                                        List.of(c(15,"Cups"),    c(14,"Swords")),
                                        List.of(c(13,"Wands"),   c(12,"Pentacles"), c(11,"Cups"),  c(2,"Swords")),
                                        List.of(5, 15)),

                                /* 37 — 6 consecutive {9-14}, picks K-high (10-14) */
                                Arguments.of("Straight: 6 consecutive {9-14} — evaluator picks K-high",
                                        List.of(c(14,"Cups"),    c(9,"Swords")),
                                        List.of(c(10,"Wands"),   c(11,"Pentacles"), c(12,"Cups"),  c(13,"Swords")),
                                        List.of(5, 14)),

                                /* 38 — 6 consecutive {8-13}, picks Q-high (9-13) */
                                Arguments.of("Straight: 6 consecutive {8-13} — evaluator picks Q-high",
                                        List.of(c(13,"Cups"),    c(8,"Swords")),
                                        List.of(c(9,"Wands"),    c(10,"Pentacles"), c(11,"Cups"),  c(12,"Swords")),
                                        List.of(5, 13)),

                                /* 39 — Kn-high (8-9-10-11-12), one junk dropped */
                                Arguments.of("Straight: Knight-high (8-9-10-11-12) — junk 3 dropped",
                                        List.of(c(12,"Cups"),    c(3,"Swords")),
                                        List.of(c(8,"Wands"),    c(9,"Pentacles"),  c(10,"Cups"), c(11,"Swords")),
                                        List.of(5, 12)),

                                /* 40 — Page-high (7-8-9-10-11) */
                                Arguments.of("Straight: Page-high (7-8-9-10-11) — junk 2 dropped",
                                        List.of(c(11,"Cups"),    c(2,"Swords")),
                                        List.of(c(7,"Wands"),    c(8,"Pentacles"),  c(9,"Cups"),  c(10,"Swords")),
                                        List.of(5, 11)),

                                /* 41 — 10-high (6-7-8-9-10) */
                                Arguments.of("Straight: 10-high (6-7-8-9-10) — junk 2 dropped",
                                        List.of(c(10,"Cups"),    c(2,"Swords")),
                                        List.of(c(6,"Wands"),    c(7,"Pentacles"),  c(8,"Cups"),  c(9,"Swords")),
                                        List.of(5, 10)),

                                /* 42 — 6 consecutive {10-15}, A-high is best straight */
                                Arguments.of("Straight: 6 consecutive {10-15} — picks A-high (11-15)",
                                        List.of(c(15,"Cups"),    c(11,"Swords")),
                                        List.of(c(14,"Wands"),   c(13,"Pentacles"), c(12,"Cups"), c(10,"Swords")),
                                        // {10,11,12,13,14,15}: best straight = 11-12-13-14-15 = A-high
                                        // (no flush: Cups={A,12}=2 ✓  Swords={11,10}=2 ✓)
                                        List.of(5, 15))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("straightCases")
                void straight(String desc, List<MinorArcanaCard> hole,
                              List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // THREE OF A KIND → [4, tripsPower, k1, k2]                tests 43-49
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> tripsOfAKindCases() {
                        return Stream.of(
                                /* 43 — trips Aces, kickers K and Q from table */
                                Arguments.of("Trips Aces: kickers K(14) Q(13) from table",
                                        List.of(c(15,"Cups"),    c(15,"Swords")),
                                        List.of(c(15,"Wands"),   c(14,"Pentacles"), c(13,"Cups"),  c(2,"Swords")),
                                        List.of(4, 15, 14, 13)),

                                /* 44 — trips Kings, 3 kicker candidates — picks best 2 */
                                Arguments.of("Trips Kings: 3 kicker candidates {A,Q,Page} — picks A and Q",
                                        List.of(c(14,"Cups"),    c(14,"Swords")),
                                        List.of(c(14,"Wands"),   c(15,"Pentacles"), c(13,"Cups"),  c(11,"Swords")),
                                        List.of(4, 14, 15, 13)),

                                /* 45 — trips Queens, kickers A from hole + K from table */
                                Arguments.of("Trips Queens(13): A from hole + K from table",
                                        List.of(c(13,"Cups"),    c(15,"Swords")),
                                        List.of(c(13,"Swords"),  c(13,"Wands"),     c(14,"Pentacles"), c(2,"Cups")),
                                        List.of(4, 13, 15, 14)),

                                /* 46 — trips Jacks(11), kickers A+K from table */
                                Arguments.of("Trips Jacks(11): kickers A(15) K(14) both from table",
                                        List.of(c(11,"Cups"),    c(11,"Swords")),
                                        List.of(c(11,"Wands"),   c(15,"Pentacles"), c(14,"Cups"),  c(2,"Swords")),
                                        List.of(4, 11, 15, 14)),

                                /* 47 — trips 9s, 3 kicker candidates — picks best 2 */
                                Arguments.of("Trips 9s: 3 kickers {A,K,Q} available — picks A and K",
                                        List.of(c(9,"Cups"),     c(9,"Swords")),
                                        List.of(c(9,"Wands"),    c(15,"Pentacles"), c(14,"Cups"),  c(13,"Swords")),
                                        List.of(4, 9, 15, 14)),

                                /* 48 — trips 7s, kickers Knight(12) and Page(11) */
                                Arguments.of("Trips 7s: kickers Knight(12) and Page(11)",
                                        List.of(c(7,"Cups"),     c(7,"Swords")),
                                        List.of(c(7,"Wands"),    c(12,"Pentacles"), c(11,"Cups"),  c(10,"Swords")),
                                        List.of(4, 7, 12, 11)),

                                /* 49 — trips 5s, hole provides the better kicker */
                                Arguments.of("Trips 5s: hole K(14) beats Q(13) from table as kicker-1",
                                        List.of(c(5,"Cups"),     c(14,"Swords")),
                                        List.of(c(5,"Swords"),   c(5,"Wands"),      c(15,"Pentacles"), c(13,"Cups")),
                                        List.of(4, 5, 15, 14))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("tripsOfAKindCases")
                void tripsOfAKind(String desc, List<MinorArcanaCard> hole,
                                  List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // TWO PAIR → [3, highPair, lowPair, kicker]                tests 50-56
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> twoPairCases() {
                        return Stream.of(
                                /* 50 — A-A + K-K, kicker Q from table */
                                Arguments.of("Two Pair A-A+K-K: kicker Q(13) from table",
                                        List.of(c(15,"Cups"),    c(15,"Swords")),
                                        List.of(c(14,"Cups"),    c(14,"Swords"),    c(13,"Wands"),  c(2,"Pentacles")),
                                        List.of(3, 15, 14, 13)),

                                /* 51 — K-K + Q-Q, kicker A from table */
                                Arguments.of("Two Pair K-K+Q-Q: kicker A(15) from table",
                                        List.of(c(14,"Cups"),    c(14,"Swords")),
                                        List.of(c(13,"Cups"),    c(13,"Swords"),    c(15,"Wands"),  c(2,"Pentacles")),
                                        List.of(3, 14, 13, 15)),

                                /* 52 — Q-Q + Page(11)-Page, kicker K from table */
                                Arguments.of("Two Pair Q-Q+Page-Page: kicker K(14) from table",
                                        List.of(c(13,"Cups"),    c(13,"Swords")),
                                        List.of(c(11,"Cups"),    c(11,"Swords"),    c(14,"Wands"),  c(2,"Pentacles")),
                                        List.of(3, 13, 11, 14)),

                                /* 53 — 3 pairs available in 6 cards — picks best 2 */
                                Arguments.of("Two Pair: 3 pairs {A,K,Q} available — picks A-A+K-K kicker Q",
                                        List.of(c(15,"Cups"),    c(14,"Swords")),
                                        List.of(c(15,"Swords"),  c(14,"Cups"),      c(13,"Wands"),  c(13,"Pentacles")),
                                        List.of(3, 15, 14, 13)),

                                /* 54 — 10-10 + 9-9, kicker A from table */
                                Arguments.of("Two Pair 10-10+9-9: kicker A(15) from table",
                                        List.of(c(10,"Cups"),    c(10,"Swords")),
                                        List.of(c(9,"Cups"),     c(9,"Swords"),     c(15,"Wands"),  c(2,"Pentacles")),
                                        List.of(3, 10, 9, 15)),

                                /* 55 — A-A + 3-3, kicker K from table */
                                Arguments.of("Two Pair A-A+3-3: kicker K(14) from table",
                                        List.of(c(15,"Cups"),    c(15,"Swords")),
                                        List.of(c(3,"Cups"),     c(3,"Swords"),     c(14,"Wands"),  c(2,"Pentacles")),
                                        List.of(3, 15, 3, 14)),

                                /* 56 — 7-7 + 6-6, kicker A from table */
                                Arguments.of("Two Pair 7-7+6-6: kicker A(15) from table",
                                        List.of(c(7,"Cups"),     c(7,"Swords")),
                                        List.of(c(6,"Cups"),     c(6,"Swords"),     c(15,"Wands"),  c(2,"Pentacles")),
                                        List.of(3, 7, 6, 15))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("twoPairCases")
                void twoPair(String desc, List<MinorArcanaCard> hole,
                             List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // ONE PAIR → [2, pairPower, k1, k2, k3]                   tests 57-63
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> onePairCases() {
                        return Stream.of(
                                /* 57 — pair Aces, kickers K Q Page from table */
                                Arguments.of("Pair Aces: kickers K(14) Q(13) Page(11) from table",
                                        List.of(c(15,"Cups"),    c(15,"Swords")),
                                        List.of(c(14,"Wands"),   c(13,"Pentacles"), c(11,"Cups"),  c(2,"Swords")),
                                        List.of(2, 15, 14, 13, 11)),

                                /* 58 — pair Kings, kickers A Q Page from table */
                                Arguments.of("Pair Kings: kickers A(15) Q(13) Page(11) from table",
                                        List.of(c(14,"Cups"),    c(14,"Swords")),
                                        List.of(c(15,"Wands"),   c(13,"Pentacles"), c(11,"Cups"),  c(2,"Swords")),
                                        List.of(2, 14, 15, 13, 11)),

                                /* 59 — pair Queens, kickers A K Page from table */
                                Arguments.of("Pair Queens(13): kickers A(15) K(14) Page(11) from table",
                                        List.of(c(13,"Cups"),    c(13,"Swords")),
                                        List.of(c(15,"Wands"),   c(14,"Pentacles"), c(11,"Cups"),  c(2,"Swords")),
                                        List.of(2, 13, 15, 14, 11)),

                                /* 60 — pair 9s, 4 kicker candidates — picks best 3 */
                                Arguments.of("Pair 9s: 4 kicker candidates {A,K,Q,Page} — picks A, K, Q",
                                        List.of(c(9,"Cups"),     c(9,"Swords")),
                                        List.of(c(15,"Wands"),   c(14,"Pentacles"), c(13,"Cups"),  c(11,"Swords")),
                                        List.of(2, 9, 15, 14, 13)),

                                /* 61 — pair 7s, kickers K Page 10 */
                                Arguments.of("Pair 7s: kickers K(14) Page(11) 10",
                                        List.of(c(7,"Cups"),     c(7,"Swords")),
                                        List.of(c(14,"Wands"),   c(11,"Pentacles"), c(10,"Cups"),  c(2,"Swords")),
                                        List.of(2, 7, 14, 11, 10)),

                                /* 62 — pair formed by hole + table card, kickers from remaining */
                                Arguments.of("Pair 5s (hole+table): kickers A(15) K(14) Q(13) from table",
                                        List.of(c(5,"Cups"),     c(15,"Swords")),
                                        List.of(c(5,"Wands"),    c(14,"Pentacles"), c(13,"Cups"),  c(2,"Swords")),
                                        List.of(2, 5, 15, 14, 13)),

                                /* 63 — pair 3s, kickers A K Q from table */
                                Arguments.of("Pair 3s: kickers A(15) K(14) Q(13) from table",
                                        List.of(c(3,"Cups"),     c(3,"Swords")),
                                        List.of(c(15,"Wands"),   c(14,"Pentacles"), c(13,"Cups"),  c(11,"Swords")),
                                        List.of(2, 3, 15, 14, 13))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("onePairCases")
                void onePair(String desc, List<MinorArcanaCard> hole,
                             List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // HIGH CARD → [1, c1, c2, c3, c4, c5] desc                tests 64-70
                //
                // Verified per test: no pair, no flush (max 2 same-suit per player),
                // no straight (no 5 consecutive power values in the 6-card set).
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> highCardCases() {
                        return Stream.of(
                                /* 64 — 6 cards {2,9,11,13,14,15}: drops 2 → A-K-Q-Page-9 */
                                Arguments.of("High Card: drops low junk (2) → A-K-Q(13)-Page(11)-9",
                                        List.of(c(15,"Cups"),    c(14,"Swords")),
                                        List.of(c(13,"Wands"),   c(11,"Pentacles"), c(9,"Cups"),   c(2,"Swords")),
                                        // No straight: {9,11,13,14,15} — gap at 10,12 ✓
                                        List.of(1, 15, 14, 13, 11, 9)),

                                /* 65 — 6 cards {2,7,9,13,14,15}: drops 2 → A-K-Q-9-7 */
                                Arguments.of("High Card: drops 2 → A-K-Q(13)-9-7",
                                        List.of(c(15,"Cups"),    c(7,"Swords")),
                                        List.of(c(14,"Wands"),   c(13,"Pentacles"), c(9,"Cups"),   c(2,"Swords")),
                                        List.of(1, 15, 14, 13, 9, 7)),

                                /* 66 — 6 cards {2,7,10,13,14,15}: drops 2 → A-K-Q-10-7 */
                                Arguments.of("High Card: drops 2 → A-K-Q(13)-10-7",
                                        List.of(c(15,"Cups"),    c(7,"Swords")),
                                        List.of(c(14,"Wands"),   c(13,"Pentacles"), c(10,"Cups"),  c(2,"Swords")),
                                        List.of(1, 15, 14, 13, 10, 7)),

                                /* 67 — 6 cards {2,7,9,11,13,14}: drops 2 → K-Q-Page-9-7 */
                                Arguments.of("High Card: drops 2 → K(14)-Q(13)-Page(11)-9-7",
                                        List.of(c(14,"Cups"),    c(9,"Swords")),
                                        List.of(c(13,"Wands"),   c(11,"Pentacles"), c(7,"Cups"),   c(2,"Swords")),
                                        List.of(1, 14, 13, 11, 9, 7)),

                                /* 68 — 6 cards {3,7,9,11,13,15}: drops 3 → A-Q-Page-9-7 */
                                Arguments.of("High Card: drops 3 → A(15)-Q(13)-Page(11)-9-7",
                                        List.of(c(15,"Cups"),    c(9,"Swords")),
                                        List.of(c(13,"Wands"),   c(11,"Pentacles"), c(7,"Cups"),   c(3,"Swords")),
                                        // All odd: no 5 consecutive ✓
                                        List.of(1, 15, 13, 11, 9, 7)),

                                /* 69 — 6 cards {5,7,9,11,13,14}: drops 5 → K-Q-Page-9-7 */
                                Arguments.of("High Card: drops 5 → K(14)-Q(13)-Page(11)-9-7",
                                        List.of(c(14,"Cups"),    c(5,"Swords")),
                                        List.of(c(13,"Wands"),   c(11,"Pentacles"), c(9,"Cups"),   c(7,"Swords")),
                                        List.of(1, 14, 13, 11, 9, 7)),

                                /* 70 — 6 cards {2,6,8,13,14,15}: drops 2 → A-K-Q-8-6 */
                                Arguments.of("High Card: drops 2 → A(15)-K(14)-Q(13)-8-6",
                                        List.of(c(15,"Cups"),    c(8,"Swords")),
                                        List.of(c(14,"Wands"),   c(13,"Pentacles"), c(6,"Cups"),   c(2,"Swords")),
                                        // No straight: {6,8,13,14,15} — gap at 7,9-12 ✓
                                        List.of(1, 15, 14, 13, 8, 6))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("highCardCases")
                void highCard(String desc, List<MinorArcanaCard> hole,
                              List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }
        }

        @Nested
        class BestHandScoreTestSixCards {

                private List<Integer> score(List<MinorArcanaCard> hole, List<MinorArcanaCard> table) {
                        return determiner.getBestHandScore(hole, table);
                }

                // ════════════════════════════════════════════════════════════════════
                // ROYAL FLUSH → [10]                                       tests 01-07
                //
                // With 6 table cards there are three possible RF distributions:
                //   5 RF on table + 1 junk table + 2 junk hole  (board plays)
                //   4 RF on table + 1 RF in hole + 1 junk table + 1 junk hole
                //   3 RF on table + 2 RF in hole + 3 junk table
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> royalFlushCases() {
                        return Stream.of(
                                /* 01 — RF entirely on board (5 RF + 1 junk on table, 2 junk in hole) */
                                Arguments.of("RF: 5 Wands RF cards on board — board plays, hole irrelevant",
                                        List.of(c(2,"Cups"),    c(3,"Swords")),
                                        List.of(c(15,"Wands"),  c(14,"Wands"),  c(13,"Wands"),
                                                c(12,"Wands"),  c(11,"Wands"),  c(4,"Cups")),
                                        List.of(10)),

                                /* 02 — 4 table RF + 1 hole RF + 1 junk table + 1 junk hole */
                                Arguments.of("RF: 4 table Swords RF + A in hole",
                                        List.of(c(15,"Swords"), c(2,"Cups")),
                                        List.of(c(14,"Swords"), c(13,"Swords"), c(12,"Swords"),
                                                c(11,"Swords"), c(4,"Cups"),    c(5,"Wands")),
                                        List.of(10)),

                                /* 03 — 3 table RF + 2 hole RF + 3 junk table */
                                Arguments.of("RF: 3 table Cups RF + A+K in hole",
                                        List.of(c(15,"Cups"),   c(14,"Cups")),
                                        List.of(c(13,"Cups"),   c(12,"Cups"),   c(11,"Cups"),
                                                c(4,"Swords"),  c(5,"Wands"),   c(6,"Pentacles")),
                                        List.of(10)),

                                /* 04 — 6 consecutive same-suit including A; picks A-high = Royal Flush */
                                Arguments.of("RF: 6 consecutive Swords {10-15}, evaluator picks A-high = RF",
                                        List.of(c(15,"Swords"), c(10,"Swords")),
                                        List.of(c(14,"Swords"), c(13,"Swords"), c(12,"Swords"),
                                                c(11,"Swords"), c(4,"Cups"),    c(5,"Wands")),
                                        // {10,11,12,13,14,15} all Swords: best SF = 11-15 = RF [10]
                                        List.of(10)),

                                /* 05 — 7 suited including A; picks RF from 7 Cups cards */
                                Arguments.of("RF: 7 Cups {9-15} available, evaluator finds A-high SF = RF",
                                        List.of(c(15,"Cups"),   c(9,"Cups")),
                                        List.of(c(14,"Cups"),   c(13,"Cups"),   c(12,"Cups"),
                                                c(11,"Cups"),   c(10,"Cups"),   c(2,"Swords")),
                                        // 7 Cups {9..15}: getStraightHighCard returns 15 → RF [10]
                                        List.of(10)),

                                /* 06 — 4 table RF (Pentacles) + 1 hole RF + 3 other junk */
                                Arguments.of("RF: 4 table Pentacles RF + Knight in hole",
                                        List.of(c(12,"Pentacles"), c(2,"Cups")),
                                        List.of(c(15,"Pentacles"), c(14,"Pentacles"), c(13,"Pentacles"),
                                                c(11,"Pentacles"), c(4,"Swords"),     c(5,"Wands")),
                                        List.of(10)),

                                /* 07 — Full 5-card RF in hole+table, 3 junk cards (all suits verified) */
                                Arguments.of("RF: A+K in hole, Q+J+Page on table, 3 junk (Wands)",
                                        List.of(c(15,"Wands"),  c(14,"Wands")),
                                        List.of(c(13,"Wands"),  c(12,"Wands"),  c(11,"Wands"),
                                                c(2,"Cups"),    c(3,"Swords"),   c(4,"Pentacles")),
                                        List.of(10))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("royalFlushCases")
                void royalFlush(String desc, List<MinorArcanaCard> hole,
                                List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // STRAIGHT FLUSH → [9, highCard]                           tests 08-14
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> straightFlushCases() {
                        return Stream.of(
                                /* 08 — K-high SF + 3 junk cards */
                                Arguments.of("SF: K-high (10-J-Q-Kn-K) in Swords + 3 junk",
                                        List.of(c(14,"Swords"), c(2,"Cups")),
                                        List.of(c(10,"Swords"), c(11,"Swords"), c(12,"Swords"),
                                                c(13,"Swords"), c(4,"Cups"),    c(5,"Wands")),
                                        List.of(9, 14)),

                                /* 09 — 6 consecutive Cups {9-14}: picks K-high (10-14) not RF */
                                Arguments.of("SF: 6 consecutive Cups {9-14}, picks K-high (10-14)",
                                        List.of(c(14,"Cups"),   c(9,"Cups")),
                                        List.of(c(10,"Cups"),   c(11,"Cups"),   c(12,"Cups"),
                                                c(13,"Cups"),   c(4,"Swords"),  c(5,"Wands")),
                                        // {9,10,11,12,13,14} no 15 → not RF; highest SF = 10-14 [9,14]
                                        List.of(9, 14)),

                                /* 10 — Q-high SF + 3 junk */
                                Arguments.of("SF: Q-high (9-10-11-12-13) in Wands + 3 junk",
                                        List.of(c(13,"Wands"),  c(2,"Cups")),
                                        List.of(c(9,"Wands"),   c(10,"Wands"),  c(11,"Wands"),
                                                c(12,"Wands"),  c(4,"Cups"),    c(5,"Swords")),
                                        List.of(9, 13)),

                                /* 11 — 7 consecutive Swords {8-14}: picks K-high */
                                Arguments.of("SF: 7 consecutive Swords {8-14}, picks K-high (10-14)",
                                        List.of(c(14,"Swords"), c(8,"Swords")),
                                        List.of(c(9,"Swords"),  c(10,"Swords"), c(11,"Swords"),
                                                c(12,"Swords"), c(13,"Swords"), c(3,"Cups")),
                                        // sorted desc {8,9,10,11,12,13,14}: i=0 high=14 low=10: 14-10=4 ✓ → [9,14]
                                        List.of(9, 14)),

                                /* 12 — Kn-high SF (8-9-10-11-12) in Cups + 3 junk */
                                Arguments.of("SF: Knight-high (8-9-10-11-12) in Cups + 3 junk",
                                        List.of(c(12,"Cups"),   c(2,"Swords")),
                                        List.of(c(8,"Cups"),    c(9,"Cups"),    c(10,"Cups"),
                                                c(11,"Cups"),   c(5,"Wands"),   c(6,"Pentacles")),
                                        List.of(9, 12)),

                                /* 13 — 9-high SF + 3 junk */
                                Arguments.of("SF: 9-high (5-6-7-8-9) in Wands + 3 junk",
                                        List.of(c(9,"Wands"),   c(2,"Cups")),
                                        List.of(c(5,"Wands"),   c(6,"Wands"),   c(7,"Wands"),
                                                c(8,"Wands"),   c(3,"Cups"),    c(4,"Swords")),
                                        List.of(9, 9)),

                                /* 14 — 8-high SF in Pentacles + 3 junk */
                                Arguments.of("SF: 8-high (4-5-6-7-8) in Pentacles + 3 junk",
                                        List.of(c(8,"Pentacles"), c(2,"Cups")),
                                        List.of(c(4,"Pentacles"), c(5,"Pentacles"), c(6,"Pentacles"),
                                                c(7,"Pentacles"), c(3,"Cups"),      c(9,"Swords")),
                                        List.of(9, 8))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("straightFlushCases")
                void straightFlush(String desc, List<MinorArcanaCard> hole,
                                   List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // FOUR OF A KIND → [8, quadPower, kickerPower]             tests 15-21
                // With 8 cards: quad uses 4, leaving 4 kicker candidates.
                // Evaluator picks the single best kicker from those 4.
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> fourOfAKindCases() {
                        return Stream.of(
                                /* 15 — quad Aces, 4 kicker candidates, picks K */
                                Arguments.of("Quads Aces: 4 kicker candidates {K,Q,Page,2} — picks K(14)",
                                        List.of(c(15,"Cups"),   c(15,"Swords")),
                                        List.of(c(15,"Wands"),  c(15,"Pentacles"), c(14,"Cups"),
                                                c(13,"Swords"), c(11,"Wands"),     c(2,"Pentacles")),
                                        List.of(8, 15, 14)),

                                /* 16 — quad Kings, kicker A from table */
                                Arguments.of("Quads Kings: 4 kicker candidates {A,2,3,4} — picks A(15)",
                                        List.of(c(14,"Cups"),   c(14,"Swords")),
                                        List.of(c(14,"Wands"),  c(14,"Pentacles"), c(15,"Cups"),
                                                c(2,"Swords"),  c(3,"Wands"),      c(4,"Pentacles")),
                                        List.of(8, 14, 15)),

                                /* 17 — quad Queens, A from hole beats all table kickers */
                                Arguments.of("Quads Queens: A(15) from hole beats Page(11) from table",
                                        List.of(c(13,"Cups"),   c(15,"Swords")),
                                        List.of(c(13,"Swords"), c(13,"Wands"),     c(13,"Pentacles"),
                                                c(11,"Cups"),   c(2,"Wands"),      c(3,"Pentacles")),
                                        List.of(8, 13, 15)),

                                /* 18 — quad Jacks(11), picks A from 4 candidates */
                                Arguments.of("Quads Jacks(11): 4 kicker candidates {A,K,2,3} — picks A(15)",
                                        List.of(c(11,"Cups"),   c(11,"Swords")),
                                        List.of(c(11,"Wands"),  c(11,"Pentacles"), c(15,"Cups"),
                                                c(14,"Swords"), c(2,"Wands"),      c(3,"Pentacles")),
                                        List.of(8, 11, 15)),

                                /* 19 — quad 9s, picks best from 4 candidates {A,K,Q,Kn} */
                                Arguments.of("Quads 9s: 4 kicker candidates {A,K,Q,Kn} — picks A(15)",
                                        List.of(c(9,"Cups"),    c(9,"Swords")),
                                        List.of(c(9,"Wands"),   c(9,"Pentacles"),  c(15,"Cups"),
                                                c(14,"Swords"), c(13,"Wands"),     c(12,"Pentacles")),
                                        List.of(8, 9, 15)),

                                /* 20 — quad 7s, kicker K from table (hole cards both in the quad) */
                                Arguments.of("Quads 7s: 4 kicker candidates {K,2,3,4} — picks K(14)",
                                        List.of(c(7,"Cups"),    c(7,"Swords")),
                                        List.of(c(7,"Wands"),   c(7,"Pentacles"),  c(14,"Cups"),
                                                c(2,"Swords"),  c(3,"Wands"),      c(4,"Pentacles")),
                                        List.of(8, 7, 14)),

                                /* 21 — quad 5s on board, hole provides A = best kicker */
                                Arguments.of("Quads 5s on board: A(15) from hole beats Q(13) from table",
                                        List.of(c(15,"Cups"),   c(13,"Swords")),
                                        List.of(c(5,"Cups"),    c(5,"Swords"),     c(5,"Wands"),
                                                c(5,"Pentacles"), c(2,"Wands"),    c(3,"Pentacles")),
                                        List.of(8, 5, 15))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("fourOfAKindCases")
                void fourOfAKind(String desc, List<MinorArcanaCard> hole,
                                 List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // FULL HOUSE → [7, tripsPower, pairPower]                  tests 22-28
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> fullHouseCases() {
                        return Stream.of(
                                /* 22 — Aces full of Kings + 3 junk */
                                Arguments.of("FH: Aces full of Kings + 3 junk dropped",
                                        List.of(c(15,"Cups"),   c(15,"Swords")),
                                        List.of(c(15,"Wands"),  c(14,"Cups"),   c(14,"Swords"),
                                                c(2,"Pentacles"), c(3,"Wands"), c(4,"Swords")),
                                        List.of(7, 15, 14)),

                                /* 23 — Kings full of Aces + 3 junk */
                                Arguments.of("FH: Kings full of Aces + 3 junk dropped",
                                        List.of(c(14,"Cups"),   c(14,"Swords")),
                                        List.of(c(14,"Wands"),  c(15,"Cups"),   c(15,"Swords"),
                                                c(2,"Pentacles"), c(3,"Wands"), c(4,"Swords")),
                                        List.of(7, 14, 15)),

                                /* 24 — two complete triples in 8 cards; picks Aces as trips (higher) */
                                Arguments.of("FH: two triples {A×3, K×3} in 8 cards — picks Aces-full-of-Kings",
                                        List.of(c(15,"Cups"),   c(15,"Swords")),
                                        List.of(c(15,"Wands"),  c(14,"Cups"),   c(14,"Swords"),
                                                c(14,"Wands"),  c(2,"Pentacles"), c(3,"Swords")),
                                        List.of(7, 15, 14)),

                                /* 25 — trips + two different pairs; picks higher pair for FH */
                                Arguments.of("FH: trips Q(13) + pairs {Page(11), 10} — picks Q-full-of-Page",
                                        List.of(c(13,"Cups"),   c(13,"Swords")),
                                        List.of(c(13,"Wands"),  c(11,"Cups"),   c(11,"Swords"),
                                                c(10,"Cups"),   c(10,"Swords"),  c(2,"Pentacles")),
                                        // pairs available: Page(11) and 10; FH uses higher pair = Page [7,13,11]
                                        List.of(7, 13, 11)),

                                /* 26 — FH built entirely from board; hole junk */
                                Arguments.of("FH: A-A-A + K-K on board, hole junk — board plays",
                                        List.of(c(2,"Cups"),    c(3,"Swords")),
                                        List.of(c(15,"Cups"),   c(15,"Swords"),  c(15,"Wands"),
                                                c(14,"Cups"),   c(14,"Swords"),  c(4,"Pentacles")),
                                        List.of(7, 15, 14)),

                                /* 27 — 10s full of 9s + 3 junk */
                                Arguments.of("FH: 10s full of 9s + 3 junk dropped",
                                        List.of(c(10,"Cups"),   c(10,"Swords")),
                                        List.of(c(10,"Wands"),  c(9,"Cups"),    c(9,"Swords"),
                                                c(2,"Pentacles"), c(3,"Wands"), c(4,"Swords")),
                                        List.of(7, 10, 9)),

                                /* 28 — trips from table, pair from hole + 3 junk */
                                Arguments.of("FH: trips Q(13) on board, pair Page(11) from hole",
                                        List.of(c(11,"Cups"),   c(11,"Swords")),
                                        List.of(c(13,"Cups"),   c(13,"Swords"),  c(13,"Wands"),
                                                c(2,"Pentacles"), c(3,"Wands"), c(4,"Swords")),
                                        List.of(7, 13, 11))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("fullHouseCases")
                void fullHouse(String desc, List<MinorArcanaCard> hole,
                               List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // FLUSH → [6, c1, c2, c3, c4, c5] desc                    tests 29-35
                //
                // With 8 total cards, flush can come from 5-8 same-suit cards.
                // Non-consecutive gaps verified per test to prevent SF.
                // Suit counts across all 8 cards kept ≤ 4 for off-suit cards.
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> flushCases() {
                        return Stream.of(
                                /* 29 — 6 Cups on board + both hole off-suit: 6 suited, picks top 5 */
                                Arguments.of("Flush: 6 Cups board {5,7,9,11,13,15} — picks top 5, drops 5",
                                        List.of(c(2,"Swords"),  c(3,"Wands")),
                                        List.of(c(15,"Cups"),   c(13,"Cups"),   c(11,"Cups"),
                                                c(9,"Cups"),    c(7,"Cups"),    c(5,"Cups")),
                                        // All odd Cups → no 5 consecutive ✓; top 5 = {7,9,11,13,15}
                                        List.of(6, 15, 13, 11, 9, 7)),

                                /* 30 — 5 Cups on board + 1 hole Cups + 2 off-suit: 6 suited, picks top 5 */
                                Arguments.of("Flush: 5 table Cups + A in hole → top 5 drops lowest",
                                        List.of(c(15,"Cups"),   c(2,"Swords")),
                                        List.of(c(14,"Cups"),   c(12,"Cups"),   c(10,"Cups"),
                                                c(8,"Cups"),    c(6,"Cups"),    c(3,"Wands")),
                                        // 6 Cups {6,8,10,12,14,15} even+A → no SF ✓; top 5 drops 6
                                        List.of(6, 15, 14, 12, 10, 8)),

                                /* 31 — 4 table + 2 hole = 6 Swords, picks top 5 */
                                Arguments.of("Flush: 4 table Swords + A+K in hole: top 5 drops lowest",
                                        List.of(c(15,"Swords"), c(14,"Swords")),
                                        List.of(c(12,"Swords"), c(10,"Swords"), c(8,"Swords"),
                                                c(6,"Swords"),  c(3,"Cups"),    c(4,"Wands")),
                                        // 6 Swords {6,8,10,12,14,15} → top 5 drops 6
                                        List.of(6, 15, 14, 12, 10, 8)),

                                /* 32 — 7 same-suit cards available; picks top 5 */
                                Arguments.of("Flush: 7 Cups {3,5,7,9,11,13,15} — picks top 5 drops two lowest",
                                        List.of(c(15,"Cups"),   c(13,"Cups")),
                                        List.of(c(11,"Cups"),   c(9,"Cups"),    c(7,"Cups"),
                                                c(5,"Cups"),    c(3,"Cups"),    c(2,"Swords")),
                                        // All odd Cups → no SF ✓; top 5 = {7,9,11,13,15}; drops 3,5
                                        List.of(6, 15, 13, 11, 9, 7)),

                                /* 33 — K-high flush from 6 Wands even values */
                                Arguments.of("Flush: 6 Wands {4,6,8,10,12,14} — K-high, drops 4",
                                        List.of(c(14,"Wands"),  c(12,"Wands")),
                                        List.of(c(10,"Wands"),  c(8,"Wands"),   c(6,"Wands"),
                                                c(4,"Wands"),   c(2,"Cups"),    c(3,"Swords")),
                                        // Even Wands → no SF ✓; top 5 drops 4
                                        List.of(6, 14, 12, 10, 8, 6)),

                                /* 34 — 8 same-suit cards; picks best 5 */
                                Arguments.of("Flush: 8 Cups {3,5,7,9,11,13,14,15} — picks top 5 {9,11,13,14,15}",
                                        List.of(c(15,"Cups"),   c(14,"Cups")),
                                        List.of(c(13,"Cups"),   c(11,"Cups"),   c(9,"Cups"),
                                                c(7,"Cups"),    c(5,"Cups"),    c(3,"Cups")),
                                        // 13-14-15 run of 3; 11 has gap at 12 → no SF ✓; drops 3,5,7
                                        List.of(6, 15, 14, 13, 11, 9)),

                                /* 35 — Knight(12)-high flush from 5 Pentacles + 3 off-suit */
                                Arguments.of("Flush: 5 Pentacles {3,5,7,9,12} — Kn-high [6,12,9,7,5,3]",
                                        List.of(c(12,"Pentacles"), c(2,"Cups")),
                                        List.of(c(9,"Pentacles"),  c(7,"Pentacles"), c(5,"Pentacles"),
                                                c(3,"Pentacles"),  c(4,"Cups"),      c(5,"Swords")),
                                        // 5 Pentacles {3,5,7,9,12}: gaps at 4,6,8,10,11 → no SF ✓
                                        List.of(6, 12, 9, 7, 5, 3))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("flushCases")
                void flush(String desc, List<MinorArcanaCard> hole,
                           List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // STRAIGHT → [5, highCard]                                 tests 36-42
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> straightCases() {
                        return Stream.of(
                                /* 36 — A-high straight + 3 junk */
                                Arguments.of("Straight: A-high (11-12-13-14-15) + 3 junk",
                                        List.of(c(15,"Cups"),   c(14,"Swords")),
                                        List.of(c(13,"Wands"),  c(12,"Pentacles"), c(11,"Cups"),
                                                c(2,"Swords"),  c(3,"Wands"),      c(4,"Pentacles")),
                                        List.of(5, 15)),

                                /* 37 — 8 consecutive {8-15}: picks A-high (11-15) */
                                Arguments.of("Straight: 8 consecutive {8-15} — picks A-high (11-15)",
                                        List.of(c(15,"Cups"),   c(14,"Swords")),
                                        List.of(c(13,"Wands"),  c(12,"Pentacles"), c(11,"Cups"),
                                                c(10,"Swords"), c(9,"Wands"),      c(8,"Pentacles")),
                                        // i=0: high=15, low=11: 15-11=4 ✓ → [5,15]
                                        List.of(5, 15)),

                                /* 38 — K-high straight + 3 junk */
                                Arguments.of("Straight: K-high (10-11-12-13-14) + 3 junk",
                                        List.of(c(14,"Cups"),   c(9,"Swords")),
                                        List.of(c(10,"Wands"),  c(11,"Pentacles"), c(12,"Cups"),
                                                c(13,"Swords"), c(2,"Wands"),      c(3,"Pentacles")),
                                        List.of(5, 14)),

                                /* 39 — 6 consecutive {8-13}: picks Q-high (9-13) */
                                Arguments.of("Straight: 6 consecutive {8-13} — picks Q-high (9-13)",
                                        List.of(c(13,"Cups"),   c(8,"Swords")),
                                        List.of(c(9,"Wands"),   c(10,"Pentacles"), c(11,"Cups"),
                                                c(12,"Swords"), c(2,"Wands"),      c(3,"Pentacles")),
                                        List.of(5, 13)),

                                /* 40 — Kn-high (8-9-10-11-12) + 3 junk */
                                Arguments.of("Straight: Knight-high (8-9-10-11-12) + 3 junk",
                                        List.of(c(12,"Cups"),   c(2,"Swords")),
                                        List.of(c(8,"Wands"),   c(9,"Pentacles"),  c(10,"Cups"),
                                                c(11,"Swords"), c(3,"Wands"),      c(4,"Pentacles")),
                                        List.of(5, 12)),

                                /* 41 — Page-high (7-8-9-10-11) + 3 junk */
                                Arguments.of("Straight: Page-high (7-8-9-10-11) + 3 junk",
                                        List.of(c(11,"Cups"),   c(2,"Swords")),
                                        List.of(c(7,"Wands"),   c(8,"Pentacles"),  c(9,"Cups"),
                                                c(10,"Swords"), c(3,"Wands"),      c(4,"Pentacles")),
                                        List.of(5, 11)),

                                /* 42 — 8 consecutive {2-9}: picks 9-high (5-9) */
                                Arguments.of("Straight: 8 consecutive {2-9} — picks 9-high (5-9)",
                                        List.of(c(9,"Cups"),    c(2,"Swords")),
                                        List.of(c(5,"Wands"),   c(6,"Pentacles"),  c(7,"Cups"),
                                                c(8,"Swords"),  c(3,"Wands"),      c(4,"Pentacles")),
                                        // {2,3,4,5,6,7,8,9}: i=0 high=9 low=5: 9-5=4 ✓ → [5,9]
                                        List.of(5, 9))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("straightCases")
                void straight(String desc, List<MinorArcanaCard> hole,
                              List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // THREE OF A KIND → [4, tripsPower, k1, k2]                tests 43-49
                // With 8 cards: trips uses 3, leaving 5 kicker candidates.
                // Evaluator picks best 2 from 5.
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> threeOfAKindCases() {
                        return Stream.of(
                                /* 43 — trips A, 5 kicker candidates: picks K and Q */
                                Arguments.of("Trips Aces: 5 kicker candidates {K,Q,Page,9,2} — picks K(14) Q(13)",
                                        List.of(c(15,"Cups"),   c(15,"Swords")),
                                        List.of(c(15,"Wands"),  c(14,"Pentacles"), c(13,"Cups"),
                                                c(11,"Swords"), c(9,"Wands"),      c(2,"Pentacles")),
                                        List.of(4, 15, 14, 13)),

                                /* 44 — trips K, 5 kicker candidates {A,Q,Page,9,2}: picks A and Q */
                                Arguments.of("Trips Kings: 5 kicker candidates {A,Q,Page,9,2} — picks A(15) Q(13)",
                                        List.of(c(14,"Cups"),   c(14,"Swords")),
                                        List.of(c(14,"Wands"),  c(15,"Pentacles"), c(13,"Cups"),
                                                c(11,"Swords"), c(9,"Wands"),      c(2,"Pentacles")),
                                        List.of(4, 14, 15, 13)),

                                /* 45 — trips Q, A from hole, K from table */
                                Arguments.of("Trips Queens(13): A from hole + K(14) from table as kickers",
                                        List.of(c(13,"Cups"),   c(15,"Swords")),
                                        List.of(c(13,"Swords"), c(13,"Wands"),     c(14,"Pentacles"),
                                                c(11,"Cups"),   c(2,"Wands"),      c(3,"Pentacles")),
                                        List.of(4, 13, 15, 14)),

                                /* 46 — trips Jacks(11), 5 kicker candidates: picks A and K */
                                Arguments.of("Trips Jacks(11): 5 kicker candidates {A,K,Q,Kn,2} — picks A K",
                                        List.of(c(11,"Cups"),   c(11,"Swords")),
                                        List.of(c(11,"Wands"),  c(15,"Pentacles"), c(14,"Cups"),
                                                c(13,"Swords"), c(8,"Wands"),     c(2,"Pentacles")),
                                        List.of(4, 11, 15, 14)),

                                /* 47 — trips 7, 5 kicker candidates: picks A and K */
                                Arguments.of("Trips 7s: 5 kicker candidates {A,K,Q,Kn,2} — picks A(15) K(14)",
                                        List.of(c(7,"Cups"),    c(7,"Swords")),
                                        List.of(c(7,"Wands"),   c(15,"Pentacles"), c(14,"Cups"),
                                                c(13,"Swords"), c(12,"Wands"),     c(2,"Pentacles")),
                                        List.of(4, 7, 15, 14)),

                                /* 48 — trips 5, kickers Knight(12) and Page(11) from 5 candidates */
                                Arguments.of("Trips 5s: 5 kicker candidates {Kn,Page,10,2,3} — picks Kn(12) Page(11)",
                                        List.of(c(5,"Cups"),    c(5,"Swords")),
                                        List.of(c(5,"Wands"),   c(12,"Pentacles"), c(11,"Cups"),
                                                c(10,"Swords"), c(2,"Wands"),      c(3,"Pentacles")),
                                        List.of(4, 5, 12, 11)),

                                /* 49 — trips 3 on board, hole provides A = best kicker */
                                Arguments.of("Trips 3s on board: A(15) from hole tops 5 kicker candidates",
                                        List.of(c(3,"Cups"),    c(15,"Swords")),
                                        List.of(c(3,"Swords"),  c(3,"Wands"),      c(14,"Pentacles"),
                                                c(13,"Cups"),   c(11,"Swords"),    c(2,"Wands")),
                                        List.of(4, 3, 15, 14))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("threeOfAKindCases")
                void threeOfAKind(String desc, List<MinorArcanaCard> hole,
                                  List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // TWO PAIR → [3, highPair, lowPair, kicker]                tests 50-56
                // With 8 cards: up to 4 pairs possible; evaluator picks best 2.
                // Kicker = best of the 4 remaining non-pair cards.
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> twoPairCases() {
                        return Stream.of(
                                /* 50 — A-A + K-K: kicker Q from 4 candidates {Q,Page,2,3} */
                                Arguments.of("Two Pair A-A+K-K: 4 kicker candidates — picks Q(13)",
                                        List.of(c(15,"Cups"),   c(15,"Swords")),
                                        List.of(c(14,"Cups"),   c(14,"Swords"),    c(13,"Wands"),
                                                c(11,"Pentacles"), c(2,"Cups"),   c(3,"Swords")),
                                        List.of(3, 15, 14, 13)),

                                /* 51 — K-K + Q-Q: kicker A from table */
                                Arguments.of("Two Pair K-K+Q-Q: 4 kicker candidates — picks A(15)",
                                        List.of(c(14,"Cups"),   c(14,"Swords")),
                                        List.of(c(13,"Cups"),   c(13,"Swords"),    c(15,"Wands"),
                                                c(11,"Pentacles"), c(2,"Cups"),   c(3,"Swords")),
                                        List.of(3, 14, 13, 15)),

                                /* 52 — 3 pairs available; picks best 2 + best remaining kicker */
                                Arguments.of("Two Pair: 3 pairs {A,K,Q} in 8 cards — picks A-A+K-K kicker Q",
                                        List.of(c(15,"Cups"),   c(14,"Swords")),
                                        List.of(c(15,"Swords"), c(14,"Cups"),      c(13,"Wands"),
                                                c(13,"Pentacles"), c(2,"Cups"),   c(3,"Swords")),
                                        List.of(3, 15, 14, 13)),

                                /* 53 — 4 pairs in 8 cards; picks best 2 + kicker */
                                Arguments.of("Two Pair: 4 pairs {A,K,Q,Page} in 8 cards — picks A-A+K-K kicker Q",
                                        List.of(c(15,"Cups"),   c(14,"Swords")),
                                        List.of(c(15,"Swords"), c(14,"Cups"),      c(13,"Wands"),
                                                c(13,"Pentacles"), c(11,"Cups"),  c(11,"Swords")),
                                        List.of(3, 15, 14, 13)),

                                /* 54 — 10-10 + 9-9: kicker A from 4 candidates */
                                Arguments.of("Two Pair 10-10+9-9: 4 kicker candidates — picks A(15)",
                                        List.of(c(10,"Cups"),   c(10,"Swords")),
                                        List.of(c(9,"Cups"),    c(9,"Swords"),     c(15,"Wands"),
                                                c(14,"Pentacles"), c(2,"Cups"),   c(3,"Swords")),
                                        List.of(3, 10, 9, 15)),

                                /* 55 — A-A + 3-3: kicker K from 4 candidates */
                                Arguments.of("Two Pair A-A+3-3: 4 kicker candidates {K,Page,2,4} — picks K(14)",
                                        List.of(c(15,"Cups"),   c(15,"Swords")),
                                        List.of(c(3,"Cups"),    c(3,"Swords"),     c(14,"Wands"),
                                                c(11,"Pentacles"), c(2,"Cups"),   c(4,"Swords")),
                                        List.of(3, 15, 3, 14)),

                                /* 56 — board has both pairs, hole junk; board plays */
                                Arguments.of("Two Pair: A-A+K-K on board, hole junk — picks Q(13) kicker",
                                        List.of(c(2,"Cups"),    c(3,"Swords")),
                                        List.of(c(15,"Wands"),  c(15,"Pentacles"), c(14,"Cups"),
                                                c(14,"Swords"), c(13,"Wands"),     c(11,"Pentacles")),
                                        List.of(3, 15, 14, 13))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("twoPairCases")
                void twoPair(String desc, List<MinorArcanaCard> hole,
                             List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // ONE PAIR → [2, pairPower, k1, k2, k3]                   tests 57-63
                // With 8 cards: 6 non-pair kicker candidates; evaluator picks best 3.
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> onePairCases() {
                        return Stream.of(
                                /* 57 — pair Aces, 6 kicker candidates: picks K Q Page */
                                Arguments.of("Pair Aces: 6 kicker candidates {K,Q,Page,9,2,3} — picks K Q Page",
                                        List.of(c(15,"Cups"),   c(15,"Swords")),
                                        List.of(c(14,"Wands"),  c(13,"Pentacles"), c(11,"Cups"),
                                                c(9,"Swords"),  c(2,"Wands"),      c(3,"Pentacles")),
                                        List.of(2, 15, 14, 13, 11)),

                                /* 58 — pair Kings, 6 kicker candidates: picks A Q Page */
                                Arguments.of("Pair Kings: 6 kicker candidates {A,Q,Page,9,2,3} — picks A Q Page",
                                        List.of(c(14,"Cups"),   c(14,"Swords")),
                                        List.of(c(15,"Wands"),  c(13,"Pentacles"), c(11,"Cups"),
                                                c(9,"Swords"),  c(2,"Wands"),      c(3,"Pentacles")),
                                        List.of(2, 14, 15, 13, 11)),

                                /* 59 — pair Queens, 6 kicker candidates: picks A K Page */
                                Arguments.of("Pair Queens(13): 6 kicker candidates {A,K,Page,9,2,3} — picks A K Page",
                                        List.of(c(13,"Cups"),   c(13,"Swords")),
                                        List.of(c(15,"Wands"),  c(14,"Pentacles"), c(11,"Cups"),
                                                c(9,"Swords"),  c(2,"Wands"),      c(3,"Pentacles")),
                                        List.of(2, 13, 15, 14, 11)),

                                /* 60 — pair 9s, 6 kicker candidates: picks A K Q */
                                Arguments.of("Pair 9s: 6 kicker candidates {A,K,Q,Page,7,2} — picks A K Q",
                                        List.of(c(9,"Cups"),    c(9,"Swords")),
                                        List.of(c(15,"Wands"),  c(14,"Pentacles"), c(13,"Cups"),
                                                c(11,"Swords"), c(7,"Wands"),      c(2,"Pentacles")),
                                        List.of(2, 9, 15, 14, 13)),

                                /* 61 — pair 7s, 6 kicker candidates: picks A K Page */
                                Arguments.of("Pair 7s: 6 kicker candidates {A,K,Page,9,2,3} — picks A K Page",
                                        List.of(c(7,"Cups"),    c(7,"Swords")),
                                        List.of(c(15,"Wands"),  c(14,"Pentacles"), c(11,"Cups"),
                                                c(9,"Swords"),  c(2,"Wands"),      c(3,"Pentacles")),
                                        List.of(2, 7, 15, 14, 11)),

                                /* 62 — pair 5s (1 from hole + 1 from table), 6 kicker candidates */
                                Arguments.of("Pair 5s (hole+table): 6 kicker candidates — picks A K Q",
                                        List.of(c(5,"Cups"),    c(15,"Swords")),
                                        List.of(c(5,"Wands"),   c(14,"Pentacles"), c(13,"Cups"),
                                                c(11,"Swords"), c(2,"Wands"),      c(3,"Pentacles")),
                                        List.of(2, 5, 15, 14, 13)),

                                /* 63 — pair 3s on board, 6 kicker candidates: picks A K Q */
                                Arguments.of("Pair 3s on board: 6 kicker candidates — picks A(15) K(14) Q(13)",
                                        List.of(c(3,"Cups"),    c(15,"Swords")),
                                        List.of(c(3,"Wands"),   c(14,"Pentacles"), c(13,"Cups"),
                                                c(11,"Swords"), c(9,"Wands"),      c(2,"Pentacles")),
                                        List.of(2, 3, 15, 14, 13))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("onePairCases")
                void onePair(String desc, List<MinorArcanaCard> hole,
                             List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }

                // ════════════════════════════════════════════════════════════════════
                // HIGH CARD → [1, c1, c2, c3, c4, c5] desc                tests 64-70
                //
                // With 8 cards: 3 are dropped, best 5 returned.
                // Verified per test:
                //   • No pair (all powers unique)
                //   • No flush (max 2 same-suit across 8 cards)
                //   • No straight (no 5 consecutive powers in 8-card set)
                //   • Comment names which 3 cards are dropped
                // ════════════════════════════════════════════════════════════════════

                static Stream<Arguments> highCardCases() {
                        return Stream.of(
                                /* 64 — 8 cards {2,3,7,9,11,13,14,15}: drops 2,3,7 → A-K-Q-Page-9 */
                                Arguments.of("High Card: 8 cards — drops 2,3,7 → A(15)-K(14)-Q(13)-Page(11)-9",
                                        List.of(c(15,"Cups"),   c(14,"Swords")),
                                        List.of(c(13,"Wands"),  c(11,"Pentacles"), c(9,"Cups"),
                                                c(7,"Swords"),  c(3,"Wands"),      c(2,"Pentacles")),
                                        // No straight: {9,11,13,14,15} gap at 10,12 ✓
                                        List.of(1, 15, 14, 13, 11, 9)),

                                /* 65 — 8 cards {2,3,5,7,9,13,14,15}: drops 2,3,5 → A-K-Q-9-7 */
                                Arguments.of("High Card: 8 cards — drops 2,3,5 → A(15)-K(14)-Q(13)-9-7",
                                        List.of(c(15,"Cups"),   c(7,"Swords")),
                                        List.of(c(14,"Wands"),  c(13,"Pentacles"), c(9,"Cups"),
                                                c(5,"Swords"),  c(3,"Wands"),      c(2,"Pentacles")),
                                        List.of(1, 15, 14, 13, 9, 7)),

                                /* 66 — 8 cards {2,3,5,7,9,11,13,14}: drops 2,3,5 → K-Q-Page-9-7 */
                                Arguments.of("High Card: 8 cards — drops 2,3,5 → K(14)-Q(13)-Page(11)-9-7",
                                        List.of(c(14,"Cups"),   c(9,"Swords")),
                                        List.of(c(13,"Wands"),  c(11,"Pentacles"), c(7,"Cups"),
                                                c(5,"Swords"),  c(3,"Wands"),      c(2,"Pentacles")),
                                        List.of(1, 14, 13, 11, 9, 7)),

                                /* 67 — 8 cards {2,3,5,7,9,11,13,15}: drops 2,3,5 → A-Q-Page-9-7 */
                                Arguments.of("High Card: 8 cards (no A+K) — drops 2,3,5 → A(15)-Q(13)-Page(11)-9-7",
                                        List.of(c(15,"Cups"),   c(9,"Swords")),
                                        List.of(c(13,"Wands"),  c(11,"Pentacles"), c(7,"Cups"),
                                                c(5,"Swords"),  c(3,"Wands"),      c(2,"Pentacles")),
                                        // {7,9,11,13,15} all odd → no straight ✓
                                        List.of(1, 15, 13, 11, 9, 7)),

                                /* 68 — 8 cards {2,3,6,8,11,13,14,15}: drops 2,3,6 → A-K-Q-Page-8 */
                                Arguments.of("High Card: 8 cards — drops 2,3,6 → A(15)-K(14)-Q(13)-Page(11)-8",
                                        List.of(c(15,"Cups"),   c(8,"Swords")),
                                        List.of(c(14,"Wands"),  c(13,"Pentacles"), c(11,"Cups"),
                                                c(6,"Swords"),  c(3,"Wands"),      c(2,"Pentacles")),
                                        List.of(1, 15, 14, 13, 11, 8)),

                                /* 69 — 8 cards {2,3,5,7,9,11,13,14}: verifies exactly 3 dropped */
                                Arguments.of("High Card: 8 cards — drops 2,3,5 → K(14)-Q(13)-Page(11)-9-7",
                                        List.of(c(14,"Cups"),   c(5,"Swords")),
                                        List.of(c(13,"Wands"),  c(11,"Pentacles"), c(9,"Cups"),
                                                c(7,"Swords"),  c(3,"Wands"),      c(2,"Pentacles")),
                                        List.of(1, 14, 13, 11, 9, 7)),

                                /* 70 — 8 cards {2,3,6,8,11,13,14,15} dropping three confirming kicker order */
                                Arguments.of("High Card: 8 cards — drops 2,3,6 → A(15)-K(14)-Q(13)-Page(11)-8",
                                        List.of(c(15,"Cups"),   c(13,"Swords")),
                                        List.of(c(14,"Wands"),  c(11,"Pentacles"), c(8,"Cups"),
                                                c(6,"Swords"),  c(3,"Wands"),      c(2,"Pentacles")),
                                        List.of(1, 15, 14, 13, 11, 8))
                        );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("highCardCases")
                void highCard(String desc, List<MinorArcanaCard> hole,
                              List<MinorArcanaCard> table, List<Integer> expected) {
                        assertEquals(expected, score(hole, table), desc);
                }
        }
}
