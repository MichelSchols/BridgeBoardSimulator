/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.filter;

import peeters.frank.bridge.deal.Direction;
import peeters.frank.bridge.deal.Board;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author frankpeeters
 */
public class BoardFilter {

    private final Map<Direction, GeneralHandFilter> handfilters;

    public BoardFilter() {
        handfilters = new HashMap<>();
        for (Direction dir : Direction.values()) {
            handfilters.put(dir, new GeneralHandFilter());
        }
    }

    public BoardFilter(Map<Direction, GeneralHandFilter> handfilters) {
        this.handfilters = handfilters;
    }

    public boolean accepts(Board board) {
        for (Direction direction : Direction.values()) {
            if (direction == Direction.NORTH) {
                HandFilter handfilter = handfilters.get(direction);
                if (handfilter != null && !handfilter.accepts(board.hand(direction))) {
                    board.incrUnacceptedProbes();
                    return false;
                } else {

                }
            } else {
                HandFilter handfilter = handfilters.get(direction);
                if (handfilter != null && !handfilter.accepts(board.hand(direction))) {
                    board.incrUnacceptedProbes();
                    board.incrUnacceptedProbesValidNorth();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Direction direction : handfilters.keySet()) {
            sb.append(direction.toString()).append(": ").
                append(handfilters.get(direction).toString()).append("\n");
        }
        return sb.toString();
    }

}
