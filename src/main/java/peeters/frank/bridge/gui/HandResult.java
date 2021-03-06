/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.gui;

import peeters.frank.bridge.deal.Direction;

/**
 *
 * @author frankpeeters
 */
class HandResult {
    Direction direction;
    String cards;

    HandResult(Direction d, String c){
        direction = d;
        cards = c;
    }
}
