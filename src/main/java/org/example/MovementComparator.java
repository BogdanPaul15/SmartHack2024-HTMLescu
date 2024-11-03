package org.example;

import java.util.Comparator;

public class MovementComparator implements Comparator<Movement> {
    public int compare(final Movement m1, final Movement m2) {
        return m1.getArrivalDay().compareTo(m2.getArrivalDay());
    }
}
