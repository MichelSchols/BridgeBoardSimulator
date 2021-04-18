/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.filter;

import peeters.frank.bridge.deal.Hand;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author frankpeeters
 */
public class GeneralHandFilter implements HandFilter {

    private List<HandFilter> filters;

    public GeneralHandFilter() {
        this.filters = new ArrayList<>();
    }

    public GeneralHandFilter(List<HandFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean accepts(Hand hand) {
        for (HandFilter filter : filters) {
            if (!filter.accepts(hand)) {
                return false;
            }
        }
        return true;
    }

    public void addFilter(HandFilter filter) {
        filters.add(filter);
    }

    @Override
    public boolean isEmpty() {
        return filters.isEmpty() || filters.get(0).isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (HandFilter filter : filters) {
            sb.append(filter.toString()).append("\n");
        }
        return sb.toString();
    }

}
