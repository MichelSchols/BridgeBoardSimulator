/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.deal;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author frankpeeters
 */
public class Propabilities {

    public double total;
    public double south;

    public String toString() {
        return "TOTAL: " + round(total, 4) + " " +
            Direction.NORTH + ": " + round(total / south, 4)
            + " " + Direction.SOUTH + ": " + round(south, 4);
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
