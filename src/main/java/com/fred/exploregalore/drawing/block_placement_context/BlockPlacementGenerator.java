package com.fred.exploregalore.drawing.block_placement_context;

import it.unimi.dsi.fastutil.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockPlacementGenerator {

    /**
     * Returns the next BlockState, and where it should be placed <i>relative to a basis BlockPos</i>.
     *
     * <p>
     *     In the case of fairly complex placement contexts, this may have the effect of <i>mutating</i> this
     *     object's state, such that the NEXT call of {@link #consumePlacement()} may return a different value.
     * </p>
     */
    BlockPlacementContext consumePlacement();

    record BlockPlacementContext(BlockPos relativePos, BlockState blockState){}

    static BlockPlacementGenerator constant(BlockPos blockPos, BlockState blockState) {
        return new ConstantBlockPlacementGenerator(blockPos, blockState);
    }
}
