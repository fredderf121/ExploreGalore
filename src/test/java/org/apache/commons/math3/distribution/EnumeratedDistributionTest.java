package org.apache.commons.math3.distribution;

import lombok.val;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EnumeratedDistributionTest {

    private enum TestEnum {
        APPLE,
        PEAR,
        PEACH,
        PLUM
    }

    @Test
    public void distributionTests() {
        val testDistribution = new EnumeratedDistribution<TestEnum>(List.of(
                Pair.create(TestEnum.APPLE, 1d),
                Pair.create(TestEnum.PEACH, 1d),
                Pair.create(TestEnum.PEAR, 10d)
        ));

        val frequencyMap = IntStream.range(0, 1000)
                .mapToObj(i -> testDistribution.sample())
                .collect(Collectors.groupingBy(
                        Function.identity(), // This is what you are grouping by (the comparison); the key (here, it is the enum itself)
                        Collectors.counting()) // For all objects that are equal, we reduce this list of objects into the value (here, the list length)
                );
        System.out.println(frequencyMap);
    }
}
