package com.fred.exploregalore.drawing.block_placement_generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Simple immutable POJO that indicates what kind of block and where the block
 * should be placed, relative to another BlockPos.
 */
public record BlockPlacementContext(BlockPos relativePos, BlockState blockState) {

    public static BlockPlacementContext of(BlockPos relativePos, BlockState blockState) {
        return new BlockPlacementContext(relativePos, blockState);
    }
}
