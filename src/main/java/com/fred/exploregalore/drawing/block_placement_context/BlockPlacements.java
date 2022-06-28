package com.fred.exploregalore.drawing.block_placement_context;

import com.fred.exploregalore.drawing.PathDrawer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;

public class BlockPlacements {

    public static final BlockPlacementGenerator[] WALL_COBBLE = {
            BlockPlacementGenerator.constant(BlockPos.ZERO, Blocks.COBBLESTONE.defaultBlockState()),
            BlockPlacementGenerator.constant(BlockPos.ZERO.offset(0, 1, 0), Blocks.COBBLESTONE.defaultBlockState()),
            BlockPlacementGenerator.constant(BlockPos.ZERO.offset(0, 2, 0), Blocks.COBBLESTONE.defaultBlockState()),

    };

    public static void placeBlocksAroundBasis(ServerLevel serverLevel, BlockPos basisPosition, BlockPlacementGenerator[] design) {
        Arrays.stream(design)
                .map(BlockPlacementGenerator::consumePlacement)
                .forEach(context -> PathDrawer.tryPlacingBlock(serverLevel, basisPosition.offset(context.relativePos()), context.blockState()));
    }
}
