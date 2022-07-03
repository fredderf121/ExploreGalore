package com.fred.exploregalore.drawing.block_placement_context;

import com.fred.exploregalore.drawing.PathDrawer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;

public class BlockPlacements {

    public static final BlockPlacementGenerator WALL_COBBLE = BlockPlacementGenerator.constant(
            BlockPlacementContext.of(BlockPos.ZERO, Blocks.COBBLESTONE.defaultBlockState()),
            BlockPlacementContext.of(BlockPos.ZERO.above(1), Blocks.COBBLESTONE.defaultBlockState()),
            BlockPlacementContext.of(BlockPos.ZERO.above(2), Blocks.COBBLESTONE.defaultBlockState()));


    public static final BlockPlacementGenerator UMIBO_GAMING_FENCE_DESIGN_2 = BlockPlacementGenerator.alternating(
            new BlockPlacementContext[][]{
                    new BlockPlacementContext[]{
                            BlockPlacementContext.of(BlockPos.ZERO, Blocks.STONE_BRICKS.defaultBlockState()),
                            BlockPlacementContext.of(BlockPos.ZERO.above(1), Blocks.STONE_BRICK_WALL.defaultBlockState()),
                            BlockPlacementContext.of(BlockPos.ZERO.above(2), Blocks.STONE_SLAB.defaultBlockState()),
                    },
                    new BlockPlacementContext[]{
                            BlockPlacementContext.of(BlockPos.ZERO, Blocks.STONE_BRICKS.defaultBlockState()),
                            BlockPlacementContext.of(BlockPos.ZERO.above(1), Blocks.AIR.defaultBlockState()),
                            BlockPlacementContext.of(BlockPos.ZERO.above(2), Blocks.STONE_SLAB.defaultBlockState())
                    }});

    public static void placeBlocksAroundBasis(ServerLevel serverLevel, BlockPos basisPosition, BlockPlacementGenerator design) {
        design.getNextPlacements()
                .forEach(context -> PathDrawer.tryPlacingBlock(serverLevel, basisPosition.offset(context.relativePos()), context.blockState()));
    }
}
