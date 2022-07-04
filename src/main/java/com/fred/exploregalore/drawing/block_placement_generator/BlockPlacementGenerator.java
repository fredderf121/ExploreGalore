package com.fred.exploregalore.drawing.block_placement_generator;

import com.fred.exploregalore.drawing.PathDrawer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Arrays;
import java.util.List;

public interface BlockPlacementGenerator {

    static void placeBlocksAroundBasis(ServerLevel serverLevel, BlockPos basisPosition, BlockPlacementGenerator design) {
        design.getNextPlacements()
                .forEach(context -> PathDrawer.tryPlacingBlock(serverLevel, basisPosition.offset(context.relativePos()), context.blockState()));
    }

    /**
     * Returns the next BlockState, and where it should be placed <i>relative to a basis BlockPos</i>.
     *
     * <p>
     *     In the case of fairly complex placement contexts, this may have the effect of <i>mutating</i> this
     *     object's state, such that the NEXT call of {@link #getNextPlacements()} may return a different value.
     * </p>
     */
    List<BlockPlacementContext> getNextPlacements();

    /**
     * Sets the generator back to its initial state.
     */
    void reset();



    static BlockPlacementGenerator constant(BlockPlacementContext... contexts) {
        return new ConstantBlockPlacementGenerator(contexts);
    }

    static BlockPlacementGenerator alternating(BlockPlacementContext[][] contexts) {
        return new AlternatingBlockPlacementGenerator(Arrays.stream(contexts).map(BlockPlacementGenerator::constant).toList());
    }

    static BlockPlacementGenerator compound(BlockPlacementGenerator... generators) {
        return new CompoundBlockPlacementGenerator(generators);
    }

}
