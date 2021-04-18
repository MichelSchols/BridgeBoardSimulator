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
public class NoFilter implements HandFilter {

    @Override
    public boolean accepts(Hand hand) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }


    @Override
    public String toString(){

        return "";
    }

}
