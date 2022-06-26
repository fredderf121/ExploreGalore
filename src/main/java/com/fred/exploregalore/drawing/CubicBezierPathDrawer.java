package com.fred.exploregalore.drawing;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.math.voxelsequences.CubicBezierVoxelSequence;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public enum CubicBezierPathDrawer implements PathDrawer {
    INSTANCE;

    @Override
    public void drawPath(ServerLevel serverLevel, Block block, BlockPos... configurationPos) throws IllegalArgumentException {
        if (configurationPos.length != numRequiredConfigurationPos()) {
            throw new IllegalArgumentException("The path requires " + numRequiredConfigurationPos() + " configuration BlockPos, but " +
                    "was provided " + configurationPos.length + ".");
        }

        for (val voxelPos : CubicBezierVoxelSequence.configuredWith(configurationPos)) {
            if (!PathDrawer.tryPlacingBlock(serverLevel, new BlockPos(voxelPos), block)) {
                ExploreGalore.LOGGER.debug("tryPlacingBlock failed, likely due to the block already being there.");
            }
        }

        ExploreGalore.LOGGER.debug("Successfully executed drawCubicBezierBlockPath");
    }

    @Override
    public int numRequiredConfigurationPos() {
        return CubicBezierVoxelSequence.NUM_CONFIGURATION_POINTS;
    }
}
