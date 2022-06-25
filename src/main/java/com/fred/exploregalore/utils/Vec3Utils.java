package com.fred.exploregalore.utils;

import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.math.NumberUtils;

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



}

