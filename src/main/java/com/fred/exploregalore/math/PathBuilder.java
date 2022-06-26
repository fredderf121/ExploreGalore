package com.fred.exploregalore.math;


import com.fred.exploregalore.math.voxelsequences.LinearVoxelSequence;
import com.fred.exploregalore.math.voxelsequences.ParametricFunction;
import com.fred.exploregalore.math.voxelsequences.ParametricFunction.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;


/**
 * Contains methods that, provided 2 points in the world, will return a series of points
 * that connect the provided points - a path.
 */
public class PathBuilder {

    private static final double HALF_BLOCK_WIDTH = 0.5;

    private final List<BlockPos> blockPosList;

    /**
     * Used when calling various line-segment generators to indicate if the currently generated
     * segment is the first/starting segment. This is needed as, when building together multiple segments,
     * we need to know whether or not to include the start/end points as to not duplicate them.
     */
    private boolean startingSegment = true;

    public PathBuilder() {
        blockPosList = new ArrayList<>();
    }

    public PathBuilder(int estimatedPathLength) {
        blockPosList = new ArrayList<>(estimatedPathLength);
    }





    /**
     * <p>
     * Returns an iterable containing a series of BlockPos that, when used to place blocks
     * in the world, will connect the provided startPos and endPos in a (approximately)
     * straight line.
     * </p>
     * <p>
     * Additionally, the path is fully connected by its edges (i.e., Minecraft rails
     * can be placed for form a fully connected path).
     * </p>
     * <p>
     * Note that this method is used as a stepping-stone for my own learning purposes.
     * As such, this method ONLY works in the x-z plane. If the two provided BlockPos
     * Differ in y-value, then only the y-value of startPos will be used, creating a 'flat'
     * line with the same y-values as startPos.
     * </p>
     * <p>
     * Credit: The algorithm is taken from <a href="https://www.redblobgames.com/grids/line-drawing.html">Red Blob Games</a>.
     * </p>
     */
    public PathBuilder linearPath2D(BlockPos startPos, BlockPos endPos) {

        int distX = Math.abs(endPos.getX() - startPos.getX());
        int distZ = Math.abs(endPos.getZ() - startPos.getZ());

        // +1 if travelling in +X or +Z direction, respectively. (-1 otherwise)
        int dirX = endPos.getX() > startPos.getX() ? 1 : -1;
        int dirZ = endPos.getZ() > startPos.getZ() ? 1 : -1;

        // I believe this should always be the total number of blocks required
        blockPosList.add(startPos);

        // ix and iy indicate where along the path we are currently on
        // We stop when either ix or iy is one block away from the final block.
        // This is because inside the loop, we calculate the NEXT block to add.
        for (int ix = 0, iz = 0; (ix < distX || iz < distZ); ) {

            // Refer to link (and my own OneNote notes) for why this works
            if ((distZ * (2 * ix + 1)) < (distX * (2 * iz + 1))) {
                ix++;
            } else {
                iz++;
            }
            blockPosList.add(startPos.offset(ix * dirX, 0, iz * dirZ));

        }

        return this;

    }


    /**
     * Use {@link LinearVoxelSequence} instead.
     */
    @Deprecated
    public PathBuilder linearPath3D(BlockPos startPos, BlockPos endPos) {

        int distX = Math.abs(endPos.getX() - startPos.getX());
        int distY = Math.abs(endPos.getY() - startPos.getY());
        int distZ = Math.abs(endPos.getZ() - startPos.getZ());

        int dirX = endPos.getX() > startPos.getX() ? 1 : -1;
        int dirY = endPos.getY() > startPos.getY() ? 1 : -1;
        int dirZ = endPos.getZ() > startPos.getZ() ? 1 : -1;

        // Manhattan distance
        int totalNumBlocksMinusStarting = distX + distY + distZ;

        if (startingSegment) {
            blockPosList.add(startPos);
            startingSegment = false;
        }

        // Similar to the 2d code, refer to onenote for explanations
        for (int ix = 0, iy = 0, iz = 0, i = 0; i < totalNumBlocksMinusStarting; i++) {

            // For rail compatibility, we prioritize an increment in z before x or y.
            if (distZ * (2* ix + 1) > distX * (2 * iz + 1) && distZ * (2* iy + 1) > distY * (2 * iz + 1)) {
                iz++;
            } else if (distX * (2* iy + 1) > distY * (2 * ix + 1)) {
                ix++;
            } else {
                iy++;
            }

            blockPosList.add(startPos.offset(dirX * ix, dirY * iy, dirZ * iz));

        }

        return this;

    }


    public PathBuilder helixPathCounterClockwiseY3d(BlockPos startPos, BlockPos endPos, int radius, int numSpirals) {
        ParametricFunction helixFunction = ParametricFunction.createHelixParametricFunction(
                Vec3.atLowerCornerOf(startPos), Vec3.atLowerCornerOf(endPos), radius, numSpirals
        );

        ParametricFunctionIterator helixFunctionIterator = helixFunction.new ParametricFunctionIterator(8 * numSpirals);

        Vec3 currentStartPoint = helixFunctionIterator.next();
        Vec3 currentEndPoint;
        while (helixFunctionIterator.hasNext()) {
            currentEndPoint = helixFunctionIterator.next();

            linearPath3D(vec3ToBlockPos(currentStartPoint), vec3ToBlockPos(currentEndPoint));

            currentStartPoint = currentEndPoint;
        }

        return this;


    }

    private BlockPos vec3ToBlockPos(Vec3 vec3) {
        return new BlockPos(Math.round(vec3.x()), Math.round(vec3.y()), Math.round(vec3.z()));
    }

    public Iterable<BlockPos> getBlockPath() {
        return this.blockPosList;
    }


}
