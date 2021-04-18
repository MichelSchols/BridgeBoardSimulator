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
public class BalancedFilter implements HandFilter {

    private boolean balanced;

    public BalancedFilter(boolean balanced) {
        this.balanced = balanced;
    }

    @Override
    public boolean accepts(Hand hand) {
        if (balanced) {
            return hand.isBalanced();
        } else {
            return !hand.isBalanced();
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toString() {
        if (balanced) {
            return "balanced";
        } else {
            return "unbalanced";
        }
    }

}
