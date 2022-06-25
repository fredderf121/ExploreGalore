package com.fred.exploregalore.utils;

import java.util.Arrays;

public class ArrayUtils {
    public static long[] doubleToLong(double[] a) {
        return Arrays.stream(a).mapToLong(d -> (long) d).toArray();
    }
}
