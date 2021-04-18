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
public class PatternFilter implements HandFilter {

    private String requiredPattern;
    private int lengthRequiredPattern;

    public PatternFilter(String pattern) {
        this.requiredPattern = pattern;
        this.lengthRequiredPattern = pattern.length();
    }

    @Override
    public boolean accepts(Hand hand) {
        String actualPattern = hand.pattern().substring(0, lengthRequiredPattern);
        return actualPattern.equals(requiredPattern);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toString() {
        return requiredPattern;
    }

}
