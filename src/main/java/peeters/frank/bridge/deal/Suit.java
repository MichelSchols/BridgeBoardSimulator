/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.deal;

import javafx.scene.paint.*;

/**
 *
 * @author frankpeeters
 */
public enum Suit {

     /**
     *
     */
    SPADES('\u2664', Color.DARKBLUE),//'\u2660'
     /**
     *
     */
    HEARTS('\u2661', Color.RED),//'\u2665'
    /**
     *
     */
    DIAMONDS('\u2662', Color.DARKORANGE),//'\u2666'
    /**
     *
     */
    CLUBS('\u2667', Color.DARKGREEN);//'\u2663'



    private char symbol;
    private Color color;

    Suit(char symbol, Color color) {
        this.symbol = symbol;
        this.color = color;
    }

    public char getSymbol(){
        return symbol;
    }

    public Color getColor() {
        return color;
    }

}
