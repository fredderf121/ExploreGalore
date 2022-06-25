package com.fred.exploregalore.drawing;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.math.PathBuilder;
import com.fred.exploregalore.math.parametricfunctions.VoxelCubicBezier;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

@Log4j2
public enum LinearPathDrawer implements PathDrawer{

    INSTANCE;


    @Override
    public void drawPath(ServerLevel serverLevel, Block block, BlockPos... configurationPos) throws IllegalArgumentException {

        if (configurationPos.length != numRequiredConfigurationPos()) {
            throw new IllegalArgumentException("The path requires " + numRequiredConfigurationPos() + " configuration BlockPos, but " +
                    "was provided " + configurationPos.length + ".");
        }

        Iterable<BlockPos> path = new PathBuilder()
                .linearPath3D(configurationPos[0], configurationPos[1])
                //.helixPathCounterClockwiseY3d(startPos, endPos, 10, 3)
                .getBlockPath();
        for (BlockPos blockPos : path) {
            if (!PathDrawer.tryPlacingBlock(serverLevel, blockPos, block)) {
                log.debug("tryPlacingBlock failed, likely due to the block already being there.");
            }
        }

        ExploreGalore.LOGGER.info("Successfully drew a linear path.");

    }

    @Override
    public int numRequiredConfigurationPos() {
        return 2;
    }


}
