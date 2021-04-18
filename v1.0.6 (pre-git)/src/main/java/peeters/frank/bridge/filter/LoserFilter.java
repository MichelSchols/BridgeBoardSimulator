/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.filter;

import peeters.frank.bridge.deal.Hand;

/**
 *
 * @author frankpeeters
 */
public class LoserFilter implements HandFilter {

    private Range<Double> loserRange;

    public LoserFilter(Range<Double> loserRange) {
        this.loserRange = loserRange;
    }

    @Override
    public boolean accepts(Hand hand) {
        return loserRange.contains(hand.losers());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toString(){
        return loserRange.toString();
    }

}
