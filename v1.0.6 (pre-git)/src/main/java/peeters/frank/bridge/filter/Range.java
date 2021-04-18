/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.filter;

/**
 *
 * @author frankpeeters
 * @param <T>
 */
public class Range<T extends Comparable> {

    private T min;
    private T max;

    /**
     *
     * @param min
     * @param max
     */
    public Range(T min, T max) {
        this.min = min;
        this.max = max;
    }

    /**
     *
     * @return
     */
    public T getMin() {
        return min;
    }

    /**
     *
     * @return
     */
    public T getMax() {
        return max;
    }

    /**
     *
     * @param t
     * @return
     */
    public boolean contains(T t) {
        return min.compareTo(t) <= 0 && t.compareTo(max) <= 0;
    }

    @Override
    public String toString() {
        return min + ".." + max;

    }

}
