package com.fred.exploregalore.math.parametricfunctions;

import com.fred.exploregalore.math.PathBuilder;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import org.jetbrains.annotations.NotNull;

public class VoxelLinear implements VoxelSequence {

    public static final int NUM_CONFIGURATION_POINTS = 2;

    private final Vec3i startPoint;
    private final Vec3i endPoint;

    public VoxelLinear(Vec3i startPoint, Vec3i endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public VoxelLinear(Vec3i... configurationPoints) {
        this(configurationPoints[0], configurationPoints[1]);
    }

    @NotNull
    @Override
    public VoxelSequenceIterator iterator() {
        return new VoxelLinearIterator();
    }

    private class VoxelLinearIterator implements VoxelSequenceIterator {

        private final int distX;
        private final int distY;
        private final int distZ;

        private final int dirX;
        private final int dirY;
        private final int dirZ;

        private final int numTotalVoxels;

        private int numVoxelsTraversed;
        private int ix, iy, iz;

        public VoxelLinearIterator() {
            distX = Math.abs(endPoint.getX() - startPoint.getX());
            distY = Math.abs(endPoint.getY() - startPoint.getY());
            distZ = Math.abs(endPoint.getZ() - startPoint.getZ());

            dirX = endPoint.getX() > startPoint.getX() ? 1 : -1;
            dirY = endPoint.getY() > startPoint.getY() ? 1 : -1;
            dirZ = endPoint.getZ() > startPoint.getZ() ? 1 : -1;

            // Need +1 because we want endpoints *inclusive*
            numTotalVoxels = distX + distY + distZ + 1;

            numVoxelsTraversed = 0;
            ix = 0;
            iy = 0;
            iz = 0;
        }

        @Override
        public boolean hasNext() {
            return numVoxelsTraversed < numTotalVoxels;
        }

        @Override
        public Vec3i next() {
            val currentVoxel = startPoint.offset(dirX * ix, dirY * iy, dirZ * iz);

            // Calculating the next voxel
            // Refer to onenote for explanations
            // For rail compatibility, we prioritize an increment in z before x or y.
            if (distZ * (2 * ix + 1) > distX * (2 * iz + 1) && distZ * (2 * iy + 1) > distY * (2 * iz + 1)) {
                iz++;
            } else if (distX * (2 * iy + 1) > distY * (2 * ix + 1)) {
                ix++;
            } else {
                iy++;
            }
            numVoxelsTraversed++;
            return currentVoxel;
        }
    }
}
