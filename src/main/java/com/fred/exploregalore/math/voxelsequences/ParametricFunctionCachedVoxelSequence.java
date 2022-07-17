package com.fred.exploregalore.math.voxelsequences;

import com.fred.exploregalore.utils.Vec3Utils;
import com.fred.exploregalore.utils.Vec3iUtils;
import com.google.common.base.Suppliers;
import lombok.val;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Precomputed in the sense that the voxel sequence is computed once and saved for future
 * {@link VoxelSequenceIterator#next()} calls.
 */
public abstract class ParametricFunctionCachedVoxelSequence implements VoxelSequence {

    private static final double ONE_WITH_ERROR = 1.0 - 0.001;

    /**
     * This should only be retrieved once the methods and any needed variables to compute
     * f(t) are initialized.
     */
    private final Supplier<List<Vec3i>> voxelSequence = Suppliers.memoize(this::voxelizeCurve);

    /**
     * @return An absolute value of independent parameter {@code t} such that the next value of the function will
     * increase at most by one unit in the x, y, or z direction.
     */
    protected abstract double get6ConnectedStepSize();

    /**
     * @param t the independent 'time' variable
     * @return f(t); the parametric function evaluated at time {@code t}.
     * @implNote No bounds checking are guaranteed in the implementation.
     */
    protected abstract Vec3 f(double t);


    protected abstract double getStartTime();

    protected abstract double getEndTime();

    public Iterator<Vec3i> iterator() {
        return voxelSequence.get().iterator();
    }

    private List<Vec3i> voxelizeCurve() {
        final double startTime = getStartTime();
        final double endTime = getEndTime();
        // Taking the direction of t into consideration
        final double stepSize = (endTime - startTime) > 0
                ? get6ConnectedStepSize() : -get6ConnectedStepSize();

        val voxelSequence = new ArrayList<Vec3i>();

        // Note that we keep the double coordinates and integer (Vec3i) coordinates
        // separate (as in we don't calculate the nextVoxel by rounding the Vec3 f(t) output)
        // to avoid casting.
        val startingPosDouble = f(startTime);
        var nextVoxel = Vec3iUtils.fromVec3Rounded(startingPosDouble);
        voxelSequence.add(nextVoxel);
        var previousPosition = startingPosDouble;

        var error = Vec3.ZERO;
        var absError = error;

        // I guesstimate the error tolerance for the double
        for (double t = startTime + stepSize; t <= endTime + stepSize / 100; t += stepSize) {
            val position = f(t);
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
        return voxelSequence;
    }


}
