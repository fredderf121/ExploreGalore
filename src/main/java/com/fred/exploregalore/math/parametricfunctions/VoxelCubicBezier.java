package com.fred.exploregalore.math.parametricfunctions;

import com.fred.exploregalore.utils.ArrayUtils;
import com.fred.exploregalore.utils.SimpleMatrixUtils;
import com.fred.exploregalore.utils.Vec3Utils;
import com.fred.exploregalore.utils.Vec3iUtils;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.math.NumberUtils;
import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.DoublePredicate;

/**
 * For all method JavaDocs, assume that:
 * <ul><li>B(t) -> R^3 represents the Bezier function</li>
 * <li>B' is the first derivative, point-wise, B'' is the second derivative, etc.</li></ul>
 * <p>The 'Voxel' in its name indicates that its iterator works with integers only.</p>
 * <p>Based off the paper: <a href="https://dl.acm.org/doi/pdf/10.1145/37402.37423">
 *     Efficient Algorithms for 3D Scan-Conversion of Parametric Curves, Surfaces, and Volumes </a></p>
 */
@Log4j2
public class VoxelCubicBezier implements VoxelSequence {

    /**
     * Matrix {@code 'M'} in the referred paper. This matrix, multiplied by the control-point
     * matrix (a matrix composed of the control points as rows) yields the coefficients of
     * the cubic Bézier curve in standard form.
     */
    private static final SimpleMatrix BASIS_MATRIX = new SimpleMatrix(new double[][]{
            {-1, 3, -3, 1},
            {3, -6, 3, 0},
            {-3, 3, 0, 0},
            {1, 0, 0, 0}
    });

    public static final int NUM_CONFIGURATION_POINTS = 4;


    // The first anchor point.
    private final Vec3i P0;
    // The first control point.
    private final Vec3i P1;
    // The second control point.
    private final Vec3i P2;
    // The second anchor point.
    private final Vec3i P3;

    // As of now, we don't even need to explicitly evaluate the bezier function itself.
    // The first derivative is needed for iterator calculations.
    private final Double2ObjectFunction<Vec3> FIRST_DERIVATIVE;

    // Possible epsilon concerns?
    private final DoublePredicate isTWithinBounds = t -> t >= 0 && t <= 1;


    public VoxelCubicBezier(Vec3i startingControlPoint,
                            Vec3i firstCurvatureControlPoint,
                            Vec3i secondCurvatureControlPoint,
                            Vec3i endingControlPoint) {
        P0 = startingControlPoint;
        P1 = firstCurvatureControlPoint;
        P2 = secondCurvatureControlPoint;
        P3 = endingControlPoint;

        log.debug("Constructing cubic bezier using points: Starting: {}, First Curvature: {}, Second Curvature: {}, Ending: {}",
                startingControlPoint, firstCurvatureControlPoint, secondCurvatureControlPoint, endingControlPoint);


        FIRST_DERIVATIVE = t ->
                Vec3.atLowerCornerOf(P1.subtract(P0)).scale(3 * Math.pow((1 - t), 2)) // 3 * (1-t)^2 * (P1 - P0)
                        .add(Vec3.atLowerCornerOf(P2.subtract(P1)).scale(6 * (1 - t) * t)) // 6 * (1-t) * t * (P2 - P1)
                        .add(Vec3.atLowerCornerOf(P3.subtract(P2)).scale(3 * Math.pow(t, 2))); // 3 * t^2 * (P3 - P2)

    }

    public VoxelCubicBezier(Vec3i ... configurationPoints) {
        this(configurationPoints[0], configurationPoints[1], configurationPoints[2], configurationPoints[3]);
    }


    /**
     * Calculates B'(t), at different values of t for each direction.
     */
    private Vec3 firstDerivativeAt(Vec3 t) {
        val oneMinusT = new Vec3(1, 1, 1).subtract(t);
        // Note that the comments on the side are in reverse order of what is written in code.
        return Vec3.atLowerCornerOf(P1.subtract(P0)).multiply(oneMinusT.multiply(oneMinusT)).scale(3) // 3 * (1-t)^2 * (P1 - P0)
                .add(Vec3.atLowerCornerOf(P2.subtract(P1)).multiply(oneMinusT.multiply(t)).scale(6)) // 6 * (1-t) * t * (P2 - P1)
                .add(Vec3.atLowerCornerOf(P3.subtract(P2)).multiply(t.multiply(t)).scale(3)); // 3 * t^2 * (P3 - P2)
    }


    @NotNull
    @Override
    public VoxelSequenceIterator iterator() {
        return new VoxelCubicBezierIterator();

    }


    // TODO: If the iterator for a given 4 points are used frequently and performance is bad,
    //  refactor the constants into another class so that they aren't calculated every time
    private class VoxelCubicBezierIterator implements VoxelSequenceIterator {


