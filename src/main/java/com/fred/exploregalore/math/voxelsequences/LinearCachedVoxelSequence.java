package com.fred.exploregalore.math.voxelsequences;

import com.fred.exploregalore.utils.Vec3Utils;
import com.fred.exploregalore.utils.Vec3iUtils;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Doesn't use the fast integer-only algorithm.
 */
@Log4j2
public class LinearCachedVoxelSequence extends ParametricFunctionCachedVoxelSequence {
    private static final double ONE_WITH_ERROR = 1.0 - 0.001;

    public static final int NUM_CONFIGURATION_POINTS = 2;

    private final Vec3 startPoint;
    private final Vec3 endPoint;

    private final int width = 3;

    /**
     * A vector normal to the curve but only in the x and z direction.
     */
    private final Vec3 unitNormalXZ;

    public LinearCachedVoxelSequence(Vec3i startPoint, Vec3i endPoint) {
        this.startPoint = Vec3.atLowerCornerOf(startPoint);
        this.endPoint = Vec3.atLowerCornerOf(endPoint);
        this.unitNormalXZ = calculateUnitNormalXZ(startPoint, endPoint);
    }

    @Override
    protected List<Vec3i> voxelizeCurve() {
        final double startTime = getStartTime();
        final double endTime = getEndTime();
        // Taking the direction of t into consideration
        final double stepSize = (endTime - startTime) > 0
                ? get6ConnectedStepSize() : -get6ConnectedStepSize();

        val voxelSequence = new ArrayList<Vec3i>();

        for (double u = -width; u <= width; u++) {
            // Note that we keep the double coordinates and integer (Vec3i) coordinates
            // separate (as in we don't calculate the nextVoxel by rounding the Vec3 f(t) output)
            // to avoid casting.
            val startingPosDouble = f(startTime, u);
            var nextVoxel = Vec3iUtils.fromVec3Rounded(startingPosDouble);
            voxelSequence.add(nextVoxel);
            var previousPosition = startingPosDouble;

            var error = Vec3.ZERO;
            var absError = error;

            // I guesstimate the error tolerance for the double
            for (double t = startTime + stepSize; t <= endTime + stepSize / 100; t += stepSize) {
                val position = f(t, u);
                error = error.add(position.subtract(previousPosition));
                absError = Vec3Utils.absPointWise(error);
                // If any of the coordinates have accumulated enough error to increment in their direction
                while (absError.x >= ONE_WITH_ERROR || absError.y >= ONE_WITH_ERROR || absError.z >= ONE_WITH_ERROR) {
                    // These if statements dictate the order of which we increment coordinates if
                    // multiple coordinates have error >= 1 (current priority is x, then y, then z)
                    if (absError.x >= absError.y && absError.x >= absError.z) {
                        // -1 or 1, in the same direction of the error.
                        double increment = Math.signum(error.x);
                        nextVoxel = nextVoxel.offset(increment, 0, 0);
                        // Reducing the error in that direction
                        error = error.subtract(increment, 0, 0);
                    } else if (absError.y >= absError.z) {
                        double increment = Math.signum(error.y);
                        nextVoxel = nextVoxel.offset(0, increment, 0);
                        error = error.subtract(0, increment, 0);
                    } else {
                        double increment = Math.signum(error.z);
                        nextVoxel = nextVoxel.offset(0, 0, increment);
                        error = error.subtract(0, 0, increment);
                    }
                    voxelSequence.add(nextVoxel);
                    absError = Vec3Utils.absPointWise(error);
                }

                previousPosition = position;

            }
        }
        log.debug("Total iterations in for-loop: {}. Total voxels generated: {}", () -> 1 / get6ConnectedStepSize(), voxelSequence::size);
        return voxelSequence;
    }

    private static Vec3 calculateUnitNormalXZ(Vec3i startPoint, Vec3i endPoint) {
        val difference = Vec3Utils.fromVec3i(endPoint.subtract(startPoint));
        val flattenedDifference = new Vec3(difference.x, 0, difference.z);
        val flattenedDifferenceNormalized = flattenedDifference.normalize();

        return new Vec3(-flattenedDifferenceNormalized.z, 0, flattenedDifferenceNormalized.x);

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

    protected Vec3 f(double t, double u) {
        // If S = startPoint, E = endPoint, and N = unit normal
        // f(t) = S + (E - S) * t + N * u
        return startPoint.add(endPoint.subtract(startPoint).scale(t)).add(unitNormalXZ.scale(u));
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
