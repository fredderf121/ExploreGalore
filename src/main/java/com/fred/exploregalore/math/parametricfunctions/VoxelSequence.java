package com.fred.exploregalore.math.parametricfunctions;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.stream.Stream;

/**
 * Marker class that indicates this class produces a sequence of voxel coordinates ({@link Vec3i})
 * with some pattern to it (such as a function).
 */
public interface VoxelSequence extends Iterable<Vec3i>{
}

