package com.fred.exploregalore.math.voxelsequences;

import com.fred.exploregalore.utils.Vec3Utils;
import com.fred.exploregalore.utils.Vec3iUtils;
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
public final class QuadraticBezierVoxelSequence extends ParametricFunctionCachedVoxelSequence {

    public static final int NUM_CONFIGURATION_POINTS = 3;

    private final Vec3 P0d, P1d, P2d;

    public QuadraticBezierVoxelSequence(Vec3i P0, Vec3i P1, Vec3i P2) {
        this.P0d = Vec3.atLowerCornerOf(P0);
        this.P1d = Vec3.atLowerCornerOf(P1);
        this.P2d = Vec3.atLowerCornerOf(P2);
    }


    @Override
    protected double get6ConnectedStepSize() {
        /*
         * To calculate the step size, we sum the absolute maxima of the first derivative
         * in each direction. For a quadratic bezier, the first derivative is linear, so the maxima occurs at
         * either extremities (t = 0 or 1).
         * This step size ensures that the next value of the function will increase at most by one unit.
         */
        val derivativeAt0 = fDerivative(0);
        val derivativeAt1 = fDerivative(1);
        val maxDerivativePointwise = Vec3Utils.maxCoordinateWise(Vec3Utils.absPointWise(derivativeAt0), Vec3Utils.absPointWise(derivativeAt1));
        return 1 / Vec3Utils.sumCoordinates(maxDerivativePointwise);
    }

    @Override
    protected Vec3 f(double t) {
        return P0d.scale((1 - t) * (1 - t)).add(P1d.scale(2 * (1 - t) * t)).add(P2d.scale(t * t));
    }

    @Override
    protected double getStartTime() {
        return 0;
    }

    @Override
    protected double getEndTime() {
        return 1;
    }

    private Vec3 fDerivative(double t) {
        return P1d.subtract(P0d).scale(2 * (1 - t)).add(P2d.subtract(P1d).scale(2 * t));
    }

    public static VoxelSequence configuredWith(Vec3i... configurationPoints) {
        if (configurationPoints.length != NUM_CONFIGURATION_POINTS) {
            throw new IllegalArgumentException("The path requires " + NUM_CONFIGURATION_POINTS + " configuration Vec3i, but " +
                    "was provided " + configurationPoints.length + ".");
        }
        if (Vec3iUtils.allEqual(configurationPoints)) {
            return new SingleVoxelSequence(configurationPoints[0]);
        }
        return new QuadraticBezierVoxelSequence(configurationPoints[0], configurationPoints[1], configurationPoints[2]);
    }

}
