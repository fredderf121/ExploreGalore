package com.fred.exploregalore.drawing.block_placement_generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.math3.util.Pair;

public class BlockPlacements {

    public static final BlockPlacementGenerator WALL_COBBLE = BlockPlacementGenerator.builder()
            .constant(
                    BlockPlacementContext.of(BlockPos.ZERO, Blocks.COBBLESTONE.defaultBlockState()),
                    BlockPlacementContext.of(BlockPos.ZERO.above(1), Blocks.COBBLESTONE.defaultBlockState()),
                    BlockPlacementContext.of(BlockPos.ZERO.above(2), Blocks.COBBLESTONE.defaultBlockState()))
            .build();

    //==== Umibo Designs ====//
    // https://www.reddit.com/r/Minecraftbuilds/comments/ve7pf4/12_fence_design_ideas/?utm_source=share&utm_medium=web2x&context=3
    public static final BlockPlacementGenerator UMIBO_GAMING_FENCE_DESIGN_2 = BlockPlacementGenerator.builder()
            .constant(BlockPlacementContext.of(BlockPos.ZERO, Blocks.STONE_BRICKS.defaultBlockState()))
            .alternatingSamePosition(
                    BlockPos.ZERO.above(1),
                    Blocks.STONE_BRICK_WALL.defaultBlockState(),
                    Blocks.AIR.defaultBlockState())
            .constant(BlockPlacementContext.of(BlockPos.ZERO.above(2), Blocks.STONE_SLAB.defaultBlockState()))
            .build();

    public static final BlockPlacementGenerator UMIBO_GAMING_FENCE_DESIGN_6 = BlockPlacementGenerator.builder()
            .randomSamePosition(
                    BlockPos.ZERO,
                    Pair.create(Blocks.SCAFFOLDING.defaultBlockState(), 6D),
                    Pair.create(Blocks.COMPOSTER.defaultBlockState(), 2D),
                    Pair.create(Blocks.BARREL.defaultBlockState(), 1D))
            .randomSamePosition(
                    BlockPos.ZERO.above(1),
                    Pair.create(Blocks.HAY_BLOCK.defaultBlockState(), 7D),
                    Pair.create(Blocks.SCAFFOLDING.defaultBlockState(), 2D)
            )
            .constant(BlockPos.ZERO.above(2), Blocks.OAK_TRAPDOOR.defaultBlockState())
            .build();

}
