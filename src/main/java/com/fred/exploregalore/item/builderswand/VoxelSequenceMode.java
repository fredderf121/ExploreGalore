package com.fred.exploregalore.item.builderswand;

import com.fred.exploregalore.math.voxelsequences.CubicBezierVoxelSequence;
import com.fred.exploregalore.math.voxelsequences.LinearVoxelSequence;
import com.fred.exploregalore.math.voxelsequences.VoxelSequence;
import lombok.AllArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

/**
 * Indicates what kind of sequence the Builder's Wand will use as the basis of the location of blocks to place.
 * Note the default scope of package-only.
 */
@AllArgsConstructor
enum VoxelSequenceMode {
    // TODO: Move strings to language files.
    LINEAR(LinearVoxelSequence::configuredWith, LinearVoxelSequence.NUM_CONFIGURATION_POINTS, "Linear"),
    CUBIC_BEZIER(CubicBezierVoxelSequence::configuredWith, CubicBezierVoxelSequence.NUM_CONFIGURATION_POINTS, "Cubic Bezier");

    public static final String TAG_NAME = "voxel_sequence";

    public static final VoxelSequenceMode DEFAULT_MODE = LINEAR;


    // Note that the order matters due to Lombok's @AllArgsConstructor.
    private final Function<Vec3i[], VoxelSequence> factoryGeneratorFunction;
    private final int numRequiredConfigurationPos;
    private final String name;

    private static final VoxelSequenceMode[] MODES = VoxelSequenceMode.values();

    public static VoxelSequenceMode fromOrdinal(int ordinal) {
        return MODES[ordinal];
    }

    public static int numModes() {
        return MODES.length;
    }

    public int numRequiredBlockPos() {
        return numRequiredConfigurationPos;
    }

    public VoxelSequence createSequenceWith(Vec3i... configurationPos) {
        return this.factoryGeneratorFunction.apply(configurationPos);
    }


    @Override
    public String toString() {
        return this.name;
    }
}
