package com.fred.exploregalore.drawing;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.math.voxelsequences.LinearVoxelSequence;
import com.fred.exploregalore.math.voxelsequences.VoxelSequence;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

@Log4j2
public enum LinearPathDrawer implements PathDrawer {

    INSTANCE;


    @Override
    public void drawPath(ServerLevel serverLevel, Block block, BlockPos... configurationPos) throws IllegalArgumentException {

        if (configurationPos.length != numRequiredConfigurationPos()) {
            throw new IllegalArgumentException("The path requires " + numRequiredConfigurationPos() + " configuration BlockPos, but " +
                    "was provided " + configurationPos.length + ".");
        }

        for (val voxelPos : LinearVoxelSequence.configuredWith(configurationPos)) {
            if (!PathDrawer.tryPlacingBlock(serverLevel, new BlockPos(voxelPos), block)) {
                log.debug("tryPlacingBlock failed, likely due to the block already being there.");
            }
        }

        ExploreGalore.LOGGER.info("Successfully drew a linear path.");

    }

    /*
     * 1. Using a series of BlockPos, configure a VoxelSequence.
     * 2. Iterate through the VoxelSequence and feed the BlockPlacementStrategy each generated Vec3i.
     *   2a. In the BlockPlacementStrategy, use the provided Vec3i to place one or more blocks in the world
     *       using the provided serverLevel.
     */

    //  default void drawPath(ServerLevel serverLevel, BlockPlacementStrategy strategy, VoxelSequence sequence)

    @Override
    public int numRequiredConfigurationPos() {
        return LinearVoxelSequence.NUM_CONFIGURATION_POINTS;
    }


}
