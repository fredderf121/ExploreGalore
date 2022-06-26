package com.fred.exploregalore.utils;

import net.minecraft.core.Vec3i;

import java.util.Arrays;

public class Vec3iUtils {

    public static int[] toIntArray(Vec3i v) {
        return new int[]{v.getX(), v.getY(), v.getZ()};
    }

    public static double[] toDoubleArray(Vec3i v) {
        return new double[]{v.getX(), v.getY(), v.getZ()};
    }

    public static boolean allEqual(Vec3i... vectors) {
        return Arrays.stream(vectors).distinct().count() == 1;
    }
}
