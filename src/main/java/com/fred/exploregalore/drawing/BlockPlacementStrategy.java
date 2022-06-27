package com.fred.exploregalore.drawing;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Defines how blocks should be placed given an initial 'basis' position.
 *
 * <p>
 *     For example, the simplest strategy would be to just place a single block at the basis position.
 * </p>
 * <p>
 *     A more complex example would be to maintain a state and to alternate the type of block being placed,
 *     and/or to place multiple blocks above/below the basis position.
 * </p>
 */
public abstract class BlockPlacementStrategy {

    private final ServerLevel serverLevel;

    protected BlockPlacementStrategy(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
    }

    public abstract void placeBlocksAround(BlockPos basis);

    protected boolean tryPlacingBlock(BlockPos blockPos, BlockState blockState) {
        // If the existing block at blockPos is a BlockEntity, we want to first remove its
        // inventory (so nothing drops)
        // TODO: I have commented this out so that entities drop their inventory when destroyed
        //       Also, need to make the replaced block drop itself for survival compatibility
//        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
//        Clearable.tryClear(blockEntity);

        // Don't know if I should use this method, or if a "safer" method exists
        // UPDATE_ALL to update client, rendering, neighboring blocks, etc.
        // Returns true if block placement was successful.
        return serverLevel.setBlock(blockPos, blockState, Block.UPDATE_ALL);

    }



    /*
     * 1. Using a series of BlockPos, configure a VoxelSequence.
     * 2. Iterate through the VoxelSequence and feed the BlockPlacementStrategy each generated Vec3i.
     *   2a. In the BlockPlacementStrategy, use the provided Vec3i to place one or more blocks in the world
     *       using the provided serverLevel.
     */

    //  default void drawPath(ServerLevel serverLevel, BlockPlacementStrategy strategy, VoxelSequence sequence)
}
