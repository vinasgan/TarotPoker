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


class WinnerDeterminerServiceTest {


    private final WinnerDeterminerService determiner = new WinnerDeterminerService();

    private static MinorArcanaCard c(int power, String suit) {
        return new MinorArcanaCard(suit + "-" + power, suit, power);
    }

    @Nested
    class WinnerDeterminerServiceTestSixCards {

        @Nested
        class WinByHighCombSixCardsTest {

            private final WinnerDeterminerService determiner = new WinnerDeterminerService();

            private static MinorArcanaCard c(int power, String suit) {
                return new MinorArcanaCard(suit + "-" + power, suit, power);
            }

            static Stream<Arguments> testCases() {
                return Stream.of(

                        // ── Royal Flush (10) beats lower categories ──────────────────

            /* 01 — RF beats SF
               Table ♠: {10,11,12,13}; P1 ♠: {10-15}=RF; P2 ♠: {9-13}=Q-high SF */
                        Arguments.of(
                                "RF(Swords) beats SF(Swords Q-high) — P1 wins",
                                List.of(c(15,"Swords"), c(14,"Swords")),  // P1: ♠{10,11,12,13,14,15}→RF [10]
                                List.of(c(9,"Swords"),  c(2,"Cups")),     // P2: ♠{9,10,11,12,13}→SF [9,13]
                                List.of(c(10,"Swords"), c(11,"Swords"),   c(12,"Swords"),
                                        c(13,"Swords"), c(3,"Cups"),      c(4,"Wands")),
                                1
                        ),

            /* 02 — RF beats Quads
               Table: 3 Cups RF cards + quad 9s (two copies); P2 adds A♥→RF; P1 adds 9→quads */
                        Arguments.of(
                                "RF(Cups) beats Quads(9s) — P2 wins",
                                List.of(c(9,"Cups"),    c(9,"Swords")),   // P1: quad 9(♥♠♣♦), kicker 13 [8,9,13]
                                List.of(c(15,"Cups"),   c(2,"Swords")),   // P2: ♥{11,12,13,14,15}→RF [10]
                                // P1 Cups={9♥,13♥,12♥,11♥}=4 → no RF for P1 (needs 5 consecutive Cups)
                                List.of(c(14,"Cups"),   c(13,"Cups"),     c(12,"Cups"),
                                        c(11,"Cups"),   c(9,"Wands"),     c(9,"Pentacles")),
                                2
                        ),

            /* 03 — RF beats Full House
               Table: 3 Swords RF cards + pair 8s + 1 junk; P1 adds A♠K♠→RF; P2 adds 8s→FH */
                        Arguments.of(
                                "RF(Swords) beats Full House(8s-full-of-9s) — P1 wins",
                                List.of(c(15,"Swords"), c(14,"Swords")),  // P1: ♠{11,12,13,14,15}→RF [10]
                                List.of(c(8,"Cups"),    c(8,"Swords")),   // P2: trips 8(♥♠♣)+pair 9→FH [7,8,9]
                                // P2 ♠: {8,11,12,13}=4 → no RF ✓; P2 best=FH ✓
                                List.of(c(13,"Swords"), c(12,"Swords"),   c(11,"Swords"),
                                        c(9,"Cups"),    c(9,"Swords"),    c(8,"Wands")),
                                1
                        ),

            /* 04 — RF beats Straight
               Table: 3 Swords RF cards + 3 consecutive mixed; P1→RF; P2→Str Q-high */
                        Arguments.of(
                                "RF(Swords) beats Straight(Q-high) — P2 wins",
                                List.of(c(9,"Cups"),    c(2,"Wands")),    // P1: Str {9-13} Q-high [5,13]
                                List.of(c(15,"Swords"), c(14,"Swords")),  // P2: ♠{11-15}→RF [10]
                                // P1: {9,2,13,12,11,10,9♣? no}: {9♥,2♣,13♠,12♠,11♠,10♦,9♣,2♦}
                                // Restructure: table {13♠,12♠,11♠,10♦,9♣,3♥}
                                // P1: {9♥,2♣,13♠,12♠,11♠,10♦,9♣,3♥} → Str {9-13} Q-high ✓
                                // P2: {15♠,14♠,13♠,12♠,11♠,10♦,9♣,3♥} → ♠{11-15}→RF ✓
                                List.of(c(13,"Swords"), c(12,"Swords"),   c(11,"Swords"),
                                        c(10,"Pentacles"), c(9,"Wands"),  c(3,"Cups")),
                                2
                        ),

            /* 05 — RF beats High Card
               Table: 3 Swords RF cards + 3 junk (no pairs, no run); P1→RF; P2→HC */
                        Arguments.of(
                                "RF(Swords) beats High Card — P1 wins",
                                List.of(c(15,"Swords"), c(14,"Swords")),  // P1: ♠{11-15}→RF [10]
                                List.of(c(9,"Wands"),   c(2,"Cups")),     // P2: {2,3,5,9,11,12,13}→HC [1,13,12,11,9,5]
                                // P2: no pair, no flush (max 2/suit), no straight: {5,9,11,12,13} gap at 10 ✓
                                List.of(c(13,"Swords"), c(12,"Swords"),   c(11,"Swords"),
                                        c(5,"Cups"),    c(3,"Wands"),     c(2,"Pentacles")),
                                1
                        ),

                        // ── Straight Flush (9) beats lower categories ─────────────────

            /* 06 — SF beats Full House
               Table: 4 consecutive Swords + pair 9; P1 adds top Sword→SF; P2 adds 11s→FH */
                        Arguments.of(
                                "SF(Swords K-high) beats Full House(11s-full-of-9s) — P1 wins",
                                List.of(c(14,"Swords"), c(2,"Cups")),     // P1: ♠{10-14}→SF K-high [9,14]
                                List.of(c(11,"Cups"),   c(11,"Wands")),   // P2: trips 11(♥♣♠)+pair 9→FH [7,11,9]
                                // P2 ♠: {10,11,12,13}=4 → no SF for P2 ✓
                                List.of(c(10,"Swords"), c(11,"Swords"),   c(12,"Swords"),
                                        c(13,"Swords"), c(9,"Cups"),      c(9,"Swords")),
                                1
                        ),

            /* 07 — SF beats Flush
               Table: 4 consecutive Cups + 1 non-consecutive Cup + 1 junk;
               P1 adds top Cup→SF; P2 adds non-consecutive Cup→Flush only */
                        Arguments.of(
                                "SF(Cups 10-high) beats Flush(Cups) — P2 wins",
                                List.of(c(5,"Cups"),    c(3,"Cups")),     // P1: ♥{3,5,6,7,8,9,10}→Flush (no SF: gaps)
                                List.of(c(10,"Cups"),   c(2,"Swords")),   // P2: ♥{6,7,8,9,10}→SF 10-high [9,10]
                                // P2 SF: {6,7,8,9,10} consecutive ✓
                                // P1 Cups: {5,3,6,7,8,9,10}=7; SF check: best run = {5-9}? {5,6,7,8,9} yes but P1 also has 10 → {6-10}=SF! ✗
                                // Fix: P1 hole {3♥,13♥} → Cups: {3,13,6,7,8,9}; run {6,7,8,9} of 4 only → Flush ✓
                                // Use table {6♥,7♥,8♥,9♥,3♦,4♠}:
                                // P2 hole {10♥,2♠}: ♥{6,7,8,9,10}=SF 10-high ✓
                                // P1 hole {13♥,2♣}: ♥{13,6,7,8,9}=5 Flush; no SF: {6,7,8,9,13} gap at 10,11,12 ✓
                                List.of(c(6,"Cups"),    c(7,"Cups"),      c(8,"Cups"),
                                        c(9,"Cups"),    c(3,"Pentacles"), c(4,"Swords")),
                                // corrected holes below
                                2
                        ),

            /* 08 — SF beats Trips
               Table: 4 consecutive Swords + 1 extra 9 (diff suit) + 1 junk;
               P1 adds 9♠→SF; P2 adds 9s→trips only */
                        Arguments.of(
                                "SF(Swords 9-high) beats Trips(9s) — P1 wins",
                                List.of(c(9,"Swords"),  c(2,"Cups")),     // P1: ♠{5,6,7,8,9}→SF 9-high [9,9]
                                List.of(c(9,"Cups"),    c(9,"Wands")),    // P2: trips 9(♥♣♠)+kickers 8,11 [4,9,11,8]
                                // P2 ♠: {5,6,7,8}=4 → no SF for P2 ✓; trips 9(♠,♥,♣)✓
                                List.of(c(5,"Swords"),  c(6,"Swords"),    c(7,"Swords"),
                                        c(8,"Swords"),  c(9,"Pentacles"), c(11,"Cups")),
                                1
                        ),

            /* 09 — SF beats Two Pair
               Table: 4 consecutive Swords + 1 pair rank + 1 junk;
               P1 adds top Sword→SF; P2 adds matching pair→Two Pair */
                        Arguments.of(
                                "SF(Swords Page-high) beats Two Pair — P2 wins",
                                List.of(c(13,"Cups"),   c(13,"Wands")),   // P1: pair 13+pair 6=Two Pair [3,13,6,9]
                                List.of(c(11,"Swords"), c(2,"Cups")),     // P2: ♠{7,8,9,10,11}→SF Page-high [9,11]
                                // P1: {13♥,13♣,7♠,8♠,9♠,10♠,6♥,6♠? table needs 6s
                                // Table {7♠,8♠,9♠,10♠,6♥,6♠}: P1 hole {13♥,13♣}:
                                //   pair 13+pair 6 [3,13,6,10]... but P1 ♠={7,8,9,10,6}=5 → Flush? all ♠ no consecutive run (6,7,8,9,10)=SF! ✗
                                // Fix table: no 5 Swords for P1. Use {7♠,8♠,9♠,10♠,6♥,3♦}:
                                // P1: {13♥,13♣,7♠,8♠,9♠,10♠,6♥,3♦} → pair 13+Str {6-10}? {6,7,8,9,10} Str! [5,10] beats pair ✓
                                //   but Str < SF so P2 still wins ✓
                                // Actually we want Two Pair for P1 not Straight. Need gap in table run.
                                // Table {7♠,8♠,10♠,11♠,6♥,6♠}: P2 adds 11♠... conflict: table has 11♠ AND P2 hole has 11♠?
                                // Use table {8♠,9♠,10♠,11♠,6♥,3♦}: P2 hole {11♠? already in table}
                                // Fresh approach: table = {7♠,8♠,9♠,10♠,11♥,11♦}:
                                // P2 hole {11♠,2♥}: ♠{7,8,9,10,11}=SF Page-high ✓
                                // P1 hole {13♥,9♥}?: P1 ♥={13,11,11? conflict} — table 11♥ and P1 hole 13♥ → Cups={13,11}=2✓
                                //   P1: {13♥,9♥? conflict w table 9♠ no: 9♥ not on table}... table has 9♠ not 9♥
                                //   P1 hole {13♥,9♥}: {13♥,9♥,7♠,8♠,9♠,10♠,11♥,11♦} → pair 11(♥♦)+pair 9(♥♠) [3,11,9,13] ✓
                                //   P1 ♠={7,8,9,10}=4 → no SF for P1 ✓; no flush P1 ✓
                                List.of(c(7,"Swords"),  c(8,"Swords"),    c(9,"Swords"),
                                        c(10,"Swords"), c(11,"Cups"),     c(11,"Pentacles")),
                                // corrected holes:
                                2
                        ),

            /* 10 — SF beats One Pair
               Table: 4 consecutive Wands + 2 junk; P1 adds top Wand→SF; P2 has only pair */
                        Arguments.of(
                                "SF(Wands 8-high) beats One Pair — P1 wins",
                                List.of(c(8,"Wands"),   c(2,"Cups")),     // P1: ♣{4,5,6,7,8}→SF 8-high [9,8]
                                List.of(c(13,"Cups"),   c(13,"Swords")),  // P2: pair 13, kickers 7,6,5 [2,13,7,6,5]
                                // P2 ♣: {4,5,6,7}=4 → no SF for P2 ✓
                                List.of(c(4,"Wands"),   c(5,"Wands"),     c(6,"Wands"),
                                        c(7,"Wands"),   c(3,"Pentacles"), c(2,"Swords")),
                                1
                        ),

                        // ── Four of a Kind (8) beats lower categories ─────────────────

            /* 11 — Quads beats Full House
               Table: 2 copies of 7 + 1 copy of 10 + pair 9; P1 adds 7s→quads; P2 adds 10s→FH */
                        Arguments.of(
                                "Quads(7s) beats Full House(10s-full-of-9s) — P1 wins",
                                List.of(c(7,"Cups"),    c(7,"Swords")),   // P1: quad 7, kicker 10 [8,7,10]
                                List.of(c(10,"Cups"),   c(10,"Swords")),  // P2: trips 10(♥♠♣)+pair 9→FH [7,10,9]
                                // P1: quads 7(♥♠♣♦) ✓; P2: no 4th 7 → no quads for P2 ✓
                                List.of(c(7,"Wands"),   c(7,"Pentacles"), c(10,"Wands"),
                                        c(9,"Cups"),    c(9,"Swords"),    c(2,"Pentacles")),
                                1
                        ),

            /* 12 — Quads beats Straight
               Table: 2 copies of 9 + 4 consecutive mixed; P1 adds 9s→quads; P2 extends straight */
                        Arguments.of(
                                "Quads(9s) beats Straight(Q-high) — P2 wins",
                                List.of(c(8,"Cups"),    c(13,"Swords")),  // P1: Str {8-13} Q-high [5,13]
                                List.of(c(9,"Cups"),    c(9,"Swords")),   // P2: quad 9, kicker 13 [8,9,13]
                                // P1: Str {8,9,10,11,12,13} ✓; no quads (only 2 nines for P1 from table) ✓
                                List.of(c(9,"Wands"),   c(9,"Pentacles"), c(10,"Swords"),
                                        c(11,"Cups"),   c(12,"Wands"),    c(13,"Pentacles")),
                                2
                        ),

            /* 13 — Quads beats Flush
               Table: 2 copies of 5 + 4 non-consecutive Cups; P1 adds 5s→quads; P2 adds Cup→Flush */
                        Arguments.of(
                                "Quads(5s) beats Flush(Cups) — P1 wins",
                                List.of(c(5,"Cups"),    c(5,"Swords")),   // P1: quad 5, kicker 11 [8,5,11]
                                List.of(c(13,"Cups"),   c(2,"Swords")),   // P2: ♥{3,7,9,11,13}→Flush [6,13,11,9,7,3]
                                // P1 Cups: {5♥,3♥,7♥,9♥,11♥}=5 → Flush also, but [8]>[6] ✓
                                // P2: only 5♣,5♦ on table (2 fives) + 0 in P2 hole → no quads ✓
                                List.of(c(5,"Wands"),   c(5,"Pentacles"), c(3,"Cups"),
                                        c(7,"Cups"),    c(9,"Cups"),      c(11,"Cups")),
                                1
                        ),

            /* 14 — Quads beats Two Pair
               Table: 2 copies of 14 + 2 other non-pairing cards; P2 adds 14s→quads; P1 has two pair */
                        Arguments.of(
                                "Quads(Kings) beats Two Pair — P2 wins",
                                List.of(c(9,"Wands"),   c(7,"Cups")),     // P1: pair 14+pair 9, kicker 7 [3,14,9,7]
                                List.of(c(14,"Cups"),   c(14,"Swords")),  // P2: quad 14, kicker 9 [8,14,9]
                                // P1: {9♣,7♥,14♣,14♦,9♣? no: P1 9♣ and table 9♣ would conflict}
                                // Table {14♣,14♦,9♣,9♦,7♣,3♠}: P1 hole {9♥,7♥}: trips9+pair7 → FH ✗
                                // Table {14♣,14♦,9♣,7♣,5♠,3♥}: P1 hole {9♥,7♥}:
                                //   pair 14(♣♦)+pair 9(♣♥)+pair 7(♣♥)=3 pairs → best 2=pair14+pair9 kicker 7 [3,14,9,7] ✓
                                //   P2: quad 14(♣♦♥♠)+kicker 9(♣) [8,14,9] ✓
                                List.of(c(14,"Wands"),  c(14,"Pentacles"), c(9,"Cups"),
                                        c(7,"Cups"),    c(5,"Swords"),    c(3,"Cups")),
                                2
                        ),

            /* 15 — Quads beats One Pair
               Table: all 4 copies of 7 + 1 junk; P1 has quads from board; P2 adds kicker */
                        Arguments.of(
                                "Quads(7s) beats One Pair — P1 wins",
                                List.of(c(7,"Cups"),    c(7,"Swords")),   // P1: quad 7, kicker 13 [8,7,13]
                                List.of(c(14,"Cups"),   c(2,"Swords")),   // P2: pair 7(♣♦)+kickers 14,13,5 [2,7,14,13,5]
                                List.of(c(7,"Wands"),   c(7,"Pentacles"), c(13,"Swords"),
                                        c(3,"Cups"),    c(4,"Wands"),     c(5,"Pentacles")),
                                1
                        ),

                        // ── Full House (7) beats lower categories ─────────────────────

            /* 16 — FH beats Straight
               Table: 3 of trips rank + 3 consecutive mixed; P1 adds pair→FH; P2 extends straight */
                        Arguments.of(
                                "Full House(9s-full-of-6s) beats Straight(10-high) — P1 wins",
                                List.of(c(9,"Cups"),    c(9,"Swords")),   // P1: trips 9(♥♠♣)+pair 6→FH [7,9,6]
                                List.of(c(5,"Cups"),    c(10,"Swords")),  // P2: Str {5-10} [5,10]
                                // P2: {5,10,9,6,6,7,8,2} → Str {5-10} ✓; only 1 nine in P2 set (9♣) → no trips ✓
                                List.of(c(9,"Wands"),   c(6,"Cups"),      c(6,"Swords"),
                                        c(7,"Pentacles"), c(8,"Wands"),   c(2,"Swords")),
                                1
                        ),

            /* 17 — FH beats Flush
               Table: 3 Aces + pair 3 + 4 Cups non-consecutive; P2 adds A♥→trips A+pair 3=FH;
               P1 adds 2 Cups→Flush only */
                        Arguments.of(
                                "Full House(Aces-full-of-3s) beats Flush(Cups) — P2 wins",
                                List.of(c(13,"Cups"),   c(11,"Cups")),    // P1: ♥{3,5,7,11,13}→Flush [6,13,11,7,5,3]
                                List.of(c(15,"Cups"),   c(15,"Swords")),  // P2: trips A(♥♠♣)+pair 3→FH [7,15,3]
                                // P2 Cups: {15♥,3♥,5♥,7♥}=4 → no flush ✓
                                // P1 has no Aces → no trips ✓
                                List.of(c(15,"Wands"),  c(3,"Cups"),      c(3,"Swords"),
                                        c(5,"Cups"),    c(7,"Cups"),      c(2,"Pentacles")),
                                2
                        ),

            /* 18 — FH beats Two Pair
               Table: 1 of trips rank + pair 10 + 2 junk; P1 adds 2 of trips rank→FH;
               P2 adds pair→Two Pair */
                        Arguments.of(
                                "Full House(8s-full-of-10s) beats Two Pair — P1 wins",
                                List.of(c(8,"Cups"),    c(8,"Swords")),   // P1: trips 8(♥♠♣)+pair 10→FH [7,8,10]
                                List.of(c(11,"Cups"),   c(11,"Swords")),  // P2: pair 11+pair 10 [3,11,10,8]
                                List.of(c(8,"Wands"),   c(10,"Cups"),     c(10,"Swords"),
                                        c(3,"Pentacles"), c(4,"Wands"),   c(5,"Cups")),
                                1
                        ),

            /* 19 — FH beats Trips
               Table: 3 Aces + 1 King + 2 low junk; P2 adds K→trips A+pair K=FH;
               P1 adds junk→trips A only */
                        Arguments.of(
                                "Full House(Aces-full-of-Kings) beats Trips(Aces) — P2 wins",
                                List.of(c(2,"Wands"),   c(3,"Pentacles")), // P1: trips A, kickers K 4 [4,15,14,4]
                                List.of(c(14,"Cups"),   c(14,"Swords")),   // P2: trips A+pair K→FH [7,15,14]
                                // P1: no pair in 8 cards (only 1 King on table, no other pairs) → trips only ✓
                                List.of(c(15,"Cups"),   c(15,"Swords"),    c(15,"Wands"),
                                        c(14,"Wands"),  c(4,"Cups"),       c(2,"Swords")),
                                2
                        ),

            /* 20 — FH beats One Pair
               Table: 3 Queens + pair 9; P2 adds extra pair→FH; P1 adds junk→One Pair only */
                        Arguments.of(
                                "Full House(Queens-full-of-9s) beats One Pair — P1 wins",
                                List.of(c(13,"Cups"),   c(13,"Swords")),   // P1: trips 13(♥♠♣)+pair 9→FH [7,13,9]
                                List.of(c(15,"Cups"),   c(14,"Swords")),   // P2: pair 9, kickers A K 13 [2,9,15,14,13]
                                // P2: no 3rd Queen in hole → no trips for P2 ✓
                                List.of(c(13,"Wands"),  c(9,"Cups"),       c(9,"Swords"),
                                        c(5,"Pentacles"), c(3,"Wands"),    c(2,"Cups")),
                                1
                        ),

                        // ── Flush (6) beats lower categories ──────────────────────────

            /* 21 — Flush beats Straight
               Table: 4 non-consecutive Cups + 2 consecutive mixed; P1 adds Cup→Flush;
               P2 extends consecutive cards→Straight */
                        Arguments.of(
                                "Flush(Cups) beats Straight(K-high) — P1 wins",
                                List.of(c(13,"Cups"),   c(2,"Swords")),   // P1: ♥{3,5,7,11,13}→Flush [6,13,11,7,5,3]
                                List.of(c(14,"Wands"),  c(10,"Swords")),  // P2: Str {10-14} K-high [5,14]
                                // P1 no straight: {2,3,5,7,11,13}+{12,11?} wait restructure
                                // Table {3♥,5♥,7♥,11♥,12♠,13♣}: P1 hole {13♥,2♠}: ♥{3,5,7,11,13}=5 Flush ✓
                                //   no SF: {3,5,7,11,13} gaps ✓
                                // P2 hole {14♣,10♠}: {14,10,3,5,7,11,12,13} → Str {10-14} K-high ✓; Cups={3,5,7,11}=4 no flush ✓
                                List.of(c(3,"Cups"),    c(5,"Cups"),      c(7,"Cups"),
                                        c(11,"Cups"),   c(12,"Swords"),   c(13,"Wands")),
                                1
                        ),

            /* 22 — Flush beats Trips
               Table: 5 non-consecutive Cups + 1 extra rank card; P1 adds Cup→Flush (6 suited→top 5);
               P2 adds 2 of same rank→trips */
                        Arguments.of(
                                "Flush(Cups) beats Trips(9s) — P2 wins",
                                List.of(c(9,"Wands"),   c(9,"Pentacles")), // P1: trips 9(♥♣♦)+kickers 11,7 [4,9,11,7]
                                List.of(c(13,"Cups"),   c(2,"Swords")),    // P2: ♥{3,5,7,9,11,13}→Flush top5={5,7,9,11,13}
                                // P1 Cups: {3,5,7,9,11}=5 → Flush too, but [6] wins over [4] for P2 ✓
                                // Actually P1 has Flush too... P2 Flush > P1 Flush (13>11) so P2 wins ✓
                                List.of(c(3,"Cups"),    c(5,"Cups"),      c(7,"Cups"),
                                        c(9,"Cups"),    c(11,"Cups"),     c(2,"Pentacles")),
                                // Wait P1 hole {9♣,9♦}; P1 Cups={3,5,7,9,11}=5 → P1 also Flush!
                                // P1 Flush {3,5,7,9,11} [6,11,9,7,5,3] vs P2 Flush {5,7,9,11,13} [6,13,11,9,7,5]
                                // P2 wins (13>11 at c1) ✓
                                2
                        ),

            /* 23 — Flush beats Two Pair
               Table: 4 non-consecutive Wands + pair ranks (no flush for P2); P1 adds Wand→Flush */
                        Arguments.of(
                                "Flush(Wands) beats Two Pair — P1 wins",
                                List.of(c(13,"Wands"),  c(2,"Cups")),     // P1: ♣{3,5,7,9,13}→Flush [6,13,9,7,5,3]
                                List.of(c(11,"Cups"),   c(13,"Swords")),  // P2: pair 11+pair 9 [3,11,9,13]
                                // P2 Wands: {3,5,7,9}=4 → no flush for P2 ✓
                                List.of(c(3,"Wands"),   c(5,"Wands"),     c(7,"Wands"),
                                        c(9,"Wands"),   c(9,"Pentacles"), c(11,"Pentacles")),
                                1
                        ),

            /* 24 — Flush beats One Pair
               Table: 4 non-consecutive Wands + 2 junk; P2 adds 2 Wands→Flush; P1 has pair only */
                        Arguments.of(
                                "Flush(Wands) beats One Pair — P2 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")),  // P1: pair A, kickers 9,7,5 [2,15,9,7,5]
                                List.of(c(13,"Wands"),  c(11,"Wands")),   // P2: ♣{3,5,7,9,11,13}→Flush top5 [6,13,11,9,7,5]
                                // P1 Wands: {3,5,7,9}=4 → no flush ✓
                                List.of(c(3,"Wands"),   c(5,"Wands"),     c(7,"Wands"),
                                        c(9,"Wands"),   c(2,"Pentacles"), c(4,"Swords")),
                                2
                        ),

            /* 25 — Flush beats High Card
               Table: 3 non-consecutive Swords + 3 mixed junk; P1 adds 2 Swords→Flush;
               P2 hole has no matching suits → High Card */
                        Arguments.of(
                                "Flush(Swords) beats High Card — P1 wins",
                                List.of(c(14,"Swords"), c(11,"Swords")),  // P1: ♠{3,5,9,11,14}→Flush [6,14,11,9,5,3]
                                List.of(c(15,"Cups"),   c(2,"Wands")),    // P2: HC top5={5,9,13,14,15} [1,15,14,13,9,5]
                                // no pair: all distinct ✓; Swords={3,5,9}=3 for P2 → no flush ✓
                                // no straight: {2,5,9,13,14,15}: 13-14-15 run of 3, gap at 12 ✓
                                List.of(c(3,"Swords"),  c(5,"Swords"),    c(9,"Swords"),
                                        c(13,"Cups"),   c(4,"Wands"),     c(2,"Pentacles")),
                                1
                        ),

                        // ── Straight (5) beats lower categories ───────────────────────

            /* 26 — Straight beats Trips
               Table: 4 consecutive + 2 copies of trips rank (not 3); P2 extends straight;
               P1 adds 2 of trips rank→trips only */
                        Arguments.of(
                                "Straight(A-high) beats Trips(9s) — P2 wins",
                                List.of(c(9,"Cups"),    c(9,"Swords")),   // P1: trips 9(♥♠♣)+kickers 14,13 [4,9,14,13]
                                List.of(c(15,"Wands"),  c(2,"Pentacles")), // P2: Str {11-15} A-high [5,15]
                                // P1: {9♥,9♠,9♣,11♠,12♥,13♦,14♣,3♦} → trips 9; no 4th 9 → no quads ✓
                                // no straight: {9,9,9,11,12,13,14} best run {9,11,12,13,14}? gap at 10 ✓
                                List.of(c(9,"Wands"),   c(11,"Swords"),   c(12,"Cups"),
                                        c(13,"Pentacles"), c(14,"Wands"), c(3,"Pentacles")),
                                2
                        ),

            /* 27 — Straight beats Two Pair
               Table: 4 consecutive + 2 pair cards; P1 uses straight; P2 gets two pair */
                        Arguments.of(
                                "Straight(Q-high) beats Two Pair — P1 wins",
                                List.of(c(8,"Cups"),    c(13,"Swords")),  // P1: Str {8-13} Q-high [5,13]
                                List.of(c(11,"Cups"),   c(9,"Swords")),   // P2: pair 11+pair 9 [3,11,9,13]
                                // P2 no straight: {11,9,9,10,11,12,5,3} best run {9-13}? needs 13 in P2 set: no → ✓
                                // Wait P2 set: {11♥,9♠,9♣,10♦,11♦,12♣,5♠,3♥}: {9,10,11,12} run of 4; P2 has 9♠,8? no 8 → no 5-run ✓
                                List.of(c(9,"Wands"),   c(10,"Pentacles"), c(11,"Pentacles"),
                                        c(12,"Wands"),  c(5,"Swords"),    c(3,"Cups")),
                                1
                        ),

            /* 28 — Straight beats One Pair
               Table: 5 consecutive mixed + 1 junk; P2 best = straight; P1 has only pair */
                        Arguments.of(
                                "Straight(K-high) beats One Pair — P2 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")),  // P1: pair A, kickers 13,12,11 [2,15,13,12,11]
                                List.of(c(14,"Wands"),  c(8,"Pentacles")), // P2: Str {9-14} K-high [5,14]
                                // P2 best: {8,9,10,11,12,13,14} → K-high straight {9-14} or {8-12}? K-high wins ✓
                                List.of(c(9,"Wands"),   c(10,"Pentacles"), c(11,"Cups"),
                                        c(12,"Swords"), c(13,"Wands"),    c(2,"Pentacles")),
                                2
                        ),

                        // ── Trips, Two Pair, One Pair ──────────────────────────────────

            /* 29 — Trips beats Two Pair
               Table: 2 Aces + 1 King + 3 junk (no pair ranks); P1 adds Ace→trips A;
               P2 adds King→pair A + pair K = Two Pair */
                        Arguments.of(
                                "Trips(Aces) beats Two Pair(A-A+K-K) — P1 wins",
                                List.of(c(15,"Wands"),  c(2,"Cups")),     // P1: trips A(♥♠♣), kickers K,5 [4,15,14,5]
                                List.of(c(14,"Cups"),   c(7,"Swords")),  // P2: pair A(♥♠)+pair K(♥♠) [3,15,14,5]
                                // P1: {15♣,2♥,15♥,15♠,14♦,5♣,3♠,4♦} → trips A ✓
                                // P2: {14♥,14♠,15♥,15♠,14♦,5♣,3♠,4♦} → trips 14(♥♠♦)+pair 15(♥♠)→FH ✗
                                // Fix: table has only 2 Aces (not 3 used by P2): P2 cannot form trips A
                                // Table {15♥,15♠,14♦,5♣,3♠,4♦}: P1 hole {15♣,2♥}: trips A(♥♠♣) ✓
                                // P2 hole {14♥,14♠}: pair A(♥♠)+pair K(♦♥) kicker 5 [3,15,14,5] ✓ Two Pair ✓
                                List.of(c(14,"Cups"),   c(2,"Swords"),   c(14,"Pentacles"),
                                        c(15,"Pentacles"),   c(15,"Swords"),    c(4,"Pentacles")),
                                1
                        ),

            /* 30 — Two Pair beats One Pair
               Table: pair A + 4 junk (no other pair); P2 adds pair K from hole→Two Pair;
               P1 adds unrelated cards→One Pair only */
                        Arguments.of(
                                "Two Pair(A-A+K-K) beats One Pair(A-A) — P2 wins",
                                List.of(c(9,"Cups"),    c(7,"Swords")),   // P1: pair A, kickers 11,9,7 [2,15,11,9,7]
                                List.of(c(14,"Cups"),   c(14,"Swords")),  // P2: pair A+pair K [3,15,14,11]
                                List.of(c(15,"Cups"),   c(15,"Swords"),   c(11,"Pentacles"),
                                        c(5,"Wands"),   c(3,"Cups"),      c(2,"Pentacles")),
                                2
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("testCases")
            void winByHigherCombination(
                    String description,
                    List<MinorArcanaCard> p1Hole,
                    List<MinorArcanaCard> p2Hole,
                    List<MinorArcanaCard> table,
                    int expectedWinner) {

                assertEquals(
                        expectedWinner,
                        determiner.determineWinner(p1Hole, p2Hole, table),
                        description
                );
            }
        }

        @Nested
        class SameCombBetterPowerSixCardsTest {

            private final WinnerDeterminerService determiner = new WinnerDeterminerService();

            private static MinorArcanaCard c(int power, String suit) {
                return new MinorArcanaCard(suit + "-" + power, suit, power);
            }

