package com.fred.exploregalore.drawing;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.commands.DrawBlockPathCommand;
import com.fred.exploregalore.math.PathBuilder;
import com.fred.exploregalore.math.parametricfunctions.VoxelCubicBezier;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

@Log4j2
public class LinearPathDrawer {


    public static int drawBlockPath(ServerLevel serverLevel, Block block, BlockPos startPos, BlockPos endPos) {
        ExploreGalore.LOGGER.info("Explore Galore 'drawblockpath' Command Execute Success!");


        Iterable<BlockPos> path = new PathBuilder()
                .linearPath3D(startPos, endPos)
                //.helixPathCounterClockwiseY3d(startPos, endPos, 10, 3)
                .getBlockPath();
        for (BlockPos blockPos : path) {
            if (!tryPlacingBlock(serverLevel, blockPos, block)) {
//                throw DrawBlockPathCommand.ERROR_FAILED.create();
                throw new RuntimeException("Failed to draw block, TODO handle");
            }
        }

//        commandSourceStack.sendSuccess(new TranslatableComponent("commands.drawblockpath.success"), true);
//        return Command.SINGLE_SUCCESS;

        return 0; // TODO


    }

    // TODO: Refactor into one method!
    public static int drawCubicBezierBlockPath(ServerLevel serverLevel, Block block,
                                               BlockPos P0, BlockPos P1, BlockPos P2, BlockPos P3) {
        ExploreGalore.LOGGER.debug("Executing drawCubicBezierBlockPath");

        for (val voxelPos : new VoxelCubicBezier(P0, P1, P2, P3)) {
            if (!tryPlacingBlock(serverLevel, new BlockPos(voxelPos), block)) {
//                throw DrawBlockPathCommand.ERROR_FAILED.create();
                throw new RuntimeException("Failed to draw block, TODO handle");
            }
        }

//        commandSourceStack.sendSuccess(new TranslatableComponent("commands.drawblockpath.success"), true);
//        return Command.SINGLE_SUCCESS;

        return 0; // TODO


    }

    private static boolean tryPlacingBlock(ServerLevel serverLevel, BlockPos blockPos, Block blockToPlace) {
        // If the existing block at blockPos is a BlockEntity, we want to first remove its
        // inventory (so nothing drops)
        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
        Clearable.tryClear(blockEntity);

        // Don't know if I should use this method, or if a "safer" method exists
        // UPDATE_ALL to update client, rendering, neighboring blocks, etc.
        // Returns true if block placement was successful.
        return serverLevel.setBlock(blockPos, blockToPlace.defaultBlockState(), Block.UPDATE_ALL);

        // Delivering neighboring updates, don't need it when using Block.UPDATE_ALL
        // serverLevel.blockUpdated(blockPos, PINK_WOOL_BLOCK);
    }
}
