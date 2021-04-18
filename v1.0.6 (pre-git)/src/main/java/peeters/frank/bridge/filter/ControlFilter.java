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
public class ControlFilter implements HandFilter{

    private Range<Integer> controlRange;
    public ControlFilter(Range<Integer> controlRange) {
        this.controlRange = controlRange;
    }

    @Override
    public boolean accepts(Hand hand) {
        return controlRange.contains(hand.controls());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toString(){
        return controlRange.toString();
    }
}
