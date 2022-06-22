package com.fred.exploregalore.math.utils;

import java.util.Arrays;

public class ArrayUtils {
    public static long[] doubleToLong(double[] a) {
        return Arrays.stream(a).mapToLong(d -> (long) d).toArray();
    }
}
