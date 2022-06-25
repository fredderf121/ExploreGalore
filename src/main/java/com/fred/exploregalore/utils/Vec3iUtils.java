package com.fred.exploregalore.utils;

import net.minecraft.core.Vec3i;

public class Vec3iUtils {

    public static int[] toIntArray(Vec3i v) {
        return new int[]{v.getX(), v.getY(), v.getZ()};
    }

    public static double[] toDoubleArray(Vec3i v) {
        return new double[]{v.getX(), v.getY(), v.getZ()};
    }
}
