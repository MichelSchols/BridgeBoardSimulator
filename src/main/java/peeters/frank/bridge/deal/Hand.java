/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.deal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;

/**
 *
 * @author frankpeeters
 */
public class Hand {

    private Map<Suit, List<Card>> sortedCards;
    private final Card[] cards;
    public static final String[] BALANCED_PATTERNS = {"4333", "4432", "5332", "5422"};
    public static final String[] PATTERNS = {
        "unknown", "balanced", "unbalanced",
        "4", "4333", "44", "4432", "4441",
        "5", "5332", "54", "5422", "5431", "5440", "55", "5521", "5530",
        "6", "63", "6322", "6331", "64", "6421", "6430", "65", "6511", "6520", "6610",
        "7", "7222", "73", "7321", "7330", "74", "7411", "7420", "7510", "7600",
        "8", "8221", "83", "8311", "8320", "8410", "8500",
        "9", "92", "9211", "9220", "9310", "9400"};

    private final Map<String, Double> losersSuit = new HashMap<>();

    /**
     *
     * @param cards
     */
    public Hand(Card[] cards) {
        this.cards = cards;
        sort();
        initLosers();
    }

    private void initLosers() {
        losersSuit.put("", 0.0);
        losersSuit.put("A", -0.5);
        losersSuit.put("K", 0.5);
        losersSuit.put("Q", 1.0);
        losersSuit.put("x", 1.0);
        losersSuit.put("AK", -0.5);
        losersSuit.put("AQ", 0.5);
        losersSuit.put("KQ", 1.0);
        losersSuit.put("Ax", 0.5);
        losersSuit.put("Kx", 1.0);
        losersSuit.put("Qx", 2.0);
        losersSuit.put("xx", 2.0);
        losersSuit.put("AKQ", 0.0);
        losersSuit.put("AKx", 0.5);
        losersSuit.put("AQx", 1.0);
        losersSuit.put("KQx", 1.5);
        losersSuit.put("Axx", 1.5);
        losersSuit.put("Kxx", 2.0);
        losersSuit.put("QJx", 2.5);
        losersSuit.put("QTx", 2.5);
        losersSuit.put("Qxx", 3.0);
        losersSuit.put("xxx", 3.0);
    }

    String headOfSuit(Suit suit) {
        StringBuilder sb = new StringBuilder();
        int length = sortedCards.get(suit).size();
        if (length > 3) {
            length = 3;
        }
        for (int i = 0; i < length; i++) {
            Card card = sortedCards.get(suit).get(i);
            if (card.isTopHonnor()) {
                sb.append(card.value());
            } else {
                sb.append("x");
            }
        }
        if (length == 3) {
            if (sortedCards.get(suit).get(0).intvalue() == Card.QUEEN) {
                if (sortedCards.get(suit).get(1).intvalue() == Card.JACK) {
                    return "QJx";
                } else if (sortedCards.get(suit).get(1).intvalue() == Card.TEN) {
                    return "QTx";
                }
            }
        }
        return sb.toString();
    }

    /**
     *
     * @return
     */
    public int hcp() {
        int hcp = 0;
        for (Card card : cards) {
            hcp += card.hcp();
        }
        return hcp;
    }

    /**
     *
     * @return
     */
    public double nt_hcp() {
        double hcp = 0;
        for (Card card : cards) {
            hcp += card.nt_hcp();
        }
        return hcp;
    }

    public int controls() {
        int controls = 0;
        for (Card card : cards) {
            int value = card.intvalue();
            if (value > 12) {
                controls += value - 12;
            }
        }
        return controls;
    }

    public double losers() {
        double losers = 0.0;
        for (Suit suit : Suit.values()) {
            String headOfSuit = headOfSuit(suit);
            losers += losersSuit.get(headOfSuit);
        }
        String pattern = pattern();
        if (pattern.equals("4441")) {
            return losers + 1;
        } else {
            return losers;
        }
    }

    private void sort() {
        Arrays.sort(cards);
        sortedCards = new HashMap();
        for (Suit suit : Suit.values()) {
            sortedCards.put(suit, new ArrayList<>());
        }

        for (Card card : cards) {
            sortedCards.get(card.suit()).add(card);
        }

        for (Suit suit : Suit.values()) {
            Collections.sort(sortedCards.get(suit), new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return -((Card) o1).compareTo((Card) o2);
                }
            });
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String separator = ", ";
        Suit suit = Suit.SPADES;
        {
            for (Card card : sortedCards.get(suit)) {
                sb.append(card.value());
            }
        }
        suit = Suit.HEARTS;
        {
            sb.append(separator);
            for (Card card : sortedCards.get(suit)) {
                sb.append(card.value());
            }
        }
        suit = Suit.DIAMONDS;
        {
            sb.append(separator);
            for (Card card : sortedCards.get(suit)) {
                sb.append(card.value());
            }
        }
        suit = Suit.CLUBS;
        {
            sb.append(separator);
            for (Card card : sortedCards.get(suit)) {
                sb.append(card.value());
            }
        }

        return sb.toString();
    }

    /**
     *
     * @param suit
     * @return
     */
    public int length(Suit suit) {
        return sortedCards.get(suit).size();
    }

    /**
     *
     * @return
     */
    public String distribution() {
        StringBuilder sb = new StringBuilder();
        // problem neglected: suit length of 10, 11, 12, 13
        for (Suit suit : Suit.values()) {
            sb.append(length(suit));
        }
        return sb.toString();
    }

    /**
     *
     * @return
     */
    public String pattern() {
        List<Integer> lengths = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            lengths.add(-length(suit));
        }
        Collections.sort(lengths);
        StringBuilder sb = new StringBuilder();
        for (Integer i : lengths) {
            sb.append(-i);
        }
        return sb.toString();
    }

    /**
     *
     * @return
     */
    public boolean isBalanced() {
        String pattern = pattern();
        for (String balancedPattern : BALANCED_PATTERNS) {
            if (pattern.equals(balancedPattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param lower
     * @param upper
     * @param fiveCardMajorAllowed
     * @return
     */
    public boolean isNT(int lower, int upper, boolean fiveCardMajorAllowed) {
        int hcp = hcp();
        if (hcp < lower) {
            return false;
        }
        if (hcp > upper) {
            return false;
        }

        if (!fiveCardMajorAllowed) {
            if (length(Suit.HEARTS) > 4 || length(Suit.SPADES) > 4) {
                return false;
            }
        }

        boolean isNT = false;
        String pattern = pattern();
        for (String balancedPattern : BALANCED_PATTERNS) {
            if (balancedPattern.equals(pattern)) {
                isNT = true;
            }
        }

        if (isNT) {
            if (pattern.equals("5332")) {
                return hcp < upper;
            } else if (pattern.equals("4333")) {
                return true;
            }
            double nt_hcp = nt_hcp();
            return nt_hcp >= lower + 0.5 && nt_hcp < upper + 0.5;
        } else {
            return false;
        }
    }

    public String suit(Suit suit) {
        StringBuilder sb = new StringBuilder();
        for (Card card : sortedCards.get(suit)) {
            sb.append(card.value());
        }
        return sb.toString();
    }

}
