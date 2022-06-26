package com.fred.exploregalore.math.voxelsequences;

import net.minecraft.core.Vec3i;

/**
 * Marker class that indicates this class produces a sequence of voxel coordinates ({@link Vec3i})
 * with some pattern to it (such as a function).
 */
public interface VoxelSequence extends Iterable<Vec3i>{
}

