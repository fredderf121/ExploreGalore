package com.fred.exploregalore.math.voxelsequences;

import net.minecraft.core.Vec3i;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * To represent a single voxel location.
 */
public class SingleVoxelSequence implements VoxelSequence {

    private final Vec3i position;

    public SingleVoxelSequence(Vec3i position) {
        this.position = position;
    }

    @NotNull
    @Override
    public VoxelSequenceIterator iterator() {
        return new SingleVoxelSequenceIterator();
    }

    private final class SingleVoxelSequenceIterator implements VoxelSequenceIterator {

        private boolean hasNext = true;

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public Vec3i next() {
            hasNext = false;
            return position;
        }
    }
}
