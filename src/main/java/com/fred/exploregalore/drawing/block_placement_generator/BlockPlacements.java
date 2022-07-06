package com.fred.exploregalore.drawing.block_placement_generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

public class BlockPlacements {

    public static final BlockPlacementGenerator WALL_COBBLE = BlockPlacementGenerator.builder()
            .constant(
                    BlockPlacementContext.of(BlockPos.ZERO, Blocks.COBBLESTONE.defaultBlockState()),
                    BlockPlacementContext.of(BlockPos.ZERO.above(1), Blocks.COBBLESTONE.defaultBlockState()),
                    BlockPlacementContext.of(BlockPos.ZERO.above(2), Blocks.COBBLESTONE.defaultBlockState()))
            .build();

    // TODO: THE ALTERNATING PART ISN"T WORKING!
    public static final BlockPlacementGenerator UMIBO_GAMING_FENCE_DESIGN_2 = BlockPlacementGenerator.builder()
            .constant(BlockPlacementContext.of(BlockPos.ZERO, Blocks.STONE_BRICKS.defaultBlockState()))
            .alternatingSamePosition(
                    BlockPos.ZERO.above(1),
                    Blocks.STONE_BRICK_WALL.defaultBlockState(),
                    Blocks.AIR.defaultBlockState())
            .constant(BlockPlacementContext.of(BlockPos.ZERO.above(2), Blocks.STONE_SLAB.defaultBlockState()))
            .build();

}
