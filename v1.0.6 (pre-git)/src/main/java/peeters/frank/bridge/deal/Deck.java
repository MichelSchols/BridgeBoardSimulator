/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.deal;

import peeters.frank.bridge.filter.BoardFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author frankpeeters
 */
public class Deck {

    public static final int MAX_INVALID_PROBES = 200000;

    private List<Card> cards;
    private final Random random;
    private Map<Direction, List<Card>> handsFixed;

    /**
     *
     */
    public Deck() {
        unfix();
        random = new Random();
        initialShuffle();
    }

    public boolean isFixed() {
        return !handsFixed.get(Direction.NORTH).isEmpty()
            || !handsFixed.get(Direction.SOUTH).isEmpty()
            || !handsFixed.get(Direction.EAST).isEmpty()
            || !handsFixed.get(Direction.WEST).isEmpty();
    }

    public boolean isFixed(Direction direction) {
        return !handsFixed.get(direction).isEmpty();
    }

    public String fixedHand(Direction direction) {
        StringBuilder sb = new StringBuilder();

        List<Card> cards = handsFixed.get(direction);
        Collections.sort(cards);
        int threshold = 39;
        for (int i=cards.size()-1; i>=0; i--) {
            Card card = cards.get(i);
            while (card.getNr() < threshold) {
                sb.append(", ");
                threshold -= 13;
            }
            sb.append(card.value() + "");
        }
        while (threshold > 0) {
            sb.append(", ");
            threshold -= 13;
        }

        return sb.toString();
    }

    public void unfix() {
        handsFixed = new HashMap();
        for (Direction direction : Direction.values()) {
            handsFixed.put(direction, new ArrayList<>());
        }
        cards = new ArrayList<>();
        for (int nr = 0; nr < 52; nr++) {
            cards.add(new Card(nr));
        }
    }

    public void fix(String fixedHandString, Direction direction) throws HandException {
        cards.addAll(handsFixed.get(direction));
        handsFixed.put(direction, new ArrayList<>());
        List<Card> fixedHand = convert(fixedHandString);
        checkIntersection(fixedHand, direction);
        cards.removeAll(fixedHand);
        handsFixed.put(direction, fixedHand);
        initialShuffle();
    }

    /**
     *
     */
    private void shuffle() {
        int to;
        int cap = cards.size();
        for (int from = 0; from < cap; from++) {
            to = random.nextInt(cap);
            swap(from, to);
        }
    }

    private void initialShuffle() {
        int from, to;
        int cap = cards.size();
        for (int i = 0; i < 200; i++) {
            from = random.nextInt(cap);
            to = random.nextInt(cap);
            swap(from, to);
        }
    }

    private void swap(int from, int to) {
        Card c = cards.get(from);
        cards.set(from, cards.get(to));
        cards.set(to, c);
    }

    /**
     *
     * @return
     */
    public Map<Direction, Card[]> getHands() {
        shuffle();
        Direction[] directions = Direction.values();
        int cardsIndex = 0;
        Map<Direction, Card[]> handArrays = new HashMap<>();
        for (Direction direction : directions) {
            List<Card> handFixed = handsFixed.get(direction);
            Card[] handCards = new Card[13];
            handArrays.put(direction, handCards);
            for (int i = 0; i < handFixed.size(); i++) {
                handCards[i] = handFixed.get(i);
            }
            for (int i = handFixed.size(); i < 13; i++) {
                handCards[i] = cards.get(cardsIndex);
                cardsIndex++;
            }
        }

        return handArrays;
    }

    public Map<Direction, List<Card>> getHandsFixed(){
        Direction[] directions = Direction.values();
        int cardsIndex = 0;
        Map<Direction, List<Card>> handLists = new HashMap<>();
        for (Direction direction : directions) {
            List<Card> handFixed = new ArrayList<Card>(handsFixed.get(direction));
            handLists.put(direction, handFixed);
        }

        return handLists;
    }

    public Board getBoard(BoardFilter filter) {
        shuffle();
        Board board = new Board(getHands());
        while (!filter.accepts(board)) {
            shuffle();
            board.setHands(getHands());
            if (board.getInvalidProbes() > MAX_INVALID_PROBES) {
                return null;
            }
        }
        return board;
    }

    public SampleOfBoards getBoards(BoardFilter filter, int sampleSize) {
        List<Board> boards = new ArrayList<>();
        for (int i = 0; i < sampleSize; i++) {
            Board board = getBoard(filter);
            if (board == null) {
                return new SampleOfBoards(boards);
            }
            boards.add(board);
        }
        return new SampleOfBoards(boards);
    }

    private List<Card> convert(String fixedHandString) throws HandException {
        List<Card> cards = new ArrayList<>();
        String[] suits = fixedHandString.split(",");
        if (suits.length == 0) {
            return cards;
        }

        if (suits.length != 4) {
            throw new HandException("Please use three comma's to delimit the suits");
        }

        for (int suit = 0; suit < 4; suit++) {
            suits[suit] = suits[suit].trim();
            for (int i = 0; i < suits[suit].length(); i++) {
                char symbol = suits[suit].charAt(i);
                int index = Card.VALUES.indexOf(symbol);
                if (index == -1) {
                    throw new HandException("Only these symbols '"
                        + Card.VALUES + "' are allowed; "+
                        " '" + symbol +
                        "' does not belong to it.");
                }
                cards.add(new Card(13 * (3 - suit) + index));
            }
        }
        if (cards.size() > 13) {
            throw new HandException("This hand contains more than 13 cards");
        }
        return cards;
    }

    private void checkIntersection(List<Card> fixedHand, Direction direction) throws HandException {

        for (Direction d : Direction.values()) {
            if (!d.equals(direction)) {
                List<Card> otherHand = handsFixed.get(d);
                for (Card c : fixedHand) {
                    if (otherHand.contains(c)) {
                        throw new HandException(d + " already contains " + c.toString());
                    }
                }

            }
        }
    }
}
