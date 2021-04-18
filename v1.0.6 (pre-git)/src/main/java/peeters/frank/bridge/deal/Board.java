/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.deal;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author frankpeeters
 */
public class Board {

    private Map<Direction, Hand> hands;
    private int invalidProbes;
    private int invalidProbesValidNorth;
    private String comment;

    /**
     *
     * @param cards
     */
    public Board(Map<Direction, Card[]> cards) {
        hands = new HashMap();
        for (Direction direction : Direction.values()) {
            hands.put(direction, new Hand(cards.get(direction)));
        }
        this.invalidProbes = 0;
        this.invalidProbesValidNorth = 0;
        this.comment = "";
    }

    public void setHands(Map<Direction, Card[]> cards) {
        hands = new HashMap();
        for (Direction direction : Direction.values()) {
            hands.put(direction, new Hand(cards.get(direction)));
        }
    }

    public int getInvalidProbes() {
        return invalidProbes;
    }

    public void incrUnacceptedProbes() {
        invalidProbes++;
    }

    public int getUnacceptedProbesValidNorth() {
        return invalidProbesValidNorth;
    }

    public void incrUnacceptedProbesValidNorth() {
        invalidProbesValidNorth++;
    }

    /**
     *
     * @param direction
     * @return
     */
    public Hand hand(Direction direction) {
        return hands.get(direction);
    }

    @Override
    public String toString() {
        return getBoardString() + "\n" + comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (!comment.trim().isEmpty()) {
            this.comment = comment;
        }
    }

    public String getBoardString() {
        String SPACES = "                     ";

        StringBuilder sb = new StringBuilder();
        sb.append(SPACES.substring(0, 20));
        sb.append(hands.get(Direction.NORTH).toString());
        sb.append("\n");
        sb.append(hands.get(Direction.WEST).toString());
        sb.append(SPACES.substring(0, 21));
        sb.append(hands.get(Direction.EAST).toString());
        sb.append("\n");
        sb.append(SPACES.substring(0, 20));
        sb.append(hands.get(Direction.SOUTH).toString());
        sb.append("\n");
        return sb.toString();
    }

}
