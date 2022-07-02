package com.fred.exploregalore.drawing.block_placement_context;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public record BlockPlacementContext(BlockPos relativePos, BlockState blockState) {
}