        /**
         * {@code n} as used in the paper.
         */
        private final long N;
        /**
         * {@code n^3}
         */
        private final long N_CUBED;
        /**
         * {@code 2 * n^3}
         */
        private final long N_DOUBLED_CUBED;


        private long[] xForwardDifferencesScaled;
        private long[] yForwardDifferencesScaled;
        private long[] zForwardDifferencesScaled;

        private Vec3i currentVoxel;
        private Vec3i nextVoxel;


        public VoxelCubicBezierIterator() {
            // Setting currentVoxel to an 'invalid' value. This is equivalent to
            // 'one-before-the-first' (similar and opposite to one-past-the-end).
            currentVoxel = new Vec3i(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
            nextVoxel = P0;

            val maxFirstDerivativeAbs = maxFirstDerivativeAbs();

            // Sum of maximum first derivatives, needed for 6-connectivity
            N = (int) Math.ceil(Vec3Utils.sumCoordinates(maxFirstDerivativeAbs));
            N_CUBED = N * N * N;
            N_DOUBLED_CUBED = 2 * N_CUBED;
            log.debug("Calculating N: {}, N^3: {}, 2N^3: {}", N, N_CUBED, N_DOUBLED_CUBED);

            val forwardDifferenceMatrixAtZero = forwardDifferenceMatrixAtZero();

            double[][] forwardDifferenceMatrixAtZeroArray = SimpleMatrixUtils.toDoubleArray(forwardDifferenceMatrixAtZero.transpose());
            xForwardDifferencesScaled = ArrayUtils.doubleToLong(forwardDifferenceMatrixAtZeroArray[0]);
            yForwardDifferencesScaled = ArrayUtils.doubleToLong(forwardDifferenceMatrixAtZeroArray[1]);
            zForwardDifferencesScaled = ArrayUtils.doubleToLong(forwardDifferenceMatrixAtZeroArray[2]);
            log.debug("Initial forward differences at zero, x: {}, y: {}, z: {}",
                    Arrays.toString(xForwardDifferencesScaled),
                    Arrays.toString(yForwardDifferencesScaled),
                    Arrays.toString(zForwardDifferencesScaled));

        }



        @Override
        public boolean hasNext() {
            return !currentVoxel.equals(P3);
        }

        @Override
        public Vec3i next() {
            currentVoxel = nextVoxel;

            int debug_NumRepeatedUpdates = 0;

            // Calculating the next voxel before returning the current one.
            // We may need to repeat this multiple times as the step size is
            // derived from the *maximum* first difference, meaning one round of updates
            // may not be substantial enough to cause a step in any direction.
            do {
                debug_NumRepeatedUpdates++;
                // For 6-connectivity, we may only update one coordinate at a time, and so we always choose
                // the largest to update.
                if (Math.abs(xForwardDifferencesScaled[0]) > Math.abs(yForwardDifferencesScaled[0]) &&
                        Math.abs(xForwardDifferencesScaled[0]) > Math.abs(zForwardDifferencesScaled[0])) {
                    if (xForwardDifferencesScaled[0] > N_CUBED) {
                        nextVoxel = nextVoxel.offset(1, 0, 0);
                        xForwardDifferencesScaled[0] -= N_DOUBLED_CUBED;

                    } else if (xForwardDifferencesScaled[0] < -N_CUBED) {
                        nextVoxel = nextVoxel.offset(-1, 0, 0);
                        xForwardDifferencesScaled[0] += N_DOUBLED_CUBED;
                    }
                } else if (Math.abs(yForwardDifferencesScaled[0]) > Math.abs(zForwardDifferencesScaled[0])) {
                    if (yForwardDifferencesScaled[0] > N_CUBED) {
                        nextVoxel = nextVoxel.offset(0, 1, 0);
                        yForwardDifferencesScaled[0] -= N_DOUBLED_CUBED;

                    } else if (yForwardDifferencesScaled[0] < -N_CUBED) {
                        nextVoxel = nextVoxel.offset(0, -1, 0);
                        yForwardDifferencesScaled[0] += N_DOUBLED_CUBED;
                    }
                } else {
                    if (zForwardDifferencesScaled[0] > N_CUBED) {
                        nextVoxel = nextVoxel.offset(0, 0, 1);
                        zForwardDifferencesScaled[0] -= N_DOUBLED_CUBED;

                    } else if (zForwardDifferencesScaled[0] < -N_CUBED) {
                        nextVoxel = nextVoxel.offset(0, 0, -1);
                        zForwardDifferencesScaled[0] += N_DOUBLED_CUBED;
                    }
                }

                updateForwardDifferences(xForwardDifferencesScaled);
                updateForwardDifferences(yForwardDifferencesScaled);
                updateForwardDifferences(zForwardDifferencesScaled);
            } while (nextVoxel.equals(currentVoxel));

            log.debug("Next voxel position: {}", currentVoxel);
            log.trace("The loop required {} iterations to find the next step", debug_NumRepeatedUpdates);

            return currentVoxel;
        }

        /**
         * Calculates the <b>absolute</b> max first derivative value of the curve for each coordinate axis.
         */
        private Vec3 maxFirstDerivativeAbs() {
            // Assuming B(t) -> R^3, B'(t) = dB/dt, the following lines calculate:

            // Note that the -AtZero and -AtOne vectors are simply the tangent vectors at the two anchor points.
            // B'(0) = 3 * (P1 - P0)
            val firstDerivativeAtZero = Vec3.atLowerCornerOf(P1.subtract(P0)).scale(3);
            // B'(1) = 3 * (P3 - P2)
            val firstDerivativeAtOne = Vec3.atLowerCornerOf(P3.subtract(P2)).scale(3);

            // The time for when B''(t) = 0 (unique for each coordinate axis)
            val secondDerivativeZeroTime = secondDerivativeZeroTime();

            // The calculated second derivative zero time (t) must be within a certain range (normally [0, 1])
            // to be valid for Bézier curves. Otherwise, in the below extrema calculations, the extrema
            // occurs at either t = 0 or t = 1. Here, if it's not valid, we set the time to zero.
            val validatedSecondDerivativeZeroTime = new Vec3(
                    isTWithinBounds.test(secondDerivativeZeroTime.x) ? secondDerivativeZeroTime.x : 0,
                    isTWithinBounds.test(secondDerivativeZeroTime.y) ? secondDerivativeZeroTime.y : 0,
                    isTWithinBounds.test(secondDerivativeZeroTime.z) ? secondDerivativeZeroTime.z : 0
            );
            val firstDerivativeLocalExtrema = firstDerivativeAt(validatedSecondDerivativeZeroTime);


            // Note the absolute value!
            return new Vec3(
                    NumberUtils.max(Math.abs(firstDerivativeAtZero.x), Math.abs(firstDerivativeAtOne.x), Math.abs(firstDerivativeLocalExtrema.x)),
                    NumberUtils.max(Math.abs(firstDerivativeAtZero.y), Math.abs(firstDerivativeAtOne.y), Math.abs(firstDerivativeLocalExtrema.y)),
                    NumberUtils.max(Math.abs(firstDerivativeAtZero.z), Math.abs(firstDerivativeAtOne.z), Math.abs(firstDerivativeLocalExtrema.z)));

        }

        /**
         * Given a cubic bezier B(t) -> R^3,
         *
         * @return t for when B''(t) = 0, one for each coordinate axis.
         */
        private Vec3 secondDerivativeZeroTime() {
            // - (P0 - 2 * P1 + P2)
            val numerator = Vec3.atLowerCornerOf(P0.subtract(P1.multiply(2)).offset(P2).multiply(-1));

            // -P0 + 3 * (P1 - P2) + P3
            val denominator = Vec3.atLowerCornerOf(P0.multiply(-1).offset(P1.subtract(P2).multiply(3)).offset(P3));

            // numerator / denominator (point-wise)
            return numerator.multiply(1 / denominator.x, 1 / denominator.y, 1 / denominator.z);
        }

        /**
         * Returns a matrix where each column is the forward difference (up to 3rd forwards difference)
         * for each coordinate axis. Dimensions (4 rows x 3 cols). Column 0 = x, 1 = y, 2 = z.
         */
        private SimpleMatrix forwardDifferenceMatrixAtZero() {
            // Matrix G in the paper with a relative offset so that P0 is at (0, 0, 0).
            val controlPointsMatrix = new SimpleMatrix(new double[][]{
                    Vec3iUtils.toDoubleArray(P0.subtract(P0)),
                    Vec3iUtils.toDoubleArray(P1.subtract(P0)),
                    Vec3iUtils.toDoubleArray(P2.subtract(P0)),
                    Vec3iUtils.toDoubleArray(P3.subtract(P0)),
            });

            // The matrix for the forward differences of the polynomial t^3 + t^2 + t + 1 with unity coefficients,
            // up to the 3rd forward difference (the 4th is zero, I think).
            // Scaled by 2n^3 so that when calculating the points that lie on the curve, only integer
            // arithmetic is required.
            // Matrix E_n in the paper.
            val unitThirdOrderForwardDifferenceMatrixScaled = new SimpleMatrix(new double[][]{
                    {0, 0, 0, N_DOUBLED_CUBED},
                    {2, 2 * N, 2 * N * N, 0},
                    {12, 4 * N, 0, 0},
                    {12, 0, 0, 0}
            });

            return unitThirdOrderForwardDifferenceMatrixScaled
                    .mult(BASIS_MATRIX)
                    .mult(controlPointsMatrix);
        }


        private void updateForwardDifferences(long[] forwardDifferences) {
            for (int i = 0; i < forwardDifferences.length - 1; i++) {
                forwardDifferences[i] += forwardDifferences[i + 1];
            }
        }
    }
}
