package com.fred.exploregalore.drawing.block_placement_strategy;

import com.fred.exploregalore.ExploreGalore;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class SingleBlockPlacementStrategy extends BlockPlacementStrategy {

    private final BlockState blockState;

    public SingleBlockPlacementStrategy(ServerLevel serverLevel, BlockState blockState) {
        super(serverLevel);

        this.blockState = blockState;
    }

    @Override
    public void placeBlocksAround(BlockPos basis) {
        if (!tryPlacingBlock(basis, blockState)) {
            ExploreGalore.LOGGER.debug("""
                    Block was not placed.
                    It is likely that the block already at the basis position already had the same BlockState.""");
        }
    }
}
