package com.fred.exploregalore.utils;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;

public final class Vec3Utils {
    public static Vec3 absPointWise(Vec3 src) {
        return new Vec3(Math.abs(src.x), Math.abs(src.y), Math.abs(src.z));
    }

    public static double maxCoordinateVal(Vec3 v) {
        return NumberUtils.max(v.x, v.y, v.z);
    }

    public static double sumCoordinates(Vec3 v) {
        return v.x + v.y + v.z;
    }

    public static Vec3 maxCoordinateWise(Vec3... vec3s) {
        return Arrays.stream(vec3s)
                .reduce(Vec3.ZERO, (v1, v2) -> new Vec3(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y), Math.max(v1.z, v2.z)));
    }

    public static boolean anyGreaterEqThan(Vec3 v, double value) {
        return (v.x >= value) || (v.y >= value) || (v.z >= value);
    }

    public static Vec3 fromVec3i(Vec3i v) {
        return Vec3.atLowerCornerOf(v);
    }


}

