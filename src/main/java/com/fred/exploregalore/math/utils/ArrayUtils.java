package com.fred.exploregalore.math.utils;

import java.util.Arrays;

public class ArrayUtils {
    public static int[] doubleToInt(double[] a) {
        return Arrays.stream(a).mapToInt(d -> (int) d).toArray();
    }
}