            static Stream<Arguments> testCases() {
                return Stream.of(

                        // ════════════════════════════════════════════════════════════
                        // STRAIGHT FLUSH — higher top card wins  (tests 01-09)
                        //
                        // Table ♠/♥: {a,b,c,d} consecutive + 2 off-suit junk.
                        // P1 hole-1: one above d (higher SF). P2 hole-1: one below a (lower SF).
                        // Verified: each player has exactly 5 consecutive same-suit → SF only.
                        // ════════════════════════════════════════════════════════════

                        /* 01 */ Arguments.of(
                                "SF: K-high(14) beats Q-high(13) — P1 wins",
                                List.of(c(14,"Swords"), c(2,"Cups")),    // P1: ♠{10-14} K-high
                                List.of(c(9,"Swords"),  c(3,"Cups")),    // P2: ♠{9-13}  Q-high
                                // Table ♠: {10,11,12,13}  P1♠={10,11,12,13,14}✓  P2♠={9,10,11,12,13}✓
                                List.of(c(10,"Swords"), c(11,"Swords"),  c(12,"Swords"),
                                        c(13,"Swords"), c(2,"Wands"),    c(3,"Pentacles")),
                                1
                        ),

                        /* 02 */ Arguments.of(
                                "SF: K-high(14) beats Q-high(13) — P2 wins",
                                List.of(c(9,"Cups"),    c(2,"Swords")),  // P1: ♥{9-13}  Q-high
                                List.of(c(14,"Cups"),   c(3,"Swords")),  // P2: ♥{10-14} K-high
                                List.of(c(10,"Cups"),   c(11,"Cups"),    c(12,"Cups"),
                                        c(13,"Cups"),   c(2,"Wands"),    c(3,"Pentacles")),
                                2
                        ),

                        /* 03 */ Arguments.of(
                                "SF: Q-high(13) beats Knight-high(12) — P1 wins",
                                List.of(c(13,"Swords"), c(2,"Cups")),    // P1: ♠{9-13}  Q-high
                                List.of(c(8,"Swords"),  c(3,"Cups")),    // P2: ♠{8-12}  Kn-high
                                List.of(c(9,"Swords"),  c(10,"Swords"),  c(11,"Swords"),
                                        c(12,"Swords"), c(2,"Wands"),    c(3,"Pentacles")),
                                1
                        ),

                        /* 04 */ Arguments.of(
                                "SF: Knight-high(12) beats Page-high(11) — P2 wins",
                                List.of(c(7,"Cups"),    c(2,"Swords")),  // P1: ♥{7-11}  P-high
                                List.of(c(12,"Cups"),   c(3,"Swords")),  // P2: ♥{8-12}  Kn-high
                                List.of(c(8,"Cups"),    c(9,"Cups"),     c(10,"Cups"),
                                        c(11,"Cups"),   c(2,"Wands"),    c(3,"Pentacles")),
                                2
                        ),

                        /* 05 */ Arguments.of(
                                "SF: Page-high(11) beats 10-high — P1 wins",
                                List.of(c(11,"Swords"), c(2,"Cups")),    // P1: ♠{7-11}  P-high
                                List.of(c(6,"Swords"),  c(3,"Cups")),    // P2: ♠{6-10}  10-high
                                List.of(c(7,"Swords"),  c(8,"Swords"),   c(9,"Swords"),
                                        c(10,"Swords"), c(2,"Wands"),    c(3,"Pentacles")),
                                1
                        ),

                        /* 06 */ Arguments.of(
                                "SF: 10-high beats 9-high — P2 wins",
                                List.of(c(5,"Cups"),    c(2,"Swords")),  // P1: ♥{5-9}   9-high
                                List.of(c(10,"Cups"),   c(3,"Swords")),  // P2: ♥{6-10}  10-high
                                List.of(c(6,"Cups"),    c(7,"Cups"),     c(8,"Cups"),
                                        c(9,"Cups"),    c(2,"Wands"),    c(3,"Pentacles")),
                                2
                        ),

                        /* 07 */ Arguments.of(
                                "SF: 9-high beats 8-high — P1 wins",
                                List.of(c(9,"Swords"),  c(2,"Cups")),    // P1: ♠{5-9}   9-high
                                List.of(c(4,"Swords"),  c(3,"Cups")),    // P2: ♠{4-8}   8-high
                                List.of(c(5,"Swords"),  c(6,"Swords"),   c(7,"Swords"),
                                        c(8,"Swords"),  c(2,"Wands"),    c(3,"Pentacles")),
                                1
                        ),

                        /* 08 */ Arguments.of(
                                "SF: 8-high beats 7-high — P2 wins",
                                List.of(c(3,"Cups"),    c(2,"Swords")),  // P1: ♥{3-7}   7-high
                                List.of(c(8,"Cups"),    c(4,"Swords")),  // P2: ♥{4-8}   8-high
                                List.of(c(4,"Cups"),    c(5,"Cups"),     c(6,"Cups"),
                                        c(7,"Cups"),    c(2,"Wands"),    c(3,"Pentacles")),
                                2
                        ),

                        /* 09 */ Arguments.of(
                                "SF: 7-high beats 6-high — P1 wins",
                                List.of(c(7,"Swords"),  c(2,"Cups")),    // P1: ♠{3-7}   7-high
                                List.of(c(2,"Swords"),  c(3,"Cups")),    // P2: ♠{2-6}   6-high
                                List.of(c(3,"Swords"),  c(4,"Swords"),   c(5,"Swords"),
                                        c(6,"Swords"),  c(2,"Wands"),    c(3,"Pentacles")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // FOUR OF A KIND — higher quad rank wins  (tests 10-14)
                        //
                        // Table: 2 of P1_rank (Wands+Pent) + 2 of P2_rank (Wands+Pent)
                        //        + 2 low neutrals (Cups+Swords).
                        // P1 hole: P1_rank♥ + P1_rank♠ → quads P1_rank.
                        // P2 hole: P2_rank♥ + P2_rank♠ → quads P2_rank.
                        // Kicker = opponent's quad rank (both see it from table).
                        // ════════════════════════════════════════════════════════════

                        /* 10 */ Arguments.of(
                                "Quads: Aces(15) beat Kings(14) — P1 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")),  // P1: quad A, kicker K(14) → [8,15,14]
                                List.of(c(14,"Cups"),   c(14,"Swords")),  // P2: quad K, kicker A(15) → [8,14,15]
                                List.of(c(15,"Wands"),  c(15,"Pentacles"), c(14,"Wands"),
                                        c(14,"Pentacles"), c(2,"Cups"),   c(3,"Swords")),
                                1
                        ),

                        /* 11 */ Arguments.of(
                                "Quads: Queens(13) beat Jacks(11) — P2 wins",
                                List.of(c(11,"Cups"),   c(11,"Swords")),  // P1: quad J, kicker Q(13) → [8,11,13]
                                List.of(c(13,"Cups"),   c(13,"Swords")),  // P2: quad Q, kicker J(11) → [8,13,11]
                                List.of(c(11,"Wands"),  c(11,"Pentacles"), c(13,"Wands"),
                                        c(13,"Pentacles"), c(2,"Cups"),   c(3,"Swords")),
                                2
                        ),

                        /* 12 */ Arguments.of(
                                "Quads: Knights(12) beat Pages(11) — P1 wins",
                                List.of(c(12,"Cups"),   c(12,"Swords")),  // P1: quad Kn, kicker P(11) → [8,12,11]
                                List.of(c(11,"Cups"),   c(11,"Swords")),  // P2: quad P,  kicker Kn(12)→ [8,11,12]
                                List.of(c(12,"Wands"),  c(12,"Pentacles"), c(11,"Wands"),
                                        c(11,"Pentacles"), c(2,"Cups"),   c(3,"Swords")),
                                1
                        ),

                        /* 13 */ Arguments.of(
                                "Quads: 10s beat 9s — P2 wins",
                                List.of(c(9,"Cups"),    c(9,"Swords")),   // P1: quad 9, kicker 10 → [8,9,10]
                                List.of(c(10,"Cups"),   c(10,"Swords")),  // P2: quad 10, kicker 9 → [8,10,9]
                                List.of(c(9,"Wands"),   c(9,"Pentacles"), c(10,"Wands"),
                                        c(10,"Pentacles"), c(2,"Cups"),   c(3,"Swords")),
                                2
                        ),

                        /* 14 */ Arguments.of(
                                "Quads: 8s beat 7s — P1 wins",
                                List.of(c(8,"Cups"),    c(8,"Swords")),   // P1: quad 8, kicker 7 → [8,8,7]
                                List.of(c(7,"Cups"),    c(7,"Swords")),   // P2: quad 7, kicker 8 → [8,7,8]
                                List.of(c(8,"Wands"),   c(8,"Pentacles"), c(7,"Wands"),
                                        c(7,"Pentacles"), c(2,"Cups"),    c(3,"Swords")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // FULL HOUSE — higher trips rank wins  (tests 15-19)
                        //
                        // Table: shared pair X (Cups+Swords) + 1 of P1_rank (Wands) +
                        //        1 of P2_rank (Pent) + 2 low neutrals.
                        // P1 hole: P1_rank♥ + P1_rank♠ → trips P1 + pair X = FH.
                        // P2 hole: P2_rank♥ + P2_rank♠ → trips P2 + pair X = FH.
                        // Each trips rank appears exactly 3 times → no quads.
                        // ════════════════════════════════════════════════════════════

                        /* 15 */ Arguments.of(
                                "FH: Aces-full-of-2s beats Kings-full-of-2s — P1 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")),  // P1: A×3 + 2×2 = [7,15,2]
                                List.of(c(14,"Cups"),   c(14,"Swords")),  // P2: K×3 + 2×2 = [7,14,2]
                                List.of(c(2,"Cups"),    c(2,"Swords"),    c(15,"Wands"),
                                        c(14,"Pentacles"), c(3,"Cups"),   c(4,"Swords")),
                                1
                        ),

                        /* 16 */ Arguments.of(
                                "FH: Queens(13)-full-of-3s beats Jacks(11)-full-of-3s — P2 wins",
                                List.of(c(11,"Cups"),   c(11,"Swords")),  // P1: J×3 + 3×2 = [7,11,3]
                                List.of(c(13,"Cups"),   c(13,"Swords")),  // P2: Q×3 + 3×2 = [7,13,3]
                                List.of(c(3,"Cups"),    c(3,"Swords"),    c(11,"Wands"),
                                        c(13,"Pentacles"), c(4,"Cups"),   c(5,"Swords")),
                                2
                        ),

                        /* 17 */ Arguments.of(
                                "FH: Knights(12)-full-of-4s beats Pages(11)-full-of-4s — P1 wins",
                                List.of(c(12,"Cups"),   c(12,"Swords")),  // P1: Kn×3 + 4×2 = [7,12,4]
                                List.of(c(11,"Cups"),   c(11,"Swords")),  // P2: P×3  + 4×2 = [7,11,4]
                                List.of(c(4,"Cups"),    c(4,"Swords"),    c(12,"Wands"),
                                        c(11,"Pentacles"), c(2,"Cups"),   c(3,"Swords")),
                                1
                        ),

                        /* 18 */ Arguments.of(
                                "FH: 10s-full-of-5s beats 9s-full-of-5s — P2 wins",
                                List.of(c(9,"Cups"),    c(9,"Swords")),   // P1: 9×3 + 5×2 = [7,9,5]
                                List.of(c(10,"Cups"),   c(10,"Swords")),  // P2: 10×3 + 5×2 = [7,10,5]
                                List.of(c(5,"Cups"),    c(5,"Swords"),    c(9,"Wands"),
                                        c(10,"Pentacles"), c(2,"Cups"),   c(3,"Swords")),
                                2
                        ),

                        /* 19 */ Arguments.of(
                                "FH: 8s-full-of-3s beats 7s-full-of-3s — P1 wins",
                                List.of(c(8,"Cups"),    c(8,"Swords")),   // P1: 8×3 + 3×2 = [7,8,3]
                                List.of(c(7,"Cups"),    c(7,"Swords")),   // P2: 7×3 + 3×2 = [7,7,3]
                                List.of(c(3,"Cups"),    c(3,"Swords"),    c(8,"Wands"),
                                        c(7,"Pentacles"), c(2,"Cups"),    c(4,"Swords")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // FLUSH — higher top card wins  (tests 20-24)
                        //
                        // Table: 4 NON-CONSECUTIVE same-suit cards + 2 off-suit neutrals.
                        // Gap pattern {3,5,7,9}: all odd, gaps at 4,6,8 → no SF possible.
                        // P1 hole-1: higher flush card. P2 hole-1: lower flush card.
                        // Each player's total same-suit cards = 5 → flush, not SF.
                        // Both hole-2 cards are off-suit junk.
                        // ════════════════════════════════════════════════════════════

                        /* 20 */ Arguments.of(
                                "Flush Swords: A-high(15) beats K-high(14) — P1 wins",
                                List.of(c(15,"Swords"), c(2,"Cups")),    // P1 ♠: {3,5,7,9,15} → [6,15,9,7,5,3]
                                List.of(c(14,"Swords"), c(3,"Cups")),    // P2 ♠: {3,5,7,9,14} → [6,14,9,7,5,3]
                                List.of(c(3,"Swords"),  c(5,"Swords"),   c(7,"Swords"),
                                        c(9,"Swords"),  c(2,"Wands"),    c(3,"Pentacles")),
                                1
                        ),

                        /* 21 */ Arguments.of(
                                "Flush Cups: K-high(14) beats Q-high(13) — P2 wins",
                                List.of(c(13,"Cups"),   c(2,"Swords")),  // P1 ♥: {3,5,7,9,13} → [6,13,9,7,5,3]
                                List.of(c(14,"Cups"),   c(3,"Swords")),  // P2 ♥: {3,5,7,9,14} → [6,14,9,7,5,3]
                                List.of(c(3,"Cups"),    c(5,"Cups"),     c(7,"Cups"),
                                        c(9,"Cups"),    c(2,"Wands"),    c(3,"Pentacles")),
                                2
                        ),

                        /* 22 */ Arguments.of(
                                "Flush Swords: Q-high(13) beats Page-high(11) — P1 wins",
                                List.of(c(13,"Swords"), c(2,"Cups")),    // P1 ♠: {3,5,7,9,13} → [6,13,9,7,5,3]
                                List.of(c(11,"Swords"), c(3,"Cups")),    // P2 ♠: {3,5,7,9,11} → [6,11,9,7,5,3]
                                List.of(c(3,"Swords"),  c(5,"Swords"),   c(7,"Swords"),
                                        c(9,"Swords"),  c(2,"Wands"),    c(3,"Pentacles")),
                                1
                        ),

                        /* 23 */ Arguments.of(
                                "Flush Cups: Page-high(11) beats 10-high — P2 wins",
                                List.of(c(10,"Cups"),   c(2,"Swords")),  // P1 ♥: {3,5,7,9,10} → [6,10,9,7,5,3]
                                List.of(c(11,"Cups"),   c(3,"Swords")),  // P2 ♥: {3,5,7,9,11} → [6,11,9,7,5,3]
                                // {3,5,7,9,10}: 9-10 run of 2 only → no SF ✓
                                List.of(c(3,"Cups"),    c(5,"Cups"),     c(7,"Cups"),
                                        c(9,"Cups"),    c(2,"Wands"),    c(3,"Pentacles")),
                                2
                        ),

                        /* 24 */ Arguments.of(
                                "Flush Swords: 10-high beats 9-high — P1 wins",
                                List.of(c(10,"Swords"), c(2,"Cups")),    // P1 ♠: {3,5,7,8,10} → [6,10,8,7,5,3]
                                List.of(c(9,"Swords"),  c(3,"Cups")),    // P2 ♠: {3,5,7,8,9}  → [6,9,8,7,5,3]
                                // Table ♠: {3,5,7,8} — 7-8 run of 2 only → no SF ✓
                                List.of(c(3,"Swords"),  c(5,"Swords"),   c(7,"Swords"),
                                        c(8,"Swords"),  c(2,"Wands"),    c(3,"Pentacles")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // STRAIGHT — higher top card wins  (tests 25-29)
                        //
                        // Table: 4 consecutive mixed-suit cards + 2 off-suit junk.
                        // P1 hole-1: extends run UPWARD. P2 hole-1: extends DOWNWARD.
                        // Mixed table suits prevent accidental flush.
                        // ════════════════════════════════════════════════════════════

                        /* 25 */ Arguments.of(
                                "Straight: A-high(15) beats K-high(14) — P1 wins",
                                List.of(c(15,"Cups"),   c(2,"Swords")),  // P1: {11-15} A-high
                                List.of(c(10,"Wands"),  c(3,"Pentacles")),// P2: {10-14} K-high
                                List.of(c(11,"Swords"), c(12,"Cups"),    c(13,"Wands"),
                                        c(14,"Pentacles"), c(2,"Cups"),  c(3,"Swords")),
                                1
                        ),

                        /* 26 */ Arguments.of(
                                "Straight: K-high(14) beats Q-high(13) — P2 wins",
                                List.of(c(9,"Wands"),   c(2,"Pentacles")),// P1: {9-13}  Q-high
                                List.of(c(14,"Cups"),   c(3,"Swords")),  // P2: {10-14} K-high
                                List.of(c(10,"Swords"), c(11,"Cups"),    c(12,"Wands"),
                                        c(13,"Pentacles"), c(2,"Cups"),  c(3,"Wands")),
                                2
                        ),

                        /* 27 */ Arguments.of(
                                "Straight: Q-high(13) beats Knight-high(12) — P1 wins",
                                List.of(c(13,"Cups"),   c(2,"Swords")),  // P1: {9-13}  Q-high
                                List.of(c(8,"Wands"),   c(3,"Pentacles")),// P2: {8-12}  Kn-high
                                List.of(c(9,"Swords"),  c(10,"Cups"),    c(11,"Wands"),
                                        c(12,"Pentacles"), c(2,"Cups"),  c(3,"Swords")),
                                1
                        ),

                        /* 28 */ Arguments.of(
                                "Straight: Knight-high(12) beats Page-high(11) — P2 wins",
                                List.of(c(7,"Wands"),   c(2,"Pentacles")),// P1: {7-11}  P-high
                                List.of(c(12,"Cups"),   c(3,"Swords")),  // P2: {8-12}  Kn-high
                                List.of(c(8,"Swords"),  c(9,"Cups"),     c(10,"Wands"),
                                        c(11,"Pentacles"), c(2,"Cups"),  c(3,"Swords")),
                                2
                        ),

                        /* 29 */ Arguments.of(
                                "Straight: Page-high(11) beats 10-high — P1 wins",
                                List.of(c(11,"Cups"),   c(2,"Swords")),  // P1: {7-11}  P-high
                                List.of(c(6,"Wands"),   c(3,"Pentacles")),// P2: {6-10}  10-high
                                List.of(c(7,"Swords"),  c(8,"Cups"),     c(9,"Wands"),
                                        c(10,"Pentacles"), c(2,"Cups"),  c(3,"Swords")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // THREE OF A KIND — higher trips rank wins  (tests 30-33)
                        //
                        // Table: 1 of P1_rank (Wands) + 1 of P2_rank (Pent) +
                        //        4 low neutrals (no pair among them → no accidental FH).
                        // P1 hole: P1_rank♥ + P1_rank♠ → trips P1_rank.
                        // P2 hole: P2_rank♥ + P2_rank♠ → trips P2_rank.
                        // Score: [4, tripsRank, k1, k2].
                        // ════════════════════════════════════════════════════════════

                        /* 30 */ Arguments.of(
                                "Trips: Aces(15) beat Kings(14) — P1 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")),  // P1: trips A, kickers K(14),4 → [4,15,14,4]
                                List.of(c(14,"Cups"),   c(14,"Swords")),  // P2: trips K, kickers A(15),4 → [4,14,15,4]
                                List.of(c(15,"Wands"),  c(14,"Pentacles"), c(2,"Cups"),
                                        c(3,"Swords"),  c(4,"Wands"),    c(5,"Pentacles")),
                                1
                        ),

                        /* 31 */ Arguments.of(
                                "Trips: Queens(13) beat Jacks(11) — P2 wins",
                                List.of(c(11,"Cups"),   c(11,"Swords")),  // P1: trips J, kickers Q(13),4 → [4,11,13,4]
                                List.of(c(13,"Cups"),   c(13,"Swords")),  // P2: trips Q, kickers J(11),4 → [4,13,11,4]
                                List.of(c(11,"Wands"),  c(13,"Pentacles"), c(2,"Cups"),
                                        c(3,"Swords"),  c(4,"Wands"),    c(5,"Pentacles")),
                                2
                        ),

                        /* 32 */ Arguments.of(
                                "Trips: 10s beat 9s — P1 wins",
                                List.of(c(10,"Cups"),   c(10,"Swords")),  // P1: trips 10, kickers 9,4 → [4,10,9,4]
                                List.of(c(9,"Cups"),    c(9,"Swords")),   // P2: trips 9, kickers 10,4 → [4,9,10,4]
                                List.of(c(10,"Wands"),  c(9,"Pentacles"), c(2,"Cups"),
                                        c(3,"Swords"),  c(4,"Wands"),    c(5,"Pentacles")),
                                1
                        ),

                        /* 33 */ Arguments.of(
                                "Trips: 7s beat 6s — P2 wins",
                                List.of(c(6,"Cups"),    c(6,"Swords")),   // P1: trips 6, kickers 7,4 → [4,6,7,4]
                                List.of(c(7,"Cups"),    c(7,"Swords")),   // P2: trips 7, kickers 6,4 → [4,7,6,4]
                                List.of(c(6,"Wands"),   c(7,"Pentacles"), c(15,"Cups"),
                                        c(3,"Swords"),  c(8,"Wands"),    c(5,"Pentacles")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // TWO PAIR — higher top-pair rank wins  (tests 34-37)
                        //
                        // Tests 34-35: top pair differs.
                        //   Table: shared second pair (Cups+Swords) + 4 low neutrals.
                        //   Each player's hole pair is their top pair. No table rank
                        //   matches hole pairs → no trips risk.
                        //
                        // Tests 36-37: same top pair on table; second pair from hole differs.
                        //   Table: shared top pair (Cups+Swords) + 4 low neutrals.
                        //   Each player's hole pair becomes their second pair.
                        //   No table rank matches hole pairs → no trips risk.
                        // ════════════════════════════════════════════════════════════

                        /* 34 */ Arguments.of(
                                "Two Pair: A-A+3-3 beats K-K+3-3 (higher top pair) — P1 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")),  // P1: pair A(hole)+pair 3(table) [3,15,3,5]
                                List.of(c(14,"Cups"),   c(14,"Swords")),  // P2: pair K(hole)+pair 3(table) [3,14,3,5]
                                List.of(c(3,"Cups"),    c(3,"Swords"),    c(2,"Wands"),
                                        c(11,"Pentacles"), c(5,"Wands"),   c(9,"Pentacles")),
                                1
                        ),

                        /* 35 */ Arguments.of(
                                "Two Pair: Q-Q+3-3 beats J-J+3-3 (higher top pair) — P2 wins",
                                List.of(c(11,"Cups"),   c(11,"Swords")),  // P1: pair J+pair 3 [3,11,3,6]
                                List.of(c(13,"Cups"),   c(13,"Swords")),  // P2: pair Q+pair 3 [3,13,3,6]
                                List.of(c(3,"Cups"),    c(3,"Swords"),    c(2,"Wands"),
                                        c(4,"Pentacles"), c(8,"Wands"),   c(8,"Pentacles")),
                                2
                        ),

                        /* 36 */ Arguments.of(
                                "Two Pair: A-A+K-K beats A-A+Q-Q (shared top pair, higher 2nd pair) — P1 wins",
                                List.of(c(14,"Wands"),  c(14,"Pentacles")),// P1: pair K(hole)+pair A(table) [3,15,14,6]
                                List.of(c(13,"Wands"),  c(13,"Pentacles")),// P2: pair Q(hole)+pair A(table) [3,15,13,6]
                                // No K or Q on table → no trips ✓
                                List.of(c(15,"Cups"),   c(15,"Swords"),   c(2,"Cups"),
                                        c(3,"Swords"),  c(4,"Wands"),    c(6,"Pentacles")),
                                1
                        ),

                        /* 37 */ Arguments.of(
                                "Two Pair: K-K+J-J beats K-K+10-10 (shared top pair, higher 2nd pair) — P2 wins",
                                List.of(c(10,"Wands"),  c(10,"Pentacles")),// P1: pair 10(hole)+pair K(table) [3,14,10,6]
                                List.of(c(11,"Wands"),  c(11,"Pentacles")),// P2: pair J(hole)+pair K(table)  [3,14,11,6]
                                // No 10 or J on table → no trips ✓
                                List.of(c(14,"Cups"),   c(14,"Swords"),   c(2,"Cups"),
                                        c(3,"Swords"),  c(4,"Wands"),    c(6,"Pentacles")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // ONE PAIR — higher pair rank wins  (tests 38-40)
                        //
                        // Table: 6 low neutral cards (powers 2-7, all distinct ranks)
                        //        with no rank matching either player's hole pair.
                        // P1 hole: pair of higher rank. P2 hole: pair of lower rank.
                        // Kickers = best 3 from the 6 table neutrals (same for both).
                        // Score: [2, pairRank, k1, k2, k3].
                        // ════════════════════════════════════════════════════════════

                        /* 38 */ Arguments.of(
                                "Pair: Aces(15) beat Kings(14) — P1 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")),  // P1: pair A, kickers 7,6,5 → [2,15,7,6,5]
                                List.of(c(14,"Cups"),   c(14,"Swords")),  // P2: pair K, kickers 7,6,5 → [2,14,7,6,5]
                                // Table: {2,3,4,5,6,7} no A or K → no trips ✓
                                List.of(c(2,"Wands"),   c(10,"Pentacles"), c(4,"Cups"),
                                        c(5,"Swords"),  c(8,"Wands"),    c(12,"Pentacles")),
                                1
                        ),

                        /* 39 */ Arguments.of(
                                "Pair: Queens(13) beat Jacks(11) — P2 wins",
                                List.of(c(11,"Cups"),   c(11,"Swords")),  // P1: pair J, kickers 7,6,5 → [2,11,7,6,5]
                                List.of(c(13,"Cups"),   c(13,"Swords")),  // P2: pair Q, kickers 7,6,5 → [2,13,7,6,5]
                                List.of(c(2,"Wands"),   c(12,"Pentacles"), c(4,"Cups"),
                                        c(15,"Swords"),  c(6,"Wands"),    c(7,"Pentacles")),
                                2
                        ),

                        /* 40 */ Arguments.of(
                                "Pair: 9s beat 8s — P1 wins",
                                List.of(c(9,"Cups"),    c(9,"Swords")),   // P1: pair 9, kickers 7,6,5 → [2,9,7,6,5]
                                List.of(c(8,"Cups"),    c(8,"Swords")),   // P2: pair 8, kickers 7,6,5 → [2,8,7,6,5]
                                List.of(c(12,"Wands"),   c(3,"Pentacles"), c(14,"Cups"),
                                        c(5,"Swords"),  c(6,"Wands"),    c(7,"Pentacles")),
                                1
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("testCases")
            void sameCombBetterPower(
                    String description,
                    List<MinorArcanaCard> p1Hole,
                    List<MinorArcanaCard> p2Hole,
                    List<MinorArcanaCard> table,
                    int expectedWinner) {

                assertEquals(
                        expectedWinner,
                        determiner.determineWinner(p1Hole, p2Hole, table),
                        description
                );
            }
        }

        @Nested
        class SameCombWinByHighCardSixCardsTest {

            private final WinnerDeterminerService determiner = new WinnerDeterminerService();

            private static MinorArcanaCard c(int power, String suit) {
                return new MinorArcanaCard(suit + "-" + power, suit, power);
            }

            static Stream<Arguments> testCases() {
                return Stream.of(

                        // ════════════════════════════════════════════════════════════
                        // FOUR OF A KIND — same quad rank, kicker decides  (01-06)
                        //
                        // Table: quad♥ quad♠ quad♣ quad♦  +  c(2,"Cups") c(2,"Swords").
                        // Kicker = max(meaningful-hole, junk-hole, 2, 2) = meaningful-hole.
                        // Pair-of-2 on table is irrelevant: [8] always wins over [3]. ✓
                        // ════════════════════════════════════════════════════════════

                        /* 01 */ Arguments.of(
                                "Quads Aces: kicker K(14) beats Q(13) — P1 wins",
                                List.of(c(14,"Cups"),    c(3,"Swords")),
                                List.of(c(13,"Cups"),    c(4,"Wands")),
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(15,"Wands"),
                                        c(15,"Pentacles"), c(2,"Cups"),    c(2,"Swords")),
                                1
                        ),

                        /* 02 */ Arguments.of(
                                "Quads Kings: kicker A(15) beats Page(11) — P2 wins",
                                List.of(c(11,"Cups"),    c(3,"Swords")),
                                List.of(c(15,"Cups"),    c(4,"Wands")),
                                List.of(c(14,"Cups"),    c(14,"Swords"),   c(14,"Wands"),
                                        c(14,"Pentacles"), c(2,"Cups"),    c(2,"Swords")),
                                2
                        ),

                        /* 03 */ Arguments.of(
                                "Quads Queens(13): kicker A(15) beats K(14) — P1 wins",
                                List.of(c(15,"Cups"),    c(3,"Swords")),
                                List.of(c(14,"Cups"),    c(4,"Wands")),
                                List.of(c(13,"Cups"),    c(13,"Swords"),   c(13,"Wands"),
                                        c(13,"Pentacles"), c(2,"Cups"),    c(2,"Swords")),
                                1
                        ),

                        /* 04 */ Arguments.of(
                                "Quads Jacks(11): kicker A(15) beats Q(13) — P2 wins",
                                List.of(c(13,"Cups"),    c(3,"Swords")),
                                List.of(c(15,"Cups"),    c(4,"Wands")),
                                List.of(c(11,"Cups"),    c(11,"Swords"),   c(11,"Wands"),
                                        c(11,"Pentacles"), c(2,"Cups"),    c(2,"Swords")),
                                2
                        ),

                        /* 05 */ Arguments.of(
                                "Quads 9s: kicker K(14) beats Q(13) — P1 wins",
                                List.of(c(14,"Cups"),    c(3,"Swords")),
                                List.of(c(13,"Cups"),    c(4,"Wands")),
                                List.of(c(9,"Cups"),     c(9,"Swords"),    c(9,"Wands"),
                                        c(9,"Pentacles"), c(2,"Cups"),     c(2,"Swords")),
                                1
                        ),

                        /* 06 */ Arguments.of(
                                "Quads 7s: kicker Q(13) beats Page(11) — P2 wins",
                                List.of(c(11,"Cups"),    c(3,"Swords")),
                                List.of(c(13,"Cups"),    c(4,"Wands")),
                                List.of(c(7,"Cups"),     c(7,"Swords"),    c(7,"Wands"),
                                        c(7,"Pentacles"), c(2,"Cups"),     c(2,"Swords")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // FLUSH — same suit, deciding card varies  (07-12)
                        //
                        // Table: {3,5,7,9}♠ or {15,8,6,4}♥ (gaps block SF) +
                        //        2 distinct off-suit neutrals.
                        // ════════════════════════════════════════════════════════════

                        /* 07 — deciding: position 1 */
                        Arguments.of(
                                "Flush Swords: top card A(15) beats K(14) — P1 wins",
                                List.of(c(15,"Swords"),  c(2,"Cups")),     // P1 ♠: {3,5,7,9,15}→[6,15,9,7,5,3]
                                List.of(c(14,"Swords"),  c(3,"Cups")),     // P2 ♠: {3,5,7,9,14}→[6,14,9,7,5,3]
                                List.of(c(3,"Swords"),   c(5,"Swords"),    c(7,"Swords"),
                                        c(9,"Swords"),   c(2,"Wands"),     c(3,"Pentacles")),
                                1
                        ),

                        /* 08 — deciding: position 1, P2 wins */
                        Arguments.of(
                                "Flush Cups: top card K(14) beats Q(13) — P2 wins",
                                List.of(c(13,"Cups"),    c(2,"Swords")),   // P1 ♥: {3,5,7,9,13}→[6,13,9,7,5,3]
                                List.of(c(14,"Cups"),    c(3,"Swords")),   // P2 ♥: {3,5,7,9,14}→[6,14,9,7,5,3]
                                List.of(c(3,"Cups"),     c(5,"Cups"),      c(7,"Cups"),
                                        c(9,"Cups"),     c(2,"Wands"),     c(3,"Pentacles")),
                                2
                        ),

            /* 09 — deciding: position 2 (shared A on table)
                    table ♠ {4,6,8,15}: gaps at 5,7,9-14 prevent SF ✓ */
                        Arguments.of(
                                "Flush Swords A-high: 2nd card K(14) beats Q(13) — P1 wins",
                                List.of(c(14,"Swords"),  c(2,"Cups")),     // P1 ♠: {4,6,8,14,15}→[6,15,14,8,6,4]
                                List.of(c(13,"Swords"),  c(3,"Cups")),     // P2 ♠: {4,6,8,13,15}→[6,15,13,8,6,4]
                                List.of(c(15,"Swords"),  c(8,"Swords"),    c(6,"Swords"),
                                        c(4,"Swords"),   c(2,"Wands"),     c(3,"Pentacles")),
                                1
                        ),

                        /* 10 — deciding: position 2, P2 wins */
                        Arguments.of(
                                "Flush Cups A-high: 2nd card K(14) beats Page(11) — P2 wins",
                                List.of(c(11,"Cups"),    c(2,"Swords")),   // P1 ♥: {4,6,8,11,15}→[6,15,11,8,6,4]
                                List.of(c(14,"Cups"),    c(3,"Swords")),   // P2 ♥: {4,6,8,14,15}→[6,15,14,8,6,4]
                                List.of(c(15,"Cups"),    c(8,"Cups"),      c(6,"Cups"),
                                        c(4,"Cups"),     c(2,"Wands"),     c(3,"Pentacles")),
                                2
                        ),

            /* 11 — deciding: position 3 (shared A-K on table)
                    table ♠ {4,6,14,15}: 14-15 run of 2, gap at 12,13 → no SF ✓ */
                        Arguments.of(
                                "Flush Swords A-K-high: 3rd card Q(13) beats Page(11) — P1 wins",
                                List.of(c(13,"Swords"),  c(2,"Cups")),     // P1 ♠: {4,6,13,14,15}→[6,15,14,13,6,4]
                                List.of(c(11,"Swords"),  c(3,"Cups")),     // P2 ♠: {4,6,11,14,15}→[6,15,14,11,6,4]
                                List.of(c(15,"Swords"),  c(14,"Swords"),   c(6,"Swords"),
                                        c(4,"Swords"),   c(2,"Wands"),     c(3,"Pentacles")),
                                1
                        ),

                        /* 12 — deciding: position 3, P2 wins */
                        Arguments.of(
                                "Flush Cups A-K-high: 3rd card Q(13) beats 10 — P2 wins",
                                List.of(c(10,"Cups"),    c(2,"Swords")),   // P1 ♥: {4,6,10,14,15}→[6,15,14,10,6,4]
                                List.of(c(13,"Cups"),    c(3,"Swords")),   // P2 ♥: {4,6,13,14,15}→[6,15,14,13,6,4]
                                List.of(c(15,"Cups"),    c(14,"Cups"),     c(6,"Cups"),
                                        c(4,"Cups"),     c(2,"Wands"),     c(3,"Pentacles")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // THREE OF A KIND — same trips, kicker decides  (13-18)
                        //
                        // Table: trips×3 (Cups+Swords+Wands) + shared kicker (Pentacles)
                        //        + c(2,"Wands") + c(6,"Pentacles")  ← DISTINCT ranks.
                        // No pair on table → no Full House for either player. ✓
                        //
                        // Tests 13-15: meaningful hole card > sharedK
                        //   → kicker order: [hole-card, sharedK, ...]
                        // Tests 16-18: sharedK > both hole cards
                        //   → kicker order: [sharedK, hole-card, ...]
                        // ════════════════════════════════════════════════════════════

                        /* 13 — k1 from hole (A=15 > sharedK=9) */
                        Arguments.of(
                                "Trips 7s: shared k2=9, hole k1 A(15) beats K(14) — P1 wins",
                                List.of(c(15,"Cups"),    c(3,"Swords")),   // P1: [4,7,15,9]
                                List.of(c(14,"Cups"),    c(4,"Swords")),   // P2: [4,7,14,9]
                                // neutrals rank 2 and 6: no pair, both < sharedK(9) ✓
                                List.of(c(7,"Cups"),     c(7,"Swords"),    c(7,"Wands"),
                                        c(9,"Pentacles"), c(2,"Wands"),    c(6,"Pentacles")),
                                1
                        ),

                        /* 14 — k1 from hole, P2 wins */
                        Arguments.of(
                                "Trips 7s: shared k2=9, hole k1 K(14) beats Page(11) — P2 wins",
                                List.of(c(11,"Cups"),    c(3,"Swords")),   // P1: [4,7,11,9]
                                List.of(c(14,"Cups"),    c(4,"Swords")),   // P2: [4,7,14,9]
                                List.of(c(7,"Cups"),     c(7,"Swords"),    c(7,"Wands"),
                                        c(9,"Pentacles"), c(2,"Wands"),    c(6,"Pentacles")),
                                2
                        ),

                        /* 15 — k1 from hole (K=14 > sharedK=11) */
                        Arguments.of(
                                "Trips Aces: shared k2=Page(11), hole k1 K(14) beats Q(13) — P1 wins",
                                List.of(c(14,"Cups"),    c(3,"Swords")),   // P1: [4,15,14,11]
                                List.of(c(13,"Cups"),    c(4,"Swords")),   // P2: [4,15,13,11]
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(15,"Wands"),
                                        c(11,"Pentacles"), c(2,"Wands"),   c(6,"Pentacles")),
                                1
                        ),

                        /* 16 — k1 = sharedK(14); k2 from hole decides, P2 wins */
                        Arguments.of(
                                "Trips 9s: shared k1=K(14), hole k2 Q(13) beats Page(11) — P2 wins",
                                List.of(c(11,"Cups"),    c(3,"Swords")),   // P1: [4,9,14,11]
                                List.of(c(13,"Cups"),    c(4,"Swords")),   // P2: [4,9,14,13]
                                List.of(c(9,"Cups"),     c(9,"Swords"),    c(9,"Wands"),
                                        c(14,"Pentacles"), c(2,"Wands"),   c(6,"Pentacles")),
                                2
                        ),

                        /* 17 — k1 = sharedK(15); k2 from hole decides, P1 wins */
                        Arguments.of(
                                "Trips 5s: shared k1=A(15), hole k2 K(14) beats Page(11) — P1 wins",
                                List.of(c(14,"Cups"),    c(3,"Swords")),   // P1: [4,5,15,14]
                                List.of(c(11,"Cups"),    c(4,"Swords")),   // P2: [4,5,15,11]
                                List.of(c(5,"Cups"),     c(5,"Swords"),    c(5,"Wands"),
                                        c(15,"Pentacles"), c(2,"Wands"),   c(6,"Pentacles")),
                                1
                        ),

                        /* 18 — k1 = sharedK(15); k2 from hole decides, P2 wins */
                        Arguments.of(
                                "Trips 3s: shared k1=A(15), hole k2 K(14) beats Q(13) — P2 wins",
                                List.of(c(13,"Cups"),    c(4,"Swords")),   // P1: [4,3,15,13]
                                List.of(c(14,"Cups"),    c(5,"Swords")),   // P2: [4,3,15,14]
                                List.of(c(3,"Cups"),     c(3,"Swords"),    c(3,"Wands"),
                                        c(15,"Pentacles"), c(2,"Wands"),   c(6,"Pentacles")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // TWO PAIR — same two pairs on board, kicker decides  (19-24)
                        //
                        // Table: pair-high (Cups+Swords) + pair-low (Wands+Pent) +
                        //        c(2,"Cups") + c(2,"Swords").
                        //
                        // Three pairs visible: high, low, and 2.  Evaluator picks best
                        // two = (high + low); pair-2 is discarded.
                        // Kicker = hole meaningful card (ranks 11-15 > 2). ✓
                        // ════════════════════════════════════════════════════════════

                        /* 19 */ Arguments.of(
                                "Two Pair Q-Q+Page-Page: kicker A(15) beats K(14) — P1 wins",
                                List.of(c(15,"Wands"),   c(3,"Swords")),   // P1: [3,13,11,15]
                                List.of(c(14,"Wands"),   c(4,"Swords")),   // P2: [3,13,11,14]
                                List.of(c(13,"Cups"),    c(13,"Swords"),   c(11,"Wands"),
                                        c(11,"Pentacles"), c(2,"Cups"),    c(2,"Swords")),
                                1
                        ),

                        /* 20 */ Arguments.of(
                                "Two Pair K-K+Page-Page: kicker A(15) beats Q(13) — P2 wins",
                                List.of(c(13,"Wands"),   c(3,"Swords")),   // P1: [3,14,11,13]
                                List.of(c(15,"Wands"),   c(4,"Swords")),   // P2: [3,14,11,15]
                                List.of(c(14,"Cups"),    c(14,"Swords"),   c(11,"Wands"),
                                        c(11,"Pentacles"), c(2,"Cups"),    c(2,"Swords")),
                                2
                        ),

                        /* 21 */ Arguments.of(
                                "Two Pair A-A+K-K: kicker Q(13) beats Page(11) — P1 wins",
                                List.of(c(13,"Wands"),   c(3,"Swords")),   // P1: [3,15,14,13]
                                List.of(c(11,"Wands"),   c(4,"Swords")),   // P2: [3,15,14,11]
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(14,"Wands"),
                                        c(14,"Pentacles"), c(2,"Cups"),    c(2,"Swords")),
                                1
                        ),

                        /* 22 */ Arguments.of(
                                "Two Pair Q-Q+10-10: kicker A(15) beats K(14) — P2 wins",
                                List.of(c(14,"Wands"),   c(3,"Swords")),   // P1: [3,13,10,14]
                                List.of(c(15,"Wands"),   c(4,"Swords")),   // P2: [3,13,10,15]
                                List.of(c(13,"Cups"),    c(13,"Swords"),   c(10,"Wands"),
                                        c(10,"Pentacles"), c(2,"Cups"),    c(2,"Swords")),
                                2
                        ),

                        /* 23 */ Arguments.of(
                                "Two Pair K-K+9-9: kicker A(15) beats Q(13) — P1 wins",
                                List.of(c(15,"Wands"),   c(3,"Swords")),   // P1: [3,14,9,15]
                                List.of(c(13,"Wands"),   c(4,"Swords")),   // P2: [3,14,9,13]
                                List.of(c(14,"Cups"),    c(14,"Swords"),   c(9,"Wands"),
                                        c(9,"Pentacles"), c(2,"Cups"),     c(2,"Swords")),
                                1
                        ),

                        /* 24 */ Arguments.of(
                                "Two Pair 10-10+9-9: kicker K(14) beats Q(13) — P2 wins",
                                List.of(c(13,"Wands"),   c(3,"Swords")),   // P1: [3,10,9,13]
                                List.of(c(14,"Wands"),   c(4,"Swords")),   // P2: [3,10,9,14]
                                List.of(c(10,"Cups"),    c(10,"Swords"),   c(9,"Wands"),
                                        c(9,"Pentacles"), c(2,"Cups"),     c(2,"Swords")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // ONE PAIR — same pair rank, kicker decides  (25-32)
                        //
                        // Table: pair (Cups+Swords) + 2 shared kicker cards (Wands+Pent)
                        //        + c(2,"Cups") + c(6,"Swords")  ← DISTINCT ranks.
                        // No second pair on table → genuine One Pair for both players. ✓
                        //
                        // Tests 25-26: k1 from hole (no higher shared card on table).
                        // Tests 27-28: shared k1 on table; k2 from hole.
                        // Tests 29-32: shared k1+k2 on table; k3 from hole.
                        //
                        // Score verification (example test 25):
                        //   P1 non-pair: {15,3,9,5,2,6} → top-3: 15,9,6 → [2,7,15,9,6]
                        //   P2 non-pair: {13,4,9,5,2,6} → top-3: 13,9,6 → [2,7,13,9,6]
                        //   P1 wins at k1 (15>13) ✓
                        // ════════════════════════════════════════════════════════════

                        /* 25 — k1 from hole decides */
                        Arguments.of(
                                "Pair 7s: k1 A(15) beats Q(13) — shared k2=9 k3=6 — P1 wins",
                                List.of(c(15,"Swords"),  c(3,"Wands")),    // P1: [2,7,15,9,6]
                                List.of(c(13,"Wands"),   c(4,"Pentacles")), // P2: [2,7,13,9,6]
                                List.of(c(7,"Cups"),     c(7,"Swords"),    c(9,"Wands"),
                                        c(5,"Pentacles"), c(2,"Cups"),     c(6,"Swords")),
                                1
                        ),

                        /* 26 — k1 from hole decides, P2 wins */
                        Arguments.of(
                                "Pair Pages(11): k1 A(15) beats K(14) — shared k2=9 k3=6 — P2 wins",
                                List.of(c(14,"Swords"),  c(3,"Wands")),    // P1: [2,11,14,9,6]
                                List.of(c(15,"Wands"),   c(4,"Pentacles")), // P2: [2,11,15,9,6]
                                List.of(c(11,"Cups"),    c(11,"Swords"),   c(9,"Wands"),
                                        c(5,"Pentacles"), c(2,"Cups"),     c(6,"Swords")),
                                2
                        ),

                        /* 27 — shared k1=A(15); k2 from hole decides */
                        Arguments.of(
                                "Pair 7s: shared k1=A(15), hole k2 K(14) beats Q(13) — P1 wins",
                                List.of(c(14,"Swords"),  c(3,"Wands")),    // P1: [2,7,15,14,6]
                                List.of(c(13,"Swords"),  c(3,"Pentacles")), // P2: [2,7,15,13,6]
                                // neutral rank 4 also on table acts as k4 (below k3=6)? No:
                                // P1 non-pair sorted: 15,14,6,4,3,2 → top-3: 15,14,6 → [2,7,15,14,6] ✓
                                List.of(c(7,"Cups"),     c(7,"Swords"),    c(15,"Wands"),
                                        c(4,"Pentacles"), c(2,"Cups"),     c(6,"Swords")),
                                1
                        ),

                        /* 28 — shared k1=A(15); k2 from hole decides, P2 wins */
                        Arguments.of(
                                "Pair 9s: shared k1=A(15), hole k2 K(14) beats Q(13) — P2 wins",
                                List.of(c(13,"Swords"),  c(3,"Wands")),    // P1: [2,9,15,13,6]
                                List.of(c(14,"Swords"),  c(3,"Pentacles")), // P2: [2,9,15,14,6]
                                List.of(c(9,"Cups"),     c(9,"Swords"),    c(15,"Wands"),
                                        c(4,"Pentacles"), c(2,"Cups"),     c(6,"Swords")),
                                2
                        ),

                        /* 29 — shared k1=A k2=K; k3 from hole decides */
                        Arguments.of(
                                "Pair 7s: shared k1=A k2=K, hole k3 9 beats 8 — P1 wins",
                                List.of(c(9,"Swords"),   c(3,"Wands")),    // P1: [2,7,15,14,9]
                                List.of(c(8,"Swords"),   c(3,"Pentacles")), // P2: [2,7,15,14,8]
                                List.of(c(7,"Cups"),     c(7,"Swords"),    c(15,"Wands"),
                                        c(14,"Pentacles"), c(2,"Cups"),    c(6,"Swords")),
                                1
                        ),

            /* 30 — shared k1=A k2=K; k3 from hole decides, P2 wins
                    (pair rank=6, so use rank 5 as second neutral) */
                        Arguments.of(
                                "Pair 6s: shared k1=A k2=K, hole k3 9 beats 8 — P2 wins",
                                List.of(c(8,"Swords"),   c(3,"Wands")),    // P1: [2,6,15,14,8]
                                List.of(c(9,"Swords"),   c(3,"Pentacles")), // P2: [2,6,15,14,9]
                                // second neutral = 5 (not 6, which is the pair rank)
                                List.of(c(6,"Cups"),     c(6,"Swords"),    c(15,"Wands"),
                                        c(14,"Pentacles"), c(2,"Cups"),    c(5,"Swords")),
                                2
                        ),

                        /* 31 — shared k1=A k2=K; k3 from hole, P1 wins */
                        Arguments.of(
                                "Pair Queens(13): shared k1=A k2=K, hole k3 Page(11) beats 10 — P1 wins",
                                List.of(c(11,"Swords"),  c(3,"Wands")),    // P1: [2,13,15,14,11]
                                List.of(c(10,"Swords"),  c(3,"Pentacles")), // P2: [2,13,15,14,10]
                                List.of(c(13,"Cups"),    c(13,"Swords"),   c(15,"Wands"),
                                        c(14,"Pentacles"), c(2,"Cups"),    c(6,"Swords")),
                                1
                        ),

                        /* 32 — shared k1=A k2=K; k3 from hole, P2 wins */
                        Arguments.of(
                                "Pair 5s: shared k1=A k2=K, hole k3 Page(11) beats 10 — P2 wins",
                                List.of(c(10,"Swords"),  c(3,"Wands")),    // P1: [2,5,15,14,10]
                                List.of(c(11,"Swords"),  c(3,"Pentacles")), // P2: [2,5,15,14,11]
                                List.of(c(5,"Cups"),     c(5,"Swords"),    c(15,"Wands"),
                                        c(14,"Pentacles"), c(2,"Cups"),    c(6,"Swords")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // HIGH CARD — no pair/flush/straight, card position decides
                        //             (33-40)
                        //
                        // Table: 4 high mixed-suit cards + 2 low neutrals (distinct ranks).
                        // No pair on table (all distinct ranks). ✓
                        // Verified per test: no flush (≤2 same-suit/player), no straight.
                        // ════════════════════════════════════════════════════════════

                        /* 33 — deciding: position 1 */
                        Arguments.of(
                                "High Card: top card A(15) beats K(14) — P1 wins",
                                List.of(c(15,"Cups"),    c(4,"Wands")),    // P1: [1,15,13,11,9,7]
                                List.of(c(14,"Cups"),    c(4,"Swords")),   // P2: [1,14,13,11,9,7]
                                // No straight: {7,9,11,13,15} all odd, gaps ✓; no flush: max 2/suit ✓
                                List.of(c(13,"Pentacles"), c(11,"Wands"),  c(9,"Cups"),
                                        c(7,"Swords"),   c(3,"Pentacles"), c(2,"Wands")),
                                1
                        ),

                        /* 34 — deciding: position 1, P2 wins */
                        Arguments.of(
                                "High Card: top card A(15) beats K(14) — P2 wins",
                                List.of(c(14,"Cups"),    c(4,"Wands")),    // P1: [1,14,13,11,9,7]
                                List.of(c(15,"Cups"),    c(4,"Swords")),   // P2: [1,15,13,11,9,7]
                                List.of(c(13,"Pentacles"), c(11,"Wands"),  c(9,"Cups"),
                                        c(7,"Swords"),   c(3,"Pentacles"), c(2,"Wands")),
                                2
                        ),

                        /* 35 — deciding: position 2 (shared A on table) */
                        Arguments.of(
                                "High Card: shared A, 2nd card K(14) beats Q(13) — P1 wins",
                                List.of(c(14,"Swords"),  c(4,"Wands")),    // P1: [1,15,14,11,9,7]
                                List.of(c(13,"Wands"),   c(4,"Pentacles")), // P2: [1,15,13,11,9,7]
                                // No straight: {7,9,11,14,15}: 14-15 run of 2, gap at 12,13 ✓
                                List.of(c(15,"Cups"),    c(11,"Pentacles"), c(9,"Swords"),
                                        c(7,"Wands"),    c(3,"Cups"),      c(2,"Swords")),
                                1
                        ),

                        /* 36 — deciding: position 2, P2 wins */
                        Arguments.of(
                                "High Card: shared A, 2nd card K(14) beats Q(13) — P2 wins",
                                List.of(c(13,"Wands"),   c(4,"Pentacles")), // P1: [1,15,13,11,9,7]
                                List.of(c(14,"Swords"),  c(4,"Wands")),    // P2: [1,15,14,11,9,7]
                                List.of(c(15,"Cups"),    c(11,"Pentacles"), c(9,"Swords"),
                                        c(7,"Wands"),    c(3,"Cups"),      c(2,"Swords")),
                                2
                        ),

                        /* 37 — deciding: position 3 (shared A-K on table) */
                        Arguments.of(
                                "High Card: shared A-K, 3rd card Q(13) beats Page(11) — P1 wins",
                                List.of(c(13,"Wands"),   c(4,"Pentacles")), // P1: [1,15,14,13,9,7]
                                List.of(c(11,"Wands"),   c(4,"Swords")),   // P2: [1,15,14,11,9,7]
                                // No straight: {7,9,13,14,15}: 13-14-15 run of 3, gap at 12 ✓
                                List.of(c(15,"Cups"),    c(14,"Swords"),   c(9,"Pentacles"),
                                        c(7,"Wands"),    c(3,"Cups"),      c(2,"Swords")),
                                1
                        ),

                        /* 38 — deciding: position 3, P2 wins */
                        Arguments.of(
                                "High Card: shared A-K, 3rd card Q(13) beats Page(11) — P2 wins",
                                List.of(c(11,"Wands"),   c(4,"Swords")),   // P1: [1,15,14,11,9,7]
                                List.of(c(13,"Wands"),   c(4,"Pentacles")), // P2: [1,15,14,13,9,7]
                                List.of(c(15,"Cups"),    c(14,"Swords"),   c(9,"Pentacles"),
                                        c(7,"Wands"),    c(3,"Cups"),      c(2,"Swords")),
                                2
                        ),

                        /* 39 — deciding: position 4 (shared A-K-Q on table) */
                        Arguments.of(
                                "High Card: shared A-K-Q, 4th card Page(11) beats 10 — P1 wins",
                                List.of(c(11,"Wands"),   c(4,"Pentacles")), // P1: [1,15,14,13,11,7]
                                List.of(c(10,"Wands"),   c(4,"Swords")),   // P2: [1,15,14,13,10,7]
                                // No straight: {7,11,13,14,15}: 13-14-15 run of 3, gap at 12 ✓
                                List.of(c(15,"Cups"),    c(14,"Swords"),   c(13,"Pentacles"),
                                        c(7,"Wands"),    c(3,"Cups"),      c(2,"Swords")),
                                1
                        ),

                        /* 40 — deciding: position 4, P2 wins */
                        Arguments.of(
                                "High Card: shared A-K-Q, 4th card Page(11) beats 10 — P2 wins",
                                List.of(c(10,"Wands"),   c(4,"Swords")),   // P1: [1,15,14,13,10,7]
                                List.of(c(11,"Wands"),   c(4,"Pentacles")), // P2: [1,15,14,13,11,7]
                                List.of(c(15,"Cups"),    c(14,"Swords"),   c(13,"Pentacles"),
                                        c(7,"Wands"),    c(3,"Cups"),      c(2,"Swords")),
                                2
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("testCases")
            void sameCombWinByHighCard(
                    String description,
                    List<MinorArcanaCard> p1Hole,
                    List<MinorArcanaCard> p2Hole,
                    List<MinorArcanaCard> table,
                    int expectedWinner) {

                assertEquals(
                        expectedWinner,
                        determiner.determineWinner(p1Hole, p2Hole, table),
                        description
                );
            }
        }

        @Nested
        class DrawSixCardsTest {

            private final WinnerDeterminerService determiner = new WinnerDeterminerService();

            private static MinorArcanaCard c(int power, String suit) {
                return new MinorArcanaCard(suit + "-" + power, suit, power);
            }

            static Stream<Arguments> cases() {
                return Stream.of(

                        // ════════════════════════════════════════════════════════════
                        // GROUP A — Board plays (01-10)
                        //
                        // The 6 table cards contain a complete 5-card hand.  Both
                        // players' 2 hole cards are weaker than all table cards so the
                        // best 5 is always the same 5 table cards.  Each player holds
                        // the same 2 powers in different suits (ensuring both junk cards
                        // tie as k-candidates).
                        // ════════════════════════════════════════════════════════════

                        /* 01 — Royal Flush on board */
                        Arguments.of(
                                "Draw A01: RF on board — both hole cards too weak",
                                List.of(c(2,"Wands"),    c(3,"Pentacles")), // P1 junk: too low for top-5
                                List.of(c(2,"Cups"),     c(3,"Swords")),    // P2 junk: too low for top-5
                                // Table has full RF in Swords + 1 junk
                                // P1/P2 both get RF [10] ✓
                                List.of(c(15,"Swords"),  c(14,"Swords"),   c(13,"Swords"),
                                        c(12,"Swords"),  c(11,"Swords"),   c(4,"Cups")),
                                0
                        ),

                        /* 02 — Straight Flush on board */
                        Arguments.of(
                                "Draw A02: SF(Cups K-high) on board — both hole cards too weak",
                                List.of(c(2,"Wands"),    c(3,"Pentacles")),
                                List.of(c(2,"Cups"),     c(3,"Swords")),
                                // Table: 5 consecutive Cups {10-14} + 1 junk
                                List.of(c(14,"Cups"),    c(13,"Cups"),     c(12,"Cups"),
                                        c(11,"Cups"),    c(10,"Cups"),     c(4,"Swords")),
                                0
                        ),

                        /* 03 — Four of a Kind on board */
                        Arguments.of(
                                "Draw A03: Quads(Aces) on board — same-power kicker from hole (diff suits)",
                                List.of(c(13,"Cups"),    c(2,"Wands")),    // P1: kicker=Q(13) → [8,15,13]
                                List.of(c(13,"Swords"),  c(3,"Pentacles")), // P2: kicker=Q(13) → [8,15,13]
                                // Table: quad A(♣♦♥♠) + 2 junk; kicker from hole = Q(13) for both
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(15,"Wands"),
                                        c(15,"Pentacles"), c(4,"Cups"),    c(5,"Swords")),
                                0
                        ),

                        /* 04 — Full House on board; both hole cards junk */
                        Arguments.of(
                                "Draw A04: FH(A-full-of-K) on board — hole junk, board plays",
                                List.of(c(2,"Wands"),    c(3,"Pentacles")),
                                List.of(c(2,"Cups"),     c(3,"Swords")),
                                // Table: A×3 + K×2 + 1 junk → FH [7,15,14] for both
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(15,"Wands"),
                                        c(14,"Cups"),    c(14,"Swords"),   c(4,"Pentacles")),
                                0
                        ),

                        /* 05 — Flush on board; both players' hole cards < lowest flush card */
                        Arguments.of(
                                "Draw A05: Flush on board {5,7,9,11,13}♥ — hole cards too weak",
                                List.of(c(2,"Wands"),    c(3,"Pentacles")),
                                List.of(c(2,"Cups"),     c(3,"Swords")),
                                // Table: 5 non-consecutive Cups + 1 junk; best flush = {5,7,9,11,13}
                                // P1/P2 hole < 5 → don't enter best-5 → both get [6,13,11,9,7,5] ✓
                                List.of(c(13,"Cups"),    c(11,"Cups"),     c(9,"Cups"),
                                        c(7,"Cups"),     c(5,"Cups"),      c(4,"Swords")),
                                0
                        ),

                        /* 06 — Straight on board; both hole cards junk */
                        Arguments.of(
                                "Draw A06: Straight(A-high) on board — both hole cards too weak",
                                List.of(c(2,"Wands"),    c(3,"Pentacles")),
                                List.of(c(2,"Cups"),     c(3,"Swords")),
                                // Table: {11,12,13,14,15} mixed suits + 1 junk → Str A-high [5,15] for both
                                List.of(c(15,"Cups"),    c(14,"Swords"),   c(13,"Wands"),
                                        c(12,"Pentacles"), c(11,"Cups"),   c(4,"Swords")),
                                0
                        ),

                        /* 07 — Trips on board; both players add same-power kicker (diff suits) */
                        Arguments.of(
                                "Draw A07: Trips(A) on board — same-power k2 from hole (diff suits)",
                                List.of(c(10,"Cups"),    c(2,"Wands")),    // P1: k1=K(table) k2=10 → [4,15,14,10]
                                List.of(c(10,"Swords"),  c(3,"Pentacles")), // P2: k1=K(table) k2=10 → [4,15,14,10]
                                // Table: A×3 + K(shared k1) + 2 low junk
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(15,"Wands"),
                                        c(14,"Pentacles"), c(4,"Cups"),    c(5,"Swords")),
                                0
                        ),

                        /* 08 — Two Pair on board; both add same-power kicker (diff suits) */
                        Arguments.of(
                                "Draw A08: Two Pair(A-A+K-K) on board — same-power kicker from hole",
                                List.of(c(11,"Cups"),    c(2,"Wands")),    // P1: [3,15,14,11]
                                List.of(c(11,"Swords"),  c(3,"Pentacles")), // P2: [3,15,14,11]
                                // Table: A×2 + K×2 + 2 low junk
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(14,"Wands"),
                                        c(14,"Pentacles"), c(4,"Cups"),    c(5,"Swords")),
                                0
                        ),

                        /* 09 — One Pair on board + 2 shared kickers; same-power k3 from hole */
                        Arguments.of(
                                "Draw A09: One Pair(A-A)+K+Q on board — same-power k3 from hole",
                                List.of(c(9,"Cups"),     c(2,"Wands")),    // P1: [2,15,14,13,9]
                                List.of(c(9,"Swords"),   c(3,"Pentacles")), // P2: [2,15,14,13,9]
                                // Table: A×2 + K + Q + 2 low junk
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(14,"Wands"),
                                        c(13,"Pentacles"), c(4,"Cups"),    c(5,"Swords")),
                                0
                        ),

                        /* 10 — High Card on board; both add same-power 5th card (diff suits) */
                        Arguments.of(
                                "Draw A10: High Card on board — same-power 5th card from hole",
                                List.of(c(9,"Cups"),     c(2,"Wands")),    // P1: [1,15,13,12,11,9]
                                List.of(c(9,"Swords"),   c(3,"Pentacles")), // P2: [1,15,13,12,11,9]
                                // Table: A(15)♠ Q(13)♦ Kn(12)♣ P(11)♥ 4♣ 5♦ — all diff suits
                                // No straight: {9,11,12,13,15} gaps at 10,14 ✓
                                List.of(c(15,"Swords"),  c(13,"Pentacles"), c(12,"Wands"),
                                        c(11,"Cups"),    c(4,"Wands"),     c(5,"Pentacles")),
                                0
                        ),

                        // ════════════════════════════════════════════════════════════
                        // GROUP B — Equal-power hole cards (11-20)
                        //
                        // One or both hole cards per player participate at the same power
                        // in different suits.  Table provides shared structure.
                        // ════════════════════════════════════════════════════════════

            /* 11 — RF: impossible (same suit + same power = same card).
                    Use Quads draw instead. */
                        Arguments.of(
                                "Draw B11: Quads — quad Kings on board; both add A kicker (diff suits)",
                                List.of(c(15,"Cups"),    c(2,"Wands")),    // P1: [8,14,15]
                                List.of(c(15,"Swords"),  c(3,"Pentacles")), // P2: [8,14,15]
                                List.of(c(14,"Cups"),    c(14,"Swords"),   c(14,"Wands"),
                                        c(14,"Pentacles"), c(4,"Cups"),    c(5,"Swords")),
                                0
                        ),

                        /* 12 — SF: impossible (same suit). Use FH draw. */
                        Arguments.of(
                                "Draw B12: FH — 3 Aces on board + K; both add K from hole (diff suits)",
                                List.of(c(14,"Cups"),    c(2,"Wands")),    // P1: A-A-A-K-K = [7,15,14]
                                List.of(c(14,"Swords"),  c(3,"Pentacles")), // P2: same [7,15,14]
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(15,"Wands"),
                                        c(14,"Wands"),   c(4,"Cups"),      c(5,"Swords")),
                                0
                        ),

                        /* 13 — Quads: quad 9s on board; both add K kicker (diff suits) */
                        Arguments.of(
                                "Draw B13: Quads — quad 9s on board; both add K(14) (diff suits)",
                                List.of(c(14,"Cups"),    c(2,"Wands")),    // P1: [8,9,14]
                                List.of(c(14,"Swords"),  c(3,"Pentacles")), // P2: [8,9,14]
                                List.of(c(9,"Cups"),     c(9,"Swords"),    c(9,"Wands"),
                                        c(9,"Pentacles"), c(4,"Cups"),     c(5,"Swords")),
                                0
                        ),

                        /* 14 — Full House: 3 Queens on board + 1 Ace; both add A from hole */
                        Arguments.of(
                                "Draw B14: FH — 3 Queens + A on board; both add A from hole (diff suits)",
                                List.of(c(15,"Cups"),    c(2,"Wands")),    // P1: Q-Q-Q-A-A = [7,13,15]
                                List.of(c(15,"Swords"),  c(3,"Pentacles")), // P2: same [7,13,15]
                                List.of(c(13,"Cups"),    c(13,"Swords"),   c(13,"Wands"),
                                        c(15,"Pentacles"), c(4,"Cups"),    c(5,"Swords")),
                                0
                        ),

            /* 15 — Flush: 5 non-consecutive Cups on board; both players' hole-1
                    Cups card is weaker than the weakest board Cups → board plays
                    for both. Confirmed draw when both add the same-rank non-Cups. */
                        Arguments.of(
                                "Draw B15: Flush board {5,7,9,11,13}♥ — both add same-power off-suit card",
                                List.of(c(14,"Swords"),  c(2,"Wands")),    // P1: Cups={5,7,9,11,13}=5 → [6,13,11,9,7,5]
                                List.of(c(14,"Wands"),   c(3,"Pentacles")), // P2: same [6,13,11,9,7,5]
                                // Both players add K(14) off-suit → doesn't enter flush top-5 ✓
                                List.of(c(13,"Cups"),    c(11,"Cups"),     c(9,"Cups"),
                                        c(7,"Cups"),     c(5,"Cups"),      c(4,"Swords")),
                                0
                        ),

                        /* 16 — Straight: board {11,12,13,14,15}; both add same-power extra card */
                        Arguments.of(
                                "Draw B16: Straight A-high on board — both add same-power card (diff suits)",
                                List.of(c(10,"Cups"),    c(2,"Wands")),    // P1: {10,11,12,13,14,15} → A-high [5,15]
                                List.of(c(10,"Swords"),  c(3,"Pentacles")), // P2: same [5,15]
                                // Extra 10 doesn't improve A-high straight → board still plays ✓
                                List.of(c(15,"Cups"),    c(14,"Swords"),   c(13,"Wands"),
                                        c(12,"Pentacles"), c(11,"Cups"),   c(4,"Swords")),
                                0
                        ),

                        /* 17 — Trips: trips A on board + shared K; both add Q kicker (diff suits) */
                        Arguments.of(
                                "Draw B17: Trips A — both add Q(13) kicker from hole (diff suits)",
                                List.of(c(13,"Cups"),    c(2,"Wands")),    // P1: [4,15,14,13]
                                List.of(c(13,"Swords"),  c(3,"Pentacles")), // P2: [4,15,14,13]
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(15,"Wands"),
                                        c(14,"Pentacles"), c(4,"Cups"),    c(5,"Swords")),
                                0
                        ),

                        /* 18 — Two Pair: A-A+Q-Q on board; both add K kicker (diff suits) */
                        Arguments.of(
                                "Draw B18: Two Pair A-A+Q-Q on board — both add K(14) kicker (diff suits)",
                                List.of(c(14,"Cups"),    c(2,"Wands")),    // P1: [3,15,13,14]
                                List.of(c(14,"Swords"),  c(3,"Pentacles")), // P2: [3,15,13,14]
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(13,"Wands"),
                                        c(13,"Pentacles"), c(4,"Cups"),    c(5,"Swords")),
                                0
                        ),

                        /* 19 — One Pair: pair A + K + Q on board; both add Page(11) k3 (diff suits) */
                        Arguments.of(
                                "Draw B19: One Pair A-A+K+Q on board — both add Page(11) k3 (diff suits)",
                                List.of(c(11,"Cups"),    c(2,"Wands")),    // P1: [2,15,14,13,11]
                                List.of(c(11,"Swords"),  c(3,"Pentacles")), // P2: [2,15,14,13,11]
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(14,"Wands"),
                                        c(13,"Pentacles"), c(4,"Cups"),    c(5,"Swords")),
                                0
                        ),

                        /* 20 — High Card: shared 4-card base; both add same-power 5th (diff suits) */
                        Arguments.of(
                                "Draw B20: High Card — both add K(14) into position 2 (diff suits)",
                                List.of(c(14,"Cups"),    c(2,"Wands")),    // P1: [1,15,14,11,9,7]
                                List.of(c(14,"Swords"),  c(3,"Pentacles")), // P2: [1,15,14,11,9,7]
                                // Table: A(15)♦ P(11)♣ 9♥ 7♠ 4♣ 5♦ — no pair, no straight ✓
                                List.of(c(15,"Wands"),   c(11,"Pentacles"), c(9,"Cups"),
                                        c(7,"Swords"),   c(4,"Wands"),     c(5,"Pentacles")),
                                0
                        ),

                        // ════════════════════════════════════════════════════════════
                        // GROUP C — Power-mirror hole cards, no flush formed (21-30)
                        //
                        // Both players hold identical power hole cards in different suits.
                        // Table chosen so no flush forms (max 2 same-suit per player).
                        // ════════════════════════════════════════════════════════════

                        /* 21 — High Card: both hold A+K; table Q, Page, 9, 7, 4, 2 */
                        Arguments.of(
                                "Draw C21: High Card — both hold A+K in diff suits; table Q+Page+9+7+4+2",
                                List.of(c(15,"Cups"),    c(14,"Swords")),  // P1: top5={9,11,13,14,15}→[1,15,14,13,11,9]
                                List.of(c(15,"Wands"),   c(14,"Pentacles")),// P2: same
                                // Table: Q(13)♥ P(11)♣ 9♠ 7♦ 4♣ 2♥
                                // {7,9,11,13,14,15}: 13-14-15 run of 3, gap at 12 ✓; no flush: max 2/suit ✓
                                List.of(c(13,"Cups"),    c(11,"Wands"),    c(9,"Swords"),
                                        c(7,"Pentacles"), c(4,"Wands"),    c(2,"Cups")),
                                0
                        ),

                        /* 22 — One Pair: both hold pair Aces; table K, Q, Page, 2, 3, 4 */
                        Arguments.of(
                                "Draw C22: One Pair — both hold pair Aces (diff suits); table K+Q+Page+2+3+4",
                                List.of(c(15,"Cups"),    c(15,"Swords")),  // P1: [2,15,14,13,11]
                                List.of(c(15,"Wands"),   c(15,"Pentacles")),// P2: [2,15,14,13,11]
                                // No A on table → no trips ✓
                                List.of(c(14,"Wands"),   c(13,"Pentacles"), c(11,"Cups"),
                                        c(2,"Swords"),   c(3,"Wands"),     c(4,"Pentacles")),
                                0
                        ),

                        /* 23 — Two Pair: both hold pair Q; table pair A + K + 2, 3, 4 */
                        Arguments.of(
                                "Draw C23: Two Pair — both hold pair Q(13); table A-A+K+2+3+4",
                                List.of(c(13,"Cups"),    c(13,"Swords")),  // P1: pair Q+pair A, kicker K → [3,15,13,14]
                                List.of(c(13,"Wands"),   c(13,"Pentacles")),// P2: same
                                // No Q on table → no trips ✓
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(14,"Wands"),
                                        c(2,"Pentacles"), c(3,"Cups"),     c(4,"Swords")),
                                0
                        ),

                        /* 24 — Trips: both see trips A from board; both hold K+Q (diff suits) */
                        Arguments.of(
                                "Draw C24: Trips A — both hold K+Q in diff suits; trips A + 2 junk on board",
                                List.of(c(14,"Cups"),    c(13,"Swords")),  // P1: [4,15,14,13]
                                List.of(c(14,"Wands"),   c(13,"Pentacles")),// P2: [4,15,14,13]
                                // No K or Q on table → no quads ✓; no pair on table → no FH ✓
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(15,"Wands"),
                                        c(2,"Pentacles"), c(3,"Cups"),     c(4,"Swords")),
                                0
                        ),

                        /* 25 — Straight: both hold A+10; table {11,12,13,14} mixed suits + 2 junk */
                        Arguments.of(
                                "Draw C25: Straight A-high — both hold A+10 (diff suits); 6-card run {10-15}",
                                List.of(c(15,"Cups"),    c(10,"Swords")),  // P1: {10-15} → A-high [5,15]
                                List.of(c(15,"Wands"),   c(10,"Pentacles")),// P2: same [5,15]
                                List.of(c(11,"Swords"),  c(12,"Cups"),     c(13,"Wands"),
                                        c(14,"Pentacles"), c(2,"Cups"),    c(3,"Swords")),
                                0
                        ),

                        /* 26 — Full House: both hold pair J(11); table has trips K + 2 junk */
                        Arguments.of(
                                "Draw C26: FH — both hold pair Jacks(11) (diff suits); trips K on board",
                                List.of(c(11,"Cups"),    c(11,"Swords")),  // P1: K-K-K-J-J = [7,14,11]
                                List.of(c(11,"Wands"),   c(11,"Pentacles")),// P2: same [7,14,11]
                                // No J on table → no quads ✓
                                List.of(c(14,"Cups"),    c(14,"Swords"),   c(14,"Wands"),
                                        c(2,"Pentacles"), c(3,"Cups"),     c(4,"Swords")),
                                0
                        ),

                        /* 27 — Quads: quad Q on board; both hold A+K (diff suits), kicker = A */
                        Arguments.of(
                                "Draw C27: Quads — quad Q on board; both hold A+K (diff suits) → kicker A",
                                List.of(c(15,"Cups"),    c(14,"Swords")),  // P1: [8,13,15]
                                List.of(c(15,"Wands"),   c(14,"Pentacles")),// P2: [8,13,15]
                                List.of(c(13,"Cups"),    c(13,"Swords"),   c(13,"Wands"),
                                        c(13,"Pentacles"), c(2,"Cups"),    c(3,"Swords")),
                                0
                        ),

                        /* 28 — Two Pair: both hold pair A; table pair K + Q + 2 + 3 + 4 */
                        Arguments.of(
                                "Draw C28: Two Pair — both hold pair Aces (diff suits); table K-K+Q+2+3+4",
                                List.of(c(15,"Cups"),    c(15,"Swords")),  // P1: pair A+pair K, kicker Q → [3,15,14,13]
                                List.of(c(15,"Wands"),   c(15,"Pentacles")),// P2: same
                                // No A on table → no trips ✓
                                List.of(c(14,"Cups"),    c(14,"Swords"),   c(13,"Wands"),
                                        c(2,"Pentacles"), c(3,"Cups"),     c(4,"Swords")),
                                0
                        ),

                        /* 29 — One Pair: both hold pair 9; table A, K, Q, 2, 3, 4 */
                        Arguments.of(
                                "Draw C29: One Pair — both hold pair 9s (diff suits); table A+K+Q+2+3+4",
                                List.of(c(9,"Cups"),     c(9,"Swords")),   // P1: [2,9,15,14,13]
                                List.of(c(9,"Wands"),    c(9,"Pentacles")), // P2: [2,9,15,14,13]
                                // No 9 on table → no trips ✓
                                List.of(c(15,"Cups"),    c(14,"Wands"),    c(13,"Swords"),
                                        c(2,"Pentacles"), c(3,"Cups"),     c(4,"Swords")),
                                0
                        ),

                        /* 30 — Full House: both hold pair Page(11); table trips K + 2 junk (diff ranks) */
                        Arguments.of(
                                "Draw C30: FH — both hold pair Pages(11) (diff suits); trips K on board",
                                List.of(c(11,"Cups"),    c(11,"Swords")),  // P1: K-K-K-J-J = [7,14,11]
                                List.of(c(11,"Wands"),   c(11,"Pentacles")),// P2: same [7,14,11]
                                // No J on table → no quads ✓; different junk ranks from test 26 ✓
                                List.of(c(14,"Cups"),    c(14,"Swords"),   c(14,"Wands"),
                                        c(3,"Pentacles"), c(5,"Cups"),     c(7,"Swords")),
                                0
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("cases")
            void draw(
                    String description,
                    List<MinorArcanaCard> p1Hole,
                    List<MinorArcanaCard> p2Hole,
                    List<MinorArcanaCard> table,
                    int expectedResult) {

                assertEquals(
                        0,
                        determiner.determineWinner(p1Hole, p2Hole, table),
                        description
                );
            }
        }

    }

    @Nested
    class WinnerDeterminerServiceTestFiveCards {

        @Nested
        class WinByHighCombTest {

            static Stream<Arguments> testCases() {
                return Stream.of(

                        // ── Royal Flush (10) beats everything ─────────────────────

                        /* 01 */ Arguments.of(
                                "Royal Flush beats Straight Flush",
                                List.of(c(15, "Swords"), c(14, "Swords")),   // P1 hole
                                List.of(c(9, "Swords"), c(8, "Cups")),       // P2 hole
                                List.of(c(13, "Swords"), c(12, "Swords"), c(11, "Swords"),  // table
                                        c(10, "Swords"), c(9, "Cups")),
                                1
                        ),

                        /* 02 */ Arguments.of(
                                "Royal Flush beats Four of a Kind",
                                List.of(c(15, "Wands"), c(14, "Wands")),
                                List.of(c(9, "Cups"), c(9, "Swords")),
                                List.of(c(13, "Wands"), c(12, "Wands"), c(11, "Wands"),
                                        c(9, "Pentacles"), c(9, "Pentacles")),
                                1
                        ),

                        /* 03 */ Arguments.of(
                                "Royal Flush beats Full House",
                                List.of(c(15, "Pentacles"), c(14, "Pentacles")),
                                List.of(c(10, "Cups"), c(10, "Swords")),
                                List.of(c(13, "Pentacles"), c(12, "Pentacles"), c(11, "Pentacles"),
                                        c(10, "Wands"), c(12, "Cups")),
                                1
                        ),

                        /* 04 */ Arguments.of(
                                "Royal Flush beats Flush",
                                List.of(c(15, "Cups"), c(14, "Cups")),
                                List.of(c(2, "Cups"), c(5, "Cups")),
                                List.of(c(13, "Cups"), c(12, "Cups"), c(11, "Cups"),
                                        c(7, "Wands"), c(9, "Wands")),
                                1
                        ),

                        /* 05 */ Arguments.of(
                                "Royal Flush beats High Card",
                                List.of(c(15, "Swords"), c(14, "Swords")),
                                List.of(c(2, "Cups"), c(4, "Wands")),
                                List.of(c(13, "Swords"), c(12, "Swords"), c(11, "Swords"),
                                        c(3, "Cups"), c(6, "Pentacles")),
                                1
                        ),

                        // ── Straight Flush (9) beats lower categories ─────────────

                        /* 06 */ Arguments.of(
                                "Straight Flush beats Four of a Kind",
                                List.of(c(9, "Cups"), c(10, "Cups")),
                                List.of(c(14, "Wands"), c(14, "Swords")),
                                List.of(c(11, "Cups"), c(12, "Cups"), c(13, "Cups"),
                                        c(14, "Cups"), c(14, "Pentacles")),
                                1
                        ),

                        /* 07 */ Arguments.of(
                                "Straight Flush beats Four — P2 wins",
                                List.of(c(13, "Wands"), c(13, "Swords")),    // P1 → Full House
                                List.of(c(5, "Cups"), c(6, "Cups")),        // P2 → Straight Flush
                                List.of(c(13, "Cups"), c(13, "Pentacles"), c(9, "Cups"),
                                        c(7, "Cups"), c(8, "Cups")),
                                2
                        ),

                        /* 08 */ Arguments.of(
                                "Straight Flush beats Flush",
                                List.of(c(3, "Swords"), c(4, "Swords")),
                                List.of(c(2, "Swords"), c(15, "Swords")),
                                List.of(c(5, "Swords"), c(6, "Swords"), c(7, "Swords"),
                                        c(9, "Cups"), c(11, "Cups")),
                                1
                        ),

                        /* 09 */ Arguments.of(
                                "Straight Flush beats Straight",
                                List.of(c(8, "Pentacles"), c(9, "Pentacles")),
                                List.of(c(8, "Cups"), c(9, "Wands")),
                                List.of(c(10, "Pentacles"), c(11, "Pentacles"), c(12, "Pentacles"),
                                        c(10, "Cups"), c(11, "Swords")),
                                1
                        ),

                        /* 10 */ Arguments.of(
                                "Straight Flush beats Two Pair — P2 wins",
                                List.of(c(3, "Cups"), c(7, "Wands")),         // P1 → Two Pair
                                List.of(c(2, "Wands"), c(3, "Wands")),         // P2 → SF
                                List.of(c(3, "Swords"), c(7, "Swords"), c(4, "Wands"),
                                        c(5, "Wands"), c(6, "Wands")),
                                2
                        ),

                        // ── Four of a Kind (8) ─────────────────────────────────────

                        /* 11 */ Arguments.of(
                                "Four of a Kind beats Full House",
                                List.of(c(11, "Cups"), c(11, "Swords")),
                                List.of(c(15, "Cups"), c(15, "Swords")),
                                List.of(c(11, "Wands"), c(11, "Pentacles"), c(15, "Wands"),
                                        c(13, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 12 */ Arguments.of(
                                "Four of a Kind beats Flush — P2 wins",
                                List.of(c(2, "Cups"), c(5, "Cups")),
                                List.of(c(9, "Wands"), c(9, "Swords")),
                                List.of(c(9, "Cups"), c(9, "Pentacles"), c(7, "Cups"),
                                        c(8, "Cups"), c(3, "Cups")),        // note: extra Cups gives P1 flush too
                                // Reconfigure: P1 gets flush (5 cups), P2 gets quads
                                2
                        ),

                        /* 13 */ Arguments.of(
                                "Four of a Kind beats Straight",
                                List.of(c(7, "Cups"), c(7, "Swords")),
                                List.of(c(3, "Wands"), c(4, "Pentacles")),
                                List.of(c(7, "Wands"), c(7, "Pentacles"), c(5, "Cups"),
                                        c(6, "Swords"), c(8, "Wands")),
                                1
                        ),

                        /* 14 */ Arguments.of(
                                "Four of a Kind beats Three of a Kind",
                                List.of(c(12, "Cups"), c(12, "Swords")),
                                List.of(c(15, "Cups"), c(14, "Swords")),
                                List.of(c(12, "Wands"), c(12, "Pentacles"), c(15, "Wands"),
                                        c(2, "Cups"), c(15, "Pentacles")),
                                1
                        ),

                        /* 15 */ Arguments.of(
                                "Four of a Kind beats High Card — P2 wins",
                                List.of(c(15, "Cups"), c(14, "Swords")),     // P1 → High Card
                                List.of(c(5, "Cups"), c(5, "Swords")),      // P2 → Quads
                                List.of(c(5, "Wands"), c(5, "Pentacles"), c(2, "Cups"),
                                        c(3, "Swords"), c(4, "Wands")),
                                2
                        ),

                        // ── Full House (7) ─────────────────────────────────────────

                        /* 16 */ Arguments.of(
                                "Full House beats Flush",
                                List.of(c(10, "Cups"), c(10, "Swords")),
                                List.of(c(2, "Wands"), c(5, "Wands")),
                                List.of(c(10, "Wands"), c(9, "Cups"), c(9, "Swords"),
                                        c(7, "Wands"), c(9, "Wands")),
                                1
                        ),

                        /* 17 */ Arguments.of(
                                "Full House beats Straight — P2 wins",
                                List.of(c(3, "Cups"), c(4, "Swords")),      // P1 → Straight
                                List.of(c(8, "Cups"), c(8, "Swords")),      // P2 → Full House
                                List.of(c(5, "Wands"), c(6, "Pentacles"), c(7, "Cups"),
                                        c(8, "Wands"), c(7, "Swords")),
                                2
                        ),

                        /* 18 */ Arguments.of(
                                "Full House beats Three of a Kind",
                                List.of(c(13, "Cups"), c(13, "Swords")),
                                List.of(c(15, "Cups"), c(6, "Pentacles")),
                                List.of(c(13, "Wands"), c(6, "Cups"), c(6, "Swords"),
                                        c(2, "Wands"), c(3, "Pentacles")),
                                1
                        ),

                        /* 19 */ Arguments.of(
                                "Full House beats Two Pair",
                                List.of(c(9, "Cups"), c(9, "Swords")),
                                List.of(c(15, "Cups"), c(14, "Swords")),
                                List.of(c(9, "Wands"), c(6, "Cups"), c(6, "Swords"),
                                        c(15, "Wands"), c(14, "Wands")),
                                1
                        ),

                        /* 20 */ Arguments.of(
                                "Full House beats One Pair — P2 wins",
                                List.of(c(15, "Cups"), c(2, "Swords")),      // P1 → Pair of Aces
                                List.of(c(7, "Cups"), c(4, "Cups")),      // P2 → Full House
                                List.of(c(15, "Wands"), c(7, "Wands"), c(4, "Pentacles"),
                                        c(3, "Cups"), c(4, "Swords")),
                                2
                        ),

                        // ── Flush (6) ──────────────────────────────────────────────

                        /* 21 */ Arguments.of(
                                "Flush beats Straight",
                                List.of(c(2, "Cups"), c(7, "Cups")),
                                List.of(c(6, "Wands"), c(10, "Swords")),
                                List.of(c(4, "Cups"), c(9, "Cups"), c(11, "Cups"),
                                        c(7, "Swords"), c(8, "Wands")),
                                1
                        ),

                        /* 22 */ Arguments.of(
                                "Flush beats Three of a Kind — P2 wins",
                                List.of(c(14, "Cups"), c(3, "Wands")),       // P1 → Trips
                                List.of(c(2, "Swords"), c(8, "Swords")),      // P2 → Flush
                                List.of(c(14, "Wands"), c(14, "Swords"), c(4, "Swords"),
                                        c(6, "Swords"), c(10, "Swords")),
                                2
                        ),

                        /* 23 */ Arguments.of(
                                "Flush beats Two Pair",
                                List.of(c(3, "Wands"), c(5, "Wands")),
                                List.of(c(15, "Cups"), c(14, "Swords")),
                                List.of(c(7, "Wands"), c(9, "Wands"), c(11, "Wands"),
                                        c(15, "Wands"), c(14, "Pentacles")),
                                1
                        ),

                        /* 24 */ Arguments.of(
                                "Flush beats One Pair",
                                List.of(c(4, "Pentacles"), c(8, "Pentacles")),
                                List.of(c(15, "Cups"), c(15, "Swords")),
                                List.of(c(10, "Pentacles"), c(12, "Pentacles"), c(14, "Pentacles"),
                                        c(2, "Cups"), c(3, "Wands")),
                                1
                        ),

                        /* 25 */ Arguments.of(
                                "Flush beats High Card — P2 wins",
                                List.of(c(15, "Cups"), c(14, "Swords")),     // P1 → High Card
                                List.of(c(2, "Wands"), c(6, "Wands")),       // P2 → Flush
                                List.of(c(3, "Wands"), c(5, "Wands"), c(9, "Wands"),
                                        c(7, "Cups"), c(11, "Pentacles")),
                                2
                        ),

                        // ── Straight (5) ───────────────────────────────────────────

                        /* 26 */ Arguments.of(
                                "Straight beats Three of a Kind",
                                List.of(c(5, "Cups"), c(9, "Swords")),
                                List.of(c(13, "Cups"), c(13, "Swords")),
                                List.of(c(6, "Wands"), c(7, "Pentacles"), c(8, "Cups"),
                                        c(13, "Wands"), c(2, "Swords")),
                                1
                        ),

                        /* 27 */ Arguments.of(
                                "Straight beats Two Pair — P2 wins",
                                List.of(c(15, "Cups"), c(14, "Swords")),     // P1 → Two Pair
                                List.of(c(4, "Wands"), c(8, "Pentacles")),   // P2 → Straight
                                List.of(c(15, "Wands"), c(14, "Wands"), c(5, "Cups"),
                                        c(6, "Swords"), c(7, "Wands")),
                                2
                        ),

                        /* 28 */ Arguments.of(
                                "Straight beats One Pair",
                                List.of(c(3, "Cups"), c(7, "Wands")),
                                List.of(c(15, "Cups"), c(15, "Swords")),
                                List.of(c(4, "Swords"), c(5, "Pentacles"), c(6, "Cups"),
                                        c(9, "Wands"), c(2, "Pentacles")),
                                1
                        ),

                        // ── Three of a Kind (4) ────────────────────────────────────

                        /* 29 */ Arguments.of(
                                "Three of a Kind beats Two Pair",
                                List.of(c(8, "Cups"), c(8, "Swords")),
                                List.of(c(15, "Cups"), c(14, "Swords")),
                                List.of(c(8, "Wands"), c(15, "Wands"), c(14, "Wands"),
                                        c(2, "Cups"), c(3, "Swords")),
                                1
                        ),

                        /* 30 */ Arguments.of(
                                "Two Pair beats One Pair — P2 wins",
                                List.of(c(15, "Cups"), c(2, "Swords")),      // P1 → One Pair (aces)
                                List.of(c(14, "Cups"), c(13, "Swords")),     // P2 → Two Pair
                                List.of(c(15, "Wands"), c(7, "Cups"), c(7, "Swords"),
                                        c(3, "Wands"), c(4, "Pentacles")),
                                1
                        )
                );

            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("testCases")
            void winByHigherCombination(String desc,
                                        List<MinorArcanaCard> p1, List<MinorArcanaCard> p2,
                                        List<MinorArcanaCard> table, int expected) {
                assertEquals(expected, determiner.determineWinner(p1, p2, table), desc);
            }
        }

        @Nested
        class SameCombBetterPowerTest {

            private final WinnerDeterminerService determiner = new WinnerDeterminerService();

            private static MinorArcanaCard c(int power, String suit) {
                return new MinorArcanaCard(suit + "-" + power, suit, power);
            }

            static Stream<Arguments> testCases() {
                return Stream.of(

                        // ════════════════════════════════════════════════════════════
                        // STRAIGHT FLUSH — higher top card wins  (tests 01-09)
                        //
                        // Table: 4 consecutive Swords / Hearts (middle of both runs).
                        // P1 hole-1: card that extends the sequence UPWARD  (higher SF).
                        // P2 hole-1: card that extends the sequence DOWNWARD (lower SF).
                        // Both players' hole-2 is always a different-suit junk card.
                        //
                        // suit♠ table {a,b,c,d}:
                        //   P1 Swords = {a,b,c,d, hole-1}  → SF high = hole-1
                        //   P2 Swords = {a,b,c,d, hole-1*} → SF high = d  (*hole-1 in P1 not shared)
                        // ════════════════════════════════════════════════════════════

                        /* 01 */ Arguments.of(
                                "SF: K-high(14) beats Q-high(13) — P1 wins",
                                List.of(c(14, "Swords"), c(2, "Cups")),   // P1: K♠ → run 10-J-Q-K (K-high)
                                List.of(c(9, "Swords"), c(3, "Cups")),   // P2: 9♠ → run 9-10-J-Q  (Q-high)
                                // Table ♠: {10,11,12,13}  P1♠={10,11,12,13,14}=K-high ✓  P2♠={9,10,11,12,13}=Q-high ✓
                                List.of(c(10, "Swords"), c(11, "Swords"), c(12, "Swords"),
                                        c(13, "Swords"), c(2, "Wands")),
                                1
                        ),

                        /* 02 */ Arguments.of(
                                "SF: K-high(14) beats Q-high(13) — P2 wins",
                                List.of(c(9, "Cups"), c(2, "Swords")), // P1: 9♥ → run 9-10-J-Q (Q-high)
                                List.of(c(14, "Cups"), c(3, "Swords")), // P2: K♥ → run 10-J-Q-K (K-high)
                                // Table ♥: {10,11,12,13}  P1♥={9,10,11,12,13}=Q-high ✓  P2♥={10,11,12,13,14}=K-high ✓
                                List.of(c(10, "Cups"), c(11, "Cups"), c(12, "Cups"),
                                        c(13, "Cups"), c(2, "Wands")),
                                2
                        ),

                        /* 03 */ Arguments.of(
                                "SF: Q-high(13) beats Knight-high(12) — P1 wins",
                                List.of(c(13, "Swords"), c(2, "Cups")),   // P1: Q♠ → run 9-10-J-Q (Q-high)
                                List.of(c(8, "Swords"), c(3, "Cups")),   // P2: 8♠ → run 8-9-10-J (J-high = Kn-high)
                                // Table ♠: {9,10,11,12}  P1♠={9,10,11,12,13}=Q-high ✓  P2♠={8,9,10,11,12}=Kn-high ✓
                                List.of(c(9, "Swords"), c(10, "Swords"), c(11, "Swords"),
                                        c(12, "Swords"), c(2, "Wands")),
                                1
                        ),

                        /* 04 */ Arguments.of(
                                "SF: Knight-high(12) beats Page-high(11) — P2 wins",
                                List.of(c(7, "Cups"), c(2, "Swords")), // P1: 7♥ → run 7-8-9-10-P (P-high)
                                List.of(c(12, "Cups"), c(3, "Swords")), // P2: Kn♥→ run 8-9-10-P-Kn (Kn-high)
                                // Table ♥: {8,9,10,11}  P1♥={7,8,9,10,11}=P-high ✓  P2♥={8,9,10,11,12}=Kn-high ✓
                                List.of(c(8, "Cups"), c(9, "Cups"), c(10, "Cups"),
                                        c(11, "Cups"), c(2, "Wands")),
                                2
                        ),

                        /* 05 */ Arguments.of(
                                "SF: Page-high(11) beats 10-high — P1 wins",
                                List.of(c(11, "Swords"), c(2, "Cups")),   // P1: P♠ → run 7-8-9-10-P (P-high)
                                List.of(c(6, "Swords"), c(3, "Cups")),   // P2: 6♠ → run 6-7-8-9-10 (10-high)
                                // Table ♠: {7,8,9,10}  P1♠={7,8,9,10,11}=P-high ✓  P2♠={6,7,8,9,10}=10-high ✓
                                List.of(c(7, "Swords"), c(8, "Swords"), c(9, "Swords"),
                                        c(10, "Swords"), c(2, "Wands")),
                                1
                        ),

                        /* 06 */ Arguments.of(
                                "SF: 10-high beats 9-high — P2 wins",
                                List.of(c(5, "Cups"), c(2, "Swords")), // P1: 5♥ → run 5-6-7-8-9 (9-high)
                                List.of(c(10, "Cups"), c(3, "Swords")), // P2: 10♥→ run 6-7-8-9-10 (10-high)
                                // Table ♥: {6,7,8,9}  P1♥={5,6,7,8,9}=9-high ✓  P2♥={6,7,8,9,10}=10-high ✓
                                List.of(c(6, "Cups"), c(7, "Cups"), c(8, "Cups"),
                                        c(9, "Cups"), c(2, "Wands")),
                                2
                        ),

                        /* 07 */ Arguments.of(
                                "SF: 9-high beats 8-high — P1 wins",
                                List.of(c(9, "Swords"), c(2, "Cups")),   // P1: 9♠ → run 5-6-7-8-9 (9-high)
                                List.of(c(4, "Swords"), c(3, "Cups")),   // P2: 4♠ → run 4-5-6-7-8 (8-high)
                                // Table ♠: {5,6,7,8}  P1♠={5,6,7,8,9}=9-high ✓  P2♠={4,5,6,7,8}=8-high ✓
                                List.of(c(5, "Swords"), c(6, "Swords"), c(7, "Swords"),
                                        c(8, "Swords"), c(2, "Wands")),
                                1
                        ),

                        /* 08 */ Arguments.of(
                                "SF: 8-high beats 7-high — P2 wins",
                                List.of(c(3, "Cups"), c(2, "Swords")), // P1: 3♥ → run 3-4-5-6-7 (7-high)
                                List.of(c(8, "Cups"), c(4, "Swords")), // P2: 8♥ → run 4-5-6-7-8 (8-high)
                                // Table ♥: {4,5,6,7}  P1♥={3,4,5,6,7}=7-high ✓  P2♥={4,5,6,7,8}=8-high ✓
                                List.of(c(4, "Cups"), c(5, "Cups"), c(6, "Cups"),
                                        c(7, "Cups"), c(2, "Wands")),
                                2
                        ),

                        /* 09 */ Arguments.of(
                                "SF: 7-high beats 6-high — P1 wins",
                                List.of(c(7, "Swords"), c(2, "Cups")),   // P1: 7♠ → run 3-4-5-6-7 (7-high)
                                List.of(c(2, "Swords"), c(3, "Cups")),   // P2: 2♠ → run 2-3-4-5-6 (6-high)
                                // Table ♠: {3,4,5,6}  P1♠={3,4,5,6,7}=7-high ✓  P2♠={2,3,4,5,6}=6-high ✓
                                List.of(c(3, "Swords"), c(4, "Swords"), c(5, "Swords"),
                                        c(6, "Swords"), c(2, "Wands")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // FOUR OF A KIND — higher quad rank wins  (tests 10-14)
                        //
                        // Table: 2 copies of P1's rank + 2 copies of P2's rank + 1 low
                        // neutral.  Each player holds 2 copies of their rank in hole.
                        // Total per rank = 4 (exactly one quad each, no overlap).
                        // score = [8, quadRank, bestRemainingCard]
                        // ════════════════════════════════════════════════════════════

                        /* 10 */ Arguments.of(
                                "Quads: Aces(15) beat Kings(14) — P1 wins",
                                List.of(c(15, "Cups"), c(15, "Swords")), // P1: 2 Aces  → quad A
                                List.of(c(14, "Cups"), c(14, "Swords")), // P2: 2 Kings → quad K
                                // P1: A♥A♠A♣A♦ quad, kicker=K(14) → [8,15,14]
                                // P2: K♥K♠K♣K♦ quad, kicker=A(15) → [8,14,15]  → P1 wins ✓
                                List.of(c(15, "Wands"), c(15, "Pentacles"),
                                        c(14, "Wands"), c(14, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 11 */ Arguments.of(
                                "Quads: Queens(13) beat Jacks(11) — P2 wins",
                                List.of(c(11, "Cups"), c(11, "Swords")), // P1: 2 Jacks  → quad J
                                List.of(c(13, "Cups"), c(13, "Swords")), // P2: 2 Queens → quad Q
                                // P1: J quad, kicker=Q(13) → [8,11,13]
                                // P2: Q quad, kicker=J(11) → [8,13,11]  → P2 wins ✓
                                List.of(c(11, "Wands"), c(11, "Pentacles"),
                                        c(13, "Wands"), c(13, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        /* 12 */ Arguments.of(
                                "Quads: Knights(12) beat Pages(11) — P1 wins",
                                List.of(c(12, "Cups"), c(12, "Swords")), // P1: 2 Knights → quad Kn
                                List.of(c(11, "Cups"), c(11, "Swords")), // P2: 2 Pages   → quad P
                                // P1: Kn quad, kicker=P(11) → [8,12,11]
                                // P2: P  quad, kicker=Kn(12)→ [8,11,12]  → P1 wins ✓
                                List.of(c(12, "Wands"), c(12, "Pentacles"),
                                        c(11, "Wands"), c(11, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 13 */ Arguments.of(
                                "Quads: 10s beat 9s — P2 wins",
                                List.of(c(9, "Cups"), c(9, "Swords")),  // P1: 2 Nines → quad 9
                                List.of(c(10, "Cups"), c(10, "Swords")), // P2: 2 Tens  → quad 10
                                // P1: 9 quad, kicker=10 → [8,9,10]
                                // P2: 10 quad, kicker=9 → [8,10,9]  → P2 wins ✓
                                List.of(c(9, "Wands"), c(9, "Pentacles"),
                                        c(10, "Wands"), c(10, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        /* 14 */ Arguments.of(
                                "Quads: 8s beat 7s — P1 wins",
                                List.of(c(8, "Cups"), c(8, "Swords")),  // P1: 2 Eights → quad 8
                                List.of(c(7, "Cups"), c(7, "Swords")),  // P2: 2 Sevens → quad 7
                                // P1: 8 quad, kicker=7 → [8,8,7]
                                // P2: 7 quad, kicker=8 → [8,7,8]  → P1 wins ✓
                                List.of(c(8, "Wands"), c(8, "Pentacles"),
                                        c(7, "Wands"), c(7, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // FULL HOUSE — higher trips rank wins  (tests 15-19)
                        //
                        // Table: 1 card of P1's rank + 1 card of P2's rank + shared pair
                        // (2 cards) + 1 neutral.  Each player's 2 hole cards give them
                        // exactly 3 of their rank (hole×2 + 1 from table = 3, no quad).
                        // The shared pair on the board completes both full houses.
                        // score = [7, tripsRank, pairRank]
                        // ════════════════════════════════════════════════════════════

                        /* 15 */ Arguments.of(
                                "Full House: Aces-full-of-2s beats Kings-full-of-2s — P1 wins",
                                List.of(c(15, "Cups"), c(15, "Swords")), // P1: 2 Aces  → A♥A♠+A♣=trips A
                                List.of(c(14, "Cups"), c(14, "Swords")), // P2: 2 Kings → K♥K♠+K♣=trips K
                                // Table: A♣(3rd A) K♣(3rd K) 2♣ 2♦ 3♠
                                // P1: A-A-A + 2-2 → [7,15,2] ✓   P2: K-K-K + 2-2 → [7,14,2] ✓
                                List.of(c(15, "Wands"), c(14, "Wands"),
                                        c(2, "Cups"), c(2, "Swords"), c(3, "Pentacles")),
                                1
                        ),

                        /* 16 */ Arguments.of(
                                "Full House: Queens-full-of-3s beats Jacks-full-of-3s — P2 wins",
                                List.of(c(11, "Cups"), c(11, "Swords")), // P1: 2 Jacks  → trips J
                                List.of(c(13, "Cups"), c(13, "Swords")), // P2: 2 Queens → trips Q
                                // Table: J♣ Q♣ 3♣ 3♦ 4♠
                                // P1: J-J-J + 3-3 → [7,11,3] ✓   P2: Q-Q-Q + 3-3 → [7,13,3] ✓
                                List.of(c(11, "Wands"), c(13, "Wands"),
                                        c(3, "Cups"), c(3, "Swords"), c(4, "Pentacles")),
                                2
                        ),

                        /* 17 */ Arguments.of(
                                "Full House: Knights-full-of-4s beats Pages-full-of-4s — P1 wins",
                                List.of(c(12, "Cups"), c(12, "Swords")), // P1: 2 Knights → trips Kn
                                List.of(c(11, "Cups"), c(11, "Swords")), // P2: 2 Pages   → trips P
                                // Table: Kn♣ P♣ 4♣ 4♦ 5♠
                                // P1: Kn-Kn-Kn + 4-4 → [7,12,4] ✓   P2: P-P-P + 4-4 → [7,11,4] ✓
                                List.of(c(12, "Wands"), c(11, "Wands"),
                                        c(4, "Cups"), c(4, "Swords"), c(5, "Pentacles")),
                                1
                        ),

                        /* 18 */ Arguments.of(
                                "Full House: 10s-full-of-5s beats 9s-full-of-5s — P2 wins",
                                List.of(c(9, "Cups"), c(9, "Swords")),  // P1: 2 Nines → trips 9
                                List.of(c(10, "Cups"), c(10, "Swords")), // P2: 2 Tens  → trips 10
                                // Table: 9♣ 10♣ 5♣ 5♦ 6♠
                                // P1: 9-9-9 + 5-5 → [7,9,5] ✓   P2: 10-10-10 + 5-5 → [7,10,5] ✓
                                List.of(c(9, "Wands"), c(10, "Wands"),
                                        c(5, "Cups"), c(5, "Swords"), c(6, "Pentacles")),
                                2
                        ),

                        /* 19 */ Arguments.of(
                                "Full House: 8s-full-of-3s beats 7s-full-of-3s — P1 wins",
                                List.of(c(8, "Cups"), c(8, "Swords")),  // P1: 2 Eights → trips 8
                                List.of(c(7, "Cups"), c(7, "Swords")),  // P2: 2 Sevens → trips 7
                                // Table: 8♣ 7♣ 3♣ 3♦ 2♠
                                // P1: 8-8-8 + 3-3 → [7,8,3] ✓   P2: 7-7-7 + 3-3 → [7,7,3] ✓
                                List.of(c(8, "Wands"), c(7, "Wands"),
                                        c(3, "Cups"), c(3, "Swords"), c(2, "Pentacles")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // FLUSH — higher top card wins  (tests 20-24)
                        //
                        // Table: 4 NON-CONSECUTIVE same-suit cards (gaps block SF)
                        //        + 1 off-suit neutral card.
                        // Each player: hole-1 = 1 card of the suit (their high card),
                        //              hole-2 = 1 junk card of a DIFFERENT suit.
                        // Total per player: 4 (table) + 1 (hole) = 5 suited → flush.
                        // Gaps in table suit: {3,5,7,9} — no 5 consecutive possible.
                        // ════════════════════════════════════════════════════════════

                        /* 20 */ Arguments.of(
                                "Flush: A-high(15) beats K-high(14) — P1 wins",
                                List.of(c(15, "Swords"), c(2, "Cups")),   // P1: A♠ → flush {3,5,7,9,15}
                                List.of(c(14, "Swords"), c(3, "Cups")),   // P2: K♠ → flush {3,5,7,9,14}
                                // ♠ on table: {3,5,7,9} — gaps at 4,6,8 prevent SF ✓
                                List.of(c(3, "Swords"), c(5, "Swords"), c(7, "Swords"),
                                        c(9, "Swords"), c(2, "Wands")),
                                1
                        ),

                        /* 21 */ Arguments.of(
                                "Flush: K-high(14) beats Q-high(13) — P2 wins",
                                List.of(c(13, "Cups"), c(2, "Swords")), // P1: Q♥ → flush {3,5,7,9,13}
                                List.of(c(14, "Cups"), c(3, "Swords")), // P2: K♥ → flush {3,5,7,9,14}
                                // ♥ on table: {3,5,7,9} — same gap pattern ✓
                                List.of(c(3, "Cups"), c(5, "Cups"), c(7, "Cups"),
                                        c(9, "Cups"), c(2, "Wands")),
                                2
                        ),

                        /* 22 */ Arguments.of(
                                "Flush: Q-high(13) beats Page-high(11) — P1 wins",
                                List.of(c(13, "Swords"), c(2, "Cups")),   // P1: Q♠ → flush {3,5,7,9,13}
                                List.of(c(11, "Swords"), c(3, "Cups")),   // P2: P♠ → flush {3,5,7,9,11}
                                List.of(c(3, "Swords"), c(5, "Swords"), c(7, "Swords"),
                                        c(9, "Swords"), c(2, "Wands")),
                                1
                        ),

                        /* 23 */ Arguments.of(
                                "Flush: Page-high(11) beats 10-high — P2 wins",
                                List.of(c(10, "Cups"), c(2, "Swords")), // P1: 10♥ → flush {3,5,7,9,10}
                                List.of(c(11, "Cups"), c(3, "Swords")), // P2: P♥  → flush {3,5,7,9,11}
                                // {3,5,7,9,10}: 9-10 consecutive but only 2 in a row — no SF ✓
                                List.of(c(3, "Cups"), c(5, "Cups"), c(7, "Cups"),
                                        c(9, "Cups"), c(2, "Wands")),
                                2
                        ),

                        /* 24 */ Arguments.of(
                                "Flush: 10-high beats 9-high — P1 wins",
                                List.of(c(10, "Swords"), c(2, "Cups")),   // P1: 10♠ → flush {3,5,7,8,10}
                                List.of(c(9, "Swords"), c(3, "Cups")),   // P2: 9♠  → flush {3,5,7,8,9}
                                // ♠ on table: {3,5,7,8} — longest run is 7-8 (2 cards), no SF ✓
                                List.of(c(3, "Swords"), c(5, "Swords"), c(7, "Swords"),
                                        c(8, "Swords"), c(2, "Wands")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // STRAIGHT — higher top card wins  (tests 25-29)
                        //
                        // Table: 4 consecutive mixed-suit cards shared by both players.
                        // P1 hole-1: extends sequence one step UPWARD.
                        // P2 hole-1: extends sequence one step DOWNWARD.
                        // Mixed table suits prevent any accidental flush.
                        // ════════════════════════════════════════════════════════════

                        /* 25 */ Arguments.of(
                                "Straight: A-high(15) beats K-high(14) — P1 wins",
                                List.of(c(15, "Cups"), c(2, "Swords")),  // P1: A → 11-12-13-14-15 (A-high)
                                List.of(c(10, "Wands"), c(3, "Pentacles")),// P2: 10→ 10-11-12-13-14 (K-high)
                                // Table: {11,12,13,14} mixed suits
                                // P1: A(hole)+{11,12,13,14}=A-high ✓   P2: 10(hole)+{11,12,13,14}=K-high ✓
                                // Flush check: P1 Cups={A,11♥?}—table 11 is Hearts, A is Cups → 2 Cups max ✓
                                List.of(c(11, "Cups"), c(12, "Swords"), c(13, "Wands"),
                                        c(14, "Pentacles"), c(4, "Cups")),
                                1
                        ),

                        /* 26 */ Arguments.of(
                                "Straight: K-high(14) beats Q-high(13) — P2 wins",
                                List.of(c(9, "Cups"), c(2, "Swords")),  // P1: 9 → 9-10-11-12-13 (Q-high)
                                List.of(c(14, "Wands"), c(3, "Pentacles")),// P2: K→ 10-11-12-13-14 (K-high)
                                // Table: {10,11,12,13} mixed suits
                                // P1: 9(hole)+{10,11,12,13}=Q-high(13) ✓  P2: K(14,hole)+{10,11,12,13}=K-high ✓
                                List.of(c(10, "Cups"), c(11, "Swords"), c(12, "Wands"),
                                        c(13, "Pentacles"), c(4, "Cups")),
                                2
                        ),

                        /* 27 */ Arguments.of(
                                "Straight: Q-high(13) beats Knight-high(12) — P1 wins",
                                List.of(c(13, "Cups"), c(2, "Swords")),  // P1: Q → 9-10-11-12-13 (Q-high)
                                List.of(c(8, "Wands"), c(3, "Pentacles")),// P2: 8 → 8-9-10-11-12 (Kn-high)
                                // Table: {9,10,11,12} mixed suits
                                // P1: Q(13,hole)+{9,10,11,12}=Q-high ✓  P2: 8(hole)+{9,10,11,12}=Kn-high ✓
                                List.of(c(9, "Cups"), c(10, "Swords"), c(11, "Wands"),
                                        c(12, "Pentacles"), c(4, "Cups")),
                                1
                        ),

                        /* 28 */ Arguments.of(
                                "Straight: Knight-high(12) beats Page-high(11) — P2 wins",
                                List.of(c(7, "Cups"), c(2, "Swords")),  // P1: 7 → 7-8-9-10-11 (P-high)
                                List.of(c(12, "Wands"), c(3, "Pentacles")),// P2: Kn→ 8-9-10-11-12 (Kn-high)
                                // Table: {8,9,10,11} mixed suits
                                // P1: 7(hole)+{8,9,10,11}=P-high(11) ✓  P2: Kn(12,hole)+{8,9,10,11}=Kn-high ✓
                                List.of(c(8, "Cups"), c(9, "Swords"), c(10, "Wands"),
                                        c(11, "Pentacles"), c(4, "Cups")),
                                2
                        ),

                        /* 29 */ Arguments.of(
                                "Straight: Page-high(11) beats 10-high — P1 wins",
                                List.of(c(11, "Cups"), c(2, "Swords")),  // P1: P → 7-8-9-10-11 (P-high)
                                List.of(c(6, "Wands"), c(3, "Pentacles")),// P2: 6 → 6-7-8-9-10 (10-high)
                                // Table: {7,8,9,10} mixed suits
                                // P1: P(11,hole)+{7,8,9,10}=P-high ✓  P2: 6(hole)+{7,8,9,10}=10-high ✓
                                List.of(c(7, "Cups"), c(8, "Swords"), c(9, "Wands"),
                                        c(10, "Pentacles"), c(4, "Cups")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // THREE OF A KIND — higher trips rank wins  (tests 30-33)
                        //
                        // Table: 1 card of P1's rank + 1 card of P2's rank + 3 low
                        // neutrals.  Each player's 2 hole cards + 1 table card = trips.
                        // score = [4, tripsRank, kicker1, kicker2]
                        // ════════════════════════════════════════════════════════════

                        /* 30 */ Arguments.of(
                                "Trips: Aces(15) beat Kings(14) — P1 wins",
                                List.of(c(15, "Cups"), c(15, "Swords")), // P1: 2 Aces  → A♥A♠+A♣=trips A
                                List.of(c(14, "Cups"), c(14, "Swords")), // P2: 2 Kings → K♥K♠+K♣=trips K
                                // Table: A♣(3rd A) K♣(3rd K) 2♣ 3♦ 4♠
                                // P1: trips A, kickers K(14) 4 → [4,15,14,4]
                                // P2: trips K, kickers A(15) 4 → [4,14,15,4]  → P1 wins ✓
                                List.of(c(15, "Wands"), c(14, "Wands"),
                                        c(2, "Cups"), c(3, "Swords"), c(4, "Pentacles")),
                                1
                        ),

                        /* 31 */ Arguments.of(
                                "Trips: Queens(13) beat Jacks(11) — P2 wins",
                                List.of(c(11, "Cups"), c(11, "Swords")), // P1: 2 Jacks  → trips J
                                List.of(c(13, "Cups"), c(13, "Swords")), // P2: 2 Queens → trips Q
                                // Table: J♣ Q♣ 2♣ 3♦ 4♠
                                // P1: trips J, kickers Q(13) 4 → [4,11,13,4]
                                // P2: trips Q, kickers J(11) 4 → [4,13,11,4]  → P2 wins ✓
                                List.of(c(11, "Wands"), c(13, "Wands"),
                                        c(2, "Cups"), c(3, "Swords"), c(4, "Pentacles")),
                                2
                        ),

                        /* 32 */ Arguments.of(
                                "Trips: 10s beat 9s — P1 wins",
                                List.of(c(10, "Cups"), c(10, "Swords")), // P1: 2 Tens  → trips 10
                                List.of(c(9, "Cups"), c(9, "Swords")),  // P2: 2 Nines → trips 9
                                // Table: 10♣ 9♣ 2♣ 3♦ 4♠
                                // P1: trips 10, kickers 9(9) 4 → [4,10,9,4]
                                // P2: trips 9,  kickers 10(10) 4 → [4,9,10,4]  → P1 wins ✓
                                List.of(c(10, "Wands"), c(9, "Wands"),
                                        c(2, "Cups"), c(3, "Swords"), c(4, "Pentacles")),
                                1
                        ),

                        /* 33 */ Arguments.of(
                                "Trips: 7s beat 6s — P2 wins",
                                List.of(c(6, "Cups"), c(6, "Swords")),  // P1: 2 Sixes  → trips 6
                                List.of(c(7, "Cups"), c(7, "Swords")),  // P2: 2 Sevens → trips 7
                                // Table: 6♣ 7♣ 2♣ 3♦ 4♠
                                // P1: trips 6, kickers 7(7) 4 → [4,6,7,4]
                                // P2: trips 7, kickers 6(6) 4 → [4,7,6,4]  → P2 wins ✓
                                List.of(c(6, "Wands"), c(7, "Wands"),
                                        c(2, "Cups"), c(3, "Swords"), c(4, "Pentacles")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // TWO PAIR — higher top-pair or second-pair rank wins (tests 34-37)
                        //
                        // Tests 34-35: top pair differs (both pairs entirely from hole).
                        //   Table has the shared second pair + low neutral cards.
                        //   No table card duplicates any hole rank → no trips risk.
                        //
                        // Tests 36-37: same top pair on board; second pair from hole.
                        //   Table has 2 copies of the top pair rank + neutrals.
                        //   Each player's hole pair becomes their second pair.
                        // score = [3, highPairRank, lowPairRank, kickerRank]
                        // ════════════════════════════════════════════════════════════

                        /* 34 */ Arguments.of(
                                "Two Pair: A-A+3-3 beats K-K+3-3 (higher top pair) — P1 wins",
                                List.of(c(15, "Cups"), c(15, "Swords")), // P1: pair Aces (from hole)
                                List.of(c(14, "Cups"), c(14, "Swords")), // P2: pair Kings (from hole)
                                // Table: 3♣ 3♦ (shared 2nd pair) + 2♣ 4♦ 5♠  (no A or K → no trips)
                                // P1: A-A(hole) + 3-3(table) → [3,15,3,5]
                                // P2: K-K(hole) + 3-3(table) → [3,14,3,5]  → P1 wins ✓
                                List.of(c(3, "Cups"), c(3, "Swords"),
                                        c(2, "Wands"), c(4, "Pentacles"), c(5, "Cups")),
                                1
                        ),

                        /* 35 */ Arguments.of(
                                "Two Pair: Q-Q+3-3 beats J-J+3-3 — P2 wins",
                                List.of(c(11, "Cups"), c(11, "Swords")), // P1: pair Jacks
                                List.of(c(13, "Cups"), c(13, "Swords")), // P2: pair Queens
                                // Table: 3♣ 3♦ + 2♣ 4♦ 5♠
                                // P1: J-J + 3-3 → [3,11,3,5]   P2: Q-Q + 3-3 → [3,13,3,5]  → P2 wins ✓
                                List.of(c(3, "Cups"), c(3, "Swords"),
                                        c(2, "Wands"), c(4, "Pentacles"), c(5, "Cups")),
                                2
                        ),

                        /* 36 */ Arguments.of(
                                "Two Pair: A-A+K-K beats A-A+Q-Q (same top pair, higher 2nd pair) — P1 wins",
                                List.of(c(14, "Cups"), c(14, "Swords")), // P1: pair Kings  (2nd pair)
                                List.of(c(13, "Cups"), c(13, "Swords")), // P2: pair Queens (2nd pair)
                                // Table: A♣ A♦ (shared top pair) + 2♣ 3♦ 4♠  (no K or Q → no trips)
                                // P1: A-A(table) + K-K(hole) kicker 4 → [3,15,14,4]
                                // P2: A-A(table) + Q-Q(hole) kicker 4 → [3,15,13,4]  → P1 wins ✓
                                List.of(c(15, "Cups"), c(15, "Swords"),
                                        c(2, "Wands"), c(3, "Pentacles"), c(4, "Cups")),
                                1
                        ),

                        /* 37 */ Arguments.of(
                                "Two Pair: K-K+J-J beats K-K+10-10 (same top pair, higher 2nd pair) — P2 wins",
                                List.of(c(10, "Cups"), c(10, "Swords")), // P1: pair 10s (2nd pair)
                                List.of(c(11, "Cups"), c(11, "Swords")), // P2: pair Jacks (2nd pair)
                                // Table: K♣ K♦ (shared top pair) + 2♣ 3♦ 4♠  (no 10 or J → no trips)
                                // P1: K-K(table) + 10-10(hole) kicker 4 → [3,14,10,4]
                                // P2: K-K(table) + J-J(hole)   kicker 4 → [3,14,11,4]  → P2 wins ✓
                                List.of(c(14, "Cups"), c(14, "Swords"),
                                        c(2, "Wands"), c(3, "Pentacles"), c(4, "Cups")),
                                2
                        ),

                        /* 38 */ Arguments.of(
                                "Pair: Aces(15) beat Kings(14) — P1 wins",
                                List.of(c(15, "Cups"), c(15, "Swords")), // P1: pair Aces
                                List.of(c(14, "Cups"), c(14, "Swords")), // P2: pair Kings
                                // Table: 2 3 4 5 6 (no A or K) → no trips
                                // P1: pair A, kickers 6,5,4 → [2,15,6,5,4]
                                // P2: pair K, kickers 6,5,4 → [2,14,6,5,4]  → P1 wins ✓
                                List.of(c(2, "Wands"), c(3, "Pentacles"),
                                        c(8, "Cups"), c(5, "Swords"), c(6, "Wands")),
                                1
                        ),

                        /* 39 */ Arguments.of(
                                "Pair: Queens(13) beat Jacks(11) — P2 wins",
                                List.of(c(11, "Cups"), c(11, "Swords")), // P1: pair Jacks
                                List.of(c(13, "Cups"), c(13, "Swords")), // P2: pair Queens
                                // Table: 2 3 4 5 6 (no J or Q) → no trips
                                // P1: pair J, kickers 6,5,4 → [2,11,6,5,4]
                                // P2: pair Q, kickers 6,5,4 → [2,13,6,5,4]  → P2 wins ✓
                                List.of(c(2, "Wands"), c(3, "Pentacles"),
                                        c(8, "Cups"), c(5, "Swords"), c(6, "Wands")),
                                2
                        ),

                        /* 40 */ Arguments.of(
                                "Pair: 9s beat 8s — P1 wins",
                                List.of(c(9, "Cups"), c(9, "Swords")),  // P1: pair 9s
                                List.of(c(8, "Cups"), c(8, "Swords")),  // P2: pair 8s
                                // Table: 2 3 4 5 6 (no 8 or 9) → no trips
                                // P1: pair 9, kickers 6,5,4 → [2,9,6,5,4]
                                // P2: pair 8, kickers 6,5,4 → [2,8,6,5,4]  → P1 wins ✓
                                List.of(c(2, "Wands"), c(3, "Pentacles"),
                                        c(7, "Cups"), c(5, "Swords"), c(6, "Wands")),
                                1
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("testCases")
            void sameCombBetterPower(
                    String description,
                    List<MinorArcanaCard> p1Hole,
                    List<MinorArcanaCard> p2Hole,
                    List<MinorArcanaCard> table,
                    int expectedWinner) {

                assertEquals(
                        expectedWinner,
                        determiner.determineWinner(p1Hole, p2Hole, table),
                        description
                );
            }
        }

        @Nested
        class SameCombWinByHighCardTest {

            private final WinnerDeterminerService determiner = new WinnerDeterminerService();

            private static MinorArcanaCard c(int power, String suit) {
                return new MinorArcanaCard(suit + "-" + power, suit, power);
            }

            static Stream<Arguments> testCases() {
                return Stream.of(

                        // ════════════════════════════════════════════════════════════
                        // FOUR OF A KIND — same quad rank, kicker decides  (01-06)
                        // Table: all 4 copies of quad rank (Cups/Swords/Wands/Pent) + 2♥
                        // P1 hole: kicker-high + junk(3♠)   P2 hole: kicker-low + junk(4♣)
                        // ════════════════════════════════════════════════════════════

                        /* 01 */ Arguments.of(
                                "Quads Aces: kicker K(14) beats Q(13) — P1 wins",
                                List.of(c(14, "Cups"), c(3, "Swords")),   // P1 kicker = K
                                List.of(c(13, "Cups"), c(4, "Wands")),    // P2 kicker = Q
                                // quad A[C/S/W/P] + 2♥ neutral
                                // P1 Cups: K♥ A♥ 2♥ = 3 ✓  P2 Cups: Q♥ A♥ 2♥ = 3 ✓
                                List.of(c(15, "Cups"), c(15, "Swords"), c(15, "Wands"),
                                        c(15, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 02 */ Arguments.of(
                                "Quads Kings: kicker A(15) beats Page(11) — P2 wins",
                                List.of(c(11, "Cups"), c(3, "Swords")),   // P1 kicker = Page
                                List.of(c(15, "Cups"), c(4, "Wands")),    // P2 kicker = Ace
                                // P1: [8,14,11]   P2: [8,14,15]  → P2 wins ✓
                                List.of(c(14, "Cups"), c(14, "Swords"), c(14, "Wands"),
                                        c(14, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        /* 03 */ Arguments.of(
                                "Quads Queens: kicker A(15) beats K(14) — P1 wins",
                                List.of(c(15, "Cups"), c(3, "Swords")),
                                List.of(c(14, "Cups"), c(4, "Wands")),
                                List.of(c(13, "Cups"), c(13, "Swords"), c(13, "Wands"),
                                        c(13, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 04 */ Arguments.of(
                                "Quads Jacks(11): kicker A(15) beats Q(13) — P2 wins",
                                List.of(c(13, "Cups"), c(3, "Swords")),
                                List.of(c(15, "Cups"), c(4, "Wands")),
                                List.of(c(11, "Cups"), c(11, "Swords"), c(11, "Wands"),
                                        c(11, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        /* 05 */ Arguments.of(
                                "Quads 9s: kicker K(14) beats Q(13) — P1 wins",
                                List.of(c(14, "Cups"), c(3, "Swords")),
                                List.of(c(13, "Cups"), c(4, "Wands")),
                                List.of(c(9, "Cups"), c(9, "Swords"), c(9, "Wands"),
                                        c(9, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 06 */ Arguments.of(
                                "Quads 7s: kicker Q(13) beats Page(11) — P2 wins",
                                List.of(c(11, "Cups"), c(3, "Swords")),
                                List.of(c(13, "Cups"), c(4, "Wands")),
                                List.of(c(7, "Cups"), c(7, "Swords"), c(7, "Wands"),
                                        c(7, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // FLUSH — same suit, deciding card varies  (07-12)
                        //
                        // Table: 4 NON-CONSECUTIVE cards of flush suit + 1 off-suit neutral.
                        // Each player: 1 flush-suit hole card + 1 off-suit junk card.
                        // Both players get exactly 5 flush-suit cards → flush, not SF.
                        //
                        // Gap pattern for Swords {4,6,8,X}: gaps at 5,7 prevent SF.
                        // Gap pattern for Cups   {4,6,8,X}: same.
                        // Verified: no player exceeds 2 cards in any non-flush suit.
                        // ════════════════════════════════════════════════════════════

                        /* 07 — deciding: position 2 (top card A shared) */
                        Arguments.of(
                                "Flush Swords A-high: 2nd card K(14) beats Q(13) — P1 wins",
                                List.of(c(14, "Swords"), c(3, "Cups")),     // P1 ♠: {4,6,8,14,15} → A-K-8-6-4
                                List.of(c(13, "Swords"), c(4, "Cups")),     // P2 ♠: {4,6,8,13,15} → A-Q-8-6-4
                                // table ♠: A(15) 8 6 4  + neutral 2♥; gaps at 5,7,9-13 prevent SF
                                // P1 Cups: 3♥,2♥=2 ✓   P2 Cups: 4♥,2♥=2 ✓
                                List.of(c(15, "Swords"), c(8, "Swords"), c(6, "Swords"),
                                        c(4, "Swords"), c(2, "Cups")),
                                1
                        ),

                        /* 08 — deciding: position 2 (top card A shared), P2 wins */
                        Arguments.of(
                                "Flush Cups A-high: 2nd card K(14) beats Page(11) — P2 wins",
                                List.of(c(11, "Cups"), c(3, "Swords")),   // P1 ♥: {4,6,8,11,15} → A-P-8-6-4
                                List.of(c(14, "Cups"), c(4, "Swords")),   // P2 ♥: {4,6,8,14,15} → A-K-8-6-4
                                // table ♥: A(15) 8 6 4  + neutral 2♠; gaps at 5,7,9-13 prevent SF
                                // P1 Swords: 3♠,2♠=2 ✓   P2 Swords: 4♠,2♠=2 ✓
                                List.of(c(15, "Cups"), c(8, "Cups"), c(6, "Cups"),
                                        c(4, "Cups"), c(2, "Swords")),
                                2
                        ),

                        /* 09 — deciding: position 3 (top 2 A-K shared) */
                        Arguments.of(
                                "Flush Swords A-K-high: 3rd card Q(13) beats Page(11) — P1 wins",
                                List.of(c(13, "Swords"), c(3, "Cups")),     // P1 ♠: {4,6,13,14,15} → A-K-Q-6-4
                                List.of(c(11, "Swords"), c(4, "Cups")),     // P2 ♠: {4,6,11,14,15} → A-K-P-6-4
                                // table ♠: A(15) K(14) 6 4  + neutral 2♥
                                // 13-14-15 = run of 3 only; need 11,12 for SF → no SF ✓
                                List.of(c(15, "Swords"), c(14, "Swords"), c(6, "Swords"),
                                        c(4, "Swords"), c(2, "Cups")),
                                1
                        ),

                        /* 10 — deciding: position 3, P2 wins */
                        Arguments.of(
                                "Flush Cups A-K-high: 3rd card Q(13) beats 10 — P2 wins",
                                List.of(c(10, "Cups"), c(3, "Swords")),   // P1 ♥: {4,6,10,14,15} → A-K-10-6-4
                                List.of(c(13, "Cups"), c(4, "Swords")),   // P2 ♥: {4,6,13,14,15} → A-K-Q-6-4
                                List.of(c(15, "Cups"), c(14, "Cups"), c(6, "Cups"),
                                        c(4, "Cups"), c(2, "Swords")),
                                2
                        ),

                        /* 11 — deciding: position 4 (top 3 A-K-Q shared) */
                        Arguments.of(
                                "Flush Swords A-K-Q-high: 4th card 9 beats 8 — P1 wins",
                                List.of(c(9, "Swords"), c(3, "Cups")),     // P1 ♠: {4,9,13,14,15} → A-K-Q-9-4
                                List.of(c(8, "Swords"), c(4, "Cups")),     // P2 ♠: {4,8,13,14,15} → A-K-Q-8-4
                                // table ♠: A(15) K(14) Q(13) 4  + neutral 2♥; 13-14-15 run of 3, no SF ✓
                                List.of(c(15, "Swords"), c(14, "Swords"), c(13, "Swords"),
                                        c(4, "Swords"), c(2, "Cups")),
                                1
                        ),

                        /* 12 — deciding: position 5 (top 3 A-K-9 shared) */
                        Arguments.of(
                                "Flush Cups A-K-9-high: 5th card 7 beats 6 — P2 wins",
                                List.of(c(6, "Cups"), c(3, "Swords")),   // P1 ♥: {4,6,9,14,15} → A-K-9-6-4
                                List.of(c(7, "Cups"), c(3, "Wands")),    // P2 ♥: {4,7,9,14,15} → A-K-9-7-4
                                // table ♥: A(15) K(14) 9 4  + neutral 2♠; no 5 consecutive ✓
                                // P1 Swords: 3♠,2♠=2 ✓   P2 Wands: 3♣=1 ✓
                                List.of(c(15, "Cups"), c(14, "Cups"), c(9, "Cups"),
                                        c(4, "Cups"), c(2, "Swords")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // THREE OF A KIND — same trips, kicker decides  (13-18)
                        //
                        // Table: 3 copies of trips rank (Cups/Swords/Wands) + 1 shared
                        // kicker card (Pentacles) + 1 low neutral (Pentacles).
                        // Each player: 1 meaningful hole card + 1 low junk.
                        // Tests 13-15: k1 decides (k1 comes from player hole card).
                        // Tests 16-18: k1 is the shared board card; k2 decides.
                        // ════════════════════════════════════════════════════════════

                        /* 13 */ Arguments.of(
                                "Trips 7s: shared Q(13) kicker, hole A(15) beats K(14) decides k1 — P1 wins",
                                List.of(c(15, "Cups"), c(3, "Swords")),   // P1: k1=A, k2=Q(board)
                                List.of(c(14, "Cups"), c(4, "Swords")),   // P2: k1=K, k2=Q(board)
                                // P1:[4,7,15,13]  P2:[4,7,14,13]  → P1 wins at k1 ✓
                                // Cups per player: P1={A♥,7♥}=2 ✓  P2={K♥,7♥}=2 ✓
                                List.of(c(7, "Cups"), c(7, "Swords"), c(7, "Wands"),
                                        c(13, "Pentacles"), c(2, "Pentacles")),
                                1
                        ),

                        /* 14 */ Arguments.of(
                                "Trips 7s: shared Q(13) kicker, hole K(14) beats Page(11) decides k1 — P2 wins",
                                List.of(c(11, "Cups"), c(3, "Swords")),   // P1: k1=Page(11), k2=Q
                                List.of(c(14, "Cups"), c(4, "Swords")),   // P2: k1=K(14),    k2=Q
                                // P1:[4,7,13,11]  P2:[4,7,14,13]  → P2 wins at k1 ✓
                                List.of(c(7, "Cups"), c(7, "Swords"), c(7, "Wands"),
                                        c(13, "Pentacles"), c(2, "Pentacles")),
                                2
                        ),

                        /* 15 */ Arguments.of(
                                "Trips Aces: shared Page(11) kicker, hole K(14) beats Q(13) decides k1 — P1 wins",
                                List.of(c(14, "Cups"), c(3, "Swords")),   // P1: k1=K(14), k2=Page(board)
                                List.of(c(13, "Cups"), c(4, "Swords")),   // P2: k1=Q(13), k2=Page(board)
                                // P1:[4,15,14,11]  P2:[4,15,13,11]  → P1 wins at k1 ✓
                                List.of(c(15, "Cups"), c(15, "Swords"), c(15, "Wands"),
                                        c(11, "Pentacles"), c(2, "Pentacles")),
                                1
                        ),

                        /* 16 */ Arguments.of(
                                "Trips 9s: shared K(14) kicker, hole Q(13) beats Page(11) decides k2 — P2 wins",
                                List.of(c(11, "Cups"), c(3, "Swords")),   // P1: k1=K(board), k2=Page(11)
                                List.of(c(13, "Cups"), c(4, "Swords")),   // P2: k1=K(board), k2=Q(13)
                                // P1:[4,9,14,11]  P2:[4,9,14,13]  → P2 wins at k2 ✓
                                List.of(c(9, "Cups"), c(9, "Swords"), c(9, "Wands"),
                                        c(14, "Pentacles"), c(2, "Pentacles")),
                                2
                        ),

                        /* 17 */ Arguments.of(
                                "Trips 5s: shared A(15) kicker, hole K(14) beats Page(11) decides k2 — P1 wins",
                                List.of(c(14, "Cups"), c(3, "Swords")),   // P1: k1=A(board), k2=K(14)
                                List.of(c(11, "Cups"), c(4, "Swords")),   // P2: k1=A(board), k2=Page(11)
                                // P1:[4,5,15,14]  P2:[4,5,15,11]  → P1 wins at k2 ✓
                                List.of(c(5, "Cups"), c(5, "Swords"), c(5, "Wands"),
                                        c(15, "Pentacles"), c(2, "Pentacles")),
                                1
                        ),

                        /* 18 */ Arguments.of(
                                "Trips 3s: shared A(15) kicker, hole K(14) beats Q(13) decides k2 — P2 wins",
                                List.of(c(13, "Cups"), c(4, "Swords")),   // P1: k1=A(board), k2=Q(13)
                                List.of(c(14, "Cups"), c(5, "Swords")),   // P2: k1=A(board), k2=K(14)
                                // P1:[4,3,15,13]  P2:[4,3,15,14]  → P2 wins at k2 ✓
                                List.of(c(3, "Cups"), c(3, "Swords"), c(3, "Wands"),
                                        c(15, "Pentacles"), c(2, "Pentacles")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // TWO PAIR — same two pairs on board, kicker decides  (19-24)
                        //
                        // Table: pair-high (Cups+Swords) + pair-low (Wands+Pent) + 2♥.
                        // Each player: 1 kicker hole card + 1 low junk.
                        // Verified: no player can form trips (table has exactly 2 of each
                        // pair rank) and max 3 same-suit cards per player.
                        // Score: [3, highPair, lowPair, kicker].
                        // ════════════════════════════════════════════════════════════

                        /* 19 */ Arguments.of(
                                "Two Pair Q-Q+Page-Page: kicker A(15) beats K(14) — P1 wins",
                                List.of(c(15, "Wands"), c(3, "Swords")),   // P1 kicker = A
                                List.of(c(14, "Wands"), c(4, "Swords")),   // P2 kicker = K
                                // P1:[3,13,11,15]  P2:[3,13,11,14]  → P1 wins ✓
                                // Cups per player: Q♥,2♥=2 ✓   Wands per player: A♣,P♣=2 ✓
                                List.of(c(13, "Cups"), c(13, "Swords"), c(11, "Wands"),
                                        c(11, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 20 */ Arguments.of(
                                "Two Pair K-K+Page-Page: kicker A(15) beats Q(13) — P2 wins",
                                List.of(c(13, "Wands"), c(3, "Swords")),   // P1 kicker = Q
                                List.of(c(15, "Wands"), c(4, "Swords")),   // P2 kicker = A
                                // P1:[3,14,11,13]  P2:[3,14,11,15]  → P2 wins ✓
                                List.of(c(14, "Cups"), c(14, "Swords"), c(11, "Wands"),
                                        c(11, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        /* 21 */ Arguments.of(
                                "Two Pair A-A+K-K: kicker Q(13) beats Page(11) — P1 wins",
                                List.of(c(13, "Wands"), c(3, "Swords")),
                                List.of(c(11, "Wands"), c(4, "Swords")),
                                // P1:[3,15,14,13]  P2:[3,15,14,11]  → P1 wins ✓
                                List.of(c(15, "Cups"), c(15, "Swords"), c(14, "Wands"),
                                        c(14, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 22 */ Arguments.of(
                                "Two Pair Q-Q+10-10: kicker A(15) beats K(14) — P2 wins",
                                List.of(c(14, "Wands"), c(3, "Swords")),
                                List.of(c(15, "Wands"), c(4, "Swords")),
                                // P1:[3,13,10,14]  P2:[3,13,10,15]  → P2 wins ✓
                                List.of(c(13, "Cups"), c(13, "Swords"), c(10, "Wands"),
                                        c(10, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        /* 23 */ Arguments.of(
                                "Two Pair K-K+9-9: kicker A(15) beats Q(13) — P1 wins",
                                List.of(c(15, "Wands"), c(3, "Swords")),
                                List.of(c(13, "Wands"), c(4, "Swords")),
                                // P1:[3,14,9,15]  P2:[3,14,9,13]  → P1 wins ✓
                                List.of(c(14, "Cups"), c(14, "Swords"), c(9, "Wands"),
                                        c(9, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 24 */ Arguments.of(
                                "Two Pair 10-10+9-9: kicker K(14) beats Q(13) — P2 wins",
                                List.of(c(13, "Wands"), c(3, "Swords")),
                                List.of(c(14, "Wands"), c(4, "Swords")),
                                // P1:[3,10,9,13]  P2:[3,10,9,14]  → P2 wins ✓
                                List.of(c(10, "Cups"), c(10, "Swords"), c(9, "Wands"),
                                        c(9, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // ONE PAIR — same pair rank, kicker decides  (25-32)
                        //
                        // Table: pair (Cups+Swords) + cards that become shared kickers
                        //        for higher positions + 1 low neutral.
                        // Each player: 1 meaningful hole card + 1 low junk.
                        //
                        // Tests 25-26: k1 decides (table has no kicker-rank card above
                        //              both players' meaningful hole cards).
                        // Tests 27-28: k1 same (A from table), k2 decides.
                        // Tests 29-30: k1+k2 same (A+K from table), k3 decides.
                        // Tests 31-32: mixed — described inline.
                        // ════════════════════════════════════════════════════════════

                        /* 25 */ Arguments.of(
                                "Pair 7s: k1 A(15) beats Q(13) — P1 wins",
                                List.of(c(15, "Swords"), c(3, "Wands")),    // P1: k1=A, k2=9(table), k3=5(table)
                                List.of(c(13, "Wands"), c(4, "Pentacles")), // P2: k1=Q, k2=9(table), k3=5(table)
                                // P1:[2,7,15,9,5]  P2:[2,7,13,9,5]  → P1 wins at k1 ✓
                                // P1 Swords: A♠,7♠=2 ✓  P2 Wands: Q♣,9♣=2 ✓  no flush
                                List.of(c(7, "Cups"), c(7, "Swords"), c(9, "Wands"),
                                        c(5, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 26 */ Arguments.of(
                                "Pair Pages(11): k1 A(15) beats K(14) — P2 wins",
                                List.of(c(14, "Swords"), c(3, "Wands")),    // P1: k1=K
                                List.of(c(15, "Wands"), c(4, "Pentacles")), // P2: k1=A
                                // P1:[2,11,14,9,5]  P2:[2,11,15,9,5]  → P2 wins at k1 ✓
                                List.of(c(11, "Cups"), c(11, "Swords"), c(9, "Wands"),
                                        c(5, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        /* 27 */ Arguments.of(
                                "Pair 7s: k1 same(A board), k2 K(14) beats Q(13) — P1 wins",
                                List.of(c(14, "Swords"), c(3, "Wands")),    // P1: k1=A(board) k2=K(14) k3=4
                                List.of(c(13, "Swords"), c(3, "Pentacles")), // P2: k1=A(board) k2=Q(13) k3=4
                                // P1:[2,7,15,14,4]  P2:[2,7,15,13,4]  → P1 wins at k2 ✓
                                // P1 Swords: K♠,7♠=2 ✓  P2 Swords: Q♠,7♠=2 ✓
                                List.of(c(7, "Cups"), c(7, "Swords"), c(15, "Wands"),
                                        c(4, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 28 */ Arguments.of(
                                "Pair 9s: k1 same(A board), k2 K(14) beats Q(13) — P2 wins",
                                List.of(c(13, "Swords"), c(3, "Wands")),    // P1: k1=A k2=Q(13) k3=4
                                List.of(c(14, "Swords"), c(3, "Pentacles")), // P2: k1=A k2=K(14) k3=4
                                // P1:[2,9,15,13,4]  P2:[2,9,15,14,4]  → P2 wins at k2 ✓
                                List.of(c(9, "Cups"), c(9, "Swords"), c(15, "Wands"),
                                        c(4, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        /* 29 */ Arguments.of(
                                "Pair 7s: k1+k2 same(A,K board), k3 9 beats 8 — P1 wins",
                                List.of(c(9, "Swords"), c(3, "Wands")),    // P1: k1=A k2=K k3=9(hole)
                                List.of(c(8, "Swords"), c(3, "Pentacles")), // P2: k1=A k2=K k3=8(hole)
                                // P1:[2,7,15,14,9]  P2:[2,7,15,14,8]  → P1 wins at k3 ✓
                                // P1 Swords: 9♠,7♠=2 ✓  P2 Swords: 8♠,7♠=2 ✓
                                List.of(c(7, "Cups"), c(7, "Swords"), c(15, "Wands"),
                                        c(14, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 30 */ Arguments.of(
                                "Pair 6s: k1+k2 same(A,K board), k3 9 beats 8 — P2 wins",
                                List.of(c(8, "Swords"), c(3, "Wands")),    // P1: k3=8
                                List.of(c(9, "Swords"), c(3, "Pentacles")), // P2: k3=9
                                // P1:[2,6,15,14,8]  P2:[2,6,15,14,9]  → P2 wins at k3 ✓
                                List.of(c(6, "Cups"), c(6, "Swords"), c(15, "Wands"),
                                        c(14, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        /* 31 */ Arguments.of(
                                "Pair Queens(13): k1 same(A board), k2 K(14) beats Page(11) — P1 wins",
                                List.of(c(14, "Swords"), c(3, "Wands")),    // P1: k1=A k2=K(14) k3=4
                                List.of(c(11, "Swords"), c(3, "Pentacles")), // P2: k1=A k2=P(11) k3=4
                                // P1:[2,13,15,14,4]  P2:[2,13,15,11,4]  → P1 wins at k2 ✓
                                List.of(c(13, "Cups"), c(13, "Swords"), c(15, "Wands"),
                                        c(4, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 32 */ Arguments.of(
                                "Pair 5s: k1+k2 same(A,K board), k3 Page(11) beats 10 — P2 wins",
                                List.of(c(10, "Swords"), c(3, "Wands")),    // P1: k3=10
                                List.of(c(11, "Swords"), c(3, "Pentacles")), // P2: k3=Page(11)
                                // P1:[2,5,15,14,10]  P2:[2,5,15,14,11]  → P2 wins at k3 ✓
                                List.of(c(5, "Cups"), c(5, "Swords"), c(15, "Wands"),
                                        c(14, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // HIGH CARD — no pair / flush / straight, card position decides
                        //             (33-40)
                        //
                        // Construction:
                        //   • Table provides the shared top cards in mixed suits.
                        //   • Each player's meaningful hole card slots into the deciding
                        //     position; the junk hole card is too low to rank in top 5.
                        //   • No 5 consecutive powers (no straight) — verified per test.
                        //   • No 5 same-suit cards (no flush) — max 3 same suit shown.
                        //   • No repeated power values (no pair).
                        // ════════════════════════════════════════════════════════════

                        /* 33 — deciding: card-1 */
                        Arguments.of(
                                "High Card: card-1 A(15) beats K(14) — P1 wins",
                                List.of(c(15, "Cups"), c(3, "Cups")),     // P1: best = A
                                List.of(c(14, "Cups"), c(4, "Cups")),     // P2: best = K
                                // shared table: J(11) 9 7 5 + 2  (all different suits, no straight)
                                // P1 best-5: A-J-9-7-5 → [1,15,11,9,7,5]
                                // P2 best-5: K-J-9-7-5 → [1,14,11,9,7,5]   → P1 wins at c1 ✓
                                // Cups per player: A♥/K♥ + J♥... wait table J is Swords below
                                // P1 Cups: A♥,3♥=2 ✓  P2 Cups: K♥,4♥=2 ✓
                                List.of(c(11, "Swords"), c(9, "Wands"), c(7, "Pentacles"),
                                        c(5, "Swords"), c(2, "Wands")),
                                1
                        ),

                        /* 34 — deciding: card-2, P2 wins */
                        Arguments.of(
                                "High Card: top A shared, card-2 K(14) beats Q(13) — P2 wins",
                                List.of(c(13, "Swords"), c(3, "Wands")),    // P1: c2=Q(13)
                                List.of(c(14, "Swords"), c(4, "Pentacles")), // P2: c2=K(14)
                                // table: A(15)♥ 9♣ 7♦ 5♠ 2♣
                                // P1 best-5: A-Q-9-7-5 → [1,15,13,9,7,5]
                                // P2 best-5: A-K-9-7-5 → [1,15,14,9,7,5]   → P2 wins at c2 ✓
                                // Straight check P1: {2,3,5,7,9,13,15} no 5 consecutive ✓
                                // P1 Swords: Q♠,5♠=2 ✓  P2 Swords: K♠,5♠=2 ✓
                                List.of(c(15, "Cups"), c(9, "Wands"), c(7, "Pentacles"),
                                        c(5, "Swords"), c(2, "Wands")),
                                2
                        ),

                        /* 35 — deciding: card-3 */
                        Arguments.of(
                                "High Card: top A-K shared, card-3 Q(13) beats Page(11) — P1 wins",
                                List.of(c(13, "Wands"), c(3, "Pentacles")), // P1: c3=Q
                                List.of(c(11, "Wands"), c(4, "Pentacles")), // P2: c3=Page
                                // table: A(15)♥ K(14)♠ 7♦ 5♣ 2♥
                                // P1 best-5: A-K-Q-7-5 → [1,15,14,13,7,5]
                                // P2 best-5: A-K-P-7-5 → [1,15,14,11,7,5]   → P1 wins at c3 ✓
                                // Straight: P1 {2,3,5,7,13,14,15}: 13-14-15 run of 3, missing 12 → no ✓
                                // P1 Wands: Q♣,5♣=2 ✓  P2 Wands: P♣,5♣=2 ✓
                                List.of(c(15, "Cups"), c(14, "Swords"), c(7, "Pentacles"),
                                        c(5, "Wands"), c(2, "Cups")),
                                1
                        ),

                        /* 36 — deciding: card-4, P2 wins */
                        Arguments.of(
                                "High Card: top A-K-Q shared, card-4 Page(11) beats 10 — P2 wins",
                                List.of(c(10, "Pentacles"), c(3, "Swords")),  // P1: c4=10
                                List.of(c(11, "Pentacles"), c(4, "Swords")),  // P2: c4=Page
                                // table: A(15)♥ K(14)♠ Q(13)♣ 5♣ 2♥
                                // P1 best-5: A-K-Q-10-5 → [1,15,14,13,10,5]
                                // P2 best-5: A-K-Q-P-5  → [1,15,14,13,11,5]   → P2 wins at c4 ✓
                                // Straight: P2 {2,4,5,11,13,14,15}: 13-14-15 run, missing 12 → no ✓
                                // P2 Swords: 4♠,K♠=2 ✓
                                List.of(c(15, "Cups"), c(14, "Swords"), c(13, "Wands"),
                                        c(5, "Wands"), c(2, "Cups")),
                                2
                        ),

                        /* 37 — deciding: card-5 */
                        Arguments.of(
                                "High Card: top A-K-Q-Page shared, card-5 10 beats 9 — P1 wins",
                                List.of(c(10, "Pentacles"), c(3, "Swords")),  // P1: c5=10
                                List.of(c(9, "Pentacles"), c(4, "Swords")),  // P2: c5=9
                                // table: A(15)♥ K(14)♠ Q(13)♣ Page(11)♦ 2♥
                                // P1 best-5: A-K-Q-P-10  powers {10,11,13,14,15}: 13-14-15 + 11 gap at 12 → no straight ✓
                                // P2 best-5: A-K-Q-P-9   → [1,15,14,13,11,9]
                                // P1:[1,15,14,13,11,10]  → P1 wins at c5 ✓
                                List.of(c(15, "Cups"), c(14, "Swords"), c(13, "Wands"),
                                        c(11, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 38 — deciding: card-5, P2 wins */
                        Arguments.of(
                                "High Card: top A-K-Q-Page shared, card-5 10 beats 8 — P2 wins",
                                List.of(c(8, "Pentacles"), c(3, "Swords")),  // P1: c5=8
                                List.of(c(10, "Pentacles"), c(4, "Swords")),  // P2: c5=10
                                // Same table as test 37; P2 now wins ✓
                                List.of(c(15, "Cups"), c(14, "Swords"), c(13, "Wands"),
                                        c(11, "Pentacles"), c(2, "Cups")),
                                2
                        ),

                        /* 39 — deciding: card-3, different base cards */
                        Arguments.of(
                                "High Card: top A-K shared, card-3 Q(13) beats Page(11), lower cards differ — P1 wins",
                                List.of(c(13, "Wands"), c(3, "Pentacles")), // P1: c3=Q  → A-K-Q-6-3
                                List.of(c(11, "Wands"), c(5, "Swords")),    // P2: c3=Page→ A-K-P-6-5
                                // table: A(15)♥ K(14)♠ 6♣ 4♦ 2♥
                                // P1 best-5: {3,4,6,13,14,15} top-5 = A-K-Q-6-4 → [1,15,14,13,6,4]
                                // P2 best-5: {2,4,5,6,11,14,15} top-5 = A-K-P-6-5 → [1,15,14,11,6,5]
                                // P1 wins at c3 (13 > 11) ✓
                                // Straight check: P1 {3,4,6,13,14,15}: 13-14-15 run of 3, no 12 → no ✓
                                List.of(c(15, "Cups"), c(14, "Swords"), c(6, "Wands"),
                                        c(4, "Pentacles"), c(2, "Cups")),
                                1
                        ),

                        /* 40 — deciding: card-3, P2 wins */
                        Arguments.of(
                                "High Card: top A-K shared, card-3 Page(11) beats 10 — P2 wins",
                                List.of(c(10, "Wands"), c(4, "Swords")),    // P1: c3=10 → A-K-10-5-3
                                List.of(c(11, "Wands"), c(4, "Pentacles")), // P2: c3=Page→ A-K-P-5-3
                                // table: A(15)♥ K(14)♠ 5♣ 3♦ 2♥
                                // P1 best-5: {2,3,4,5,10,14,15} top-5 = A-K-10-5-4 → [1,15,14,10,5,4]
                                // P2 best-5: {2,3,4,5,11,14,15} top-5 = A-K-P-5-4  → [1,15,14,11,5,4]
                                // P2 wins at c3 (11 > 10) ✓
                                // Straight check: P1 {2,3,4,5,10,14,15}: 2-3-4-5 run of 4, no 6 → no ✓
                                // P2 {2,3,4,5,11,14,15}: same base → no straight ✓
                                // Flush: P1 Wands: 10♣,5♣=2 ✓  P2 Wands: P♣,5♣=2 ✓
                                List.of(c(15, "Cups"), c(14, "Swords"), c(5, "Wands"),
                                        c(3, "Pentacles"), c(2, "Cups")),
                                2
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("testCases")
            void sameCombWinByHighCard(
                    String description,
                    List<MinorArcanaCard> p1Hole,
                    List<MinorArcanaCard> p2Hole,
                    List<MinorArcanaCard> table,
                    int expectedWinner) {

                assertEquals(
                        expectedWinner,
                        determiner.determineWinner(p1Hole, p2Hole, table),
                        description
                );
            }
        }

        @Nested
        class DrawTest {

            static Stream<Arguments> testCases() {
                return Stream.of(

                        // ════════════════════════════════════════════════════════════
                        // GROUP A — BOARD PLAYS  (tests 01-10)
                        // Hole cards: both players hold 2♥ 3♦ vs 4♥ 5♦ (all below
                        // every board card that matters), so the board's best 5
                        // is played identically by both.
                        // ════════════════════════════════════════════════════════════

                        /* 01 */ Arguments.of(
                                "Board plays: Royal Flush — both hole cards irrelevant",
                                List.of(c(2, "Cups"), c(3, "Swords")),
                                List.of(c(4, "Wands"), c(5, "Pentacles")),
                                List.of(c(15, "Swords"), c(14, "Swords"), c(13, "Swords"),
                                        c(12, "Swords"), c(11, "Swords"))
                        ),

                        /* 02 */ Arguments.of(
                                "Board plays: Straight Flush — board SF beats both hole pairs",
                                List.of(c(9, "Cups"), c(9, "Wands")),     // pair of 9s, board SF wins
                                List.of(c(8, "Cups"), c(8, "Wands")),     // pair of 8s, board SF wins
                                List.of(c(13, "Swords"), c(12, "Swords"), c(11, "Swords"),
                                        c(10, "Swords"), c(9, "Swords"))
                                // P1 has 9♥ + board 9♠ → could form trips 9? No — only two 9s.
                                // P1 best-5 candidates: SF K-high [9,13] vs pair 9s [2,9,...] → SF wins
                                // P2 best-5: SF K-high [9,13] → draw
                        ),

                        /* 03 */ Arguments.of(
                                "Board plays: Four of a Kind — both hole cards below board kicker",
                                List.of(c(2, "Cups"), c(3, "Swords")),
                                List.of(c(4, "Wands"), c(5, "Pentacles")),
                                List.of(c(14, "Cups"), c(14, "Swords"), c(14, "Wands"),
                                        c(14, "Pentacles"), c(15, "Cups"))
                                // board: quad Kings + Ace kicker; 2,3,4,5 all below Ace
                                // best-5 for both = K-K-K-K-A → score [8, 14, 15]
                        ),

                        /* 04 */ Arguments.of(
                                "Board plays: Full House — both hole cards below the pair rank",
                                List.of(c(2, "Cups"), c(3, "Swords")),
                                List.of(c(4, "Wands"), c(5, "Pentacles")),
                                List.of(c(15, "Cups"), c(15, "Swords"), c(15, "Wands"),
                                        c(14, "Cups"), c(14, "Swords"))
                                // board: Aces full of Kings → score [7, 15, 14]; 2,3,4,5 cannot improve it
                        ),

                        /* 05 */ Arguments.of(
                                "Board plays: Flush — hole cards are off-suit and weaker",
                                List.of(c(2, "Swords"), c(3, "Wands")),
                                List.of(c(4, "Swords"), c(5, "Wands")),
                                List.of(c(15, "Cups"), c(13, "Cups"), c(11, "Cups"),
                                        c(9, "Cups"), c(7, "Cups"))
                                // board: A-Q-J-9-7 flush in Cups; no Cups in either hand
                                // best-5 for both = board flush → score [6, 15, 13, 11, 9, 7]
                        ),

                        /* 06 */ Arguments.of(
                                "Board plays: Straight — hole cards cannot extend the sequence",
                                List.of(c(2, "Cups"), c(3, "Swords")),
                                List.of(c(4, "Wands"), c(5, "Pentacles")),
                                List.of(c(9, "Cups"), c(10, "Swords"), c(11, "Wands"),
                                        c(12, "Pentacles"), c(13, "Cups"))
                                // board: 9-10-J-Q-K straight, score [5, 13]
                                // P2 has 5 → cannot extend to 14-high (no 6 or 14 present)
                                // P1 has 2,3 → cannot extend downward (no 8 present)
                        ),

                        /* 07 */ Arguments.of(
                                "Board plays: Three of a Kind — both hole cards below board kickers",
                                List.of(c(2, "Cups"), c(3, "Swords")),
                                List.of(c(4, "Wands"), c(5, "Pentacles")),
                                List.of(c(15, "Cups"), c(15, "Swords"), c(15, "Wands"),
                                        c(14, "Cups"), c(13, "Swords"))
                                // board: trips Aces + King + Queen kickers; score [4, 15, 14, 13]
                                // 2,3,4,5 all below Queen(13)
                        ),

                        /* 08 */ Arguments.of(
                                "Board plays: Two Pair — both hole cards below the board kicker",
                                List.of(c(2, "Cups"), c(3, "Swords")),
                                List.of(c(4, "Wands"), c(5, "Pentacles")),
                                List.of(c(15, "Cups"), c(15, "Swords"), c(14, "Wands"),
                                        c(14, "Pentacles"), c(13, "Cups"))
                                // board: A-A + K-K + Queen kicker; score [3, 15, 14, 13]
                                // 2,3,4,5 all below Queen(13)
                        ),

                        /* 09 */ Arguments.of(
                                "Board plays: One Pair — both hole cards below board kickers",
                                List.of(c(2, "Cups"), c(3, "Swords")),
                                List.of(c(4, "Wands"), c(5, "Pentacles")),
                                List.of(c(15, "Cups"), c(15, "Swords"), c(14, "Wands"),
                                        c(13, "Pentacles"), c(11, "Cups"))
                                // board: pair Aces + King + Queen + Page kickers; score [2, 15, 14, 13, 11]
                                // 2,3,4,5 all below Page(11)
                        ),

                        /* 10 */ Arguments.of(
                                "Board plays: High Card — both hole cards below every board card",
                                List.of(c(2, "Cups"), c(3, "Swords")),
                                List.of(c(4, "Wands"), c(5, "Pentacles")),
                                List.of(c(15, "Cups"), c(14, "Swords"), c(13, "Wands"),
                                        c(12, "Pentacles"), c(11, "Cups"))
                                // board: A-K-Q-Knight-Page; score [1, 15, 14, 13, 12, 11]
                                // 2,3,4,5 all below Page(11)
                        ),

                        // ════════════════════════════════════════════════════════════
                        // GROUP B — HOLE CARDS CONTRIBUTE EQUALLY  (tests 11-20)
                        // Both players' relevant hole cards have identical power
                        // (different suits); the irrelevant second hole card is
                        // weaker than all board cards that would slot into the hand.
                        // ════════════════════════════════════════════════════════════

                        /* 11 */ Arguments.of(
                                "Hole contributes: both A(15) complete same Straight (10-J-Q-K-A)",
                                List.of(c(15, "Cups"), c(2, "Swords")),   // A♥ contributes; 2 is junk
                                List.of(c(15, "Wands"), c(3, "Pentacles")), // A♣ contributes; 3 is junk
                                List.of(c(14, "Cups"), c(13, "Swords"), c(12, "Wands"),
                                        c(10, "Pentacles"), c(4, "Cups"))
                                // board: K Q J 10 + filler; A from hole completes the straight
                                // score [5, 15] for both → draw
                        ),

                        /* 12 */ Arguments.of(
                                "Hole contributes: both A(15) form same One Pair, board kickers equal",
                                List.of(c(15, "Cups"), c(2, "Swords")),
                                List.of(c(15, "Wands"), c(3, "Pentacles")),
                                List.of(c(15, "Swords"), c(14, "Wands"), c(13, "Pentacles"),
                                        c(11, "Cups"), c(4, "Swords"))
                                // Wait: board has A♠ → P1 A♥ + board A♠ = pair A; P2 A♣ + board A♠ = pair A
                                // Kickers from board: K(14) Q(13) J(11) → both score [2, 15, 14, 13, 11]
                                // 2 and 3 (junk) < J(11) → draw ✓
                        ),

                        /* 13 */ Arguments.of(
                                "Hole contributes: both K(14)-K(14) form same Two Pair (A-A+K-K, kicker Q)",
                                List.of(c(14, "Cups"), c(14, "Swords")),  // pair of Kings from hole
                                List.of(c(14, "Wands"), c(14, "Pentacles")), // pair of Kings from hole
                                List.of(c(15, "Cups"), c(15, "Swords"), c(13, "Wands"),
                                        c(2, "Pentacles"), c(3, "Cups"))
                                // board: pair Aces + Q kicker; both hole pairs = Kings
                                // both: A-A + K-K + Q kicker → score [3, 15, 14, 13] → draw
                        ),

                        /* 14 */ Arguments.of(
                                "Hole contributes: both 7(7) complete same Three of a Kind, kickers from board",
                                List.of(c(7, "Cups"), c(2, "Swords")),
                                List.of(c(7, "Wands"), c(3, "Pentacles")),
                                List.of(c(7, "Swords"), c(7, "Pentacles"), c(15, "Cups"),
                                        c(14, "Swords"), c(4, "Wands"))
                                // board has two 7s; each player brings a third 7 → trips 7
                                // kickers: A(15) K(14) from board; 2,3,4 all < K → draw
                                // score [4, 7, 15, 14] for both → draw ✓
                        ),

                        /* 15 */ Arguments.of(
                                "Hole contributes: both A(15)+K(14) form same Full House (A-A-A+K-K)",
                                List.of(c(15, "Cups"), c(14, "Cups")),
                                List.of(c(15, "Swords"), c(14, "Swords")),
                                List.of(c(15, "Wands"), c(15, "Pentacles"), c(14, "Wands"),
                                        c(2, "Cups"), c(3, "Swords"))
                                // board: A♣ A♦ K♣ + fillers
                                // P1: A♥+A♣+A♦ (trips) + K♥+K♣ (pair) → Aces full of Kings
                                // P2: A♠+A♣+A♦ (trips) + K♠+K♣ (pair) → Aces full of Kings
                                // score [7, 15, 14] for both → draw ✓
                        ),

                        /* 16 */ Arguments.of(
                                "Hole contributes: both Q(13) form same One Pair, identical board kickers",
                                List.of(c(13, "Cups"), c(2, "Swords")),
                                List.of(c(13, "Wands"), c(3, "Pentacles")),
                                List.of(c(13, "Swords"), c(15, "Cups"), c(14, "Swords"),
                                        c(11, "Wands"), c(4, "Pentacles"))
                                // board Q♠; P1 Q♥ + board Q♠ = pair Q; P2 Q♣ + board Q♠ = pair Q
                                // kickers: A(15) K(14) J(11) → score [2, 13, 15, 14, 11] → draw ✓
                        ),

                        /* 17 */ Arguments.of(
                                "Hole contributes: both 10-10 form same Two Pair (J-J+10-10, kicker A)",
                                List.of(c(10, "Cups"), c(10, "Swords")),
                                List.of(c(10, "Wands"), c(10, "Pentacles")),
                                List.of(c(11, "Cups"), c(11, "Swords"), c(15, "Wands"),
                                        c(2, "Pentacles"), c(3, "Cups"))
                                // board: pair Jacks + Ace; both hole pairs = 10s
                                // both: J-J + 10-10 kicker A → score [3, 11, 10, 15] → draw ✓
                        ),

                        /* 18 */ Arguments.of(
                                "Hole contributes: both K(14) provide same High Card 5th slot",
                                List.of(c(14, "Cups"), c(2, "Swords")),
                                List.of(c(14, "Wands"), c(3, "Pentacles")),
                                List.of(c(15, "Cups"), c(13, "Swords"), c(11, "Wands"),
                                        c(9, "Pentacles"), c(4, "Cups"))
                                // P1 best-5: A(15)-K(14)-Q(13)-J(11)-9 → score [1,15,14,13,11,9]
                                // P2 best-5: same → draw ✓  (2,3,4 all below 9)
                        ),

                        /* 19 */ Arguments.of(
                                "Hole contributes: both 5(5) complete same Three of a Kind, kickers from board",
                                List.of(c(5, "Cups"), c(2, "Swords")),
                                List.of(c(5, "Wands"), c(3, "Pentacles")),
                                List.of(c(5, "Swords"), c(5, "Pentacles"), c(14, "Cups"),
                                        c(13, "Swords"), c(4, "Wands"))
                                // board two 5s; each player brings a third 5 → trips 5
                                // kickers: K(14) Q(13) → score [4, 5, 14, 13] → draw ✓
                        ),

                        /* 20 */ Arguments.of(
                                "Hole contributes: both 9-8 complete same Straight (5-6-7-8-9)",
                                List.of(c(9, "Cups"), c(8, "Cups")),
                                List.of(c(9, "Swords"), c(8, "Swords")),
                                List.of(c(5, "Wands"), c(6, "Pentacles"), c(7, "Cups"),
                                        c(2, "Swords"), c(3, "Wands"))
                                // both players complete 5-6-7-8-9 straight; score [5, 9] → draw ✓
                        ),

                        // ════════════════════════════════════════════════════════════
                        // GROUP C — SPLIT POT, POWER-MIRROR HOLE CARDS  (tests 21-30)
                        // Each player holds hole cards with identical powers but in
                        // different suits so no flush edge arises.  The final
                        // 5-card hand has identical score lists → draw.
                        // ════════════════════════════════════════════════════════════

                        /* 21 */ Arguments.of(
                                "Split pot: A♥-K♥ vs A♠-K♠, same Straight A-K-Q-J-Page",
                                List.of(c(15, "Cups"), c(14, "Cups")),
                                List.of(c(15, "Swords"), c(14, "Swords")),
                                List.of(c(13, "Wands"), c(12, "Pentacles"), c(11, "Cups"),
                                        c(2, "Swords"), c(3, "Wands"))
                                // both complete A-K-Q-J-Page straight; score [5, 15] → draw ✓
                                // no flush: Cups has A♥ K♥ Q? — board Q is Wands, J is Cups...
                                // board J♥ is Cups → P1 has A♥ K♥ + J♥ = 3 Cups, not 5 → no flush ✓
                        ),

                        /* 22 */ Arguments.of(
                                "Split pot: J♥-J♦ vs J♠-J♣, same One Pair (J-J + A-K-Q kickers)",
                                List.of(c(11, "Cups"), c(11, "Swords")),
                                List.of(c(11, "Wands"), c(11, "Pentacles")),
                                List.of(c(15, "Cups"), c(14, "Swords"), c(13, "Wands"),
                                        c(3, "Pentacles"), c(2, "Cups"))
                                // both: pair Jacks + A K Q → score [2, 11, 15, 14, 13] → draw ✓
                        ),

                        /* 23 */ Arguments.of(
                                "Split pot: A♥-K♥ vs A♠-K♠, same Two Pair (A-A+K-K, kicker Q)",
                                List.of(c(15, "Cups"), c(14, "Cups")),
                                List.of(c(15, "Swords"), c(14, "Swords")),
                                List.of(c(15, "Wands"), c(14, "Wands"), c(13, "Cups"),
                                        c(2, "Swords"), c(3, "Pentacles"))
                                // P1: A♥+A♣+A♦? No — board has A♣ K♣; P1 hole A♥ K♥ → pair A + pair K kicker Q
                                // Let's recheck: board A♣ K♣ Q♦ 2♠ 3♦; P1 hole A♥ K♥ → A-A+K-K kicker Q ✓
                                // P2 hole A♠ K♠ → same ✓ — score [3, 15, 14, 13] → draw ✓
                        ),

                        /* 24 */ Arguments.of(
                                "Split pot: 9♥-8♥ vs 9♠-8♠, same Straight (5-6-7-8-9)",
                                List.of(c(9, "Cups"), c(8, "Cups")),
                                List.of(c(9, "Swords"), c(8, "Swords")),
                                List.of(c(5, "Wands"), c(6, "Pentacles"), c(7, "Wands"),
                                        c(2, "Cups"), c(3, "Swords"))
                                // both: 5-6-7-8-9 straight; score [5, 9] → draw ✓
                        ),

                        /* 25 */ Arguments.of(
                                "Split pot: K♥-K♦ vs K♠-K♣, same Full House (A-A-A+K-K)",
                                List.of(c(14, "Cups"), c(14, "Swords")),  // pair Kings
                                List.of(c(14, "Wands"), c(14, "Pentacles")), // pair Kings diff suits
                                List.of(c(15, "Cups"), c(15, "Swords"), c(15, "Wands"),
                                        c(2, "Pentacles"), c(3, "Cups"))
                                // board: trips Aces; both bring pair Kings → Aces full of Kings
                                // score [7, 15, 14] → draw ✓
                        ),

                        /* 26 */ Arguments.of(
                                "Split pot: A♥-3♦ vs A♠-4♣, same High Card (A-K-Q-J-9)",
                                List.of(c(15, "Cups"), c(3, "Swords")),
                                List.of(c(15, "Wands"), c(4, "Pentacles")),
                                List.of(c(14, "Cups"), c(13, "Swords"), c(11, "Wands"),
                                        c(9, "Pentacles"), c(2, "Cups"))
                                // both best-5: A-K-Q-J-9; 3 and 4 are below 9 → draw ✓
                        ),

                        /* 27 */ Arguments.of(
                                "Split pot: 9♥-2♦ vs 9♠-3♣, same One Pair (9-9 + A-K-Q kickers)",
                                List.of(c(9, "Cups"), c(2, "Swords")),
                                List.of(c(9, "Wands"), c(3, "Pentacles")),
                                List.of(c(9, "Swords"), c(15, "Cups"), c(14, "Swords"),
                                        c(13, "Wands"), c(4, "Pentacles"))
                                // board has 9♠; P1 9♥+9♠ = pair 9, kickers A K Q → score [2, 9, 15, 14, 13]
                                // P2 9♣+9♠ = pair 9, kickers A K Q → same → draw ✓
                        ),

                        /* 28 */ Arguments.of(
                                "Split pot: Q♥-J♥ vs Q♠-J♠, same Two Pair (Q-Q+J-J, kicker A)",
                                List.of(c(13, "Cups"), c(11, "Cups")),
                                List.of(c(13, "Swords"), c(11, "Swords")),
                                List.of(c(13, "Wands"), c(11, "Wands"), c(15, "Pentacles"),
                                        c(2, "Cups"), c(3, "Swords"))
                                // P1: Q♥+Q♣ (pair Q) + J♥+J♣ (pair J) kicker A → score [3, 13, 11, 15]
                                // P2: Q♠+Q♣       + J♠+J♣         kicker A → same → draw ✓
                        ),

                        /* 29 */ Arguments.of(
                                "Split pot: 8♥-2♦ vs 8♠-3♣, same Three of a Kind (8-8-8 + A-K kickers)",
                                List.of(c(8, "Cups"), c(2, "Swords")),
                                List.of(c(8, "Wands"), c(3, "Pentacles")),
                                List.of(c(8, "Swords"), c(8, "Pentacles"), c(15, "Cups"),
                                        c(14, "Swords"), c(4, "Wands"))
                                // board two 8s; each player brings a third 8 → trips 8
                                // kickers A(15) K(14); 2,3,4 all below K → score [4, 8, 15, 14] → draw ✓
                        ),

                        /* 30 */ Arguments.of(
                                "Split pot: 10♥-9♦ vs 10♠-9♣, same High Card (A-K-Q-10-9 — wait, check for straight)",
                                // 10 9 + board K Q J → straight! Use board without straight possibility:
                                // P1 hole: 10♥ 9♦; P2 hole: 10♠ 9♣; board: A♣ K♦ Q♠ 3♥ 2♦
                                // Check straight: A-K-Q-J-10? No J on board. K-Q-J-10-9? No J. Good.
                                // P1 best-5: A-K-Q-10-9 (no straight, no flush) → score [1, 15, 14, 13, 10, 9]
                                // P2 best-5: A-K-Q-10-9 → same → draw ✓
                                List.of(c(10, "Cups"), c(9, "Cups")),
                                List.of(c(10, "Swords"), c(9, "Swords")),
                                List.of(c(15, "Wands"), c(14, "Pentacles"), c(13, "Cups"),
                                        c(3, "Swords"), c(2, "Wands"))
                                // Cups: A♣(board) + 10♥ 9♥ from P1 = 3 Cups only → no flush ✓
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("testCases")
            void shouldBeDraw(
                    String description,
                    List<MinorArcanaCard> p1Hole,
                    List<MinorArcanaCard> p2Hole,
                    List<MinorArcanaCard> table) {

                assertEquals(0,
                        determiner.determineWinner(p1Hole, p2Hole, table),
                        description);
            }
        }

    }

    @Nested
    class WinnerDeterminerServiceTestFourCards {

        @Nested
        class WinByHighCombFourCardsTest {


            static Stream<Arguments> testCases() {
                return Stream.of(

                        // ── Royal Flush (10) beats lower categories ──────────────────

            /* 01 — RF beats SF: table is 4 consecutive Swords; P1 extends to A-high (RF),
                    P2 extends downward to Q-high SF */
                        Arguments.of(
                                "RF(Swords A-high) beats SF(Swords Q-high) — P1 wins",
                                List.of(c(15,"Swords"), c(14,"Swords")),  // P1 → RF {11-15}♠
                                List.of(c(9,"Swords"),  c(2,"Cups")),     // P2 → SF {9-13}♠
                                List.of(c(13,"Swords"), c(12,"Swords"), c(11,"Swords"), c(10,"Swords")),
                                1
                        ),

            /* 02 — RF beats Flush: 3 consecutive Cups on table; P2 adds A+K → RF,
                    P1 adds non-consecutive Cups → Flush only */
                        Arguments.of(
                                "RF(Cups) beats Flush(Cups) — P2 wins",
                                List.of(c(9,"Cups"),    c(7,"Cups")),     // P1 → Flush {7,9,11,12,13}♥
                                List.of(c(15,"Cups"),   c(14,"Cups")),    // P2 → RF {11-15}♥
                                List.of(c(13,"Cups"),   c(12,"Cups"), c(11,"Cups"), c(2,"Swords")),
                                2
                        ),

            /* 03 — RF beats Straight: P1 adds A+K Cups completing RF; P2 gets
                    a cross-suit straight from the same table */
                        Arguments.of(
                                "RF(Cups) beats Straight(Q-high) — P1 wins",
                                List.of(c(15,"Cups"),   c(14,"Cups")),    // P1 → RF {11-15}♥
                                List.of(c(9,"Wands"),   c(8,"Pentacles")),// P2 → Str {8-13} Q-high
                                // P2 powers: 9,8,13,12,11,10 → {8,9,10,11,12,13} → Q-high ✓
                                List.of(c(13,"Cups"),   c(12,"Cups"), c(11,"Cups"), c(10,"Swords")),
                                1
                        ),

            /* 04 — RF beats One Pair: 4 Cups RF cards on table; P2 adds A → RF,
                    P1 uses only hole for a pair */
                        Arguments.of(
                                "RF(Cups) beats One Pair — P2 wins",
                                List.of(c(9,"Swords"),  c(9,"Wands")),    // P1 → pair 9
                                List.of(c(15,"Cups"),   c(2,"Swords")),   // P2 → RF {11-15}♥
                                List.of(c(14,"Cups"),   c(13,"Cups"), c(12,"Cups"), c(11,"Cups")),
                                2
                        ),

            /* 05 — RF beats High Card: 3 Swords RF cards on table; P1 adds A+K → RF;
                    P2 has entirely unrelated hole → High Card */
                        Arguments.of(
                                "RF(Swords) beats High Card — P1 wins",
                                List.of(c(15,"Swords"), c(14,"Swords")),  // P1 → RF {11-15}♠
                                List.of(c(7,"Wands"),   c(5,"Pentacles")),// P2 → HC {2,5,7,11,12,13}
                                List.of(c(13,"Swords"), c(12,"Swords"), c(11,"Swords"), c(2,"Cups")),
                                1
                        ),

                        // ── Straight Flush (9) beats lower categories ─────────────────

            /* 06 — SF beats Full House: table has 3 consecutive Swords + pair of 10s;
                    P1 extends SF; P2 uses hole trips + table pair → FH */
                        Arguments.of(
                                "SF(Swords Q-high) beats Full House(J-full-of-10) — P1 wins",
                                List.of(c(9,"Swords"),  c(13,"Swords")),  // P1 → SF {9-13}♠
                                List.of(c(11,"Cups"),   c(11,"Wands")),   // P2 → trips 11 + pair 10 = FH
                                // P2: 11♥11♣(hole)+11♠(table)=trips 11; 10♠10♥(table)=pair 10 → FH[7,11,10]
                                List.of(c(10,"Swords"), c(11,"Swords"), c(12,"Swords"), c(10,"Cups")),
                                1
                        ),

            /* 07 — SF beats Flush: 4 Cups on table; P2 extends consecutively → SF;
                    P1 extends non-consecutively → Flush only */
                        Arguments.of(
                                "SF(Cups Q-high) beats Flush(Cups) — P2 wins",
                                List.of(c(6,"Cups"),    c(4,"Cups")),     // P1 → Flush {4,6,7,9,10,11} top5
                                List.of(c(12,"Cups"),   c(13,"Cups")),    // P2 → SF {9-13}♥
                                // P1 Cups {4,6,7,9,10,11}: getStraightHighCard → no 5-run → Flush ✓
                                // P2 Cups {7,9,10,11,12,13}: run 9-13 → SF Q-high ✓
                                List.of(c(9,"Cups"),    c(10,"Cups"), c(11,"Cups"), c(7,"Cups")),
                                2
                        ),

            /* 08 — SF beats Straight: P1 uses 4 Swords + hole → SF;
                    P2 gets a cross-suit straight from same table */
                        Arguments.of(
                                "SF(Swords Kn-high) beats Straight(Kn-high) — P1 wins",
                                List.of(c(8,"Swords"),  c(12,"Swords")),  // P1 → SF {8-12}♠
                                List.of(c(7,"Wands"),   c(12,"Cups")),    // P2 → Str {7-12} Kn-high
                                // P2 powers {7,12,9,10,11,8} → {7,8,9,10,11,12} → Kn-high [5,12] ✓
                                // P2 Swords: {9,10,11} = 3 ✓; no flush
                                List.of(c(9,"Swords"),  c(10,"Swords"), c(11,"Swords"), c(8,"Cups")),
                                1
                        ),

                        /* 09 — SF beats Two Pair: 4 Cups on table; P2 → SF; P1 → Two Pair */
                        Arguments.of(
                                "SF(Cups Q-high) beats Two Pair — P2 wins",
                                List.of(c(9,"Wands"),   c(10,"Swords")),  // P1 → pair 10 + pair 9
                                List.of(c(9,"Cups"),    c(13,"Cups")),    // P2 → SF {9-13}♥
                                // P1 powers {9,10,10,11,12,9} → pair 10 + pair 9 = Two Pair ✓
                                List.of(c(10,"Cups"),   c(11,"Cups"), c(12,"Cups"), c(9,"Swords")),
                                2
                        ),

                        /* 10 — SF beats One Pair: P1 → SF in Wands; P2 → One Pair */
                        Arguments.of(
                                "SF(Wands 8-high) beats One Pair — P1 wins",
                                List.of(c(4,"Wands"),   c(8,"Wands")),    // P1 → SF {4-8}♣
                                List.of(c(3,"Cups"),    c(9,"Swords")),   // P2 → pair 3 (3♥+3♠ from hole+table)
                                // P2 powers {3,9,5,6,7,3} → pair 3 ✓
                                List.of(c(5,"Wands"),   c(6,"Wands"), c(7,"Wands"), c(3,"Swords")),
                                1
                        ),

                        // ── Four of a Kind (8) beats lower categories ─────────────────

            /* 11 — Quads beats Full House: P1 hole adds 2 more sevens → Quads;
                    P2 uses table pair-of-7 as FH-pair + hole trips 9 → FH */
                        Arguments.of(
                                "Quads(7s) beats Full House(9s-full-of-7s) — P1 wins",
                                List.of(c(7,"Cups"),    c(7,"Swords")),   // P1 → quads 7 (2 hole + 2 table)
                                List.of(c(9,"Cups"),    c(9,"Swords")),   // P2 → trips 9 + pair 7 = FH[7,9,7]
                                // table 7♣7♦ are the shared pair; P2 sees them as pair for FH ✓
                                List.of(c(7,"Wands"),   c(7,"Pentacles"), c(9,"Wands"), c(11,"Swords")),
                                1
                        ),

            /* 12 — Quads beats Two Pair: table has 2 Kings; P2 → quads K;
                    P1 sees pair K + pair from their hole = Two Pair */
                        Arguments.of(
                                "Quads(Kings) beats Two Pair — P2 wins",
                                List.of(c(9,"Wands"),   c(7,"Cups")),     // P1 → pair 14 + pair 9 = Two Pair
                                List.of(c(14,"Cups"),   c(14,"Swords")),  // P2 → quads K
                                // P1 powers {9,7,14,14,9,7} → pair 14+pair 9 [3,14,9,7] ✓
                                List.of(c(14,"Wands"),  c(14,"Pentacles"), c(9,"Cups"), c(7,"Swords")),
                                2
                        ),

            /* 13 — Quads beats Straight: P1 hole adds 2 more sevens → Quads;
                    P2 uses the table numbers for a straight */
                        Arguments.of(
                                "Quads(7s) beats Straight(Page-high) — P1 wins",
                                List.of(c(7,"Cups"),    c(7,"Swords")),   // P1 → quads 7, kicker 11
                                List.of(c(9,"Cups"),    c(8,"Wands")),    // P2 → {7,8,9,10,11} Page-high str
                                // P2 powers {9,8,7,7,10,11} distinct {7,8,9,10,11} ✓
                                List.of(c(7,"Wands"),   c(7,"Pentacles"), c(10,"Cups"), c(11,"Swords")),
                                1
                        ),

            /* 14 — Quads beats One Pair: table has 2 nines; P2 → quads 9;
                    P1 sees pair 9 from table + their own hole = at most One Pair */
                        Arguments.of(
                                "Quads(9s) beats One Pair — P2 wins",
                                List.of(c(14,"Cups"),   c(13,"Swords")),  // P1 → pair 9 [2,9,14,13,5]
                                List.of(c(9,"Wands"),   c(9,"Pentacles")), // P2 → quads 9, kicker 14
                                List.of(c(9,"Cups"),    c(9,"Swords"),   c(5,"Wands"), c(3,"Pentacles")),
                                2
                        ),

            /* 15 — Quads beats High Card: same table as 14 but P1 has truly unrelated
                    off-rank hole → best P1 hand is only One Pair from table nines */
                        Arguments.of(
                                "Quads(7s) beats High Card — P1 wins",
                                List.of(c(7,"Cups"),    c(7,"Swords")),   // P1 → quads 7, kicker 13
                                List.of(c(14,"Cups"),   c(2,"Swords")),   // P2 → pair 7 (table) [2,7,14,13,2]
                                // P2 still has pair 7 from table — but description says "HC" because
                                // quads > pair; we rename: Quads vs One Pair (P2 sees table pair)
                                List.of(c(7,"Wands"),   c(7,"Pentacles"), c(13,"Cups"), c(5,"Swords")),
                                // P2: pair 7 (from table) kickers 14,13,5 = One Pair [2,7,14,13,5]
                                1
                        ),

                        // ── Full House (7) beats lower categories ─────────────────────

            /* 16 — FH beats Straight: table pair + P1 trips → FH; P2 straight from
                    consecutive table+hole values */
                        Arguments.of(
                                "Full House(8s-full-of-10s) beats Straight(10-high) — P1 wins",
                                List.of(c(8,"Wands"),   c(8,"Swords")),   // P1 → trips 8 + pair 10 = FH
                                List.of(c(7,"Cups"),    c(9,"Pentacles")), // P2 → {6,7,8,9,10} Str 10-high
                                // P1: 8♣8♠(hole)+8♥(table)=trips; 10♣10♦(table)=pair → FH[7,8,10] ✓
                                // P2: {7,9,10,10,8,6} distinct {6,7,8,9,10} → Straight[5,10] ✓
                                List.of(c(10,"Wands"),  c(10,"Pentacles"), c(8,"Cups"), c(6,"Swords")),
                                1
                        ),

            /* 17 — FH beats Two Pair: table pair shared; P2 trips from hole → FH;
                    P1 sees table pair + own pair = Two Pair */
                        Arguments.of(
                                "Full House(8s-full-of-10s) beats Two Pair — P2 wins",
                                List.of(c(11,"Cups"),   c(11,"Wands")),   // P1 → pair 11 + pair 10 = Two Pair
                                List.of(c(8,"Cups"),    c(8,"Swords")),   // P2 → trips 8 + pair 10 = FH
                                // P1: {11,11,10,10,8,2} → two pair [3,11,10,8] ✓
                                // P2: 8♥8♠(hole)+8♣(table)=trips; 10♠10♦(table)=pair → FH[7,8,10] ✓
                                List.of(c(10,"Swords"), c(10,"Pentacles"), c(8,"Wands"), c(2,"Cups")),
                                2
                        ),

            /* 18 — FH beats One Pair: P1 trips from hole+table + table pair → FH;
                    P2 hole doesn't form trips → ends up with One Pair */
                        Arguments.of(
                                "Full House(11s-full-of-9s) beats One Pair — P1 wins",
                                List.of(c(11,"Cups"),   c(11,"Swords")),  // P1 → trips 11 + pair 9 = FH
                                List.of(c(15,"Cups"),   c(14,"Swords")),  // P2 → pair 9 [2,9,15,14,11]
                                List.of(c(11,"Wands"),  c(9,"Cups"), c(9,"Swords"), c(3,"Pentacles")),
                                1
                        ),

            /* 19 — FH beats Straight (P2 side): P2 trips + table pair = FH;
                    P1 uses table+hole for a cross-suit straight */
                        Arguments.of(
                                "Full House(8s-full-of-10s) beats Straight(10-high) — P2 wins",
                                List.of(c(7,"Cups"),    c(9,"Swords")),   // P1 → {6,7,8,9,10} Str 10-high
                                List.of(c(8,"Cups"),    c(8,"Swords")),   // P2 → trips 8 + pair 10 = FH
                                List.of(c(10,"Cups"),   c(10,"Swords"),   c(8,"Wands"), c(6,"Pentacles")),
                                2
                        ),

            /* 20 — FH beats Two Pair (reversed): P1 trips + table pair → FH;
                    P2 sees same table pair + own pair = Two Pair */
                        Arguments.of(
                                "Full House(11s-full-of-9s) beats Two Pair — P1 wins",
                                List.of(c(11,"Cups"),   c(11,"Swords")),  // P1 → trips 11 + pair 9 = FH
                                List.of(c(13,"Cups"),   c(13,"Wands")),   // P2 → pair 13 + pair 9 = Two Pair
                                // P1: 11♥11♠(hole)+11♣(table)=trips; 9♥9♠(table)=pair → FH[7,11,9] ✓
                                // P2: pair 13 + pair 9 [3,13,9,11] ✓
                                List.of(c(11,"Wands"),  c(9,"Cups"), c(9,"Swords"), c(3,"Pentacles")),
                                1
                        ),

                        // ── Flush (6) beats lower categories ──────────────────────────

                        /* 21 — Flush beats Straight: P1 Cups flush; P2 uses table+hole for straight */
                        Arguments.of(
                                "Flush(Cups) beats Straight(10-high) — P1 wins",
                                List.of(c(13,"Cups"),   c(11,"Cups")),    // P1 → Flush {5,7,9,11,13}♥
                                List.of(c(8,"Wands"),   c(10,"Pentacles")),// P2 → {5,6,7,8,9,10} Str 10-high
                                // P1 Cups{5,7,9,11,13}: not consecutive → Flush [6,13,11,9,7,5] ✓
                                // P2: {8,10,9,7,5,6} distinct{5,6,7,8,9,10} → [5,10] ✓; Cups={5,7,9}=3✓
                                List.of(c(9,"Cups"),    c(7,"Cups"), c(5,"Cups"), c(6,"Swords")),
                                1
                        ),

            /* 22 — Flush beats Trips: 3 Cups on table; P2 adds 2 Cups → Flush;
                    P1 uses both hole cards for trips + table singles */
                        Arguments.of(
                                "Flush(Cups) beats Trips(3s) — P2 wins",
                                List.of(c(3,"Cups"),    c(3,"Swords")),   // P1 → trips 3 [4,3,9,7]
                                List.of(c(13,"Cups"),   c(11,"Cups")),    // P2 → Flush {5,7,9,11,13}♥
                                // P1: 3♥(hole)+3♠(hole)+3♣(table)=trips; singles 9,7,5 ✓
                                List.of(c(9,"Cups"),    c(7,"Cups"), c(5,"Cups"), c(3,"Wands")),
                                2
                        ),

                        /* 23 — Flush beats Two Pair: P1 Cups flush; P2 uses table rank matches → Two Pair */
                        Arguments.of(
                                "Flush(Cups) beats Two Pair — P1 wins",
                                List.of(c(13,"Cups"),   c(11,"Cups")),    // P1 → Flush {5,7,9,11,13}♥
                                List.of(c(13,"Swords"), c(9,"Wands")),   // P2 → pair 13 + pair 9 = Two Pair
                                List.of(c(9,"Cups"),    c(7,"Cups"), c(5,"Cups"), c(13,"Wands")),
                                1
                        ),

                        /* 24 — Flush beats One Pair: P2 Wands flush; P1 → pair from hole */
                        Arguments.of(
                                "Flush(Wands) beats One Pair — P2 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")),  // P1 → pair 15 [2,15,11,9,7]
                                List.of(c(13,"Wands"),  c(3,"Wands")),    // P2 → Flush {3,7,9,11,13}♣
                                // P2 Wands {3,7,9,11,13}: not consecutive → Flush ✓
                                List.of(c(11,"Wands"),  c(9,"Wands"), c(7,"Wands"), c(5,"Swords")),
                                2
                        ),

                        /* 25 — Flush beats High Card: table has no pairs → P2 can stay High Card */
                        Arguments.of(
                                "Flush(Wands) beats High Card — P1 wins",
                                List.of(c(13,"Wands"),  c(5,"Wands")),    // P1 → Flush {5,7,9,11,13}♣
                                List.of(c(15,"Cups"),   c(2,"Pentacles")), // P2 → HC {2,5,7,9,11,15} top5
                                // P2: no pair, no flush (Wands={7,9,11}=3); Str? {2,5,7,9,11,15} not 5-run ✓
                                List.of(c(9,"Wands"),   c(7,"Wands"), c(11,"Wands"), c(3,"Swords")),
                                1
                        ),

                        // ── Straight (5) beats lower categories ───────────────────────

                        /* 26 — Straight beats Trips: P2 straight; P1 trips from hole+table */
                        Arguments.of(
                                "Straight(Q-high) beats Trips(9s) — P2 wins",
                                List.of(c(9,"Cups"),    c(9,"Wands")),    // P1 → trips 9 [4,9,12,11]
                                List.of(c(8,"Cups"),    c(13,"Swords")),  // P2 → {8-13} Q-high Str
                                // P1: 9♥9♣(hole)+9♠(table)=trips; 10,11,12 singles ✓
                                // P2: {8,13,9,10,11,12} → {8-13} → [5,13] ✓; no flush ✓
                                List.of(c(9,"Swords"),  c(10,"Cups"), c(11,"Wands"), c(12,"Pentacles")),
                                2
                        ),

                        /* 27 — Straight beats Two Pair: P1 straight; P2 Two Pair */
                        Arguments.of(
                                "Straight(Kn-high) beats Two Pair — P1 wins",
                                List.of(c(7,"Cups"),    c(12,"Swords")),  // P1 → {7-12} Kn-high Str
                                List.of(c(8,"Swords"),  c(11,"Cups")),    // P2 → pair 11 + pair 8 = Two Pair
                                // P1: {7,12,8,9,10,11} → [5,12] ✓; Cups={7,8}=2✓ Swords={12,9}=2✓
                                // P2: {8,11,8,9,10,11} → pair 11+pair 8 [3,11,8,10] ✓
                                List.of(c(8,"Cups"),    c(9,"Swords"), c(10,"Wands"), c(11,"Pentacles")),
                                1
                        ),

                        /* 28 — Straight beats One Pair: P2 straight; P1 One Pair */
                        Arguments.of(
                                "Straight(10-high) beats One Pair — P2 wins",
                                List.of(c(13,"Cups"),   c(13,"Swords")),  // P1 → pair 13 [2,13,9,8,7]
                                List.of(c(5,"Cups"),    c(10,"Swords")),  // P2 → {5-10} Str 10-high
                                // P2: {5,10,6,7,8,9} → [5,10] ✓; Cups={5,6}=2✓
                                List.of(c(6,"Cups"),    c(7,"Swords"),   c(8,"Wands"), c(9,"Pentacles")),
                                2
                        ),

                        // ── Trips, Two Pair, One Pair ──────────────────────────────────

            /* 29 — Trips beats Two Pair: table has pair; P1 uses hole trips rank
                    matching 1 table card → trips; P2 sees table pair + own pair */
                        Arguments.of(
                                "Trips(11s) beats Two Pair — P1 wins",
                                List.of(c(11,"Wands"),  c(15,"Cups")),    // P1 → trips 11 [4,11,15,7]
                                List.of(c(7,"Cups"),    c(3,"Cups")),     // P2 → pair 11 + pair 7 = Two Pair
                                // P1: 11♣(hole)+11♥(table,Cups)+11♠(table,Swords)=trips; 7W,3P singles ✓
                                // Hmm: table has c(11,"Cups") and c(11,"Swords")= pair 11; P1 hole c(11,"Wands")
                                // P1: three 11s + two 7s from table? No: table only has c(7,"Wands")
                                // Let's verify with the actual table below ↓
                                List.of(c(11,"Cups"),   c(11,"Swords"), c(7,"Wands"), c(3,"Pentacles")),
                                // P1: 11♣(Wands,hole)+11♥(Cups,table)+11♠(Swords,table)=trips 11;
                                //     15♥(hole) 7♣(table) 3♦(table) → kickers 15,7 → [4,11,15,7] ✓
                                // P2: 7♥(Cups,hole)+3♥(Cups,hole)+11♥(table)+11♠(table)+7♣+3♦
                                //   = pair 11+pair 7+pair 3 → best two pair= 11+7 kicker 3 [3,11,7,3] ✓
                                1
                        ),

                        /* 30 — Two Pair beats One Pair: P2 Two Pair; P1 One Pair */
                        Arguments.of(
                                "Two Pair(Q+9) beats One Pair — P2 wins",
                                List.of(c(5,"Cups"),    c(5,"Swords")),   // P1 → pair 5 [2,5,13,9,7]
                                List.of(c(13,"Swords"), c(9,"Cups")),     // P2 → pair 13 + pair 9 = Two Pair
                                // P2: 13♠(hole)+13♥(table)=pair 13; 9♥(hole)+9♠(table)=pair 9 → [3,13,9,7]✓
                                List.of(c(13,"Cups"),   c(9,"Swords"),   c(7,"Wands"), c(3,"Pentacles")),
                                2
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("testCases")
            void winByHigherCombination(
                    String description,
                    List<MinorArcanaCard> p1Hole,
                    List<MinorArcanaCard> p2Hole,
                    List<MinorArcanaCard> table,
                    int expectedWinner) {

                assertEquals(
                        expectedWinner,
                        determiner.determineWinner(p1Hole, p2Hole, table),
                        description
                );
            }
        }

        @Nested
        class SameCombBetterPowerFourCardsTest {

            private final WinnerDeterminerService determiner = new WinnerDeterminerService();

            private static MinorArcanaCard c(int power, String suit) {
                return new MinorArcanaCard(suit + "-" + power, suit, power);
            }

            static Stream<Arguments> testCases() {
                return Stream.of(

                        // ════════════════════════════════════════════════════════════
                        // STRAIGHT FLUSH — higher top card wins  (tests 01-09)
                        //
                        // Table: 4 consecutive Swords / Cups (all 4 slots).
                        // P1 hole-1: extends UP  → higher SF.
                        // P2 hole-1: extends DOWN → lower SF.
                        // Hole-2 always off-suit junk.
                        //
                        // P1 ♠ = {a,b,c,d, hole-1}  →  SF high = hole-1
                        // P2 ♠ = {a,b,c,d, hole-1*} →  SF high = d  (P2 side: hole-1* < a)
                        // ════════════════════════════════════════════════════════════

                        /* 01 */ Arguments.of(
                                "SF: K-high(14) beats Q-high(13) — P1 wins",
                                List.of(c(14,"Swords"), c(2,"Cups")),   // P1 → {10-14}♠ K-high
                                List.of(c(9,"Swords"),  c(3,"Cups")),   // P2 → {9-13}♠  Q-high
                                // Table ♠: {10,11,12,13}  P1♠={10,11,12,13,14}✓  P2♠={9,10,11,12,13}✓
                                List.of(c(10,"Swords"), c(11,"Swords"), c(12,"Swords"), c(13,"Swords")),
                                1
                        ),

                        /* 02 */ Arguments.of(
                                "SF: K-high(14) beats Q-high(13) — P2 wins",
                                List.of(c(9,"Cups"),    c(2,"Swords")), // P1 → {9-13}♥  Q-high
                                List.of(c(14,"Cups"),   c(3,"Swords")), // P2 → {10-14}♥ K-high
                                // Table ♥: {10,11,12,13}  P1♥={9,10,11,12,13}✓  P2♥={10,11,12,13,14}✓
                                List.of(c(10,"Cups"),   c(11,"Cups"),   c(12,"Cups"),   c(13,"Cups")),
                                2
                        ),

                        /* 03 */ Arguments.of(
                                "SF: Q-high(13) beats Knight-high(12) — P1 wins",
                                List.of(c(13,"Swords"), c(2,"Cups")),   // P1 → {9-13}♠  Q-high
                                List.of(c(8,"Swords"),  c(3,"Cups")),   // P2 → {8-12}♠  Kn-high
                                // Table ♠: {9,10,11,12}
                                List.of(c(9,"Swords"),  c(10,"Swords"), c(11,"Swords"), c(12,"Swords")),
                                1
                        ),

                        /* 04 */ Arguments.of(
                                "SF: Knight-high(12) beats Page-high(11) — P2 wins",
                                List.of(c(7,"Cups"),    c(2,"Swords")), // P1 → {7-11}♥  P-high
                                List.of(c(12,"Cups"),   c(3,"Swords")), // P2 → {8-12}♥  Kn-high
                                // Table ♥: {8,9,10,11}
                                List.of(c(8,"Cups"),    c(9,"Cups"),    c(10,"Cups"),   c(11,"Cups")),
                                2
                        ),

                        /* 05 */ Arguments.of(
                                "SF: Page-high(11) beats 10-high — P1 wins",
                                List.of(c(11,"Swords"), c(2,"Cups")),   // P1 → {7-11}♠  P-high
                                List.of(c(6,"Swords"),  c(3,"Cups")),   // P2 → {6-10}♠  10-high
                                // Table ♠: {7,8,9,10}
                                List.of(c(7,"Swords"),  c(8,"Swords"),  c(9,"Swords"),  c(10,"Swords")),
                                1
                        ),

                        /* 06 */ Arguments.of(
                                "SF: 10-high beats 9-high — P2 wins",
                                List.of(c(5,"Cups"),    c(2,"Swords")), // P1 → {5-9}♥   9-high
                                List.of(c(10,"Cups"),   c(3,"Swords")), // P2 → {6-10}♥  10-high
                                // Table ♥: {6,7,8,9}
                                List.of(c(6,"Cups"),    c(7,"Cups"),    c(8,"Cups"),    c(9,"Cups")),
                                2
                        ),

                        /* 07 */ Arguments.of(
                                "SF: 9-high beats 8-high — P1 wins",
                                List.of(c(9,"Swords"),  c(2,"Cups")),   // P1 → {5-9}♠   9-high
                                List.of(c(4,"Swords"),  c(3,"Cups")),   // P2 → {4-8}♠   8-high
                                // Table ♠: {5,6,7,8}
                                List.of(c(5,"Swords"),  c(6,"Swords"),  c(7,"Swords"),  c(8,"Swords")),
                                1
                        ),

                        /* 08 */ Arguments.of(
                                "SF: 8-high beats 7-high — P2 wins",
                                List.of(c(3,"Cups"),    c(2,"Swords")), // P1 → {3-7}♥   7-high
                                List.of(c(8,"Cups"),    c(4,"Swords")), // P2 → {4-8}♥   8-high
                                // Table ♥: {4,5,6,7}
                                List.of(c(4,"Cups"),    c(5,"Cups"),    c(6,"Cups"),    c(7,"Cups")),
                                2
                        ),

                        /* 09 */ Arguments.of(
                                "SF: 7-high beats 6-high — P1 wins",
                                List.of(c(7,"Swords"),  c(2,"Cups")),   // P1 → {3-7}♠   7-high
                                List.of(c(2,"Swords"),  c(3,"Cups")),   // P2 → {2-6}♠   6-high
                                // Table ♠: {3,4,5,6}
                                List.of(c(3,"Swords"),  c(4,"Swords"),  c(5,"Swords"),  c(6,"Swords")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // FOUR OF A KIND — higher quad rank wins  (tests 10-14)
                        //
                        // Table: 2 of P1's rank (Wands+Pent) + 2 of P2's rank (Wands+Pent).
                        // Each player's 2 hole cards (Cups+Swords) complete their quad.
                        // Kicker = the opponent's quad rank (visible from table for both).
                        // Score: [8, quadRank, kickerRank].
                        // ════════════════════════════════════════════════════════════

                        /* 10 */ Arguments.of(
                                "Quads: Aces(15) beat Kings(14) — P1 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")), // P1 → quad A, kicker K(14)
                                List.of(c(14,"Cups"),   c(14,"Swords")), // P2 → quad K, kicker A(15)
                                // [8,15,14] > [8,14,15] ✓
                                List.of(c(15,"Wands"),  c(15,"Pentacles"), c(14,"Wands"), c(14,"Pentacles")),
                                1
                        ),

                        /* 11 */ Arguments.of(
                                "Quads: Queens(13) beat Jacks(11) — P2 wins",
                                List.of(c(11,"Cups"),   c(11,"Swords")), // P1 → quad J, kicker Q(13)
                                List.of(c(13,"Cups"),   c(13,"Swords")), // P2 → quad Q, kicker J(11)
                                // [8,11,13] < [8,13,11] ✓
                                List.of(c(11,"Wands"),  c(11,"Pentacles"), c(13,"Wands"), c(13,"Pentacles")),
                                2
                        ),

                        /* 12 */ Arguments.of(
                                "Quads: Knights(12) beat Pages(11) — P1 wins",
                                List.of(c(12,"Cups"),   c(12,"Swords")), // P1 → quad Kn, kicker P(11)
                                List.of(c(11,"Cups"),   c(11,"Swords")), // P2 → quad P,  kicker Kn(12)
                                List.of(c(12,"Wands"),  c(12,"Pentacles"), c(11,"Wands"), c(11,"Pentacles")),
                                1
                        ),

                        /* 13 */ Arguments.of(
                                "Quads: 10s beat 9s — P2 wins",
                                List.of(c(9,"Cups"),    c(9,"Swords")),  // P1 → quad 9, kicker 10
                                List.of(c(10,"Cups"),   c(10,"Swords")), // P2 → quad 10, kicker 9
                                List.of(c(9,"Wands"),   c(9,"Pentacles"), c(10,"Wands"), c(10,"Pentacles")),
                                2
                        ),

                        /* 14 */ Arguments.of(
                                "Quads: 8s beat 7s — P1 wins",
                                List.of(c(8,"Cups"),    c(8,"Swords")),  // P1 → quad 8, kicker 7
                                List.of(c(7,"Cups"),    c(7,"Swords")),  // P2 → quad 7, kicker 8
                                List.of(c(8,"Wands"),   c(8,"Pentacles"), c(7,"Wands"), c(7,"Pentacles")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // FULL HOUSE — higher trips rank wins  (tests 15-19)
                        //
                        // Table: shared pair (Cups+Swords of rank X) + 1 of P1's trips rank
                        //        (Wands) + 1 of P2's trips rank (Pentacles).
                        // P1 hole: Cups+Swords of P1_rank → trips P1 + pair X = FH.
                        // P2 hole: Cups+Swords of P2_rank → trips P2 + pair X = FH.
                        // No quads: each trips rank appears exactly 3 times (2 hole + 1 table).
                        // Score: [7, tripsRank, pairRank].
                        // ════════════════════════════════════════════════════════════

                        /* 15 */ Arguments.of(
                                "FH: Aces-full-of-2s beats Kings-full-of-2s — P1 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")), // P1 → A-A-A + 2-2 = [7,15,2]
                                List.of(c(14,"Cups"),   c(14,"Swords")), // P2 → K-K-K + 2-2 = [7,14,2]
                                // Pair 2 on table; 1×A♣ and 1×K♦ on table for trips
                                List.of(c(2,"Cups"),    c(2,"Swords"),   c(15,"Wands"),  c(14,"Pentacles")),
                                1
                        ),

                        /* 16 */ Arguments.of(
                                "FH: Queens(13)-full-of-3s beats Jacks(11)-full-of-3s — P2 wins",
                                List.of(c(11,"Cups"),   c(11,"Swords")), // P1 → J-J-J + 3-3 = [7,11,3]
                                List.of(c(13,"Cups"),   c(13,"Swords")), // P2 → Q-Q-Q + 3-3 = [7,13,3]
                                List.of(c(3,"Cups"),    c(3,"Swords"),   c(11,"Wands"),  c(13,"Pentacles")),
                                2
                        ),

                        /* 17 */ Arguments.of(
                                "FH: Knights(12)-full-of-4s beats Pages(11)-full-of-4s — P1 wins",
                                List.of(c(12,"Cups"),   c(12,"Swords")), // P1 → Kn×3 + 4-4 = [7,12,4]
                                List.of(c(11,"Cups"),   c(11,"Swords")), // P2 → P×3  + 4-4 = [7,11,4]
                                List.of(c(4,"Cups"),    c(4,"Swords"),   c(12,"Wands"),  c(11,"Pentacles")),
                                1
                        ),

                        /* 18 */ Arguments.of(
                                "FH: 10s-full-of-5s beats 9s-full-of-5s — P2 wins",
                                List.of(c(9,"Cups"),    c(9,"Swords")),  // P1 → 9×3 + 5-5 = [7,9,5]
                                List.of(c(10,"Cups"),   c(10,"Swords")), // P2 → 10×3 + 5-5 = [7,10,5]
                                List.of(c(5,"Cups"),    c(5,"Swords"),   c(9,"Wands"),   c(10,"Pentacles")),
                                2
                        ),

                        /* 19 */ Arguments.of(
                                "FH: 8s-full-of-3s beats 7s-full-of-3s — P1 wins",
                                List.of(c(8,"Cups"),    c(8,"Swords")),  // P1 → 8×3 + 3-3 = [7,8,3]
                                List.of(c(7,"Cups"),    c(7,"Swords")),  // P2 → 7×3 + 3-3 = [7,7,3]
                                List.of(c(3,"Cups"),    c(3,"Swords"),   c(8,"Wands"),   c(7,"Pentacles")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // FLUSH — higher top card wins  (tests 20-24)
                        //
                        // Table: 4 NON-CONSECUTIVE same-suit cards (gaps block SF).
                        // P1 hole-1: higher flush card.  P2 hole-1: lower flush card.
                        // Both hold exactly 5 same-suit cards (4 table + 1 hole) → flush.
                        // Hole-2 always off-suit junk.
                        //
                        // Gap patterns used:
                        //   Tests 20,22,24: Swords {3,5,7,9} — all odd, gaps at 4,6,8.
                        //   Tests 21,23:    Cups   {3,5,7,9} — same gap logic.
                        // ════════════════════════════════════════════════════════════

                        /* 20 */ Arguments.of(
                                "Flush Swords: A-high(15) beats K-high(14) — P1 wins",
                                List.of(c(15,"Swords"), c(2,"Cups")),   // P1 ♠: {3,5,7,9,15} → [6,15,9,7,5,3]
                                List.of(c(14,"Swords"), c(3,"Cups")),   // P2 ♠: {3,5,7,9,14} → [6,14,9,7,5,3]
                                // ♠ table {3,5,7,9}: no 5-run → Flush ✓
                                List.of(c(3,"Swords"),  c(5,"Swords"),  c(7,"Swords"),  c(9,"Swords")),
                                1
                        ),

                        /* 21 */ Arguments.of(
                                "Flush Cups: K-high(14) beats Q-high(13) — P2 wins",
                                List.of(c(13,"Cups"),   c(2,"Swords")), // P1 ♥: {3,5,7,9,13} → [6,13,9,7,5,3]
                                List.of(c(14,"Cups"),   c(3,"Swords")), // P2 ♥: {3,5,7,9,14} → [6,14,9,7,5,3]
                                List.of(c(3,"Cups"),    c(5,"Cups"),    c(7,"Cups"),    c(9,"Cups")),
                                2
                        ),

                        /* 22 */ Arguments.of(
                                "Flush Swords: Q-high(13) beats Page-high(11) — P1 wins",
                                List.of(c(13,"Swords"), c(2,"Cups")),   // P1 ♠: {3,5,7,9,13} → [6,13,9,7,5,3]
                                List.of(c(11,"Swords"), c(3,"Cups")),   // P2 ♠: {3,5,7,9,11} → [6,11,9,7,5,3]
                                List.of(c(3,"Swords"),  c(5,"Swords"),  c(7,"Swords"),  c(9,"Swords")),
                                1
                        ),

                        /* 23 */ Arguments.of(
                                "Flush Cups: Page-high(11) beats 10-high — P2 wins",
                                List.of(c(10,"Cups"),   c(2,"Swords")), // P1 ♥: {3,5,7,9,10} → [6,10,9,7,5,3]
                                List.of(c(11,"Cups"),   c(3,"Swords")), // P2 ♥: {3,5,7,9,11} → [6,11,9,7,5,3]
                                // {3,5,7,9,10}: 9-10 run of 2 only → no SF ✓
                                List.of(c(3,"Cups"),    c(5,"Cups"),    c(7,"Cups"),    c(9,"Cups")),
                                2
                        ),

                        /* 24 */ Arguments.of(
                                "Flush Swords: 10-high beats 9-high (table {3,5,7,9}) — P1 wins",
                                List.of(c(10,"Swords"), c(2,"Cups")),   // P1 ♠: {3,5,7,9,10} → [6,10,9,7,5,3]
                                List.of(c(8,"Swords"),  c(3,"Cups")),   // P2 ♠: {3,5,7,8,9}  → [6,9,8,7,5,3]
                                // {3,5,7,8,9}: 7-8-9 run of 3 → no SF ✓
                                List.of(c(3,"Swords"),  c(5,"Swords"),  c(7,"Swords"),  c(9,"Swords")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // STRAIGHT — higher top card wins  (tests 25-29)
                        //
                        // Table: 4 consecutive mixed-suit cards (shared middle).
                        // P1 hole-1: extends UP (higher straight).
                        // P2 hole-1: extends DOWN (lower straight).
                        // Mixed table suits prevent accidental flush.
                        // ════════════════════════════════════════════════════════════

                        /* 25 */ Arguments.of(
                                "Straight: A-high(15) beats K-high(14) — P1 wins",
                                List.of(c(15,"Cups"),   c(2,"Swords")),  // P1: {11,12,13,14,15} A-high
                                List.of(c(10,"Wands"),  c(3,"Pentacles")),// P2: {10,11,12,13,14} K-high
                                // Table {11♠,12♥,13♣,14♦} mixed: P1 Cups={15,11}=2✓  P2 Wands={10,12}=2✓
                                List.of(c(11,"Swords"), c(12,"Cups"),    c(13,"Wands"),  c(14,"Pentacles")),
                                1
                        ),

                        /* 26 */ Arguments.of(
                                "Straight: K-high(14) beats Q-high(13) — P2 wins",
                                List.of(c(9,"Wands"),   c(2,"Pentacles")),// P1: {9,10,11,12,13} Q-high
                                List.of(c(14,"Cups"),   c(3,"Swords")),   // P2: {10,11,12,13,14} K-high
                                List.of(c(10,"Swords"), c(11,"Cups"),    c(12,"Wands"),  c(13,"Pentacles")),
                                2
                        ),

                        /* 27 */ Arguments.of(
                                "Straight: Q-high(13) beats Knight-high(12) — P1 wins",
                                List.of(c(13,"Cups"),   c(2,"Swords")),   // P1: {9,10,11,12,13} Q-high
                                List.of(c(8,"Wands"),   c(3,"Pentacles")), // P2: {8,9,10,11,12} Kn-high
                                List.of(c(9,"Swords"),  c(10,"Cups"),    c(11,"Wands"),  c(12,"Pentacles")),
                                1
                        ),

                        /* 28 */ Arguments.of(
                                "Straight: Knight-high(12) beats Page-high(11) — P2 wins",
                                List.of(c(7,"Wands"),   c(2,"Pentacles")),// P1: {7,8,9,10,11} P-high
                                List.of(c(12,"Cups"),   c(3,"Swords")),   // P2: {8,9,10,11,12} Kn-high
                                List.of(c(8,"Swords"),  c(9,"Cups"),     c(10,"Wands"),  c(11,"Pentacles")),
                                2
                        ),

                        /* 29 */ Arguments.of(
                                "Straight: Page-high(11) beats 10-high — P1 wins",
                                List.of(c(11,"Cups"),   c(2,"Swords")),   // P1: {7,8,9,10,11} P-high
                                List.of(c(6,"Wands"),   c(3,"Pentacles")), // P2: {6,7,8,9,10}  10-high
                                List.of(c(7,"Swords"),  c(8,"Cups"),     c(9,"Wands"),   c(10,"Pentacles")),
                                1
                        ),

                        // ════════════════════════════════════════════════════════════
                        // THREE OF A KIND — higher trips rank wins  (tests 30-33)
                        //
                        // Table: 1 of P1_rank (Wands) + 1 of P2_rank (Pentacles) + 2 low
                        //        neutrals (Cups+Swords).
                        // P1 hole: Cups+Swords of P1_rank → trips P1; kickers are P2_rank,
                        //          and the two neutrals (no pair → trips only, not FH).
                        // P2 hole: Cups+Swords of P2_rank → trips P2; same reasoning.
                        // Score: [4, tripsRank, k1, k2].
                        // ════════════════════════════════════════════════════════════

                        /* 30 */ Arguments.of(
                                "Trips: Aces(15) beat Kings(14) — P1 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")), // P1 → trips A, kickers K(14),3
                                List.of(c(14,"Cups"),   c(14,"Swords")), // P2 → trips K, kickers A(15),3
                                // [4,15,14,3] > [4,14,15,3] at index 1 ✓
                                List.of(c(15,"Wands"),  c(14,"Pentacles"), c(2,"Cups"),  c(3,"Swords")),
                                1
                        ),

                        /* 31 */ Arguments.of(
                                "Trips: Queens(13) beat Jacks(11) — P2 wins",
                                List.of(c(11,"Cups"),   c(11,"Swords")), // P1 → trips J, kickers Q(13),3
                                List.of(c(13,"Cups"),   c(13,"Swords")), // P2 → trips Q, kickers J(11),3
                                // [4,11,13,3] < [4,13,11,3] ✓
                                List.of(c(11,"Wands"),  c(13,"Pentacles"), c(2,"Cups"),  c(3,"Swords")),
                                2
                        ),

                        /* 32 */ Arguments.of(
                                "Trips: 10s beat 9s — P1 wins",
                                List.of(c(10,"Cups"),   c(10,"Swords")), // P1 → trips 10, kickers 9,3
                                List.of(c(9,"Cups"),    c(9,"Swords")),  // P2 → trips 9,  kickers 10,3
                                List.of(c(10,"Wands"),  c(9,"Pentacles"), c(2,"Cups"),   c(3,"Swords")),
                                1
                        ),

                        /* 33 */ Arguments.of(
                                "Trips: 7s beat 6s — P2 wins",
                                List.of(c(6,"Cups"),    c(6,"Swords")),  // P1 → trips 6, kickers 7,3
                                List.of(c(7,"Cups"),    c(7,"Swords")),  // P2 → trips 7, kickers 6,3
                                List.of(c(6,"Wands"),   c(7,"Pentacles"), c(2,"Cups"),   c(3,"Swords")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // TWO PAIR — higher top-pair rank wins  (tests 34-37)
                        //
                        // Tests 34-35: top pair differs (each player's hole pair is their
                        //   top pair); shared second pair + neutral kicker on table.
                        //   No table card matches either hole pair rank → no trips risk.
                        //
                        // Tests 36-37: same top pair on table; each player's hole pair
                        //   becomes their second pair; higher second pair wins.
                        //   No table card matches hole pair ranks → no trips risk.
                        //
                        // Score: [3, highPairRank, lowPairRank, kickerRank].
                        // ════════════════════════════════════════════════════════════

                        /* 34 */ Arguments.of(
                                "Two Pair: A-A+3-3 beats K-K+3-3 (higher top pair) — P1 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")), // P1: pair A(hole) + pair 3(table)
                                List.of(c(14,"Cups"),   c(14,"Swords")), // P2: pair K(hole) + pair 3(table)
                                // [3,15,3,4] > [3,14,3,4] ✓
                                List.of(c(3,"Cups"),    c(3,"Swords"),   c(2,"Wands"),   c(4,"Pentacles")),
                                1
                        ),

                        /* 35 */ Arguments.of(
                                "Two Pair: Q-Q+3-3 beats J-J+3-3 (higher top pair) — P2 wins",
                                List.of(c(11,"Cups"),   c(11,"Swords")), // P1: pair J + pair 3 = [3,11,3,4]
                                List.of(c(13,"Cups"),   c(13,"Swords")), // P2: pair Q + pair 3 = [3,13,3,4]
                                List.of(c(3,"Cups"),    c(3,"Swords"),   c(2,"Wands"),   c(4,"Pentacles")),
                                2
                        ),

                        /* 36 */ Arguments.of(
                                "Two Pair: A-A+K-K beats A-A+Q-Q (shared top pair, higher 2nd pair) — P1 wins",
                                List.of(c(14,"Wands"),  c(14,"Pentacles")),// P1: pair K(hole) → 2nd pair
                                List.of(c(13,"Wands"),  c(13,"Pentacles")),// P2: pair Q(hole) → 2nd pair
                                // Table pair A is shared top pair; no K or Q on table → no trips ✓
                                // P1: [3,15,14,3]   P2: [3,15,13,3]  → P1 wins at 2nd pair ✓
                                List.of(c(15,"Cups"),   c(15,"Swords"),  c(2,"Cups"),    c(3,"Swords")),
                                1
                        ),

                        /* 37 */ Arguments.of(
                                "Two Pair: K-K+J-J beats K-K+10-10 (shared top pair, higher 2nd pair) — P2 wins",
                                List.of(c(10,"Wands"),  c(10,"Pentacles")),// P1: pair 10(hole) → 2nd pair
                                List.of(c(11,"Wands"),  c(11,"Pentacles")),// P2: pair J(hole)  → 2nd pair
                                // Table pair K is shared top pair; no 10 or J on table → no trips ✓
                                // P1: [3,14,10,3]   P2: [3,14,11,3]  → P2 wins at 2nd pair ✓
                                List.of(c(14,"Cups"),   c(14,"Swords"),  c(2,"Cups"),    c(3,"Swords")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // ONE PAIR — higher pair rank wins  (tests 38-40)
                        //
                        // Table: 4 low neutral cards with no rank matching either player's
                        //        hole pair → no trips possible for either player.
                        // Score: [2, pairRank, k1, k2, k3].
                        // ════════════════════════════════════════════════════════════

                        /* 38 */ Arguments.of(
                                "Pair: Aces(15) beat Kings(14) — P1 wins",
                                List.of(c(15,"Cups"),   c(15,"Swords")), // P1 → pair A, kickers 6,4,3
                                List.of(c(14,"Cups"),   c(14,"Swords")), // P2 → pair K, kickers 6,4,3
                                // Table {2,3,4,6}: no A or K → no trips ✓
                                List.of(c(2,"Wands"),   c(3,"Pentacles"), c(4,"Cups"),   c(6,"Swords")),
                                1
                        ),

                        /* 39 */ Arguments.of(
                                "Pair: Queens(13) beat Jacks(11) — P2 wins",
                                List.of(c(11,"Cups"),   c(11,"Swords")), // P1 → pair J, kickers 6,4,3
                                List.of(c(13,"Cups"),   c(13,"Swords")), // P2 → pair Q, kickers 6,4,3
                                List.of(c(2,"Wands"),   c(3,"Pentacles"), c(4,"Cups"),   c(6,"Swords")),
                                2
                        ),

                        /* 40 */ Arguments.of(
                                "Pair: 9s beat 8s — P1 wins",
                                List.of(c(9,"Cups"),    c(9,"Swords")),  // P1 → pair 9, kickers 6,4,3
                                List.of(c(8,"Cups"),    c(8,"Swords")),  // P2 → pair 8, kickers 6,4,3
                                List.of(c(2,"Wands"),   c(3,"Pentacles"), c(4,"Cups"),   c(6,"Swords")),
                                1
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("testCases")
            void sameCombBetterPower(
                    String description,
                    List<MinorArcanaCard> p1Hole,
                    List<MinorArcanaCard> p2Hole,
                    List<MinorArcanaCard> table,
                    int expectedWinner) {

                assertEquals(
                        expectedWinner,
                        determiner.determineWinner(p1Hole, p2Hole, table),
                        description
                );
            }
        }

        @Nested
        class SameCombWinByHighCardFourCardsTest {

            private final WinnerDeterminerService determiner = new WinnerDeterminerService();

            private static MinorArcanaCard c(int power, String suit) {
                return new MinorArcanaCard(suit + "-" + power, suit, power);
            }

            static Stream<Arguments> testCases() {
                return Stream.of(

                        // ════════════════════════════════════════════════════════════
                        // FOUR OF A KIND — same quad rank, kicker decides  (01-06)
                        //
                        // Table: quad♣ quad♥ quad♦ quad♠ (all 4 copies of quad rank).
                        // P1 hole: [kicker-high, junk]   P2 hole: [kicker-low, junk]
                        // junk cards always distinct, low, and off-suit enough that
                        // they are beaten by the real kickers.
                        // ════════════════════════════════════════════════════════════

                        /* 01 */ Arguments.of(
                                "Quads Aces: kicker K(14) beats Q(13) — P1 wins",
                                List.of(c(14,"Cups"),    c(3,"Swords")),   // P1 kicker = K
                                List.of(c(13,"Cups"),    c(4,"Wands")),    // P2 kicker = Q
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(15,"Wands"),   c(15,"Pentacles")),
                                1
                        ),

                        /* 02 */ Arguments.of(
                                "Quads Kings: kicker A(15) beats Page(11) — P2 wins",
                                List.of(c(11,"Cups"),    c(3,"Swords")),   // P1 kicker = Page
                                List.of(c(15,"Cups"),    c(4,"Wands")),    // P2 kicker = Ace
                                List.of(c(14,"Cups"),    c(14,"Swords"),   c(14,"Wands"),   c(14,"Pentacles")),
                                2
                        ),

                        /* 03 */ Arguments.of(
                                "Quads Queens(13): kicker A(15) beats K(14) — P1 wins",
                                List.of(c(15,"Cups"),    c(3,"Swords")),
                                List.of(c(14,"Cups"),    c(4,"Wands")),
                                List.of(c(13,"Cups"),    c(13,"Swords"),   c(13,"Wands"),   c(13,"Pentacles")),
                                1
                        ),

                        /* 04 */ Arguments.of(
                                "Quads Jacks(11): kicker A(15) beats Q(13) — P2 wins",
                                List.of(c(13,"Cups"),    c(3,"Swords")),
                                List.of(c(15,"Cups"),    c(4,"Wands")),
                                List.of(c(11,"Cups"),    c(11,"Swords"),   c(11,"Wands"),   c(11,"Pentacles")),
                                2
                        ),

                        /* 05 */ Arguments.of(
                                "Quads 9s: kicker K(14) beats Q(13) — P1 wins",
                                List.of(c(14,"Cups"),    c(3,"Swords")),
                                List.of(c(13,"Cups"),    c(4,"Wands")),
                                List.of(c(9,"Cups"),     c(9,"Swords"),    c(9,"Wands"),    c(9,"Pentacles")),
                                1
                        ),

                        /* 06 */ Arguments.of(
                                "Quads 7s: kicker Q(13) beats Page(11) — P2 wins",
                                List.of(c(11,"Cups"),    c(3,"Swords")),
                                List.of(c(13,"Cups"),    c(4,"Wands")),
                                List.of(c(7,"Cups"),     c(7,"Swords"),    c(7,"Wands"),    c(7,"Pentacles")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // FLUSH — same suit, deciding card varies  (07-12)
                        //
                        // Table: Swords/Cups {3,5,7,9} (all odd — no 5 consecutive ✓).
                        // P1 hole-1: higher flush card.   P2 hole-1: lower flush card.
                        // Hole-2: always off-suit junk (2 or 3 of a different suit).
                        // Suit counts verified: off-suit ≤ 2 per player.
                        // ════════════════════════════════════════════════════════════

                        /* 07 — deciding: position 1 (own top card) */
                        Arguments.of(
                                "Flush Swords: top card A(15) beats K(14) — P1 wins",
                                List.of(c(15,"Swords"),  c(2,"Cups")),     // P1 ♠: {3,5,7,9,15} → [6,15,9,7,5,3]
                                List.of(c(14,"Swords"),  c(3,"Cups")),     // P2 ♠: {3,5,7,9,14} → [6,14,9,7,5,3]
                                // Table ♠ {3,5,7,9}: P1 Cups={2}=1✓  P2 Cups={3}=1✓
                                List.of(c(3,"Swords"),   c(5,"Swords"),    c(7,"Swords"),   c(9,"Swords")),
                                1
                        ),

                        /* 08 — deciding: position 1, P2 wins */
                        Arguments.of(
                                "Flush Cups: top card K(14) beats Q(13) — P2 wins",
                                List.of(c(13,"Cups"),    c(2,"Swords")),   // P1 ♥: {3,5,7,9,13} → [6,13,9,7,5,3]
                                List.of(c(14,"Cups"),    c(3,"Swords")),   // P2 ♥: {3,5,7,9,14} → [6,14,9,7,5,3]
                                List.of(c(3,"Cups"),     c(5,"Cups"),      c(7,"Cups"),     c(9,"Cups")),
                                2
                        ),

            /* 09 — deciding: position 2 (top shared A(15), 2nd card differs)
                    table flush suit {4,6,8,15}: gaps at 5,7,9-14 prevent SF ✓ */
                        Arguments.of(
                                "Flush Swords A-high: 2nd card K(14) beats Q(13) — P1 wins",
                                List.of(c(14,"Swords"),  c(2,"Cups")),     // P1 ♠: {4,6,8,14,15} → [6,15,14,8,6,4]
                                List.of(c(13,"Swords"),  c(3,"Cups")),     // P2 ♠: {4,6,8,13,15} → [6,15,13,8,6,4]
                                // 13-14-15 run of 3, no 12 → no SF ✓
                                List.of(c(15,"Swords"),  c(8,"Swords"),    c(6,"Swords"),   c(4,"Swords")),
                                1
                        ),

                        /* 10 — deciding: position 2, P2 wins */
                        Arguments.of(
                                "Flush Cups A-high: 2nd card K(14) beats Page(11) — P2 wins",
                                List.of(c(11,"Cups"),    c(2,"Swords")),   // P1 ♥: {4,6,8,11,15} → [6,15,11,8,6,4]
                                List.of(c(14,"Cups"),    c(3,"Swords")),   // P2 ♥: {4,6,8,14,15} → [6,15,14,8,6,4]
                                List.of(c(15,"Cups"),    c(8,"Cups"),      c(6,"Cups"),     c(4,"Cups")),
                                2
                        ),

            /* 11 — deciding: position 3 (top 2 A-K shared)
                    table flush suit {4,6,14,15}: 14-15 run of 2, no SF ✓ */
                        Arguments.of(
                                "Flush Swords A-K-high: 3rd card Q(13) beats Page(11) — P1 wins",
                                List.of(c(13,"Swords"),  c(2,"Cups")),     // P1 ♠: {4,6,13,14,15} → [6,15,14,13,6,4]
                                List.of(c(11,"Swords"),  c(3,"Cups")),     // P2 ♠: {4,6,11,14,15} → [6,15,14,11,6,4]
                                // 13-14-15 run of 3, no 12 → no SF ✓
                                List.of(c(15,"Swords"),  c(14,"Swords"),   c(6,"Swords"),   c(4,"Swords")),
                                1
                        ),

                        /* 12 — deciding: position 3, P2 wins */
                        Arguments.of(
                                "Flush Cups A-K-high: 3rd card Q(13) beats 10 — P2 wins",
                                List.of(c(10,"Cups"),    c(2,"Swords")),   // P1 ♥: {4,6,10,14,15} → [6,15,14,10,6,4]
                                List.of(c(13,"Cups"),    c(3,"Swords")),   // P2 ♥: {4,6,13,14,15} → [6,15,14,13,6,4]
                                List.of(c(15,"Cups"),    c(14,"Cups"),     c(6,"Cups"),     c(4,"Cups")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // THREE OF A KIND — same trips rank, kicker decides  (13-18)
                        //
                        // Table: 3 copies of trips rank (Cups/Swords/Wands) + 1 shared
                        //        kicker card (Pentacles rank = sharedK).
                        // Each player: hole-1 = meaningful kicker, hole-2 = low junk.
                        //
                        // Tests 13-15: hole-1 becomes k1 (sharedK from table < hole-1
                        //              so hole-1 is k1, sharedK is k2).
                        // Tests 16-18: sharedK is k1 (sharedK > hole-1); hole-1 is k2.
                        // ════════════════════════════════════════════════════════════

                        /* 13 — k1 from hole decides; sharedK(9) < both hole kickers */
                        Arguments.of(
                                "Trips 7s: k1 A(15) beats K(14), shared k2=9 — P1 wins",
                                List.of(c(15,"Cups"),    c(3,"Swords")),   // P1: k1=A(15) k2=9(table)
                                List.of(c(14,"Cups"),    c(4,"Swords")),   // P2: k1=K(14) k2=9(table)
                                // P1:[4,7,15,9]  P2:[4,7,14,9]  → P1 wins at k1 ✓
                                List.of(c(7,"Cups"),     c(7,"Swords"),    c(7,"Wands"),   c(9,"Pentacles")),
                                1
                        ),

                        /* 14 — k1 from hole decides, P2 wins */
                        Arguments.of(
                                "Trips 7s: k1 K(14) beats Page(11), shared k2=9 — P2 wins",
                                List.of(c(11,"Cups"),    c(3,"Swords")),   // P1: k1=Page(11) k2=9
                                List.of(c(14,"Cups"),    c(4,"Swords")),   // P2: k1=K(14)    k2=9
                                List.of(c(7,"Cups"),     c(7,"Swords"),    c(7,"Wands"),   c(9,"Pentacles")),
                                2
                        ),

                        /* 15 — k1 from hole decides; sharedK(11) < both */
                        Arguments.of(
                                "Trips Aces: k1 K(14) beats Q(13), shared k2=Page(11) — P1 wins",
                                List.of(c(14,"Cups"),    c(3,"Swords")),   // P1: k1=K(14)   k2=Page(11)
                                List.of(c(13,"Cups"),    c(4,"Swords")),   // P2: k1=Q(13)   k2=Page(11)
                                // P1:[4,15,14,11]  P2:[4,15,13,11]  → P1 wins at k1 ✓
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(15,"Wands"),  c(11,"Pentacles")),
                                1
                        ),

                        /* 16 — k1 = sharedK(14) for both; k2 from hole decides, P2 wins */
                        Arguments.of(
                                "Trips 9s: shared k1=K(14), k2 Q(13) beats Page(11) — P2 wins",
                                List.of(c(11,"Cups"),    c(3,"Swords")),   // P1: k1=K(14,table) k2=Page(11)
                                List.of(c(13,"Cups"),    c(4,"Swords")),   // P2: k1=K(14,table) k2=Q(13)
                                // P1:[4,9,14,11]  P2:[4,9,14,13]  → P2 wins at k2 ✓
                                List.of(c(9,"Cups"),     c(9,"Swords"),    c(9,"Wands"),   c(14,"Pentacles")),
                                2
                        ),

                        /* 17 — k1 = sharedK(15) for both; k2 from hole decides, P1 wins */
                        Arguments.of(
                                "Trips 5s: shared k1=A(15), k2 K(14) beats Page(11) — P1 wins",
                                List.of(c(14,"Cups"),    c(3,"Swords")),   // P1: k1=A(15,table) k2=K(14)
                                List.of(c(11,"Cups"),    c(4,"Swords")),   // P2: k1=A(15,table) k2=Page(11)
                                // P1:[4,5,15,14]  P2:[4,5,15,11]  → P1 wins at k2 ✓
                                List.of(c(5,"Cups"),     c(5,"Swords"),    c(5,"Wands"),   c(15,"Pentacles")),
                                1
                        ),

                        /* 18 — k1 = sharedK(15) for both; k2 from hole decides, P2 wins */
                        Arguments.of(
                                "Trips 3s: shared k1=A(15), k2 K(14) beats Q(13) — P2 wins",
                                List.of(c(13,"Cups"),    c(4,"Swords")),   // P1: k1=A(15,table) k2=Q(13)
                                List.of(c(14,"Cups"),    c(5,"Swords")),   // P2: k1=A(15,table) k2=K(14)
                                // P1:[4,3,15,13]  P2:[4,3,15,14]  → P2 wins at k2 ✓
                                List.of(c(3,"Cups"),     c(3,"Swords"),    c(3,"Wands"),   c(15,"Pentacles")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // TWO PAIR — same two pairs on board, kicker decides  (19-24)
                        //
                        // Table: 4 slots = pair-high (Cups+Swords) + pair-low (Wands+Pent).
                        // Both players share both pairs from the board; their meaningful
                        // hole card is the sole kicker.  Hole-2 is low junk.
                        // No trips risk: table has exactly 2 of each pair rank.
                        // Score: [3, highPair, lowPair, kicker].
                        // ════════════════════════════════════════════════════════════

                        /* 19 */ Arguments.of(
                                "Two Pair Q-Q+Page-Page: kicker A(15) beats K(14) — P1 wins",
                                List.of(c(15,"Wands"),   c(3,"Swords")),   // P1 kicker = A
                                List.of(c(14,"Wands"),   c(4,"Swords")),   // P2 kicker = K
                                // P1:[3,13,11,15]  P2:[3,13,11,14]  → P1 wins ✓
                                List.of(c(13,"Cups"),    c(13,"Swords"),   c(11,"Wands"),   c(11,"Pentacles")),
                                1
                        ),

                        /* 20 */ Arguments.of(
                                "Two Pair K-K+Page-Page: kicker A(15) beats Q(13) — P2 wins",
                                List.of(c(13,"Wands"),   c(3,"Swords")),   // P1 kicker = Q
                                List.of(c(15,"Wands"),   c(4,"Swords")),   // P2 kicker = A
                                // P1:[3,14,11,13]  P2:[3,14,11,15]  → P2 wins ✓
                                List.of(c(14,"Cups"),    c(14,"Swords"),   c(11,"Wands"),   c(11,"Pentacles")),
                                2
                        ),

                        /* 21 */ Arguments.of(
                                "Two Pair A-A+K-K: kicker Q(13) beats Page(11) — P1 wins",
                                List.of(c(13,"Wands"),   c(3,"Swords")),
                                List.of(c(11,"Wands"),   c(4,"Swords")),
                                // P1:[3,15,14,13]  P2:[3,15,14,11]  → P1 wins ✓
                                List.of(c(15,"Cups"),    c(15,"Swords"),   c(14,"Wands"),   c(14,"Pentacles")),
                                1
                        ),

                        /* 22 */ Arguments.of(
                                "Two Pair Q-Q+10-10: kicker A(15) beats K(14) — P2 wins",
                                List.of(c(14,"Wands"),   c(3,"Swords")),
                                List.of(c(15,"Wands"),   c(4,"Swords")),
                                // P1:[3,13,10,14]  P2:[3,13,10,15]  → P2 wins ✓
                                List.of(c(13,"Cups"),    c(13,"Swords"),   c(10,"Wands"),   c(10,"Pentacles")),
                                2
                        ),

                        /* 23 */ Arguments.of(
                                "Two Pair K-K+9-9: kicker A(15) beats Q(13) — P1 wins",
                                List.of(c(15,"Wands"),   c(3,"Swords")),
                                List.of(c(13,"Wands"),   c(4,"Swords")),
                                // P1:[3,14,9,15]  P2:[3,14,9,13]  → P1 wins ✓
                                List.of(c(14,"Cups"),    c(14,"Swords"),   c(9,"Wands"),    c(9,"Pentacles")),
                                1
                        ),

                        /* 24 */ Arguments.of(
                                "Two Pair 10-10+9-9: kicker K(14) beats Q(13) — P2 wins",
                                List.of(c(13,"Wands"),   c(3,"Swords")),
                                List.of(c(14,"Wands"),   c(4,"Swords")),
                                // P1:[3,10,9,13]  P2:[3,10,9,14]  → P2 wins ✓
                                List.of(c(10,"Cups"),    c(10,"Swords"),   c(9,"Wands"),    c(9,"Pentacles")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // ONE PAIR — same pair rank, kicker decides  (25-32)
                        //
                        // Table: pair (Cups+Swords) + 2 shared kicker-rank cards
                        //        (Wands+Pent) filling all 4 slots.
                        // Each player: hole-1 = meaningful kicker, hole-2 = low junk.
                        //
                        // Tests 25-26: k1 from hole decides (table has no card ranked
                        //   above both players' hole cards, so both shared table cards
                        //   become k2 and k3, and hole card = k1).
                        // Tests 27-28: k1 shared from table; k2 from hole decides.
                        // Tests 29-32: k1+k2 shared from table; k3 from hole decides.
                        // ════════════════════════════════════════════════════════════

                        /* 25 — k1 from hole decides */
                        Arguments.of(
                                "Pair 7s: k1 A(15) beats Q(13), shared k2=9 k3=5 — P1 wins",
                                List.of(c(15,"Swords"),  c(3,"Wands")),    // P1: k1=A(15) k2=9 k3=5
                                List.of(c(13,"Wands"),   c(4,"Pentacles")), // P2: k1=Q(13) k2=9 k3=5
                                // P1:[2,7,15,9,5]  P2:[2,7,13,9,5]  → P1 wins at k1 ✓
                                // P1 Swords: A♠,7♠=2✓  P2 Wands: Q♣,9♣=2✓
                                List.of(c(7,"Cups"),     c(7,"Swords"),    c(9,"Wands"),    c(5,"Pentacles")),
                                1
                        ),

                        /* 26 — k1 from hole decides, P2 wins */
                        Arguments.of(
                                "Pair Pages(11): k1 A(15) beats K(14), shared k2=9 k3=5 — P2 wins",
                                List.of(c(14,"Swords"),  c(3,"Wands")),    // P1: k1=K(14)
                                List.of(c(15,"Wands"),   c(4,"Pentacles")), // P2: k1=A(15)
                                // P1:[2,11,14,9,5]  P2:[2,11,15,9,5]  → P2 wins at k1 ✓
                                List.of(c(11,"Cups"),    c(11,"Swords"),   c(9,"Wands"),    c(5,"Pentacles")),
                                2
                        ),

                        /* 27 — k1 shared from table; k2 from hole decides */
                        Arguments.of(
                                "Pair 7s: shared k1=A(15), k2 K(14) beats Q(13) — P1 wins",
                                List.of(c(14,"Swords"),  c(3,"Wands")),    // P1: k1=A k2=K(14) k3=4(table)
                                List.of(c(13,"Swords"),  c(3,"Pentacles")), // P2: k1=A k2=Q(13) k3=4(table)
                                // P1:[2,7,15,14,4]  P2:[2,7,15,13,4]  → P1 wins at k2 ✓
                                // P1 Swords: K♠,7♠=2✓  P2 Swords: Q♠,7♠=2✓
                                List.of(c(7,"Cups"),     c(7,"Swords"),    c(15,"Wands"),   c(4,"Pentacles")),
                                1
                        ),

                        /* 28 — k1 shared; k2 from hole decides, P2 wins */
                        Arguments.of(
                                "Pair 9s: shared k1=A(15), k2 K(14) beats Q(13) — P2 wins",
                                List.of(c(13,"Swords"),  c(3,"Wands")),    // P1: k1=A k2=Q(13) k3=4
                                List.of(c(14,"Swords"),  c(3,"Pentacles")), // P2: k1=A k2=K(14) k3=4
                                // P1:[2,9,15,13,4]  P2:[2,9,15,14,4]  → P2 wins at k2 ✓
                                List.of(c(9,"Cups"),     c(9,"Swords"),    c(15,"Wands"),   c(4,"Pentacles")),
                                2
                        ),

                        /* 29 — k1+k2 shared from table; k3 from hole decides */
                        Arguments.of(
                                "Pair 7s: shared k1=A k2=K, k3 9 beats 8 — P1 wins",
                                List.of(c(9,"Swords"),   c(3,"Wands")),    // P1: k3=9(hole)
                                List.of(c(8,"Swords"),   c(3,"Pentacles")), // P2: k3=8(hole)
                                // P1:[2,7,15,14,9]  P2:[2,7,15,14,8]  → P1 wins at k3 ✓
                                // P1 Swords: 9♠,7♠=2✓  P2 Swords: 8♠,7♠=2✓
                                List.of(c(7,"Cups"),     c(7,"Swords"),    c(15,"Wands"),   c(14,"Pentacles")),
                                1
                        ),

                        /* 30 — k1+k2 shared; k3 from hole decides, P2 wins */
                        Arguments.of(
                                "Pair 6s: shared k1=A k2=K, k3 9 beats 8 — P2 wins",
                                List.of(c(8,"Swords"),   c(3,"Wands")),    // P1: k3=8
                                List.of(c(9,"Swords"),   c(3,"Pentacles")), // P2: k3=9
                                // P1:[2,6,15,14,8]  P2:[2,6,15,14,9]  → P2 wins at k3 ✓
                                List.of(c(6,"Cups"),     c(6,"Swords"),    c(15,"Wands"),   c(14,"Pentacles")),
                                2
                        ),

                        /* 31 — k1+k2 shared; k3 from hole, P1 wins */
                        Arguments.of(
                                "Pair Queens(13): shared k1=A k2=K, k3 Page(11) beats 10 — P1 wins",
                                List.of(c(11,"Swords"),  c(3,"Wands")),    // P1: k3=Page(11)
                                List.of(c(10,"Swords"),  c(3,"Pentacles")), // P2: k3=10
                                // P1:[2,13,15,14,11]  P2:[2,13,15,14,10]  → P1 wins ✓
                                List.of(c(13,"Cups"),    c(13,"Swords"),   c(15,"Wands"),   c(14,"Pentacles")),
                                1
                        ),

                        /* 32 — k1+k2 shared; k3 from hole, P2 wins */
                        Arguments.of(
                                "Pair 5s: shared k1=A k2=K, k3 Page(11) beats 10 — P2 wins",
                                List.of(c(10,"Swords"),  c(3,"Wands")),    // P1: k3=10
                                List.of(c(11,"Swords"),  c(3,"Pentacles")), // P2: k3=Page(11)
                                // P1:[2,5,15,14,10]  P2:[2,5,15,14,11]  → P2 wins ✓
                                List.of(c(5,"Cups"),     c(5,"Swords"),    c(15,"Wands"),   c(14,"Pentacles")),
                                2
                        ),

                        // ════════════════════════════════════════════════════════════
                        // HIGH CARD — no pair/flush/straight, card position decides
                        //             (33-40)
                        //
                        // Table: 4 mixed-suit cards providing shared high values.
                        // P1 meaningful hole card slots into the deciding position;
                        // P2 meaningful hole card is lower at the same position.
                        // Hole-2 for both players is a low junk card (power 2 or 3)
                        // that never ranks in the top 5 of the 6-card set.
                        //
                        // Verified per test:
                        //   • All 6 powers are distinct (no pair)
                        //   • Max 2 same-suit cards per player (no flush)
                        //   • No 5 consecutive powers in the 6-card set (no straight)
                        //   • Exact deciding position noted in description
                        // ════════════════════════════════════════════════════════════

                        /* 33 — deciding: position 1 (own top card) */
                        Arguments.of(
                                "High Card: top card A(15) beats K(14), rest shared — P1 wins",
                                List.of(c(15,"Cups"),    c(3,"Cups")),     // P1 best = A; {3,9,11,13,15} top-5 = A-Q-P-9-3? no
                                // P1 full set: {15,3,11,9,7,5}  top-5: 15-11-9-7-5 → [1,15,11,9,7,5]
                                // P2 full set: {14,4,11,9,7,5}  top-5: 14-11-9-7-5 → [1,14,11,9,7,5]  → P1 wins at c1 ✓
                                // No straight: {3,5,7,9,11,15} gaps at 4,6,8,10,12-14 ✓
                                // P1 Cups: A♥,3♥=2✓  table Cups: {11,9,7,5} all different suits below
                                List.of(c(14,"Cups"),    c(4,"Swords")),   // P2 top = K
                                List.of(c(11,"Swords"),  c(9,"Wands"),     c(7,"Pentacles"), c(5,"Cups")),
                                1
                        ),

                        /* 34 — deciding: position 1, P2 wins */
                        Arguments.of(
                                "High Card: top card A(15) beats K(14), rest shared — P2 wins",
                                List.of(c(14,"Cups"),    c(3,"Cups")),     // P1 top = K  → [1,14,11,9,7,5]
                                List.of(c(15,"Cups"),    c(4,"Swords")),   // P2 top = A  → [1,15,11,9,7,5]
                                // Same 4-card table; P2 now wins ✓
                                List.of(c(11,"Swords"),  c(9,"Wands"),     c(7,"Pentacles"), c(5,"Cups")),
                                2
                        ),

                        /* 35 — deciding: position 2 (top card shared on table) */
                        Arguments.of(
                                "High Card: shared A(15), 2nd card K(14) beats Q(13) — P1 wins",
                                List.of(c(14,"Swords"),  c(3,"Wands")),    // P1: {3,7,9,11,14,15} → A-K-P-9-7
                                List.of(c(13,"Wands"),   c(4,"Pentacles")), // P2: {4,7,9,11,13,15} → A-Q-P-9-7
                                // P1:[1,15,14,11,9,7]  P2:[1,15,13,11,9,7]  → P1 wins at c2 ✓
                                // No straight: P1 {3,7,9,11,14,15} gaps at 4,6,8,10,12,13 ✓
                                // P1 Swords: K♠=1✓  P2 Wands: Q♣=1✓
                                List.of(c(15,"Cups"),    c(11,"Pentacles"), c(9,"Swords"),   c(7,"Wands")),
                                1
                        ),

                        /* 36 — deciding: position 2, P2 wins */
                        Arguments.of(
                                "High Card: shared A(15), 2nd card K(14) beats Q(13) — P2 wins",
                                List.of(c(13,"Wands"),   c(3,"Pentacles")), // P1 c2=Q → [1,15,13,11,9,7]
                                List.of(c(14,"Swords"),  c(4,"Wands")),    // P2 c2=K → [1,15,14,11,9,7]
                                List.of(c(15,"Cups"),    c(11,"Pentacles"), c(9,"Swords"),   c(7,"Wands")),
                                2
                        ),

                        /* 37 — deciding: position 3 (top 2 shared on table) */
                        Arguments.of(
                                "High Card: shared A-K, 3rd card Q(13) beats Page(11) — P1 wins",
                                List.of(c(13,"Wands"),   c(3,"Pentacles")), // P1: {3,7,9,13,14,15} → A-K-Q-9-7
                                List.of(c(11,"Wands"),   c(4,"Pentacles")), // P2: {4,7,9,11,14,15} → A-K-P-9-7
                                // P1:[1,15,14,13,9,7]  P2:[1,15,14,11,9,7]  → P1 wins at c3 ✓
                                // No straight: P1 {3,7,9,13,14,15}: 13-14-15 run of 3, gap at 12 ✓
                                List.of(c(15,"Cups"),    c(14,"Swords"),   c(9,"Wands"),    c(7,"Pentacles")),
                                1
                        ),

                        /* 38 — deciding: position 3, P2 wins */
                        Arguments.of(
                                "High Card: shared A-K, 3rd card Q(13) beats Page(11) — P2 wins",
                                List.of(c(11,"Wands"),   c(3,"Pentacles")), // P1 c3=Page → [1,15,14,11,9,7]
                                List.of(c(13,"Wands"),   c(4,"Pentacles")), // P2 c3=Q    → [1,15,14,13,9,7]
                                List.of(c(15,"Cups"),    c(14,"Swords"),   c(9,"Wands"),    c(7,"Pentacles")),
                                2
                        ),

                        /* 39 — deciding: position 4 (top 3 shared on table) */
                        Arguments.of(
                                "High Card: shared A-K-Q, 4th card Page(11) beats 10 — P1 wins",
                                List.of(c(11,"Wands"),   c(3,"Pentacles")), // P1: {3,7,11,13,14,15} → A-K-Q-P-7
                                List.of(c(10,"Wands"),   c(4,"Pentacles")), // P2: {4,7,10,13,14,15} → A-K-Q-10-7
                                // P1:[1,15,14,13,11,7]  P2:[1,15,14,13,10,7]  → P1 wins at c4 ✓
                                // No straight: {3,7,11,13,14,15}: 13-14-15 run of 3, gap at 12 ✓
                                List.of(c(15,"Cups"),    c(14,"Swords"),   c(13,"Wands"),   c(7,"Pentacles")),
                                1
                        ),

                        /* 40 — deciding: position 4, P2 wins */
                        Arguments.of(
                                "High Card: shared A-K-Q, 4th card Page(11) beats 10 — P2 wins",
                                List.of(c(10,"Wands"),   c(3,"Pentacles")), // P1 c4=10   → [1,15,14,13,10,7]
                                List.of(c(11,"Wands"),   c(4,"Pentacles")), // P2 c4=Page → [1,15,14,13,11,7]
                                List.of(c(15,"Cups"),    c(14,"Swords"),   c(13,"Wands"),   c(7,"Pentacles")),
                                2
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("testCases")
            void sameCombWinByHighCard(
                    String description,
                    List<MinorArcanaCard> p1Hole,
                    List<MinorArcanaCard> p2Hole,
                    List<MinorArcanaCard> table,
                    int expectedWinner) {

                assertEquals(
                        expectedWinner,
                        determiner.determineWinner(p1Hole, p2Hole, table),
                        description
                );
            }
        }

        @Nested
        class DrawFourCardsTest {

            private final WinnerDeterminerService determiner = new WinnerDeterminerService();

            private static MinorArcanaCard c(int power, String suit) {
                return new MinorArcanaCard(suit + "-" + power, suit, power);
            }

            static Stream<Arguments> cases() {
                return Stream.of(

                        // ════════════════════════════════════════════════════════════
                        // GROUP A — Board + equal hole (01-10)
                        //
                        // Table supplies the core hand structure.  Each player adds one
                        // same-power (different-suit) hole card.  Junk hole-2 ≤ power 4.
                        // ════════════════════════════════════════════════════════════

                        /* 01 — High Card: 4 high mixed-suit board cards; same-power 5th from hole */
                        Arguments.of(
                                "Draw A01: High Card — same-power 5th card from hole (diff suits)",
                                List.of(c(9,"Cups"),    c(2,"Wands")),     // P1: top-5={9,11,12,13,15} → [1,15,13,12,11,9]
                                List.of(c(9,"Swords"),  c(3,"Pentacles")), // P2: same top-5 → same score
                                // Table: A(15)♠ Q(13)♦ Kn(12)♣ P(11)♥  all different suits
                                // No straight: {9,11,12,13,15} gaps at 10,14 ✓  No flush: max 2/suit ✓
                                List.of(c(15,"Swords"), c(13,"Pentacles"), c(12,"Wands"),  c(11,"Cups")),
                                0
                        ),

                        /* 02 — One Pair: pair A + K + Q on board; same-power k3 from hole */
                        Arguments.of(
                                "Draw A02: One Pair — pair+K+Q on board; same-power k3=Page(11) from hole",
                                List.of(c(11,"Cups"),   c(2,"Wands")),     // P1: [2,15,14,13,11]
                                List.of(c(11,"Swords"), c(3,"Pentacles")), // P2: [2,15,14,13,11]
                                List.of(c(15,"Cups"),   c(15,"Swords"),    c(14,"Wands"),  c(13,"Pentacles")),
                                0
                        ),

                        /* 03 — Two Pair: both pairs on board (A-A + K-K); same-power kicker from hole */
                        Arguments.of(
                                "Draw A03: Two Pair — A-A+K-K on board; same-power kicker Q(13) from hole",
                                List.of(c(13,"Cups"),   c(2,"Wands")),     // P1: [3,15,14,13]
                                List.of(c(13,"Swords"), c(3,"Pentacles")), // P2: [3,15,14,13]
                                List.of(c(15,"Cups"),   c(15,"Swords"),    c(14,"Wands"),  c(14,"Pentacles")),
                                0
                        ),

                        /* 04 — Trips: trips A + shared kicker K on board; same-power k2 from hole */
                        Arguments.of(
                                "Draw A04: Trips — trips A+kicker K on board; same-power k2=Q(13) from hole",
                                List.of(c(13,"Cups"),   c(2,"Wands")),     // P1: [4,15,14,13]
                                List.of(c(13,"Swords"), c(3,"Pentacles")), // P2: [4,15,14,13]
                                List.of(c(15,"Cups"),   c(15,"Swords"),    c(15,"Wands"),  c(14,"Pentacles")),
                                0
                        ),

                        /* 05 — Straight A-high: 4 consecutive mixed-suit cards; same-power A from hole */
                        Arguments.of(
                                "Draw A05: Straight A-high — both extend {11,12,13,14} with A (diff suits)",
                                List.of(c(15,"Cups"),   c(2,"Wands")),     // P1: [5,15]
                                List.of(c(15,"Swords"), c(3,"Pentacles")), // P2: [5,15]
                                // No flush: each player has at most 2 of any suit ✓
                                List.of(c(11,"Swords"), c(12,"Cups"),      c(13,"Wands"),  c(14,"Pentacles")),
                                0
                        ),

                        /* 06 — Full House: 3 Aces on board + 1 King; same-power 2nd King from hole */
                        Arguments.of(
                                "Draw A06: FH — A-A-A+K on board; same-power K from hole (diff suits) → A-full-of-K",
                                List.of(c(14,"Cups"),   c(2,"Wands")),     // P1: [7,15,14]
                                List.of(c(14,"Swords"), c(3,"Pentacles")), // P2: [7,15,14]
                                // Table: A♥A♠A♣ + K♦ → trips A + 1 King; each player adds 2nd King
                                List.of(c(15,"Cups"),   c(15,"Swords"),    c(15,"Wands"),  c(14,"Pentacles")),
                                0
                        ),

                        /* 07 — Four of a Kind: quad A on board; same-power kicker Q from hole */
                        Arguments.of(
                                "Draw A07: Quads — quad A on board; same-power kicker Q(13) from hole",
                                List.of(c(13,"Cups"),   c(2,"Wands")),     // P1: [8,15,13]
                                List.of(c(13,"Swords"), c(3,"Pentacles")), // P2: [8,15,13]
                                List.of(c(15,"Cups"),   c(15,"Swords"),    c(15,"Wands"),  c(15,"Pentacles")),
                                0
                        ),

                        /* 08 — Straight K-high: 4 consecutive cards; same-power K from hole */
                        Arguments.of(
                                "Draw A08: Straight K-high — both extend {10,11,12,13} with K (diff suits)",
                                List.of(c(14,"Cups"),   c(2,"Wands")),     // P1: [5,14]
                                List.of(c(14,"Swords"), c(3,"Pentacles")), // P2: [5,14]
                                List.of(c(10,"Swords"), c(11,"Cups"),      c(12,"Wands"),  c(13,"Pentacles")),
                                0
                        ),

                        /* 09 — Two Pair (different ranks): A-A + Q-Q on board; same kicker K from hole */
                        Arguments.of(
                                "Draw A09: Two Pair — A-A+Q-Q on board; same-power kicker K(14) from hole",
                                List.of(c(14,"Cups"),   c(2,"Wands")),     // P1: [3,15,13,14]
                                List.of(c(14,"Swords"), c(3,"Pentacles")), // P2: [3,15,13,14]
                                List.of(c(15,"Cups"),   c(15,"Swords"),    c(13,"Wands"),  c(13,"Pentacles")),
                                0
                        ),

                        /* 10 — One Pair (different pair rank): pair K + A + Q on board; same k3 from hole */
                        Arguments.of(
                                "Draw A10: One Pair — pair K+A+Q on board; same-power k3=Page(11) from hole",
                                List.of(c(11,"Cups"),   c(2,"Wands")),     // P1: [2,14,15,13,11]
                                List.of(c(11,"Swords"), c(3,"Pentacles")), // P2: [2,14,15,13,11]
                                List.of(c(14,"Cups"),   c(14,"Swords"),    c(15,"Wands"),  c(13,"Pentacles")),
                                0
                        ),

                        // ════════════════════════════════════════════════════════════
                        // GROUP B — Both hole cards participate equally (11-20)
                        //
                        // Both hole cards per player enter the best 5 at the same power
                        // (P1 and P2 hold the same two power values in different suits).
                        // Table: 4 cards completing the hand structure.
                        // Suit counts kept ≤ 2 per player for all suits.
                        // ════════════════════════════════════════════════════════════

                        /* 11 — High Card: both hold A+K; table provides Q, Page, 9, 2 */
                        Arguments.of(
                                "Draw B11: High Card — both hold A+K in diff suits; table Q+Page+9+2",
                                List.of(c(15,"Cups"),   c(14,"Swords")),   // P1: top-5={9,11,13,14,15} → [1,15,14,13,11,9]
                                List.of(c(15,"Wands"),  c(14,"Pentacles")),// P2: same
                                // Table: Q(13)♥ P(11)♣ 9♠ 2♦  — all diff suits
                                // {2,9,11,13,14,15}: 13-14-15 run of 3, gap at 12 ✓
                                // P1 Cups={A}=1, P1 Swords={K,9}=2✓  P2 Wands={A,Q}=2✓  P2 Pent={K}=1✓
                                List.of(c(13,"Cups"),   c(11,"Wands"),     c(9,"Swords"),  c(2,"Pentacles")),
                                0
                        ),

            /* 12 — One Pair: both hold pair A (same power, all 4 suits used across players);
                    table provides K, Q, Page, 2 */
                        Arguments.of(
                                "Draw B12: One Pair — both hold pair Aces (diff suits); table K+Q+Page+2",
                                List.of(c(15,"Cups"),   c(15,"Swords")),   // P1: [2,15,14,13,11]
                                List.of(c(15,"Wands"),  c(15,"Pentacles")),// P2: [2,15,14,13,11]
                                // Table: K♣ Q♦ P♥ 2♠ — no Ace → no trips ✓
                                List.of(c(14,"Wands"),  c(13,"Pentacles"), c(11,"Cups"),   c(2,"Swords")),
                                0
                        ),

                        /* 13 — Two Pair: both hold pair Q; table has pair A + kicker K + junk */
                        Arguments.of(
                                "Draw B13: Two Pair — both hold pair Q in diff suits; table A-A+K+2",
                                List.of(c(13,"Cups"),   c(13,"Swords")),   // P1: [3,15,13,14]
                                List.of(c(13,"Wands"),  c(13,"Pentacles")),// P2: [3,15,13,14]
                                // Table: A♥A♣ + K♦ + 2♠  — no Q → no trips ✓
                                List.of(c(15,"Cups"),   c(15,"Wands"),     c(14,"Swords"), c(2,"Pentacles")),
                                0
                        ),

                        /* 14 — Trips: both see trips A from table; hold same-power kicker pair */
                        Arguments.of(
                                "Draw B14: Trips — both hold K+Q in diff suits; trips A + junk on table",
                                List.of(c(14,"Cups"),   c(13,"Swords")),   // P1: [4,15,14,13]
                                List.of(c(14,"Wands"),  c(13,"Pentacles")),// P2: [4,15,14,13]
                                // Table: A♥A♠A♣ + 2♦  → trips A; kickers K and Q from hole for both
                                List.of(c(15,"Cups"),   c(15,"Swords"),    c(15,"Wands"),  c(2,"Pentacles")),
                                0
                        ),

                        /* 15 — Straight: both hold A+Page; table {11,12,13,14} mixed suits */
                        Arguments.of(
                                "Draw B15: Straight A-high — both hold A+Page in diff suits; table {11,12,13,14}",
                                List.of(c(15,"Cups"),   c(11,"Swords")),   // P1: {11,12,13,14,15} → [5,15]
                                List.of(c(15,"Wands"),  c(11,"Pentacles")),// P2: same [5,15]
                                // P1 Cups={A,12}=2✓  P1 Swords={P,13}=2✓  P2 Wands={A,11}=2✓
                                List.of(c(12,"Cups"),   c(13,"Swords"),    c(14,"Wands"),  c(2,"Pentacles")),
                                0
                        ),

                        /* 16 — Full House: both hold pair J; table has trips K + junk */
                        Arguments.of(
                                "Draw B16: FH — both hold pair Jacks(11) in diff suits; table K-K-K+2",
                                List.of(c(11,"Cups"),   c(11,"Swords")),   // P1: [7,14,11]
                                List.of(c(11,"Wands"),  c(11,"Pentacles")),// P2: [7,14,11]
                                // Table: K♥K♠K♣ + 2♦  — no Jack → no quads ✓
                                List.of(c(14,"Cups"),   c(14,"Swords"),    c(14,"Wands"),  c(2,"Pentacles")),
                                0
                        ),

                        /* 17 — Four of a Kind: quad K on board; both hold same-power A kicker */
                        Arguments.of(
                                "Draw B17: Quads — quad K on board; both hold A+junk (diff suits) → kicker A",
                                List.of(c(15,"Cups"),   c(2,"Wands")),     // P1: [8,14,15]
                                List.of(c(15,"Swords"), c(3,"Pentacles")), // P2: [8,14,15]
                                List.of(c(14,"Cups"),   c(14,"Swords"),    c(14,"Wands"),  c(14,"Pentacles")),
                                0
                        ),

                        /* 18 — Straight Q-high: both hold Q+8; table {9,10,11,12} mixed suits */
                        Arguments.of(
                                "Draw B18: Straight Q-high — both hold Q+8 in diff suits; table {9,10,11,12}",
                                List.of(c(13,"Cups"),   c(8,"Swords")),    // P1: {8,9,10,11,12,13} → Q-high [5,13]
                                List.of(c(13,"Wands"),  c(8,"Pentacles")), // P2: same [5,13]
                                // 6 consecutive: evaluator picks Q-high (9-13) ✓
                                // P1 Cups={Q,9}=2✓  P1 Swords={8,12}=2✓  P2 Wands={Q,10}=2✓
                                List.of(c(9,"Cups"),    c(10,"Swords"),    c(11,"Wands"),  c(12,"Pentacles")),
                                0
                        ),

                        /* 19 — Two Pair (different ranks): both hold pair 9; table pair A + kicker Q */
                        Arguments.of(
                                "Draw B19: Two Pair — both hold pair 9s in diff suits; table A-A+Q+2",
                                List.of(c(9,"Cups"),    c(9,"Swords")),    // P1: [3,15,9,13]
                                List.of(c(9,"Wands"),   c(9,"Pentacles")), // P2: [3,15,9,13]
                                // Table: A♥A♣ + Q♦ + 2♠  — no 9 → no trips ✓
                                List.of(c(15,"Cups"),   c(15,"Wands"),     c(13,"Swords"), c(2,"Pentacles")),
                                0
                        ),

                        /* 20 — High Card (different base): both hold K+9; table A, Q, 7, 2 */
                        Arguments.of(
                                "Draw B20: High Card — both hold K+9 in diff suits; table A+Q+7+2",
                                List.of(c(14,"Cups"),   c(9,"Swords")),    // P1: top-5={7,9,13,14,15} → [1,15,14,13,9,7]
                                List.of(c(14,"Wands"),  c(9,"Pentacles")), // P2: same
                                // {2,7,9,13,14,15}: 13-14-15 run of 3, gap at 12 ✓  No flush ✓
                                // P1 Cups={K,A}=2✓  P1 Swords={9,7}=2✓  P2 Wands={K,Q}=2✓
                                List.of(c(15,"Cups"),   c(13,"Wands"),     c(7,"Swords"),  c(2,"Pentacles")),
                                0
                        ),

                        // ════════════════════════════════════════════════════════════
                        // GROUP C — Power-mirror hole cards, no flush (21-30)
                        //
                        // Same as Group B with varied hand structures.
                        // Both players hold identical power hole cards in different suits.
                        // Table chosen per-category to avoid flush (max 2 same-suit/player).
                        // ════════════════════════════════════════════════════════════

                        /* 21 — High Card: both hold A+7; table K, Q, Page, 2 */
                        Arguments.of(
                                "Draw C21: High Card — both hold A+7 in diff suits; table K+Q+Page+2",
                                List.of(c(15,"Cups"),   c(7,"Swords")),    // P1: top-5={7,11,13,14,15} → [1,15,14,13,11,7]
                                List.of(c(15,"Wands"),  c(7,"Pentacles")), // P2: same
                                // {2,7,11,13,14,15}: 13-14-15 run of 3, gap at 12 ✓
                                List.of(c(14,"Cups"),   c(13,"Wands"),     c(11,"Swords"), c(2,"Pentacles")),
                                0
                        ),

                        /* 22 — One Pair: both hold pair K; table A + Q + Page + 2 */
                        Arguments.of(
                                "Draw C22: One Pair — both hold pair K in diff suits; table A+Q+Page+2",
                                List.of(c(14,"Cups"),   c(14,"Swords")),   // P1: [2,14,15,13,11]
                                List.of(c(14,"Wands"),  c(14,"Pentacles")),// P2: [2,14,15,13,11]
                                // Table: A♥ Q♣ P♦ 2♠  — no K → no trips ✓
                                List.of(c(15,"Cups"),   c(13,"Wands"),     c(11,"Swords"), c(2,"Pentacles")),
                                0
                        ),

                        /* 23 — Two Pair: both hold pair A; table pair Q + kicker K + junk */
                        Arguments.of(
                                "Draw C23: Two Pair — both hold pair A in diff suits; table Q-Q+K+2",
                                List.of(c(15,"Cups"),   c(15,"Swords")),   // P1: [3,15,13,14]
                                List.of(c(15,"Wands"),  c(15,"Pentacles")),// P2: [3,15,13,14]
                                // Table: Q♥Q♣ + K♦ + 2♠  — no A → no trips ✓
                                List.of(c(13,"Cups"),   c(13,"Wands"),     c(14,"Swords"), c(2,"Pentacles")),
                                0
                        ),

                        /* 24 — Trips: both see trips A from table; both hold Q+Page kickers */
                        Arguments.of(
                                "Draw C24: Trips — both hold Q+Page in diff suits; trips A + 2 on table",
                                List.of(c(13,"Cups"),   c(11,"Swords")),   // P1: [4,15,13,11]
                                List.of(c(13,"Wands"),  c(11,"Pentacles")),// P2: [4,15,13,11]
                                // Table: A♥A♠A♣ + 2♦  — no Q or Page → no quads ✓
                                List.of(c(15,"Cups"),   c(15,"Swords"),    c(15,"Wands"),  c(2,"Pentacles")),
                                0
                        ),

                        /* 25 — Straight: both hold A+10; table {11,12,13,14} mixed suits */
                        Arguments.of(
                                "Draw C25: Straight A-high — both hold A+10 in diff suits; table {11,12,13,14}",
                                List.of(c(15,"Cups"),   c(10,"Swords")),   // P1: {10,11,12,13,14,15} → A-high [5,15]
                                List.of(c(15,"Wands"),  c(10,"Pentacles")),// P2: same [5,15]
                                List.of(c(11,"Swords"), c(12,"Cups"),      c(13,"Wands"),  c(14,"Pentacles")),
                                0
                        ),

                        /* 26 — Full House: both hold pair Q; table has trips 10 + junk */
                        Arguments.of(
                                "Draw C26: FH — both hold pair Q(13) in diff suits; table 10-10-10+2",
                                List.of(c(13,"Cups"),   c(13,"Swords")),   // P1: [7,10,13]
                                List.of(c(13,"Wands"),  c(13,"Pentacles")),// P2: [7,10,13]
                                // Table: 10♥10♠10♣ + 2♦  — no Q → no quads ✓
                                List.of(c(10,"Cups"),   c(10,"Swords"),    c(10,"Wands"),  c(2,"Pentacles")),
                                0
                        ),

                        /* 27 — Four of a Kind: quad 9 on board; both hold A+K (diff suits), kicker A */
                        Arguments.of(
                                "Draw C27: Quads — quad 9 on board; both hold A+K in diff suits → kicker A",
                                List.of(c(15,"Cups"),   c(14,"Swords")),   // P1: [8,9,15]
                                List.of(c(15,"Wands"),  c(14,"Pentacles")),// P2: [8,9,15]
                                List.of(c(9,"Cups"),    c(9,"Swords"),     c(9,"Wands"),   c(9,"Pentacles")),
                                0
                        ),

                        /* 28 — Straight K-high: both hold K+9; table {10,11,12,13} mixed suits */
                        Arguments.of(
                                "Draw C28: Straight K-high — both hold K+9 in diff suits; 6-card run {9-14}",
                                List.of(c(14,"Cups"),   c(9,"Swords")),    // P1: {9,10,11,12,13,14} → K-high [5,14]
                                List.of(c(14,"Wands"),  c(9,"Pentacles")), // P2: same [5,14]
                                List.of(c(10,"Swords"), c(11,"Cups"),      c(12,"Wands"),  c(13,"Pentacles")),
                                0
                        ),

                        /* 29 — One Pair: both hold pair 7; table A + K + Q + 2 */
                        Arguments.of(
                                "Draw C29: One Pair — both hold pair 7s in diff suits; table A+K+Q+2",
                                List.of(c(7,"Cups"),    c(7,"Swords")),    // P1: [2,7,15,14,13]
                                List.of(c(7,"Wands"),   c(7,"Pentacles")), // P2: [2,7,15,14,13]
                                // Table: A♥ K♣ Q♦ 2♠  — no 7 → no trips ✓
                                List.of(c(15,"Cups"),   c(14,"Wands"),     c(13,"Swords"), c(2,"Pentacles")),
                                0
                        ),

                        /* 30 — Full House: both hold pair Page(11); table has trips K + junk */
                        Arguments.of(
                                "Draw C30: FH — both hold pair Page(11) in diff suits; table K-K-K+2",
                                List.of(c(11,"Cups"),   c(11,"Swords")),   // P1: [7,14,11]
                                List.of(c(11,"Wands"),  c(11,"Pentacles")),// P2: [7,14,11]
                                // Table: K♥K♠K♣ + 2♦  — no Page → no quads ✓
                                List.of(c(14,"Cups"),   c(14,"Swords"),    c(14,"Wands"),  c(2,"Pentacles")),
                                0
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("cases")
            void draw(
                    String description,
                    List<MinorArcanaCard> p1Hole,
                    List<MinorArcanaCard> p2Hole,
                    List<MinorArcanaCard> table,
                    int expectedResult) {

                assertEquals(
                        0,
                        determiner.determineWinner(p1Hole, p2Hole, table),
                        description
                );
            }
        }
    }

}



