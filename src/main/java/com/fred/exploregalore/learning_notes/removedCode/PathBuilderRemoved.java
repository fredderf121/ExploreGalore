package com.fred.exploregalore.learning_notes.removedCode;

import com.fred.exploregalore.math.PathBuilder;
import com.fred.exploregalore.math.parametricfunctions.ParametricFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class PathBuilderRemoved {

    private static final double HALF_BLOCK_WIDTH = 0.5;

    private final List<BlockPos> blockPosList;
    private int numBlocks;

    public PathBuilderRemoved() {
        this.blockPosList = new ArrayList<>();
        this.numBlocks = 0;
    }

    /**
     * The following three functions return true if the provided Vec3 lies on the respective
     * cube face.
     * <p>
     * For example, {@code isOnEitherXCubeFaces} returns true if the Vec3 lies on the
     * cube faces of BlockPos with normal vectors (1, 0, 0) or (-1, 0, 0).
     * </p>
     * <p>
     * It is assumed that the Vec3 <i>is</i> actually lying on one of the cube faces of
     * BlockPos. Otherwise, this predicate provides no useful information.
     * </p>
     */
    // Depreciated, remove if not needed for a while
/*    private static final BiPredicate<BlockPos, Vec3> isOnEitherXCubeFaces =
            (blockPos, point) -> (point.y() < blockPos.getY() + 0.5 && point.y() > blockPos.getY() - 0.5)
                    && (point.z() < blockPos.getZ() + 0.5 && point.z() > blockPos.getZ() - 0.5);

    private static final BiPredicate<BlockPos, Vec3> isOnEitherYCubeFaces =
            (blockPos, point) -> (point.x() < blockPos.getX() + 0.5 && point.x() > blockPos.getX() - 0.5)
                    && (point.z() < blockPos.getZ() + 0.5 && point.z() > blockPos.getZ() - 0.5);

    private static final BiPredicate<BlockPos, Vec3> isOnEitherZCubeFaces =
            (blockPos, point) -> (point.x() < blockPos.getX() + 0.5 && point.x() > blockPos.getX() - 0.5)
                    && (point.y() < blockPos.getY() + 0.5 && point.y() > blockPos.getY() - 0.5);*/


    /**
     * Returns true if 'b' is within a range of 'a ± margin'
     */
    private static boolean isInsideRangeInclusive(double a, double b, double margin) {
        return (b <= a + margin) && (b >= a - margin);
    }

    private static boolean isOutsideRangeInclusive(double a, double b, double margin) {
        return (b >= a + margin) || (b <= a - margin);
    }

    private static ParametricFunction.IndependentVariable determineIntersectionFace(
            ParametricFunction.IndependentVariable independentVariable,
            BlockPos blockPos, Vec3 point
    ) {
        // The priority of returns is X, then Z, then Y.
        // That said, each case prioritizes that case before the other dimensions.
        // Ex: case Y prioritizes Y, then X, then Z.
        switch (independentVariable) {


            case X -> {
                if (isInsideRangeInclusive(blockPos.getY(), point.y(), HALF_BLOCK_WIDTH)
                        && isInsideRangeInclusive(blockPos.getZ(), point.z(), HALF_BLOCK_WIDTH)) {
                    return ParametricFunction.IndependentVariable.X;
                } else if (isOutsideRangeInclusive(blockPos.getZ(), point.z(), HALF_BLOCK_WIDTH)
                        && isInsideRangeInclusive(blockPos.getY(), point.y(), HALF_BLOCK_WIDTH)) {
                    return ParametricFunction.IndependentVariable.Z;
                } else {
                    return ParametricFunction.IndependentVariable.Y;
                }
            }
            case Y -> {
                if (isInsideRangeInclusive(blockPos.getX(), point.x(), HALF_BLOCK_WIDTH)
                        && isInsideRangeInclusive(blockPos.getZ(), point.z(), HALF_BLOCK_WIDTH)) {
                    return ParametricFunction.IndependentVariable.Y;
                } else if (isOutsideRangeInclusive(blockPos.getX(), point.x(), HALF_BLOCK_WIDTH)
                        && isInsideRangeInclusive(blockPos.getZ(), point.z(), HALF_BLOCK_WIDTH)) {
                    return ParametricFunction.IndependentVariable.X;
                } else {
                    return ParametricFunction.IndependentVariable.Z;
                }
            }
            case Z -> {
                if (isInsideRangeInclusive(blockPos.getX(), point.x(), HALF_BLOCK_WIDTH)
                        && isInsideRangeInclusive(blockPos.getY(), point.y(), HALF_BLOCK_WIDTH)) {
                    return ParametricFunction.IndependentVariable.Z;
                } else if (isOutsideRangeInclusive(blockPos.getX(), point.x(), HALF_BLOCK_WIDTH)
                        && isInsideRangeInclusive(blockPos.getY(), point.y(), HALF_BLOCK_WIDTH)) {
                    return ParametricFunction.IndependentVariable.X;
                } else {
                    return ParametricFunction.IndependentVariable.Y;
                }
            }

            default -> {
                return ParametricFunction.IndependentVariable.X;
            }
        }
    }

    public PathBuilderRemoved LinearPath1d(
            BlockPos startPos, ParametricFunction.IndependentVariable independentVariable, int distance, int direction
    ) {
        IntConsumer addBlockConsumer;

        switch (independentVariable) {
            case X -> addBlockConsumer = x -> blockPosList.add(startPos.offset(x, 0, 0));
            case Y -> addBlockConsumer = y -> blockPosList.add(startPos.offset(0, y, 0));
            case Z -> addBlockConsumer = z -> blockPosList.add(startPos.offset(0, 0, z));
            default -> throw new IllegalStateException("Unexpected value: " + independentVariable);
        }
        for (int i = 0; i <= distance; i++) {
            addBlockConsumer.accept(i * direction);
        }
        return this;
    }

    /**
     * This algorithm was originally build in an attempt to be faster than the other
     * LinearPath2d algorithm, but was found to be slower instead.
     */
    @Deprecated
    public PathBuilderRemoved linearPath2D(BlockPos startPos, BlockPos endPos, boolean deprecated) {
        int distX = Math.abs(endPos.getX() - startPos.getX());
        int distZ = Math.abs(endPos.getZ() - startPos.getZ());

        // +1 if travelling in +X or +Z direction, respectively. (-1 otherwise)
        int dirX = endPos.getX() > startPos.getX() ? 1 : -1;
        int dirZ = endPos.getZ() > startPos.getZ() ? 1 : -1;

        // Handling the 1d case
        if (distX == 0) {
            return LinearPath1d(startPos, ParametricFunction.IndependentVariable.Z, distZ, dirZ);
        } else if (distZ == 0) {
            return LinearPath1d(startPos, ParametricFunction.IndependentVariable.X, distX, dirX);
        }

        blockPosList.add(startPos);

        int ix = 0, iz = 0, numBlocksInZDirection;
        /*
         * Given the linear line between startPos and endPos, shifted to (0, 0, 0), we calculate
         * The z-values (zComponent) of the point on the line at xIncrement.
         * We calculate z-values for x at 0.5, 1.5, ..., as these tell us information about where the
         * line intersects at each block edge, which in turn tells us where the next adjacent block should be placed.
         */
        for (double xIncrement = 0.5, zComponent;
             xIncrement < distX; xIncrement++
        ) {
            zComponent = (double) distZ / distX * xIncrement;

            // How many blocks we must place in the z direction before placing
            // a block in the x direction.
            numBlocksInZDirection = (int) (Math.round(zComponent) - iz);
            for (int i = 0; i < numBlocksInZDirection; i++) {
                iz++;
                blockPosList.add(startPos.offset(dirX * ix, 0, dirZ * iz));
            }
            ix++;
            blockPosList.add(startPos.offset(dirX * ix, 0, dirZ * iz));
        }

        // Don't know if it's the best method, but the above loop fails to add in a few vertical blocks
        // when distZ >= distX (more tall than wide). This loop adds in the remaining blocks.
        // Refer to notes, an example is when distX = 1, distZ = 3
        for (int zRemaining = 0; zRemaining < distZ - iz; zRemaining++) {
            iz++;
            blockPosList.add(startPos.offset(dirX * ix, 0, dirZ * iz));
        }


        return this;
    }

    /**
     * <p>
     * Returns an iterable containing a series of BlockPos that form a path connecting
     * <i>startPos</i> and <i>endPos</i> while ONLY taking orthogonal steps (no diagonals).
     * </p>
     * <p>
     * The path generated is approximately linear.
     * </p>
     *
     * <p>
     * This is being marked deprecated in favor of a cleaner algorithm that is inspired from the
     * non-deprecated LinearPath2d method.
     * </p>
     */
    @Deprecated
    public PathBuilderRemoved linearPath3D(BlockPos startPos, BlockPos endPos, boolean deprecated) {
        ParametricFunction linearPathFunction = ParametricFunction.createLinearParametricFunction(
                Vec3.atLowerCornerOf(startPos),
                Vec3.atLowerCornerOf(endPos)
        );

        ParametricFunction.IndependentVariable independentVariable = linearPathFunction.getIndependentVariable();

        int dirX = endPos.getX() > startPos.getX() ? 1 : -1;
        int dirY = endPos.getY() > startPos.getY() ? 1 : -1;
        int dirZ = endPos.getZ() > startPos.getZ() ? 1 : -1;

        // I believe this should always be the total number of blocks required
        int totalBlocksRequired = endPos.distManhattan(startPos) + 1;

        /*
         * ix, iy, iz represent the coordinates of the next BlockPos part of our path.
         * In each iteration of our loop, we calculate the next BlockPos to add to our path.
         */
        for (int ix = startPos.getX(), iy = startPos.getY(), iz = startPos.getZ(),
             blockCount = 0, t = 0;
             blockCount < totalBlocksRequired;
             blockCount++
        ) {
            BlockPos currentBlockPos = new BlockPos(ix, iy, iz);
            blockPosList.add(currentBlockPos);


            Vec3 atBlockEdge = linearPathFunction.f(t + 0.5);

            ParametricFunction.IndependentVariable intersectionFace = determineIntersectionFace(independentVariable, currentBlockPos, atBlockEdge);
            switch (intersectionFace) {

                case X -> ix += dirX;
                case Y -> iy += dirY;
                case Z -> iz += dirZ;
                default -> throw new IllegalStateException("Unexpected value: " + intersectionFace);
            }
            if (independentVariable == intersectionFace) {
                t++;
            }
        }

        return this;
    }

        /*
    Rough Draft Thinking:
    For a general explicit parametric function, at f(t + 0.5), the function can lie on any
    6 of the faces of a cube.
    For a general MONOTONIC explicit parametric function, at f(t + 0.5), the function can
    lie on any 3 of the faces of a cube. This halves the regions that we must check for!
     */
}
