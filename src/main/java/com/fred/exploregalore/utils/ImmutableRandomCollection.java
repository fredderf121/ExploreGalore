package com.fred.exploregalore.utils;


import it.unimi.dsi.fastutil.Pair;
import org.apache.commons.math3.distribution.EnumeratedDistribution;

import java.util.*;
import java.util.function.ToDoubleFunction;

/**
 * A collection of objects of type {@code E} with weightings.
 * <p>
 * Taken from <a href="https://stackoverflow.com/a/6409791/8402160">this stackoverflow page</a>.
 *
 * <p>
 * Ex: If item 1 has a weighting of 1 and item 2 has a weighting of 2, then the total weighting is 1 + 2 = 3.
 */
public class ImmutableRandomCollection<E> {
    private final NavigableMap<Double, E> map;
    private final Random random;

    private final double weightsTotal;

    public ImmutableRandomCollection(Pair<Double, E>... items) {
        this.map = new TreeMap<>();
        this.random = new Random();

        // Adding all items with a greater-than-zero weighting to the map and then
        // summing up their weights.
        weightsTotal = Arrays.stream(items)
                .filter(item -> item.key() >= 0) // A weighting less than 0 means it will never be 'drawn'
                .map(item -> {
                    this.map.put(item.key(), item.value());
                    return item.key();
                })
                .mapToDouble(d -> d)
                .sum();
    }

    public E getNext() {
        return map.higherEntry(random.nextDouble() * weightsTotal).getValue();
    }

}
