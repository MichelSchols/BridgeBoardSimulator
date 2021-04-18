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
public class HCPFilter implements HandFilter {

    private Range<Double> hcpRange;
    private boolean withMiddleCards;

    public HCPFilter(Range<Double> hcpRange, boolean withMiddleCards) {
        this.hcpRange = hcpRange;
        this.withMiddleCards = withMiddleCards;
    }

    @Override
    public boolean accepts(Hand hand) {
        if (withMiddleCards) {
            return hcpRange.contains(hand.nt_hcp());
        } else {
            return hcpRange.contains(hand.hcp() * 1.0);
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toString(){
        return hcpRange.toString();
    }

}
