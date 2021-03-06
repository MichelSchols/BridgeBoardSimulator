/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.gui;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author frankpeeters
 */
public class RangeSelectConstraint {

    private List<RangeSelect> rangeSelects;
    private int max;

    public RangeSelectConstraint(int max) {
        this.max = max;
        rangeSelects = new ArrayList<>();
    }

    void addRangeSelect(RangeSelect rs) {
        rangeSelects.add(rs);
    }

    public void adjust(RangeSelect changed) {
        int sum = 0;
        int indexChanged = 0;
        for (int i = 0; i < 4; i++) {
            RangeSelect rs = rangeSelects.get(i);
            sum += rs.getLowerBound();
            if (rs.equals(changed)) {
                indexChanged = i;
            }
        }

        if (sum > max) {
            int indexToChange = (indexChanged + 1) % rangeSelects.size();
            RangeSelect toChange = rangeSelects.get(indexToChange);
            while (toChange.getUpperBound() == toChange.getMinRange()) {
                indexToChange = (indexToChange + 1) % rangeSelects.size();
                toChange = rangeSelects.get(indexToChange);
            }

            int change = sum - max;

            while (change != 0) {
                int newValue = toChange.getLowerBound() - change;
                if (newValue < toChange.getMinRange()) {
                    change -= toChange.getLowerBound() - toChange.getMinRange();
                    toChange.setUpperBound(toChange.getMinRange());
                    indexToChange = (indexToChange + 1) % rangeSelects.size();
                    toChange = rangeSelects.get(indexToChange);
                } else {
                    change = 0;
                    toChange.setUpperBound(newValue);
                }
            }
        }
    }

}
