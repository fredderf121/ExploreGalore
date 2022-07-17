package com.fred.exploregalore.math.voxelsequences;

import com.fred.exploregalore.utils.Vec3Utils;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

/**
 * Doesn't use the fast integer-only algorithm.
 */
public class LinearCachedVoxelSequence extends ParametricFunctionCachedVoxelSequence {

    public static final int NUM_CONFIGURATION_POINTS = 2;

    private final Vec3 startPoint;
    private final Vec3 endPoint;

    public LinearCachedVoxelSequence(Vec3i startPoint, Vec3i endPoint) {
        this.startPoint = Vec3.atLowerCornerOf(startPoint);
        this.endPoint = Vec3.atLowerCornerOf(endPoint);
    }

    public static VoxelSequence configuredWith(Vec3i... configurationPoints) {
        if (configurationPoints.length != NUM_CONFIGURATION_POINTS) {
            throw new IllegalArgumentException("The path requires " + NUM_CONFIGURATION_POINTS + " configuration Vec3i, but " +
                    "was provided " + configurationPoints.length + ".");
        }
        return new LinearCachedVoxelSequence(configurationPoints[0], configurationPoints[1]);
    }

    @Override
    protected double get6ConnectedStepSize() {
        return 1 / Vec3Utils.sumCoordinates(Vec3Utils.absPointWise(endPoint.subtract(startPoint)));
    }

    @Override
    protected Vec3 f(double t) {
        // If s = startPoint, e = endPoint, f(t) = s + (e - s) * t
        return startPoint.add(endPoint.subtract(startPoint).scale(t));
    }

    @Override
    protected double getStartTime() {
        return 0;
    }

    @Override
    protected double getEndTime() {
        return 1;
    }
}
