package com.fred.exploregalore.drawing;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public interface PathDrawer {


    /**
     * Place a sequence of blocks in the world based on the provided {@code configurationPos}
     * @param serverLevel The world to place blocks in
     * @param block The block to use; TODO: Allow for a block placement strategy eventually, instead of a single block
     * @param configurationPos A series of {@link BlockPos} that determine how the path will look (e.g., a linear
     *                         path will need a starting and ending BlockPos)
     * @throws IllegalArgumentException if the series of {@link BlockPos} does not match the required configuration;
     *                                  e.g., if the linear path is not passed exactly 2 positions.
     */
    void drawPath(ServerLevel serverLevel, Block block, BlockPos... configurationPos) throws IllegalArgumentException;

    int numRequiredConfigurationPos();


    static boolean tryPlacingBlock(ServerLevel serverLevel, BlockPos blockPos, Block blockToPlace) {
        // If the existing block at blockPos is a BlockEntity, we want to first remove its
        // inventory (so nothing drops)
        // TODO: I have commented this out so that entities drop their inventory when destroyed
        //       Also, need to make the replaced block drop itself for survival compatibility
//        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
//        Clearable.tryClear(blockEntity);

        // Don't know if I should use this method, or if a "safer" method exists
        // UPDATE_ALL to update client, rendering, neighboring blocks, etc.
        // Returns true if block placement was successful.
        return serverLevel.setBlock(blockPos, blockToPlace.defaultBlockState(), Block.UPDATE_ALL);

    }
}
