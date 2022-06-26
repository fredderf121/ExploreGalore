package com.fred.exploregalore.drawing;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.math.voxelsequences.LinearVoxelSequence;
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

        for (val voxelPos : new LinearVoxelSequence(configurationPos)) {
            if (!PathDrawer.tryPlacingBlock(serverLevel, new BlockPos(voxelPos), block)) {
                log.debug("tryPlacingBlock failed, likely due to the block already being there.");
            }
        }

        ExploreGalore.LOGGER.info("Successfully drew a linear path.");

    }

    @Override
    public int numRequiredConfigurationPos() {
        return LinearVoxelSequence.NUM_CONFIGURATION_POINTS;
    }


}
