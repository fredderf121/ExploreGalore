package com.fred.exploregalore.math.voxelsequences;

import net.minecraft.world.phys.Vec3;

import java.util.function.DoubleUnaryOperator;

public class HelixParametricFunction extends ParametricFunction {
    public HelixParametricFunction(IndependentVariable independentVariable, DoubleUnaryOperator xFunc, DoubleUnaryOperator yFunc, DoubleUnaryOperator zFunc, double startT, double endT) {
        super(independentVariable, xFunc, yFunc, zFunc, startT, endT);
    }

    /**
     * <p>
     * A note about accuracy:
     * </p>
     * <p>
     * A helix function is generally defined as {@code f(t) = <cos(t), sin(t), t>}, where polar coordinates are used.
     * Converting this to cartesian coordinates, we obtain {@code f(x) = <x, sqrt(1-x^2), arccos(x)} (which only
     * represents a half-helix due to the square root and arccosine).
     * </p>
     * <p>
     * In this implementation, we have instead decided to go with the helix formula
     * {@code f(x) = <x, sqrt(1-x^2), x>}. While this does not produce as "curvy" of a helix, it is sufficient for
     * our purposes, as our function is used to create a block path, with has a large margin for error.
     * </p>
     *
     * @param startPoint
     * @param endPoint
     * @param radius
     * @param numSpirals
     * @return
     */
    public static HelixParametricFunction createHelixParametricFunction(
            Vec3 startPoint, Vec3 endPoint, double radius, int numSpirals
    ) {

        double axisScalingFactor;

        Vec3 distance = endPoint.subtract(startPoint);
        // The helix prioritizes orientation in the y-direction (for Minecraft)
        // Only implement y for now, will add others once done debugging
/*        if (distance.y() != 0 + DOUBLE_ERROR_TOLERANCE) {
            axisScalingFactor = distance.y();
            return new HelixParametricFunction(
                    IndependentVariable.Y,
                    DoubleUnaryOperator.identity(),
                    t -> Math.sqrt(radius * radius - (t - radius) * (t - radius))
            );
        }*/

        return null;

    }

}
