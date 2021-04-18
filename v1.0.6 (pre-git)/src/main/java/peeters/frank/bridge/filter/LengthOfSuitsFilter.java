/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.filter;

import peeters.frank.bridge.deal.Hand;
import peeters.frank.bridge.deal.Suit;
import java.util.Map;

/**
 *
 * @author frankpeeters
 */
public class LengthOfSuitsFilter implements HandFilter {

    private final Map<Suit, Range<Integer>> suitRanges;

    /**
     *
     * @param sr
     */
    public LengthOfSuitsFilter(Map<Suit, Range<Integer>> sr) {
        suitRanges = sr;
    }

    /**
     *
     * @param hand
     * @return
     */
    @Override
    public boolean accepts(Hand hand) {
        String distribution = hand.distribution();
        int index = 0;
        for (Suit suit : Suit.values()) {
            int length = Integer.parseInt(distribution.substring(index, index + 1));
            index++;
            Range range = suitRanges.get(suit);
            if (range != null) {
                if (!range.contains(length)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("distributional constraints: \n");
        for (Suit suit : suitRanges.keySet()) {
            sb.append(suit.toString()).append(": ").
                append(suitRanges.get(suit).toString()).
                append(" ");
        }
        return sb.toString();
    }

}
