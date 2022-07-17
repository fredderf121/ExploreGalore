package com.fred.exploregalore.utils;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

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

    public static Vec3i fromVec3Rounded(Vec3 v) {
        return new Vec3i(Math.round(v.x), Math.round(v.y), Math.round(v.z));
    }
}
