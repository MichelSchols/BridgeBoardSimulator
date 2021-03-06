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
public interface HandFilter {
    /**
     *
     * @param hand
     * @return true if hand satisfies filter, else false
     */
    boolean accepts(Hand hand);

    boolean isEmpty();

}
