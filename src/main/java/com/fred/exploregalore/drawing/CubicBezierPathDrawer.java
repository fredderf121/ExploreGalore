package com.fred.exploregalore.drawing;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.math.parametricfunctions.VoxelCubicBezier;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public enum CubicBezierPathDrawer implements PathDrawer {
    INSTANCE;

    private static final int NUM_REQUIRED_CONFIG_POS = 4;


    @Override
    public void drawPath(ServerLevel serverLevel, Block block, BlockPos... configurationPos) throws IllegalArgumentException {
        if (configurationPos.length != NUM_REQUIRED_CONFIG_POS) {
            throw new IllegalArgumentException("The path requires " + NUM_REQUIRED_CONFIG_POS + " configuration BlockPos, but " +
                    "was provided " + configurationPos.length + ".");
        }

        for (val voxelPos : new VoxelCubicBezier(configurationPos)) {
            if (!PathDrawer.tryPlacingBlock(serverLevel, new BlockPos(voxelPos), block)) {
                ExploreGalore.LOGGER.debug("tryPlacingBlock failed, likely due to the block already being there.");
            }
        }

        ExploreGalore.LOGGER.debug("Successfully executed drawCubicBezierBlockPath");
    }
}
