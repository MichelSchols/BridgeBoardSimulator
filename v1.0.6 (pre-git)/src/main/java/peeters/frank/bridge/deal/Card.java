/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.deal;

/**
 *
 * @author frankpeeters
 */
public class Card implements Comparable<Card> {

    /**
     *
     */
    public static final String VALUES = "23456789TJQKA";

    /**
     *
     */
    public static final int ACE = 14;

    /**
     *
     */
    public static final int KING = 13;

    /**
     *
     */
    public static final int QUEEN = 12;

    /**
     *
     */
    public static final int JACK = 11;

    /**
     *
     */
    public static final int TEN = 10;

    private final int nr;

    /**
     *
     * @param nr
     */
    public Card(int nr) {
        this.nr = nr;
    }

    /**
     *
     * @return
     */
    public char value() {
        return VALUES.charAt(nr % 13);
    }

    /**
     *
     * @return
     */
    public int intvalue() {
        return (nr % 13) + 2;
    }

    /**
     *
     * @return
     */
    public Suit suit() {
        switch (nr / 13) {
            case 0:
                return Suit.CLUBS;
            case 1:
                return Suit.DIAMONDS;
            case 2:
                return Suit.HEARTS;
            case 3:
                return Suit.SPADES;
            default:
                return null;
        }
    }

    /**
     *
     * @return
     */
    public int hcp() {
        int value = nr % 13;
        if (value > 8) {
            return value - 8;
        }
        return 0;
    }

    /**
     *
     * @return
     */
    public double nt_hcp() {
        int value = nr % 13;
        if (value > 8) {
            return value - 8;
        } else {
            if (value == 8) {
                return 0.5;
            }
            if (value == 7) {
                return 0.3;
            }
            if (value == 6) {
                return 0.1;
            }
        }
        return 0;
    }

    @Override
    public int compareTo(Card o) {
        if (nr == o.nr) {
            return 0;
        }
        if (nr < o.nr) {
            return -1;
        }
        return 1;

    }

    public boolean isTopHonnor() {
        return intvalue() >= Card.QUEEN;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Card) {
            Card card = (Card) obj;
            return card.nr == nr;
        }
        return false;
    }

    public String toString() {
        return "" + suit().getSymbol() + value();
    }

    public int getNr() {
        return nr;
    }
}
