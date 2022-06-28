package com.fred.exploregalore.drawing.block_placement_context;

import it.unimi.dsi.fastutil.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public record ConstantBlockPlacementGenerator(BlockPos relativeToBasis, BlockState blockState) implements BlockPlacementGenerator {

    @Override
    public BlockPlacementContext consumePlacement() {
        return new BlockPlacementContext(relativeToBasis, blockState);
    }
}
