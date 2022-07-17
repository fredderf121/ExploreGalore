package com.fred.exploregalore.math.voxelsequences;

import com.fred.exploregalore.utils.Vec3Utils;
import com.mojang.math.Vector3d;
import lombok.val;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Formulas taken from <a href="https://en.wikipedia.org/wiki/B%C3%A9zier_curve#Quadratic_B%C3%A9zier_curves">wikipedia</a>
 */
public class QuadraticBezierVoxelSequence implements VoxelSequence {

    public static final int NUM_CONFIGURATION_POINTS = 3;

    private final Vec3i P0, P1, P2;

    private final Vec3 P0d, P1d, P2d;

    private final List<Vec3i> voxelSequence;

    public QuadraticBezierVoxelSequence(Vec3i P0, Vec3i P1, Vec3i P2) {
        this.P0 = P0;
        this.P1 = P1;
        this.P2 = P2;
        this.P0d = Vec3.atLowerCornerOf(this.P0);
        this.P1d = Vec3.atLowerCornerOf(this.P1);
        this.P2d = Vec3.atLowerCornerOf(this.P2);

        voxelSequence = voxelizeCurve();
    }

    private List<Vec3i> voxelizeCurve() {
        final double ONE_WITH_ERROR = 1.0 - 0.001;

        /*
         * To calculate the step size, we sum the absolute maxima of the first derivative
         * in each direction. For a quadratic bezier, the first derivative is linear, so the maxima occurs at
         * either extremities (t = 0 or 1).
         * This step size ensures that the next value of the function will increase at most by one unit.
         */
        val derivativeAt0 = fDerivative(0);
        val derivativeAt1 = fDerivative(1);
        val maxDerivativePointwise = Vec3Utils.maxCoordinateWise(Vec3Utils.absPointWise(derivativeAt0), Vec3Utils.absPointWise(derivativeAt1));
        final double stepSize = 1 / Vec3Utils.sumCoordinates(maxDerivativePointwise);

        Vec3i nextVoxel = this.P0;
        val voxelSequence = new ArrayList<Vec3i>();
        voxelSequence.add(nextVoxel);
        var error = Vec3.ZERO;
        var absError = error;
        var previousPosition = this.P0d;
        // I guesstimate the error tolerance for the double
        for (double t = stepSize; t <= 1.00 + stepSize / 100; t += stepSize) {
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

    private Vec3 f(double t) {
        return P0d.scale((1 - t) * (1 - t)).add(P1d.scale(2 * (1 - t) * t)).add(P2d.scale(t * t));
    }

    private Vec3 fDerivative(double t) {
        return P1d.subtract(P0d).scale(2 * (1 - t)).add(P2d.subtract(P1d).scale(2 * t));
    }

    public static VoxelSequence configuredWith(Vec3i... configurationPoints) {
        if (configurationPoints.length != NUM_CONFIGURATION_POINTS) {
            throw new IllegalArgumentException("The path requires " + NUM_CONFIGURATION_POINTS + " configuration Vec3i, but " +
                    "was provided " + configurationPoints.length + ".");
        }
        return new QuadraticBezierVoxelSequence(configurationPoints[0], configurationPoints[1], configurationPoints[2]);
    }

    @NotNull
    @Override
    public Iterator<Vec3i> iterator() {
        return this.voxelSequence.iterator();
    }
}
